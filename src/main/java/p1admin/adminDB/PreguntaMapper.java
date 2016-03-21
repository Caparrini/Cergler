package p1admin.adminDB;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import p1admin.model.Pregunta;

public class PreguntaMapper extends AbstractMapper<Pregunta>{

    public PreguntaMapper(DataSource ds) {
        super(ds);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected String getTableName() {
        return "questions";
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] {"content"};
    }

    @Override
    protected String[] getKeyColumnNames() {
        return new String[] {"id"};
    }

    @Override
    protected Pregunta buildObjectFromResultSet(ResultSet rs)
            throws SQLException {
        //TODO
        /*int qId = rs.getInt(getKeyColumnNames());
        String content = rs.getString("content");

        return new Pregunta(qId, content);
        */
        return null;
    }

    @Override
    public boolean insert(Pregunta obInsert) {
        // TODO Auto-generated method stub
        return false;
    }
}
