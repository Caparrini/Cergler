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
    public boolean insert(Opcion obInsert) {
        return this.insert(new Object[] {
            obInsert.getPreguntaMadre().getId(),
            obInsert.getNumeroOrden(),
            obInsert.getTexto()
        });
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
    public boolean delete(Object[] id) {
        Integer questionId = (Integer) id[0];
        Integer questionOrder = (Integer) id[1];

        boolean res = super.delete(id);
        List<Opcion> updated = this.selectAll()
                                        .stream()
                                        .filter(e -> e.getNumeroOrden() >= questionOrder)
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
