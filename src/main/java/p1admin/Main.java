package p1admin;

import javax.swing.DefaultListModel;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import p1admin.adminDB.DBFacade;
import p1admin.adminDB.GenericDBFacade;
import p1admin.admincontroller.AllQuestionsController;
import p1admin.adminview.AllQuestionsEditor;
import p1admin.model.Opcion;
import p1admin.model.Pregunta;

public class Main {
    public static void main(String[] args) {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setJdbcUrl("jdbc:mysql://localhost/abd_test");
        cpds.setUser("root");
        cpds.setPassword("");

        cpds.setAcquireRetryAttempts(1);
        cpds.setAcquireRetryDelay(1);
        cpds.setBreakAfterAcquireFailure(true);

        GenericDBFacade<Pregunta, Opcion> facade = new DBFacade(cpds);

        DefaultListModel<Pregunta> model = new DefaultListModel<>();
        AllQuestionsController controller = new AllQuestionsController(model, facade);
        AllQuestionsEditor ed = new AllQuestionsEditor(model, controller);

        ed.setModal(true);
        ed.setVisible(true);
    }
}
