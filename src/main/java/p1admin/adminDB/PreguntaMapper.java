package p1admin.adminDB;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import p1admin.model.Pregunta;

public class PreguntaMapper extends AbstractMapper<Pregunta,Integer>{

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
		return new String[] {getKeyColumnName(), "content"};
	}

	@Override
	protected String getKeyColumnName() {
		
		return "questionId";
	}

	@Override
	protected Pregunta buildObjectFromResultSet(ResultSet rs)
			throws SQLException {
		//TODO
		/*int qId = rs.getInt(getKeyColumnName());
		String content = rs.getString("content");
		
		return new Pregunta(qId, content);
		*/
		return null;
	}

}
