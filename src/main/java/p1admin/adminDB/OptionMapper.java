package p1admin.adminDB;

import p1admin.model.Opcion;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class OptionMapper extends AbstractMapper<Opcion> {

    private DataSource ds;
    public OptionMapper(DataSource ds) {
        super(ds);
        this.ds = super.ds;
    }

    @Override
    protected String getTableName() {
        return "answers";
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] {"content"};
    }

    @Override
    protected String[] getKeyColumnNames() {
        return new String[] {"questionId", "questionOrder"};
    }

    @Override
    public boolean insert(Opcion obInsert) {
        return false;
    }

    // FIXME
    // We need to be able to change the questionOrder,
    // we can either modify the primary key, or a call on update
    // make a delete and then a create.
    @Override
    public boolean insert(Object[] values) {
        DataAccessor da = new DataAccessor(this.ds);

        List<String> columnNames = new ArrayList<>();
        Collections.addAll(columnNames, getKeyColumnNames());
        Collections.addAll(columnNames, getColumnNames());

        return da.insertRow(getTableName(), columnNames.toArray(new String[3]), values);
    }

    @Override
    protected Opcion buildObjectFromResultSet(ResultSet rs) throws SQLException {
        return null;
    }
}
