package operaciones;

import datos.Jugador;
import datos.Marcador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperacionesMarcador implements GenericDAO<Marcador>{

    private final Jugador jugador;
    public OperacionesMarcador(Jugador jugador){
        this.jugador = jugador;
    }

    @Override
    public void crear(Marcador marcador) {
        GestorConexion gestor = new GestorConexion(true);
        Connection conex = gestor.obtenerConexion();
        if (conex != null){
            String sql = "INSERT INTO marcador (idJugador, victorias, derrotas, empates) VALUES (?, ?, ?, ?)";
            try {
                PreparedStatement psql = conex.prepareStatement(sql);
                psql.setInt(1, marcador.getVictorias());
                psql.setInt(2, marcador.getDerrotas());
                psql.setInt(3, marcador.getEmpates());
                psql.setInt(4, jugador.getIdJugador());

                psql.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<Marcador> consultar() {
        GestorConexion gestor = new GestorConexion(true);
        Connection conex = gestor.obtenerConexion();
        List<Marcador> marcadores = new ArrayList<>();
        if (conex != null){
            String sql = "select victorias, derrotas, empates from marcador where idJugador = ?";
            try {
                PreparedStatement psql = conex.prepareStatement(sql);
                ResultSet resultado = psql.executeQuery();
                while (resultado.next()) {
                    Marcador marcador = new Marcador();
                    int victorias = resultado.getInt("victorias");
                    int derrotas = resultado.getInt("derrotas");
                    int empates = resultado.getInt("empates");

                    marcador.setVictorias(victorias);
                    marcador.setDerrotas(derrotas);
                    marcador.setEmpates(empates);
                    marcadores.add(marcador);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return marcadores;
    }

    @Override
    public void actualizar(Marcador marcador) {
        GestorConexion gestor = new GestorConexion(true);
        Connection conex = gestor.obtenerConexion();
        if (conex != null){
            String sql ="update marcador set victorias = ?, derrotas = ?, empates = ? where idjugador = ?";
            try {
                PreparedStatement psql = conex.prepareStatement(sql);
                psql.setInt(1, marcador.getVictorias());
                psql.setInt(2, marcador.getDerrotas());
                psql.setInt(3, marcador.getEmpates());
                psql.setInt(4, jugador.getIdJugador());
                psql.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void eliminar(Marcador marcador) {
        GestorConexion gestor = new GestorConexion(true);
        Connection conex = gestor.obtenerConexion();
        if (conex != null){
            String sql ="delete from marcador where idJugador = ?";
            try {
                PreparedStatement psql= conex.prepareStatement(sql);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
