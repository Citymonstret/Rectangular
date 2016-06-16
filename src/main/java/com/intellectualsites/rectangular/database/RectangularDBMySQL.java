package com.intellectualsites.rectangular.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.PolyJDBCBuilder;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.DialectRegistry;

public class RectangularDBMySQL extends RectangularDB {

    private final String database, user, password, host;
    private final int port;

    public RectangularDBMySQL(String database, String user, String password,
                              String host, int port, String prefix) {
        super(prefix);
        this.database = database;
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    @Override
    protected PolyJDBC createConnection() {
        Dialect dialect = DialectRegistry.MYSQL.getDialect();
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL("jdbc:mysql://" + host + ":" + port + "/" + database);
        mysqlDataSource.setUser(user);
        mysqlDataSource.setPassword(password);
        return PolyJDBCBuilder.polyJDBC(dialect).connectingToDataSource(mysqlDataSource).build();
    }


}
