package glaciar;

import java.sql.Blob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import glaciar.connection.PenguinConnection;
import glaciar.connection.PenguinConnectionJDBC;

// DAO genérico que hace uso de JDBC
public class PenguinDao 
{	
	private final PenguinConnection conn;
	
	public PenguinDao(PenguinConnection conn)
	{
		this.conn = conn;
		
	}
	
	// #####################################################################################  CREATE  #####################################################################################
	
	public <T> int create(T obj) throws SQLException
	{
		PenguinObject<T> po = new PenguinObject<T>(obj);
		return create(po);
	}
	
	//TODO: Juntar con el codigo del update para reusar el código
	public <T> int create(PenguinObject<T> po) throws SQLException
	{
		int result = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(po.getName())
		.append(" (").append(po.getFieldsSeparatedByCommas(true)).append(") ")
		.append(" VALUES (").append(po.getQuestionMarks(true)).append(")");
		
		PreparedStatement st = conn.newPreparedStatement(sql.toString());
		
		Object[] values = po.getFieldValues(true);
		setPreparedStatementParameters(st,values);
		result += st.executeUpdate();
		
		// Preguntar si es autoincrement, si ha sido generado con autoincrement obtener la nueva key y actualizarla en todos sus pengun key si estos tienen foreigns con la clase que toca
	
		if(po.isPenguinKeyAutoIncrement())
		{
			if (result > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    po.setPenguinKey(generatedId);
                }
            }
		}
		
		for(PenguinObject<?> p: po.getPenguinObjects())
		{
			create(p);
			result++;
		}
		
		// Usar programacion funcional para generar el stream de values e iterar cada uno para añadirlos al st con el get correspondiente (hará falta identificar el tipo del valor ya sea string blob o date)
		
