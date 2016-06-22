package com.intellectualsites.rectangular.database;

import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.data.RegionData;
import com.intellectualsites.rectangular.player.PlayerMeta;
import com.intellectualsites.rectangular.vector.Vector2;
import lombok.RequiredArgsConstructor;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.DeleteQuery;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.UpdateQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;
import org.polyjdbc.core.schema.SchemaInspector;
import org.polyjdbc.core.schema.SchemaManager;
import org.polyjdbc.core.schema.model.Schema;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class RectangularDB {

    private final String prefix;

    public String getMainTableName() {
        return prefix + "region";
    }

    public String getRectangleTableName() {
        return prefix + "rectangle";
    }

    public String getRegionMetaTableName() {
        return prefix + "region_meta";
    }

    public String getPlayerMetaTableName() {
        return prefix + "player_meta";
    }

    private PolyJDBC polyJDBC;

    public PolyJDBC getPolyJDBC() {
        if (polyJDBC == null) {
            polyJDBC = createConnection();
        }
        return polyJDBC;
    }

    public boolean testConnection() {
        try {
            PolyJDBC jdbc = getPolyJDBC();
            if (jdbc == null) {
                return false;
            }
        } catch (final Exception e) {
            return false;
        }
        return true;
    }

    protected abstract PolyJDBC createConnection();

    public void createSchema() {
        SchemaManager schemaManager = null;
        try {
            schemaManager = getPolyJDBC().schemaManager();
            Schema schema = new Schema(polyJDBC.dialect());
            if (!schemaExists(getMainTableName())) {
                schema.addRelation(getMainTableName())
                        .withAttribute().integer("region_id")
                        .withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                        .withAttribute().string("container_id").notNull().withMaxLength(32).and()
                        .primaryKey("pk_" + getMainTableName()).using("region_id").and().build();
            }
            if (!schemaExists(getRectangleTableName())) {
                schema.addRelation(getRectangleTableName())
                        .withAttribute().integer("rectangle_id")
                        .withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                        .withAttribute().integer("region_region_id").and()
                        .withAttribute().integer("minX").withDefaultValue(0).and()
                        .withAttribute().integer("maxX").withDefaultValue(0).and()
                        .withAttribute().integer("minY").withDefaultValue(0).and()
                        .withAttribute().integer("maxY").withDefaultValue(0).and()
                        .primaryKey("pk_" + getRectangleTableName()).using("rectangle_id").and()
                        .foreignKey("fk_" + getRectangleTableName()).on("region_region_id")
                        .references(getMainTableName(), "region_id").and()
                        .build();
            }
            if (!schemaExists(getRegionMetaTableName())) {
                schema.addRelation(getRegionMetaTableName())
                        .withAttribute().integer("meta_id").withAdditionalModifiers("AUTO_INCREMENT")
                        .notNull().and()
                        .withAttribute().integer("region_region_id").and()
                        .withAttribute().string("owner").notNull().withDefaultValue("__SERVER__")
                        .withMaxLength(48).and()
                        .withAttribute().text("data").notNull().and()
                        .primaryKey("pk_" + getRegionMetaTableName()).using("meta_id").and()
                        .foreignKey("fk_" + getRegionMetaTableName()).on("region_region_id")
                        .references(getMainTableName(), "region_id").and()
                        .build();
            }
            if (!schemaExists(getPlayerMetaTableName())) {
                schema.addRelation(getPlayerMetaTableName())
                        .withAttribute().integer("player_meta_id").withAdditionalModifiers("AUTO_INCREMENT")
                        .notNull().and()
                        .withAttribute().string("uuid").notNull().and()
                        .withAttribute().string("key").notNull().and()
                        .withAttribute().string("value").notNull().withDefaultValue("").and()
                        .primaryKey("pk_" + getPlayerMetaTableName()).using("player_meta_id").and()
                        .build();
            }
            schemaManager.create(schema);
        } finally {
            polyJDBC.close(schemaManager);
        }
    }

    public boolean schemaExists(String schemaName) {
        SchemaInspector inspector = null;
        boolean exists = false;
        try {
            inspector = getPolyJDBC().schemaInspector();
            exists = inspector.relationExists(schemaName);
        } finally {
            getPolyJDBC().close(inspector);
        }
        return exists;
    }

    public Set<Rectangle> loadRectangles() {
        SelectQuery query = getPolyJDBC().query().selectAll().from(getRectangleTableName());
        return getPolyJDBC().simpleQueryRunner().querySet(query, rectangleMapper);
    }

    private final ObjectMapper<Rectangle> rectangleMapper = resultSet ->
            new Rectangle(resultSet.getInt("region_region_id"),
            new Vector2(resultSet.getInt("minX"), resultSet.getInt("minY")),
                    new Vector2(resultSet.getInt("maxX"), resultSet.getInt("maxY")));

    private final ObjectMapper<Region> regionMapper = resultSet ->
            new Region(resultSet.getInt("region_id"), 1, resultSet.getString("container_id"));

    private final ObjectMapper<RegionData> regionDataMapper = resultSet ->
            new RegionData(resultSet.getInt("region_region_id"),
                    resultSet.getString("owner"), resultSet.getString("data"));

    private final ObjectMapper<PlayerMeta> playerMetaMapper = resultSet -> {
        Map<String, byte[]> map = new HashMap<>();
        while (resultSet.next()) {
            map.put(resultSet.getString("key"), resultSet.getString("value").getBytes());
        }
        return new PlayerMeta(map);
    };

    public PlayerMeta loadPlayerMeta(UUID uuid) {
        return this.loadPlayerMeta(uuid.toString());
    }

    public void addPlayerMeta(String uuid, String key, byte[] value) {
        InsertQuery query = getPolyJDBC().query().insert().into(getPlayerMetaTableName())
                .value("uuid", uuid).value("key", key).value("value", new String(value));
        getPolyJDBC().simpleQueryRunner().insert(query);
    }

    public void updatePlayerMeta(String uuid, String key, byte[] value) {
        UpdateQuery query = getPolyJDBC().query().update(getPlayerMetaTableName()).set("value", new String(value))
                .where("uuid = :uniqueId AND key = :ukey").withArgument("uniqueId", uuid).withArgument("ukey", key);
        getPolyJDBC().simpleQueryRunner().update(query);
    }

    public  void removePlayerMeta(String uuid, String key) {
        DeleteQuery query = getPolyJDBC().query().delete().from(getPlayerMetaTableName())
                .where("uuid = :uniqueId AND key = :ukey").withArgument("uniqueId", uuid).withArgument("ukey", key);
        getPolyJDBC().queryRunner().delete(query);
    }

    public PlayerMeta loadPlayerMeta(String uuid) {
        SelectQuery query = getPolyJDBC().query().selectAll().from(getPlayerMetaTableName()).where("uuid = :uniqueId").withArgument("uniqueId", uuid);
        return getPolyJDBC().simpleQueryRunner().queryUnique(query, playerMetaMapper).associate(uuid);
    }

    public Set<RegionData> loadRegionData() {
        SelectQuery query = getPolyJDBC().query().selectAll().from(getRegionMetaTableName());
        return getPolyJDBC().simpleQueryRunner().querySet(query, regionDataMapper);
    }

    public Set<Region> loadRegions() {
        SelectQuery query = getPolyJDBC().query().selectAll().from(getMainTableName());
        return getPolyJDBC().simpleQueryRunner().querySet(query, regionMapper);
    }
}
