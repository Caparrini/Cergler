package p1admin;

import p1admin.adminDB.DBFacade;
import p1admin.adminDB.GenericDBFacade;
import p1admin.adminDB.PreguntaMapper;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import p1admin.model.Opcion;
import p1admin.model.Pregunta;

public class ConnectionTest {

    public static void main(String[] args) {
        DatabaseProperties props = new DatabaseProperties();
        ComboPooledDataSource cpds = new ComboPooledDataSource();

        cpds.setJdbcUrl(props.getUrl());
        cpds.setUser(props.getUser());
        cpds.setPassword(props.getPass());

        cpds.setAcquireRetryAttempts(1);
        cpds.setAcquireRetryDelay(1);
        cpds.setBreakAfterAcquireFailure(true);

        GenericDBFacade<Pregunta, Opcion> facade = new DBFacade(cpds);
        PreguntaMapper pm = new PreguntaMapper(cpds);
        Object[] values = new Object[] {"¿Te gusta el pimiento?"};
        pm.insert(values);

        Pregunta p = new Pregunta();
        p.setId(1);
        p.setEnunciado("Te gusta el pimiento?");

        Opcion op = new Opcion();
        Opcion op1 = new Opcion();
        op.setTexto("This is a question");
        op1.setTexto("This is another question");

        p.addOpcion(op);
        p.addOpcion(op1);

        facade.insertAnswer(p, op);
        facade.insertAnswer(p, op1);

        op.setTexto("Booya!");
        p.intercambiarOpciones(1,2);
        facade.updateAnswer(p, op);
        facade.updateAnswer(p, op1);

        cpds.close();
    }
}