		return result;
	}
	
	private void setPreparedStatementParameters(PreparedStatement st, Object[] values) throws SQLException 
	{
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            //System.out.println(value.toString());
            // Usa un switch para manejar diferentes tipos de datos
            if (value instanceof String) {
                st.setString(i + 1, (String) value);
            } else if (value instanceof Integer) {
                st.setInt(i + 1, (Integer) value);
                
            } else if (value instanceof Blob) {
                st.setBlob(i + 1, (Blob) value);
            } else if (value instanceof byte[]) {
            	st.setBlob(i + 1, new javax.sql.rowset.serial.SerialBlob((byte[]) value));
            	
            } else if (value instanceof Date) {
                st.setDate(i + 1, (Date) value);
            } else if (value instanceof LocalDateTime) {
            	Timestamp timestamp = Timestamp.valueOf((LocalDateTime) value);
                st.setTimestamp(i + 1, timestamp);
                
            }else if (value instanceof Float) {
                st.setFloat(i + 1, (Float) value);
            } else if (value instanceof Boolean) {
                st.setBoolean(i + 1, (Boolean) value);
            }else if (value == null) {
                st.setNull(i + 1, Types.NULL); // Maneja los valores nulos
            } else {
                throw new SQLException("Unsupported data typeE: " + value.getClass().getName());
            }
        }
    }
	
	/*
	 * DEPRECATED por no poder guardar tipos complejos como BLOB o fechas
	 * 
	public <T> int create(PenguinObject<T> po) throws SQLException
	{
		int result = 0;	
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("INSERT INTO ").append(po.getName())
				.append(" (").append(po.getFieldsSeparatedByCommas())
				.append(") VALUES (").append(po.getFieldValuesSeparatedByCommas()).append(");");

		System.out.println(sql.toString());
		
		PreparedStatement st = conn.newPreparedStatement(sql.toString()); // Aquí utilizamos `prepareStatement` en lugar de `newPreparedStatement`
		result = st.executeUpdate();
		
		//Modificar los po que esten dentro del original y sumar uno al result si se hace
		for(PenguinObject<?> p: po.getPenguinObjects())
		{
			create(p);
			result++;
		}
		
		
		return result;
	}
	*/
	
	// #####################################################################################  READ EN OBRAS  #####################################################################################
	
	
	
	
	
	public <T> T findById(T obj) throws SQLException
	{
		PenguinObject<T> po = new PenguinObject<>(obj);
		T[] entityArray = findByPo(po);
		if(entityArray.length == 0) {
			return null;
		}		
		return entityArray[0];
	}
	
	
	
	private <T> T[] findByPo(PenguinObject<T> po) throws SQLException
	{
		StringBuilder sql = new StringBuilder("SELECT * FROM ").append(po.getName())
				.append(" WHERE ").append(po.getWhereId()).append(";");
	        return po.resultSetToObject(conn.lanzarSelect(sql.toString()));
	}
	
	public <T> T[] findAll(Class<T> clazz) throws SQLException 
    {   
    	PenguinObject<T> genericPo = new PenguinObject<>(clazz);
    	StringBuilder sql = new StringBuilder("SELECT * FROM ").append(genericPo.getName()).append(";");
    	ResultSet rs = conn.lanzarSelect(sql.toString());
    	
    	
    	T[] objs = genericPo.resultSetToObject(rs);
    	objs = findAllContentRelations(objs);    	
    	
	    return objs;
    }	
	
	public <T> T[] findByField(Class<T> clazz, String field, Object value) throws SQLException
	{
		PenguinObject<T> po = new PenguinObject<>(clazz);
		po.setFieldValue(field, value);
		StringBuilder sql = new StringBuilder("SELECT * FROM ").append(po.getName());
		sql.append(" WHERE ").append(po.getAsignation(field)).append(";");
		System.out.println(sql.toString());
		return po.resultSetToObject(conn.lanzarSelect(sql.toString()));
	}
	
	public <T> T findById(Class<T> clazz, Object id) throws SQLException
	{   
		PenguinObject<T> po = new PenguinObject<>(clazz, id);
		T[] entityArray = findByPo(po);
		if(entityArray.length == 0) {
			return null;
		}		
		return entityArray[0];			
    }
	
	private <T> T[] findAllContentRelations(T[] objs) throws SQLException
	{
		List<PenguinObject<T>> pObjs = new ArrayList<>();
    	for(T obj: objs)
    	{
    		PenguinObject<T> po = new PenguinObject<>(obj);
    		
    		// Test para comprobar que funciona la modificacion
    		
    		// po.setPenguinKey(444); // FUNCIONA
    		
    		//TODO:IMPORTANTE
    		
    		// () En el momento de crear el po obtener la info de que atributos de obj almacenan otra entidad
    		
    		// Ir en busca de las ForeignKeys de la otra Entidad para saber su nombre (es necesario un nuevo po)
    		
    		// Hacer una consulta en la tabla de la otra entidad, campo = nombre de la fk, valor = pk value del po
    		
    		for (Class<?> c: po.getPenguinObjectsClasses())
    		{
    			PenguinObject<?> genericPo = new PenguinObject<>(c);
    			Object[] objs2 = findByField(c, genericPo.getForeignKeyName(po.getClazz()), po.getPenguinKeyValue());
    			//po.setContentRelations(objs2);
    		}
    		
    		// por cada po dentro del po repetir
    		
    		// Añadir ese valor ranto al atributo del obj como al po
    		
    		// Repetir operacion para esos nuevos resultados (Recursividad)
    		
    		pObjs.add(po);
    	}    	
    	return objs;
	}
	
	// #####################################################################################  UPDATE  #####################################################################################
	
	public <T> int update(T obj) throws SQLException
	{
		return update(new PenguinObject<T>(obj));
	}
	
	private <T> int update(PenguinObject<T> po) throws SQLException
	{
		//"UPDATE users SET name = ?, email = ? WHERE id = ?";
		int result = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(po.getName())
		.append(" SET ").append(po.getVoidSets())
		.append(" WHERE ").append(po.getWhereId()).append(";");		

		PreparedStatement st = conn.newPreparedStatement(sql.toString());
		
		Object[] values = po.getFieldValues(true);
		setPreparedStatementParameters(st,values);
		result += st.executeUpdate();
		
		for(PenguinObject<?> p: po.getPenguinObjects())
		{
			System.out.println(p);
			update(p);
			result++;
		}
		
		// Usar programacion funcional para generar el stream de values e iterar cada uno para añadirlos al st con el get correspondiente (hará falta identificar el tipo del valor ya sea string blob o date)
		
		return result;
	}	
	
	public <T> int updateDeprected(T obj) throws SQLException
	{
		PenguinObject<T> po = new PenguinObject<>(obj);		
		StringBuilder sql = new StringBuilder("UPDATE ")
				.append(po.getName())
				.append(" SET ").append(po.getSet())
				.append(" WHERE ").append(po.getWhereId()).append(";");		
		//System.out.println(sql);		
		PreparedStatement st = conn.newPreparedStatement(sql.toString());
		int result = st.executeUpdate();
		return result;
	}	
	
	// #####################################################################################  DELETE  #####################################################################################
	
	public <T> int delete(T obj) throws SQLException
	{
		PenguinObject<T> po = new PenguinObject<>(obj);
		String sql = "DELETE FROM " + po.getName() + " WHERE " + po.getWhereId();
		PreparedStatement pstmt = conn.newPreparedStatement(sql);
        return pstmt.executeUpdate();
	}

	public void closeConn() {
		conn.close();
		
	}

}
