package p1admin;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;
import p1admin.adminDB.DBFacade;
import p1admin.model.Opcion;
import p1admin.model.Pregunta;

public class DBFacadeTest {
    public static String questionTableName = null;
    public static String optionTableName = null;
    public static String dbUrl = null;

    public static DataSource ds = null;
    
    public Pregunta p1, p2, p3;
    public Pregunta[] ps;
    
    
    
    /*
    Este método es llamado una sóla vez al arrancar los tests.
    
    Interpreta el fichero names.properties y crea el DataSource.
    */
    @BeforeClass
    public static void setUpClass() throws ClassNotFoundException {
        try {
            Properties p = new Properties();
            InputStream namesStream = DBFacadeTest.class.getResourceAsStream("names.properties");
            if (namesStream == null) throw new IOException("Cannot find names.properties");
            p.load(namesStream);
            questionTableName = p.getProperty("questions");
            optionTableName = p.getProperty("options");
            dbUrl = p.getProperty("dbUrl");
            ComboPooledDataSource cpds = new ComboPooledDataSource();
            cpds.setJdbcUrl(dbUrl);
            cpds.setUser("root2");
            cpds.setPassword("");

            cpds.setAcquireRetryAttempts(1);
            cpds.setAcquireRetryDelay(1);
            ds = cpds;

        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Cannot load names.properties");
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    /*
    Borra todas las filas de la tabla pasada como parámetro
    */
    private void deleteTable(Connection con, String tableName) {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("DELETE FROM " + tableName);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
    }
    
    /*
     * Obtiene el número de filas de la tabla pasada como parámetro 
     */
    private int numRows(Connection con, String tableName) {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    
    /*
     * Convierte la información de la BD en un string con el siguiente formato
     * Enunciado1[Opcion1,Opcion2,...] Enunciado2[Opcion1,Opcion2,...] ...
    */
    private String modelToString(Pregunta[] model) {
        return Arrays.stream(model).map(p -> 
                p.getEnunciado() + "[" + p.getOpciones().stream().map(Opcion::getTexto).collect(Collectors.joining(",")) + "]"
        ).collect(Collectors.joining(" "));
    }
    
    private void createModel() {
        Opcion o;
        p1 = new Pregunta();
        p1.setEnunciado("Pregunta 1");
        
        o = new Opcion(); o.setTexto("Opcion 11"); p1.addOpcion(o);
        o = new Opcion(); o.setTexto("Opcion 12"); p1.addOpcion(o);
        o = new Opcion(); o.setTexto("Opcion 13"); p1.addOpcion(o);
        
        p2 = new Pregunta();
        p2.setEnunciado("Pregunta 2");

        o = new Opcion(); o.setTexto("Opcion 21"); p2.addOpcion(o);
        o = new Opcion(); o.setTexto("Opcion 22"); p2.addOpcion(o);

        
        p3 = new Pregunta();
        p3.setEnunciado("Pregunta 3");
     
        ps = new Pregunta[] { p1, p2, p3 };
    }
    
    /**
     * Este método es ejecutado antes de cada test.
     * Borra el contenido de las tablas de preguntas y opciones y
     * crea el modelo inicial.
     */
    @Before
    public void setUp() {
        try (Connection con = ds.getConnection()) {
            deleteTable(con, optionTableName);
            deleteTable(con, questionTableName);
            createModel();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }

    @After
    public void tearDown() {
    }

    /*
    * Comprueba que inicialmente las tablas están vacías.
    * Esto debería cumplirse siempre, ya que se borran en el método setUp.
    */
    @Test
    public void emptyDB() throws SQLException {
        Connection con = ds.getConnection();
        assertEquals(0, numRows(con, questionTableName));
        assertEquals(0, numRows(con, optionTableName));
        con.close();
    }
    
    /*
    * Inserta una pregunta, y comprueba que el número de filas en la
    * tabla de preguntas es 1.
    */
    @Test
    public void insertQuestionOneRow() throws SQLException {
        DBFacade db = new DBFacade(ds);
        db.insertQuestion(p1);
        Connection con = ds.getConnection();
        assertEquals(1, numRows(con, questionTableName));
        con.close();
    }

    /*
    * Inserta una pregunta, y comprueba que el número de filas en la
    * tabla de opciones es cero.
    */
    @Test
    public void insertQuestionNoOptions() throws SQLException {
        DBFacade db = new DBFacade(ds);
        db.insertQuestion(p1);
        Connection con = ds.getConnection();
        assertEquals(0, numRows(con, optionTableName));
        con.close();
    }

    /*
    * Inserta las tres preguntas y las cinco opciones creadas por getModel, 
    * y comprueba el número de filas que quedan en cada tabla.
    */
    @Test
    public void insertModelWithOptions() throws SQLException {
        DBFacade db = new DBFacade(ds);
        insertModel(db, ps);
        Connection con = ds.getConnection();
        assertEquals(3, numRows(con, questionTableName));
        assertEquals(5, numRows(con, optionTableName));
        con.close();
    }
    
    private void insertModel(DBFacade db, Pregunta[] ps) {
        Arrays.stream(ps).forEach(p -> {
            db.insertQuestion(p);
            p.getOpciones().forEach(o -> db.insertAnswer(p, o));
        });
    }

    /*
    * Al obtener todas las preguntas de la BD, comprueba que se han
    * recuperado tres.
    */
    @Test
    public void getAllSize() throws SQLException {
        DBFacade db = new DBFacade(ds);
        insertModel(db, ps);
        List<Pregunta> psList = db.getAllQuestions();
        assertEquals(3, psList.size());
    }
    
    /*
    * Al obtener todas las preguntas de la BD, comprueba que se han
    * recuperado las tres preguntas que se habían insertado previamente.
    */
    @Test
    public void getAllQuestions() throws SQLException {
        DBFacade db = new DBFacade(ds);
        insertModel(db, ps);
        List<Pregunta> psList = db.getAllQuestions();
        sortByEnunciado(psList);
        assertEquals(modelToString(ps), modelToString(psList.toArray(new Pregunta[]{})));
    }

    /*
    * Partiendo de tres preguntas
    * Pregunta 1[Opcion 11,Opcion 12,Opcion 13]
    * Pregunta 2[Opcion 21,Opcion 22]
    * Pregunta 3 (sin opciones)
    *
    * Añade una opción ("Bu") a la primera y comprueba que el resultado es correcto.
    */
    @Test
    public void insertAfterGet() throws SQLException {
        DBFacade db = new DBFacade(ds);
        insertModel(db, ps);
        Opcion newOption = new Opcion();
        newOption.setTexto("Bu");
        ps[0].addOpcion(newOption);
        db.insertAnswer(ps[0], newOption);
        List<Pregunta> psList = db.getAllQuestions();
        sortByEnunciado(psList);
        assertEquals("Pregunta 1[Opcion 11,Opcion 12,Opcion 13,Bu] "
                + "Pregunta 2[Opcion 21,Opcion 22] "
                + "Pregunta 3[]", 
                modelToString(psList.toArray(new Pregunta[]{})));
    }
    
    /*
    * Realiza una búsqueda de las preguntas que contienen el texto 'unta'.
    * Deberían aparecer las tres.
    */
    @Test
    public void searchByNameAll() throws SQLException {
        DBFacade db = new DBFacade(ds);
        insertModel(db, ps);
        List<Pregunta> psList = db.findQuestionsContaining("unta");
        sortByEnunciado(psList);
        assertEquals("Pregunta 1[Opcion 11,Opcion 12,Opcion 13] "
                + "Pregunta 2[Opcion 21,Opcion 22] "
                + "Pregunta 3[]", 
                modelToString(psList.toArray(new Pregunta[]{})));
    }
    
    /*
    * Realiza una búsqueda de las preguntas que contienen el texto '3'.
    * Debería aparecer sólo la última.
    */
    @Test
    public void searchByNameOnlyThree() throws SQLException {
        DBFacade db = new DBFacade(ds);
        insertModel(db, ps);
        List<Pregunta> psList = db.findQuestionsContaining("3");
        sortByEnunciado(psList);
        assertEquals("Pregunta 3[]", 
                modelToString(psList.toArray(new Pregunta[]{})));
    }    
    
    /*
    * Realiza una búsqueda de las preguntas que contienen el texto '1'.
    * Debería recuperarse sólo la primera, con sus tres opciones.
    */
    @Test
    public void searchByNameOnlyOne() throws SQLException {
        DBFacade db = new DBFacade(ds);
        insertModel(db, ps);
        List<Pregunta> psList = db.findQuestionsContaining("1");
        sortByEnunciado(psList);
        assertEquals("Pregunta 1[Opcion 11,Opcion 12,Opcion 13]", 
                modelToString(psList.toArray(new Pregunta[]{})));
    }
    
    /*
    * Actualiza el enunciado de la pregunta 1 y luego intenta recuperar
    * dicha pregunta de la BD. Comprueba que el enunciado recuperado coincide
    * con el que se guardó previamente.
    *
    * También comprueba que la pregunta 2 no ha cambiado
    */
    @Test
    public void updateEnunciado() throws SQLException {
        DBFacade db = new DBFacade(ds);
        insertModel(db, ps);
        ps[0].setEnunciado("Otro");
        db.updateQuestion(ps[0]);
        List<Pregunta> psList = db.getAllQuestions();
        sortByEnunciado(psList);
        assertEquals("Otro", psList.get(0).getEnunciado());
        assertEquals("Pregunta 2", psList.get(1).getEnunciado());
    }        

    /*
    * Idem, pero con las opciones
    */
    @Test
    public void updateOpcion() throws SQLException {
        DBFacade db = new DBFacade(ds);
        insertModel(db, ps);
        ps[0].getOpcion(1).setTexto("Otro");
        db.updateAnswer(ps[0], ps[0].getOpcion(1));
        List<Pregunta> psList = db.getAllQuestions();
        sortByEnunciado(psList);
        assertEquals("Otro", psList.get(0).getOpcion(1).getTexto());
        assertEquals("Opcion 12", psList.get(0).getOpcion(2).getTexto());
    }    

    /*
     * Borra una pregunta, y comprueba que queden dos en la tabla.
     */
    @Test
    public void deleteQuestion() throws SQLException {
        DBFacade db = new DBFacade(ds);
        insertModel(db, ps);
        db.deleteQuestion(ps[0]);
        Connection con = ds.getConnection();
        assertEquals(2, numRows(con, questionTableName));
        con.close();
    }    
    
    /*
     * Borra una opción de la pregunta 1, y comprueba que queden dos en dicha pregunta.
     */
    @Test
    public void deleteOption() throws SQLException {
        DBFacade db = new DBFacade(ds);
        insertModel(db, ps);
        db.deleteAnswer(ps[0], ps[0].getOpcion(2));
        List<Pregunta> pList = db.getAllQuestions();
        sortByEnunciado(pList);
        assertEquals(2, pList.get(0).getOpciones().size());
    }      
    
    private void sortByEnunciado(List<Pregunta> psList) {
        psList.sort(new Comparator<Pregunta>() {
            @Override
            public int compare(Pregunta o1, Pregunta o2) {
                return o1.getEnunciado().compareTo(o2.getEnunciado());
            }
        });
    }
    
}
