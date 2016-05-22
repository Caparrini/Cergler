package p1admin.adminDB;

import p1admin.model.Opcion;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        return new String[] {
            "questionId",
            "questionOrder",
            "content"
        };
    }

    @Override
    protected String[] getKeyColumnNames() {
        return new String[] {
            "questionId",
            "questionOrder"
        };
    }

    @Override
    protected Object[] getObjectArray(Opcion obj) {
        return new Object[] {
            obj.getPreguntaMadre().getId(),
            obj.getNumeroOrden(),
            obj.getTexto()
        };
    }

    @Override
    protected Object[] getObjectId(Opcion obj) {
        return new Object[] {
            obj.getPreguntaMadre().getId(),
            obj.getNumeroOrden()
        };
    }

    @Override
    public boolean update(Object[] columnValues, Object[] keyValues) {
        // We can't change the questionId of the answer,
        // so we should strip that field from the columnNames value
        // before constructing the query
        DataAccessor da = new DataAccessor(this.ds);
        String[] columnNames = getColumnNames();
        columnNames = Arrays.copyOfRange(columnNames, 1, columnNames.length);

        return da.updateRows(getTableName(), getKeyColumnNames(),
                             keyValues, columnNames, columnValues);
    }

    @Override
    public boolean delete(Opcion option) {
        int questionId = option.getPreguntaMadre().getId();
        int questionOrder = option.getNumeroOrden();

        boolean res = super.delete(option);

        // Get all options with a higher question order
        List<Opcion> updated = this.selectAll()
                                   .stream()
                                   .filter(e -> e.getNumeroOrden() > questionOrder)
                                   .collect(Collectors.toList());

        for (Opcion opt : updated) {
            this.update(new Object[] {
                opt.getNumeroOrden() - 1,
                opt.getTexto()
            }, new Object[] {
                questionId,
                opt.getNumeroOrden()
            });
        }

        return res;
    }

    @Override
    protected Opcion buildObjectFromResultSet(ResultSet rs) throws SQLException {
        // TODO: Should we fetch the question referenced by questionId, ie:
        // op.setPreguntaMadre(PreguntaMapper.findById(
        //     rs.getInt(rs.findColumn("questionId"))
        // ));

        Opcion op = new Opcion();
        op.setNumeroOrden(rs.getInt(rs.findColumn("questionOrder")));
        op.setTexto(rs.getString(rs.findColumn(this.getTableName()+".content")));
        return op;
    }

}
