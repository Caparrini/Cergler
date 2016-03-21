package p1admin;

import p1admin.adminDB.OptionMapper;
import p1admin.adminDB.PreguntaMapper;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConnectionTest {

    public static void main(String[] args) {

        // Inicialización del pool de conexiones
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setJdbcUrl("jdbc:mysql://localhost:8889/abd_test");
        cpds.setUser("abd");
        cpds.setPassword("");

        cpds.setAcquireRetryAttempts(1);
        cpds.setAcquireRetryDelay(1);
        cpds.setBreakAfterAcquireFailure(true);

        PreguntaMapper pm = new PreguntaMapper(cpds);
        Object[] values = new Object[] {"¿Te gusta el pimiento?"};
        pm.insert(values);

        OptionMapper om = new OptionMapper(cpds);
        om.insert(new Object[] {1, 1, "This is a question"});

        // Given an answer of question 1, order 1, set its content to "Bar"
        om.update(new Object[] {"Bar"}, new Object[] {1,1});
        om.delete(new Object[] {1,1});

        cpds.close();
    }
}
