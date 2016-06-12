package com.intellectualsites.rectangular.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.schema.SchemaInspector;
import org.polyjdbc.core.schema.SchemaManager;
import org.polyjdbc.core.schema.model.Schema;

@RequiredArgsConstructor
public abstract class RectangularDB {

    private final String prefix;

    /**
     * TODO: FIX THIS
     */
    public String getMainTableName() {
        return prefix + "regions";
    }

    public String getRectangleTableName() {
        return prefix + "rectangles";
    }

    private PolyJDBC polyJDBC;

    public PolyJDBC getPolyJDBC() {
        if (polyJDBC == null) {
            polyJDBC = createConnection();
        }
        return polyJDBC;
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
                    .withAttribute().longAttr("region_id").withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                    .withAttribute().string("name").withMaxLength(50).notNull().unique().and()
                    .withAttribute().string("owner").withMaxLength(48).notNull().withDefaultValue("__SERVER__").and()
                    .primaryKey("pk_" + getMainTableName()).using("region_id").and().build();
            schema.addRelation(getRectangleTableName())
                    .withAttribute().longAttr("rectangle_id").withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                    .withAttribute().longAttr("region_region_id").notNull().and()
                    .withAttribute().integer("minX").withDefaultValue(0).and()
                    .withAttribute().integer("maxX").withDefaultValue(0).and()
                    .withAttribute().integer("minY").withDefaultValue(0).and()
                    .withAttribute().integer("maxY").withDefaultValue(0).and()
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
}
