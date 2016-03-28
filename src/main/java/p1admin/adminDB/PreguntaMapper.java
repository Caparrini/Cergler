package p1admin.adminDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import p1admin.model.Opcion;
import p1admin.model.Pregunta;

public class PreguntaMapper extends AbstractMapper<Pregunta>{

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
    protected Pregunta buildObjectFromResultSet(ResultSet rs)
            throws SQLException {
        //TODO
        int qId = rs.getInt(getKeyColumnNames()[0]);
        String content = rs.getString(getColumnNames()[0]);
        
        Pregunta p = new Pregunta();
        p.setId(qId);
        p.setEnunciado(content);
        

        return p;
    }

    @Override
    public boolean insert(Pregunta obInsert) {
        Object[] o = new Object[1];
        o[0] = obInsert.getEnunciado();
        
        return !this.insert(o);
    }

    /**
     * Actualiza el contenido de una pregunta, no las opciones
     */
	public void update(Pregunta question) {
		Object[] cv = new Object[1];
		Object[] kv = new Object[1];

		cv[0]=question.getEnunciado();
		kv[0]=question.getId();
		this.update(cv, kv);
		
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
