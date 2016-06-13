package com.intellectualsites.rectangular.database;

import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;
import org.polyjdbc.core.schema.SchemaInspector;
import org.polyjdbc.core.schema.SchemaManager;
import org.polyjdbc.core.schema.model.Schema;
import org.w3c.dom.css.Rect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

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
        if (schemaExists()) {
            return;
        }
        SchemaManager schemaManager = null;
        try {
            schemaManager = getPolyJDBC().schemaManager();
            Schema schema = new Schema(polyJDBC.dialect());
            schema.addRelation(getMainTableName())
                    .withAttribute().integer("region_id").withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                    .withAttribute().string("container_id").notNull().withMaxLength(32).and()
                    .primaryKey("pk_" + getMainTableName()).using("region_id").and().build();
            schema.addRelation(getRectangleTableName())
                    .withAttribute().integer("rectangle_id").withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                    .withAttribute().integer("region_region_id").notNull().and()
                    .withAttribute().integer("minX").withDefaultValue(0).and()
                    .withAttribute().integer("maxX").withDefaultValue(0).and()
                    .withAttribute().integer("minY").withDefaultValue(0).and()
                    .withAttribute().integer("maxY").withDefaultValue(0).and()
                    .primaryKey("pk_" + getRectangleTableName()).using("rectangle_id").and()
                    .build();
            schema.addRelation(getRegionMetaTableName())
                    .withAttribute().integer("meta_id").withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                    .withAttribute().integer("region_region_id").notNull().and()
                    .withAttribute().string("owner").notNull().withDefaultValue("__SERVER__").withMaxLength(48).and()
                    .withAttribute().text("data").notNull().withDefaultValue("").and()
                    .primaryKey("pk_" + getRegionMetaTableName()).using("meta_id").and()
                    .build();
            schemaManager.create(schema);
        } finally {
            polyJDBC.close(schemaManager);
        }
    }

    public boolean schemaExists() {
        SchemaInspector inspector = null;
        boolean exists = false;
        try {
            inspector = getPolyJDBC().schemaInspector();
            exists = inspector.relationExists(getMainTableName());
        } finally {
            getPolyJDBC().close(inspector);
        }
        return exists;
    }

    public Set<Rectangle> loadRectangles() {
        SelectQuery query = getPolyJDBC().query().selectAll().from(getRectangleTableName());
        return getPolyJDBC().simpleQueryRunner().querySet(query, rectangleMapper);
    }

    private final ObjectMapper<Rectangle> rectangleMapper = resultSet -> new Rectangle(resultSet.getInt("region_region_id"),
            new Vector2(resultSet.getInt("minX"), resultSet.getInt("minY")), new Vector2(resultSet.getInt("maxX"), resultSet.getInt("maxY")));

    private final ObjectMapper<Region> regionMapper = resultSet -> new Region(resultSet.getInt("region_id"), 1, resultSet.getString("container_id"));

    public Set<Region> loadRegions() {
        SelectQuery query = getPolyJDBC().query().select().from(getMainTableName());
        return getPolyJDBC().simpleQueryRunner().querySet(query, regionMapper);
    }
}
