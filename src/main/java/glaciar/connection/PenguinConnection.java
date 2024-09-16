package glaciar.connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface PenguinConnection {
	
	public PreparedStatement newPreparedStatement(String sql) throws SQLException;	
	public ResultSet lanzarSelect(String select) throws SQLException;    
	public void close();

}
