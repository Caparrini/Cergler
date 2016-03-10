package p1admin.adminDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import javax.sql.DataSource;



public class DataAccessor {
	private DataSource ds;

	public DataAccessor(DataSource ds) {
		super();
		this.ds = ds;
	}
	
	public boolean deleteRow(String tableName, String keyColumnNames,
			Object[] KeyValues) {
		String sql = generateDeleteStatement(tableName, keyColumnNames);
		try(Connection con = ds.getConnection();
				PreparedStatement pst = con.prepareStatement(sql)) {
				for (int i = 0; i < KeyValues.length; i++) {
					pst.setObject(i + 1, KeyValues[i]);
				}
					int numRows = pst.executeUpdate();
					return (numRows == 1);
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}	
	}
	


	public boolean updateRows(String tableName, String keyColumnName,
			Object[] keyValues,String[] columnNames,
			Object[] columnValues) {
		
		String sql = generateUpdateStatement(tableName, keyColumnName,columnNames);
		try(Connection con = ds.getConnection();
				PreparedStatement pst = con.prepareStatement(sql)) {
				for (int i = 0; i < columnValues.length; i++) {
					pst.setObject(i + 1, columnValues[i]);
				}
				
				for (int i = 0; i < keyValues.length; i++){
					pst.setObject(columnValues.length + i + 1, keyValues[i]);
				}
				
				int numRows = pst.executeUpdate();
				return (numRows == 1);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}	
	}



	/**
	 * Inserta una fila en una tabla de la BD.
	 * 
	 * @param tableName Nombre de la tabla en la que insertar la fila.
	 * @param fields Nombres de las columnas a rellenar de la nueva fila.
	 * @param values Valores a insertar en la nueva fila. Este array ha de tener
	 *       tantos elementos como <code>fields</code>
	 * @return <code>true</code> si la inserción tuvo éxito, o <code>false</code>
	 *       en caso contrario.
	 */
	public boolean insertRow(String tableName, String[] fields, Object[] values) {
		String sql = generateInsertStatement(tableName, fields);
		
		try (Connection con = ds.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
			for (int i = 0; i < values.length; i++) {
				pst.setObject(i + 1, values[i]);
			}
			int numRows = pst.executeUpdate();
			return (numRows == 1);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String generateInsertStatement(String tableName, String[] fields) {
		String fieldList = String.join( ",",fields);
		String[] marks = new String[fields.length];
		Arrays.fill(marks, "?");
		String markList = String.join(",",marks);
		return "INSERT INTO " + tableName + " (" + fieldList + ") VALUES (" + markList + ")";
	}
	
	private String generateUpdateStatement(String tableName,
			String keyColumnNames, String[] columnNames) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String generateDeleteStatement(String tableName,
			String keyColumnNames) {
		// TODO Auto-generated method stub
		return null;
	}

}
