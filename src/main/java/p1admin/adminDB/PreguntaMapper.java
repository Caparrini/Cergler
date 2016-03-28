package p1admin.adminDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import p1admin.model.Opcion;
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
        
        if(!this.insert(o)){
            return false;
        }
        
        return true;
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
         List<Pregunta> res = new LinkedList<Pregunta>();
         OptionMapper op = new OptionMapper(ds);
         String sql = "SELECT * FROM "+ this.getTableName() +" LEFT JOIN answers ON " + this.getTableName()+".id = answers.questionId";

         //TODO
         try (Connection con = ds.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

             ResultSet rs = pst.executeQuery();
             int lastId = -1;
             int currentId = -1;
        	 Pregunta p = new Pregunta();
        	 Pregunta paux = new Pregunta();
        	 Opcion o = new Opcion();



             while(rs.next()){ //While exist another line
            	 
            	 paux=this.buildObjectFromResultSet(rs);		
            	 o = op.buildObjectFromResultSet(rs);
            	 
            	 currentId = paux.getId();
            	 

            	 if(lastId!=currentId){//If id is !equal to last id is a new question
            		 if(lastId!=-1){
                		 res.add(p); //added pregunta
            		 }
            		 lastId = currentId;
            		 
            		 p=paux;

            	 }//else (qId = lId then is another option for the last question

            	 
            	 if(o.getNumeroOrden()!=0){
        			 p.addOpcion(o);	
        		 }
            	
           	 }
             
             res.add(p);
             
         } catch (SQLException e) {
             e.printStackTrace();
             
         }
		return res;
         

     }
     
     
     /**
      * Select All with the string passed as filter
      * @param Text must content the question
      * @return Lista de Preguntas de una tabla con sus respuestas
      */
     public List<Pregunta> selectAllWithOptions(String text) {
        List<Pregunta> res = new LinkedList<Pregunta>();
        OptionMapper op = new OptionMapper(ds);
        String sql = "SELECT * FROM "+ this.getTableName() +" LEFT JOIN answers ON " + 
        this.getTableName()+".id = answers.questionId WHERE "+
        this.getTableName()+"."+this.getColumnNames()[0]+" LIKE ?";
        String filter = "%"+text+"%";
        
        try (Connection con = ds.getConnection();	
            PreparedStatement pst = con.prepareStatement(sql)) {
        	pst.setObject(1, filter);
            ResultSet rs = pst.executeQuery();
            int lastId = -1;
            int currentId = -1;
            Pregunta p = new Pregunta();
            Pregunta paux = new Pregunta();
            Opcion o = new Opcion();



            while(rs.next()){ //While exist another line
           	 
           	 paux=this.buildObjectFromResultSet(rs);		
           	 o = op.buildObjectFromResultSet(rs);
           	 
           	 currentId = paux.getId();
           	 

           	 if(lastId!=currentId){//If id is !equal to last id is a new question
           		 if(lastId!=-1){
               		 res.add(p); //added pregunta
           		 }
           		 lastId = currentId;
           		 
           		 p=paux;

           	 }//else (qId = lId then is another option for the last question

           	 
           	 if(o.getNumeroOrden()!=0){
       			 p.addOpcion(o);	
       		 }
           	
          	 }
            
            res.add(p);
            
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
		return res;
        
       

        
	}
     
     
     
}
