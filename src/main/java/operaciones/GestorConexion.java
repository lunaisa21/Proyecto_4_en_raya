package operaciones;

import java.sql.Connection;
import java.sql.DriverManager;

public class GestorConexion {
    private String usuario = "admin";
    private String clave = "123456";
    private String urlConexion = "jdbc:h2:./db_mibasedatos"; // debe tener la base de datos.

    public GestorConexion(boolean esPostgres){
        if (esPostgres){
            usuario = "neondb_owner";
            clave = "npg_8mLdhS1CWwFq";
            String host = "ep-hidden-shape-be712s9x-pooler.c-9.us-east-2.aws.neon.tech";
            String bd = "neondb";

            urlConexion = "jdbc:postgresql://"+host+"/"+bd;

        }
    }

    public Connection obtenerConexion(){
        try{
            return DriverManager.getConnection(urlConexion, usuario, clave);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
