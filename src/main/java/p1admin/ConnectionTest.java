package p1admin;

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

		cpds.close();
	}
}