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
    protected String getKeyColumnName() {
        return "id";
    }
    @Override
    protected Pregunta buildObjectFromResultSet(ResultSet rs)
            throws SQLException {
        //TODO
        int qId = rs.getInt(getKeyColumnName());
        String content = rs.getString("content");
        
        Pregunta p = new Pregunta();
        p.setId(qId);
        p.setEnunciado(content);
        

        return p;
    }

    @Override
    public boolean insert(Pregunta obInsert) {
        Object[] o = new Object[1];
        o[0] = obInsert.getEnunciado();
        
        if(!this.insert(o)){
            return false;
        }
        
        return true;
    }
}
