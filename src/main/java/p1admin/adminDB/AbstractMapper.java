package p1admin.adminDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;


public abstract class AbstractMapper<T> {
	
	protected DataSource ds;

	public AbstractMapper(DataSource ds) {
		this.ds = ds;
	}

	/**
	 * Devuelve el nombre de la tabla correspondiente al mapper concreto
	 */
	protected abstract String getTableName();

	/**
	 * Devuelve un array con los nombres de las columnas de la tabla
	 */
	protected abstract String[] getColumnNames();

	/**
	 * Devuelve un array de String con los nombres de las columnas clave de la tabla
	 */
	protected abstract String getKeyColumnName();

	 /**
	  * Inserta un objeto en la base de datos (depende del mapper concreto.	
	  */
	public abstract boolean insert(T obInsert);
	/**
	 * Construye un objeto mapeado a partir del ResultSet pasado como parámetro.
	 * Esta función es la que establece la correspondencia desde el mundo
	 * relacional al mundo orientado a objetos.
	 */
	protected abstract T buildObjectFromResultSet(ResultSet rs) throws SQLException;

	public T findById(Object[] id){
		String tableName = getTableName();
		String[] columnNames = getColumnNames();
		String keyColumnName = getKeyColumnName();
		
		String sql = "SELECT " + String.join( ", ", columnNames) +
				     " FROM " + tableName +
				     " WHERE " + String.join(" AND ", keyColumnName + " = ?");
		
		try (Connection con = ds.getConnection();
			PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setObject(1, id);
			try (ResultSet rs = pst.executeQuery()){
				if (rs.next()) {
					return buildObjectFromResultSet(rs);
				}
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	//Update database, uses DataAccesor
	public boolean update(Object[] columnValues, Object[] keyValues) {
		DataAccessor da = new DataAccessor(ds);
		return da.updateRows(getTableName(), getKeyColumnName(), keyValues, getColumnNames(), columnValues);
	}

	 // ELIMINA UNA FILA EN LA TABLA CORRESPONDIENTE DE LA BASE DE DATOS
	 public boolean delete(Object[] id) {
		 DataAccessor da = new DataAccessor(ds);
		 return da.deleteRow(getTableName(), getKeyColumnName(), id); 
	 }
	 
	/**
	 * Inserción general
	 * @param id
	 * @return
	 */
	 public boolean insert(Object[] values) {
		 DataAccessor da = new DataAccessor(ds);
		 return da.insertRow(getTableName(), getColumnNames(), values);
	 }
}
