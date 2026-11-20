package operaciones;

import datos.ColorFicha;
import datos.Ficha;
import datos.Jugador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

        }
    }

    @Override
    public List<Jugador> consultar() {
        GestorConexion gestor = new GestorConexion(true);
        Connection conex = gestor.obtenerConexion();
        List<Jugador> jugadores = new ArrayList<>();
        if (conex!= null){
            String sql = "select * from Jugador";
            try {
                PreparedStatement psql = conex.prepareStatement(sql);
                ResultSet resultado = psql.executeQuery();

                while (resultado.next()) {
                    Jugador jugador = new Jugador();
                    String nombre = resultado.getString("nombre");
                    int idJugador = resultado.getInt("idJugador ");
                    boolean esIa = resultado.getBoolean("esIa");
                    ColorFicha color = resultado.getObject("color", ColorFicha.class);

                    jugador.setNombre(nombre);
                    jugador.setIdJugador(idJugador);
                    jugadores.add(jugador);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return jugadores;
    }

    @Override
    public void actualizar(Jugador jugador) {
        String sql = "update jugador set nombre = ? where idjugador = ?";
        GestorConexion gestor = new GestorConexion(true);

        try (Connection conex = gestor.obtenerConexion()) {
            if (conex == null) {
                return;
            }
            try (PreparedStatement psql = conex.prepareStatement(sql)) {
                psql.setString(1, jugador.getNombre());
                psql.setInt(2, jugador.getIdJugador());
                psql.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void eliminar(Jugador jugador) {
        String sql = "delete from jugador where idjugador = ?";
        GestorConexion gestor = new GestorConexion(true);

        try (Connection conex = gestor.obtenerConexion()) {
            if (conex == null) {
                return;
            }
            try (PreparedStatement psql = conex.prepareStatement(sql)) {
                psql.setInt(1, jugador.getIdJugador());
                psql.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
