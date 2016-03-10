package p1admin;


import javax.sql.DataSource;

import p1admin.adminDB.PreguntaMapper;
import p1admin.model.Pregunta;

import com.mchange.v2.c3p0.ComboPooledDataSource;



public class ConnectionTest {
	public static void main(String[] args) throws Exception {

		// Inicialización del pool de conexiones
		
		ComboPooledDataSource cpds = new ComboPooledDataSource(); 
		cpds.setJdbcUrl("jdbc:mysql://localhost/abd_test"); 
		cpds.setUser("root");
		cpds.setPassword("");
		
		cpds.setAcquireRetryAttempts(1); 
		cpds.setAcquireRetryDelay(1); 
		cpds.setBreakAfterAcquireFailure(true);

		DataSource ds = cpds;

		// Recuperación de un contacto

		PreguntaMapper pm = new PreguntaMapper(ds);
		Object[] values = new Object[] {1,"¿Te gusta el pimiento?"};
		System.out.println(values);
		pm.insert(values);
	

		// Recuperación de un correo de la tabla tiene_correo
		
		//System.out.println(corrMapper.findById("wall_es@yahoo.es"));

		
		// Cierre y liberación del pool de conexiones

		cpds.close();
	}
}