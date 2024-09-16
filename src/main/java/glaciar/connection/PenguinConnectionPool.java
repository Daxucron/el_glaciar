package glaciar.connection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public abstract class PenguinConnectionPool implements PenguinConnection {

    private final String PROPERTIES_FILE;

    private ResultSet rs;
    private PreparedStatement st;
    private Connection con;
    private static HikariDataSource dataSource;

    public abstract String setPropertiesFilePath();

    public PenguinConnectionPool() throws SQLException {
        PROPERTIES_FILE = setPropertiesFilePath();
        cargarDriver();
        if (dataSource == null) {
            initializeDataSource();
        }
        con = dataSource.getConnection();
    }
    
    private static void cargarDriver() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();			
		}
	}

    private void initializeDataSource() {
        Properties properties = new Properties();
        try (InputStream fis = PenguinConnectionPool.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (fis == null) {
                System.out.println("Sorry, unable to find " + PROPERTIES_FILE);
                return;
            }
            properties.load(fis);

            HikariConfig config = new HikariConfig();
            
            config.setJdbcUrl(properties.getProperty("url"));
            config.setUsername(properties.getProperty("user"));
            config.setPassword(properties.getProperty("password"));
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PreparedStatement newPreparedStatement(String sql) throws SQLException {
        if (st != null) {
            st.close();
        }
        st = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        return st;
    }

    private ResultSet newResultSet(String sql) throws SQLException {
        if (rs != null) {
            rs.close();
        }
        rs = st.executeQuery(sql);
        return rs;
    }

    public ResultSet lanzarSelect(String select) throws SQLException {
        newPreparedStatement(select);
        return newResultSet(select);
    }

    public void close() {
        try {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

