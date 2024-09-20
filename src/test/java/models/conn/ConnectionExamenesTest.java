package models.conn;

import java.sql.SQLException;

import glaciar.connection.PenguinConnectionPool;

public class ConnectionExamenesTest extends PenguinConnectionPool{

	public ConnectionExamenesTest() throws SQLException {
		super();
	}

	@Override
	public String setPropertiesFilePath() 
	{
		return "db_test.properties";
	}

}
