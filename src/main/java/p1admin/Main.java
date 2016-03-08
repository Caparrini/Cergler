package p1admin;

import javax.sql.DataSource;
import javax.swing.DefaultListModel;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import p1admin.adminDB.FauxDBFacade;
import p1admin.adminDB.GenericDBFacade;
import p1admin.admincontroller.AllQuestionsController;
import p1admin.adminview.AllQuestionsEditor;
import p1admin.model.Opcion;
import p1admin.model.Pregunta;

public class Main {
	public static void main(String[] args) {
		// TODO Inicializar conexión a BD, con patron Connection Pooling usando C3p0
		ComboPooledDataSource cpds = new ComboPooledDataSource(); 
		cpds.setJdbcUrl("jdbc:mysql://localhost/abd_test"); 
		cpds.setUser("root");
		cpds.setPassword("");
		
		cpds.setAcquireRetryAttempts(1); 
		cpds.setAcquireRetryDelay(1); 
		cpds.setBreakAfterAcquireFailure(true);
		
		DataSource ds = cpds;
		try {
			Connection con = ds.getConnection();

		
			// TODO Cambiar inicialización de fachada a BD añadiendo
			// los parámetros que sean necesarios
			GenericDBFacade<Pregunta, Opcion> facade = new FauxDBFacade();
		
			DefaultListModel<Pregunta> model = new DefaultListModel<>();
			AllQuestionsController controller = new AllQuestionsController(model, facade);
			AllQuestionsEditor ed = new AllQuestionsEditor(model, controller);
			ed.setModal(true);
			ed.setVisible(true);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: Cerrar conexión
		cpds.close();
		
	}
}
