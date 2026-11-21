package operaciones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class GestorConexion {
    private String usuario = "admin";
    private String clave = "123456";
    private String urlConexion = "jdbc:h2:./db_mibasedatos";

    public GestorConexion(boolean esPostgres) {
        if (esPostgres) {
            String host = ("ep-hidden-shape-be712s9x-pooler.c-9.us-east-2.aws.neon.tech");
            String bd = ("neondb");
            usuario = ("neondb_owner");
            clave = ("NEON_PASSWORD=npg_8mLdhS1CWwFq");
            urlConexion = "jdbc:postgresql://" + host + "/" + bd + "?sslmode=require";
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
