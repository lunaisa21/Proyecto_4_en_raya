package operaciones;

import datos.Jugador;
import datos.Marcador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperacionesMarcador implements GenericDAO<Marcador> {

    private final Jugador jugador;

    public OperacionesMarcador(Jugador jugador) {
        this.jugador = jugador;
    }

    @Override
    public void crear(Marcador marcador) {
        String sql = "INSERT INTO marcador (idjugador, victorias, derrotas, empates) VALUES (?, ?, ?, ?)";
        try (Connection conex = new GestorConexion(true).obtenerConexion();
             PreparedStatement psql = conex.prepareStatement(sql)) {
            // El orden de los setX debe coincidir con el orden de los ?
            psql.setInt(1, jugador.getIdJugador());
            psql.setInt(2, marcador.getVictorias());
            psql.setInt(3, marcador.getDerrotas());
            psql.setInt(4, marcador.getEmpates());
            psql.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Marcador> consultar() {
        List<Marcador> marcadores = new ArrayList<>();
        String sql = "SELECT victorias, derrotas, empates FROM marcador WHERE idjugador = ?";
        try (Connection conex = new GestorConexion(true).obtenerConexion();
             PreparedStatement psql = conex.prepareStatement(sql)) {
            psql.setInt(1, jugador.getIdJugador()); // faltaba dar valor al ?
            try (ResultSet rs = psql.executeQuery()) {
                while (rs.next()) {
                    Marcador marcador = new Marcador();
                    marcador.setVictorias(rs.getInt("victorias"));
                    marcador.setDerrotas(rs.getInt("derrotas"));
                    marcador.setEmpates(rs.getInt("empates"));
                    marcadores.add(marcador);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return marcadores;
    }

    @Override
    public void actualizar(Marcador marcador) {
        String sql = "UPDATE marcador SET victorias = ?, derrotas = ?, empates = ? WHERE idjugador = ?";
        try (Connection conex = new GestorConexion(true).obtenerConexion();
             PreparedStatement psql = conex.prepareStatement(sql)) {
            psql.setInt(1, marcador.getVictorias());
            psql.setInt(2, marcador.getDerrotas());
            psql.setInt(3, marcador.getEmpates());
            psql.setInt(4, jugador.getIdJugador());
            psql.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void eliminar(Marcador marcador) {
        String sql = "DELETE FROM marcador WHERE idjugador = ?";
        try (Connection conex = new GestorConexion(true).obtenerConexion();
             PreparedStatement psql = conex.prepareStatement(sql)) {
            psql.setInt(1, jugador.getIdJugador());
            psql.executeUpdate(); // faltaba ejecutar
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}