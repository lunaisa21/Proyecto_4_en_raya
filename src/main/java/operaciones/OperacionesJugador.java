package operaciones;

import datos.Jugador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperacionesJugador implements GenericDAO<Jugador> {

    /** idJugador lo genera la base de datos (SERIAL); se copia al objeto con RETURNING. */
    @Override
    public void crear(Jugador jugador) {
        String sql = "INSERT INTO jugador (nombre) VALUES (?) RETURNING idjugador";
        try (Connection conex = new GestorConexion(true).obtenerConexion();
             PreparedStatement psql = conex.prepareStatement(sql)) {
            psql.setString(1, jugador.getNombre());
            try (ResultSet rs = psql.executeQuery()) {
                if (rs.next()) {
                    jugador.setIdJugador(rs.getInt("idjugador"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Jugador> consultar() {
        List<Jugador> jugadores = new ArrayList<>();
        String sql = "SELECT idjugador, nombre FROM jugador ORDER BY idjugador";
        try (Connection conex = new GestorConexion(true).obtenerConexion();
             PreparedStatement psql = conex.prepareStatement(sql);
             ResultSet rs = psql.executeQuery()) {
            while (rs.next()) {
                Jugador jugador = new Jugador();
                jugador.setIdJugador(rs.getInt("idjugador"));
                jugador.setNombre(rs.getString("nombre"));
                jugadores.add(jugador);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return jugadores;
    }

    @Override
    public void actualizar(Jugador jugador) {
        String sql = "UPDATE jugador SET nombre = ? WHERE idjugador = ?";
        try (Connection conex = new GestorConexion(true).obtenerConexion();
             PreparedStatement psql = conex.prepareStatement(sql)) {
            psql.setString(1, jugador.getNombre());
            psql.setInt(2, jugador.getIdJugador());
            psql.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void eliminar(Jugador jugador) {
        String sql = "DELETE FROM jugador WHERE idjugador = ?";
        try (Connection conex = new GestorConexion(true).obtenerConexion();
             PreparedStatement psql = conex.prepareStatement(sql)) {
            psql.setInt(1, jugador.getIdJugador());
            psql.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}