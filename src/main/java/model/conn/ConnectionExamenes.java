package model.conn;

import java.sql.SQLException;

import glaciar.connection.PenguinConnectionJDBC;
import glaciar.connection.PenguinConnectionPool;

public class ConnectionExamenes extends PenguinConnectionPool{

	public ConnectionExamenes() throws SQLException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String setPropertiesFilePath() {
		return "db.properties";
	}

}
