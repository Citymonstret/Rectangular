package com.intellectualsites.rectangular.database;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.core.SimpleRegion;
import com.intellectualsites.rectangular.data.RegionData;
import com.intellectualsites.rectangular.player.PlayerMeta;
import com.intellectualsites.rectangular.vector.Vector2;
import lombok.RequiredArgsConstructor;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.*;
import org.polyjdbc.core.query.mapper.ObjectMapper;
import org.polyjdbc.core.schema.SchemaInspector;
import org.polyjdbc.core.schema.SchemaManager;
import org.polyjdbc.core.schema.model.Schema;

import java.util.*;

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
        return prefix + "meta";
    }

    public String getPlayerMetaTableName() {
        return prefix + "player";
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
                        .withAttribute().string("mkey").notNull().withMaxLength(32).and()
                        .withAttribute().string("value").notNull().withMaxLength(255).and()
                        .primaryKey("pk_" + getRegionMetaTableName()).using("meta_id").and()
                        .foreignKey("fk_" + getRegionMetaTableName()).on("region_region_id")
                        .references(getMainTableName(), "region_id").and()
                        .build();
            }
            if (!schemaExists(getPlayerMetaTableName())) {
                schema.addRelation(getPlayerMetaTableName())
                        .withAttribute().integer("player_meta_id").withAdditionalModifiers("AUTO_INCREMENT")
                        .notNull().and()
                        .withAttribute().string("uuid").notNull().withMaxLength(48).and()
                        .withAttribute().string("mkey").notNull().withMaxLength(32).and()
                        .withAttribute().string("value").notNull().withMaxLength(255).and()
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

    public ImmutableSet<Rectangle> loadRectangles() {
        SelectQuery query = getPolyJDBC().query().selectAll().from(getRectangleTableName());
        return ImmutableSet.copyOf(getPolyJDBC().simpleQueryRunner().querySet(query, rectangleMapper));
    }

    private final ObjectMapper<Rectangle> rectangleMapper = resultSet ->
            new Rectangle(resultSet.getInt("region_region_id"),
            new Vector2(resultSet.getInt("minX"), resultSet.getInt("minY")),
                    new Vector2(resultSet.getInt("maxX"), resultSet.getInt("maxY")));

    private final ObjectMapper<Region> regionMapper = resultSet ->
            new SimpleRegion(resultSet.getInt("region_id"), 1, resultSet.getString("container_id"));

    private final ObjectMapper<RegionData> regionDataMapper = resultSet -> {
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
        boolean cont = true;

        int regionID = -1;

        while (cont) {
            regionID = resultSet.getInt("region_region_id");
            builder.put(resultSet.getString("mkey"), resultSet.getString("value"));
            cont = resultSet.next();
        }
        Map<String, String> map = builder.build();
        return new RegionData(regionID, map.get("owner"), map);
    };

    private final ObjectMapper<PlayerMeta> playerMetaMapper = resultSet -> {

        Map<String, byte[]> map = new HashMap<>();

        boolean cont = true;
        while (cont) {
            map.put(resultSet.getString("mkey"), resultSet.getString("value").getBytes());
            cont = resultSet.next();
        }

        return new PlayerMeta(map);
    };

    public Region createRegion(String uuid, String containerId, Rectangle initialRectangle) {
        Region region = createRegionAndFetch(uuid, containerId);
        addRectangle(region.getId(), initialRectangle);
        region.setRectangles(Collections.singleton(initialRectangle));
        return region;
    }

    public void addRectangle(int regionId, Rectangle rectangle) {
        InsertQuery insertQuery = getPolyJDBC().query().insert().into(getRectangleTableName())
                .value("region_region_id", regionId).value("minX", rectangle.getMin().getX())
                .value("maxX", rectangle.getMax().getX()).value("minY", rectangle.getMin().getY())
                .value("maxY", rectangle.getMax().getY());
        getPolyJDBC().simpleQueryRunner().insert(insertQuery);
    }

    public Region createRegionAndFetch(String uuid, String container_id) {
        String randomUUID = UUID.randomUUID().toString().substring(0, 32); // Used to fetch the created region
        InsertQuery query = getPolyJDBC().query().insert().into(getMainTableName())
                .value("container_id", randomUUID);
        getPolyJDBC().simpleQueryRunner().insert(query);
        // Fetch the ID
        SelectQuery getID = getPolyJDBC().query().select("region_id").from(getMainTableName())
                .where("container_id = :cid").withArgument("cid", randomUUID);
        int[] idC = new int[] {0};
        getPolyJDBC().simpleQueryRunner().queryUnique(getID, set -> idC[0] = set.getInt("region_id"));
        final int regionID = idC[0];
        // Now we have to set the proper container id
        UpdateQuery updateQuery = getPolyJDBC().query().update(getMainTableName()).set("container_id", container_id)
                .where("region_id = :rid").withArgument("rid", regionID);
        getPolyJDBC().simpleQueryRunner().update(updateQuery);
        // Create region meta
        InsertQuery createMeta = getPolyJDBC().query().insert().into(getRegionMetaTableName())
                .value("region_region_id", regionID).value("mkey", "owner").value("value", uuid);
        getPolyJDBC().simpleQueryRunner().insert(createMeta);
        Region region = new SimpleRegion(regionID, 0, container_id);
        region.setData(loadRegionData(regionID));
        return region;
    }

    public PlayerMeta loadPlayerMeta(UUID uuid) {
        return this.loadPlayerMeta(uuid.toString());
    }

    public void addPlayerMeta(String uuid, String key, byte[] value) {
        InsertQuery query = getPolyJDBC().query().insert().into(getPlayerMetaTableName())
                .value("uuid", uuid).value("mkey", key).value("value", new String(value));
        getPolyJDBC().simpleQueryRunner().insert(query);
    }

    public void updatePlayerMeta(String uuid, String key, byte[] value) {
        UpdateQuery query = getPolyJDBC().query().update(getPlayerMetaTableName()).set("value", new String(value))
                .where("uuid = :uniqueId AND mkey = :ukey").withArgument("uniqueId", uuid).withArgument("ukey", key);
        getPolyJDBC().simpleQueryRunner().update(query);
    }

    public  void removePlayerMeta(String uuid, String key) {
        DeleteQuery query = getPolyJDBC().query().delete().from(getPlayerMetaTableName())
                .where("uuid = :uniqueId AND mkey = :ukey").withArgument("uniqueId", uuid).withArgument("ukey", key);

        QueryRunner runner = getPolyJDBC().queryRunner();
        try {
            runner.delete(query);
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            runner.close();
        }
    }

    public PlayerMeta loadPlayerMeta(String uuid) {
        SelectQuery query = getPolyJDBC().query().selectAll().from(getPlayerMetaTableName()).where("uuid = \"" + uuid + "\"");
        List<PlayerMeta> metaList = getPolyJDBC().simpleQueryRunner().queryList(query, playerMetaMapper);

        PlayerMeta m;
        if (metaList.isEmpty()) {
            m = new PlayerMeta(new HashMap<>());
        } else {
            m = metaList.get(0);
        }

        return m.associate(uuid);
    }

    public RegionData loadRegionData(int regionID) {
        SelectQuery query = getPolyJDBC().query().selectAll().from(getRegionMetaTableName())
                .where("region_region_id = :rid").withArgument("rid", regionID);
        return getPolyJDBC().simpleQueryRunner().queryUnique(query, regionDataMapper);
    }

    public ImmutableSet<Region> loadRegions() {
        SelectQuery query = getPolyJDBC().query().selectAll().from(getMainTableName());
        return ImmutableSet.copyOf(getPolyJDBC().simpleQueryRunner().querySet(query, regionMapper));
    }
}
