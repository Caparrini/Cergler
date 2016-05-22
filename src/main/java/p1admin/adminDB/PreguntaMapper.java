package p1admin.adminDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

import javax.sql.DataSource;

import p1admin.model.Opcion;
import p1admin.model.Pregunta;

public class PreguntaMapper extends AbstractMapper<Pregunta> {

    private OptionMapper optionMapper;

    public PreguntaMapper(DataSource ds) {
        super(ds);
        this.optionMapper = new OptionMapper(ds);
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
    public boolean insert(Pregunta obInsert) {
        DataAccessor da = new DataAccessor(ds);
        Integer id = da.insertAndReturn(getTableName(),
                                          getColumnNames(),
                                          getObjectArray(obInsert));

        if (id != null) {
            obInsert.setId(id);
            return true;
        }

        return false;
    }

    @Override
    protected Pregunta buildObjectFromResultSet(ResultSet rs) throws SQLException {
        int qId = rs.getInt(getKeyColumnNames()[0]);
        String content = rs.getString(getColumnNames()[0]);
        
        Pregunta p = new Pregunta();
        p.setId(qId);
        p.setEnunciado(content);

        return p;
    }

    @Override
    protected Object[] getObjectArray(Pregunta obj) {
        return new Object[] {
            obj.getEnunciado()
        };
    }

    @Override
    protected Object[] getObjectId(Pregunta obj) {
        return new Object[] {
            obj.getId()
        };
    }

    @Override
    protected Object[] getColumnValues(Pregunta question) {
        return new Object[] {
            question.getEnunciado()
        };
    }

    /**
     * Select All
     * @return Lista de Preguntas de una tabla con sus respuestas
     */
    public List<Pregunta> selectAllWithOptions() {
        List<Pregunta> res = Collections.emptyList();

        String questionTable = getTableName();
        String optionTable = optionMapper.getTableName();
        String selfId = getKeyColumnNames()[0];
        String questionId = optionMapper.getKeyColumnNames()[0];

        // SELECT * FROM questions
        // LEFT JOIN answers ON questions.id = answers.questionId;
        String sql = "SELECT * FROM " + questionTable +
                     " LEFT JOIN " + optionTable + " ON " + questionTable + "." + selfId +
                     " = " + optionTable + "." + questionId;

        try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();
            res = buildSetWithoutDuplicates(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }
     
     
    /**
     * Select All with the string passed as filter
     * @param text must content the question
     * @return Lista de Preguntas de una tabla con sus respuestas
     */
    public List<Pregunta> selectAllWithOptions(String text) {
        List<Pregunta> res = Collections.emptyList();

        String questionTable = getTableName();
        String optionTable = optionMapper.getTableName();
        String selfId = getKeyColumnNames()[0];
        String content = getColumnNames()[0];
        String questionId = optionMapper.getKeyColumnNames()[0];

        // SELECT * FROM questions
        // LEFT JOIN answers ON questions.id = answers.questionId
        // WHERE answers.content LIKE ?;
        String sql = "SELECT * FROM "+ questionTable +
                     " LEFT JOIN " + optionTable + " ON " + questionTable + "." + selfId +
                     " = " +  optionTable + "." + questionId +
                     " WHERE " + questionTable + "." + content + " LIKE ?";

        String filter = "%" + text + "%";

        try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, filter);
            ResultSet rs = pst.executeQuery();
            res = buildSetWithoutDuplicates(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
	}

    private List<Pregunta> buildSetWithoutDuplicates(ResultSet rs) throws SQLException {
        HashMap<Integer, Pregunta> set = new HashMap<>();

        while (rs.next()) {
            Pregunta question = buildObjectFromResultSet(rs);

            if (set.containsKey(question.getId())) {
                question = set.get(question.getId());
            } else {
                set.put(question.getId(), question);
            }

            Opcion opt = optionMapper.buildObjectFromResultSet(rs);
            if (opt.getNumeroOrden() != 0) {
                question.addOpcion(opt);
            }
        }

        return new ArrayList<>(set.values());
    }
}
