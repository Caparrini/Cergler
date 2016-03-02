package p1admin;

import javax.swing.DefaultListModel;

import p1admin.adminDB.FauxDBFacade;
import p1admin.adminDB.GenericDBFacade;
import p1admin.admincontroller.AllQuestionsController;
import p1admin.adminview.AllQuestionsEditor;
import p1admin.model.Opcion;
import p1admin.model.Pregunta;

public class Main {
	public static void main(String[] args) {
		// TODO Inicializar conexión a BD
		
		
		// TODO Cambiar inicialización de fachada a BD añadiendo
		// los parámetros que sean necesarios
		GenericDBFacade<Pregunta, Opcion> facade = new FauxDBFacade();
		
		DefaultListModel<Pregunta> model = new DefaultListModel<>();
		AllQuestionsController controller = new AllQuestionsController(model, facade);
		AllQuestionsEditor ed = new AllQuestionsEditor(model, controller);
		ed.setModal(true);
		ed.setVisible(true);
		
		// TODO: Cerrar conexión
	}
}
