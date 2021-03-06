package p1admin.adminDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

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
    protected abstract String[] getKeyColumnNames();

    protected abstract Object[] getObjectArray(T obj);

    protected abstract Object[] getObjectId(T obj);

    protected abstract Object[] getColumnValues(T obj);

    public boolean insert(T obInsert) {
        DataAccessor da = new DataAccessor(ds);
        return da.insertRow(getTableName(), getColumnNames(), getObjectArray(obInsert));
    }

    public boolean update(T obj) {
        DataAccessor da = new DataAccessor(ds);
        return da.updateRows(getTableName(), getKeyColumnNames(), getObjectId(obj), getColumnNames(), getColumnValues(obj));
    }

    public boolean delete(T object) {
        DataAccessor da = new DataAccessor(ds);
        return da.deleteRow(getTableName(), getKeyColumnNames(), getObjectId(object));
    }

    /**
     * Construye un objeto mapeado a partir del ResultSet pasado como parámetro.
     * Esta función es la que establece la correspondencia desde el mundo
     * relacional al mundo orientado a objetos.
     */
    protected abstract T buildObjectFromResultSet(ResultSet rs) throws SQLException;

    public T findById(Object[] id){
        String tableName = getTableName();
        String[] columnNames = getColumnNames();

        String[] keyColumnNames = getKeyColumnNames();
        for (int i = 0; i < keyColumnNames.length; i++) {
            keyColumnNames[i] = keyColumnNames[i].concat(" = ?");
        }

        String sql = "SELECT " + String.join( ", ", columnNames) +
                     " FROM " + tableName +
                     " WHERE " + String.join(" AND ", keyColumnNames);

        try (Connection con = ds.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)) {
            for (int i = 0; i < id.length; i++) {
                pst.setObject(i + 1, id[i]);
            }
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

     /**
      * Select All
      * @return Lista de objetos de una tabla
      */
    public List<T> selectAll() {
        List<T> res = new LinkedList<T>();
        String sql = "SELECT * FROM " + this.getTableName();

        try (Connection con = ds.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                res.add(this.buildObjectFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }
}
