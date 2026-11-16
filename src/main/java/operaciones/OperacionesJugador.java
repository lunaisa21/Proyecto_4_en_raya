package operaciones;

import datos.Jugador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class OperacionesJugador implements GenericDAO<Jugador> {

    @Override
    public void crear(Jugador jugador) {
        GestorConexion gestor = new GestorConexion(true);
        Connection conex = gestor.obtenerConexion();
        if (conex!= null){
            String sql = "insert into Jugador  (nombre) values (?)";
            try {
                PreparedStatement psql = conex.prepareStatement(sql);
                psql.setString(1, jugador.getNombre());

                psql.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }}}

    @Override
    public List<Jugador> consultar() {
        return List.of();
    }

    @Override
    public void actualizar(Jugador jugador) {

    }

    @Override
    public void eliminar(Jugador jugador) {

    }
}
