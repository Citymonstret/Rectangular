package com.intellectualsites.rectangular.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import lombok.RequiredArgsConstructor;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.PolyJDBCBuilder;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.DialectRegistry;

@RequiredArgsConstructor
public class RectangularDBMySQL extends RectangularDB {

    private final String database, user, password;
    private final int port;

    @Override
    protected PolyJDBC createConnection() {
        Dialect dialect = DialectRegistry.MYSQL.getDialect();
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setDatabaseName(database);
        mysqlDataSource.setUser(user);
        mysqlDataSource.setPassword(password);
        mysqlDataSource.setPort(port);
        return PolyJDBCBuilder.polyJDBC(dialect).connectingToDataSource(mysqlDataSource).build();
    }

}
