package glaciar.connection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

public abstract class PenguinConnectionJDBC implements PenguinConnection
{
	private final String PROPERTIES_FILE;
	
	private ResultSet rs;
	private PreparedStatement st;
	private Connection con;
	
	public abstract String setPropertiesFilePath();
	
	public PenguinConnectionJDBC() throws SQLException
	{
		cargarDriver();
		PROPERTIES_FILE = setPropertiesFilePath();
		String[] datosConexion = obtenerDatosConexion();
		con = DriverManager.getConnection(datosConexion[0], datosConexion[1], datosConexion[2]);
	}
	
	private static void cargarDriver() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();			
		}
	}
	
	private String[] obtenerDatosConexion()
    {
        Properties properties = new Properties();
        String[] lista = null;
        // try (FileInputStream fis = new FileInputStream(PROPERTIES_FILE_B)) {
        try (InputStream fis = PenguinConnectionJDBC.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)){
        	if (fis == null) {
                System.out.println("Sorry, unable to find db.properties");
                return null;
            }
            properties.load(fis);
            lista = new String[3];
            lista[0] = properties.getProperty("url");
            lista[1] = properties.getProperty("user");
            lista[2] = properties.getProperty("password");
            
        } catch (IOException e) {
            e.printStackTrace();
        }        
        
        return lista;
    }
	
	public PreparedStatement newPreparedStatement(String sql) throws SQLException
    {
    	if(st != null)
    	{
    		st.close();
    	}    	
    	st = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);    	
    	return st;
    }
	
	private ResultSet newResultSet(String sql) throws SQLException
	{
		if(rs != null)
		{
			rs.close();
		}
		rs = st.executeQuery(sql);
		return rs;
	}
	
	public ResultSet lanzarSelect(String select) throws SQLException
	{
		newPreparedStatement(select);
		return newResultSet(select);		
	}
    
	public void close() {
		try {
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}	
	
	

}
