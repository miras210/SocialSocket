package kz.malimov.db;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PortalRepository {
    private static PortalRepository instance = new PortalRepository();

    public static PortalRepository getInstance() {
        return instance;
    }

    private PortalRepository() {}

    public Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost/social";
            Properties props = new Properties();
            props.setProperty("user","postgres");
            props.setProperty("password","postgres");
            Connection conn = DriverManager.getConnection(url, props);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
