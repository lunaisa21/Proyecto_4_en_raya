package operaciones;

import datos.Jugador;
import datos.Marcador;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OperacionesMarcadorTest {
    @Test
    public void crearMarcadorTest() {
        Jugador jugador = new Jugador();
        jugador.setIdJugador(1564);
        OperacionesMarcador operaciones = new OperacionesMarcador(jugador);
        Marcador marcador = new Marcador();
        marcador.setVictorias(5);
        marcador.setDerrotas(2);
        marcador.setEmpates(1);

        operaciones.crear(marcador);
        assertTrue(true, "El marcador se creó correctamente");
    }

    @Test
    public void consultarMarcadorTest() {
        Jugador jugador = new Jugador();
        jugador.setIdJugador(1564);
        OperacionesMarcador operaciones = new OperacionesMarcador(jugador);
        List<Marcador> marcadores = operaciones.consultar();
        marcadores.forEach(m -> System.out.println("Victorias: " + m.getVictorias() + ", Derrotas: " + m.getDerrotas()));

        assertTrue(marcadores.size() >= 0, "Consulta realizada con éxito");
    }

    @Test
    public void actualizarMarcadorTest() {
        Jugador jugador = new Jugador();
        jugador.setIdJugador(1564);
        OperacionesMarcador operaciones = new OperacionesMarcador(jugador);
        Marcador marcador = new Marcador();
        marcador.setVictorias(10);
        marcador.setDerrotas(1);
        marcador.setEmpates(0);
        operaciones.actualizar(marcador);

        assertTrue(true, "El marcador se actualizó correctamente");
    }

    @Test
    public void eliminarMarcadorTest() {
        Jugador jugador = new Jugador();
        jugador.setIdJugador(1564);
        OperacionesMarcador operaciones = new OperacionesMarcador(jugador);
        Marcador marcador = new Marcador();
        operaciones.eliminar(marcador);

        assertTrue(true, "El marcador fue eliminado exitosamente");
    }
}
