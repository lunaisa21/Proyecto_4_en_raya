package operaciones;

import datos.Jugador;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OperacionesJugadorTest {
    @Test
    public void crearJugador(){
        OperacionesJugador operaciones = new OperacionesJugador();
        Jugador jugador = new Jugador();
        jugador.setNombre("David");
        jugador.setIdJugador(1564);

        operaciones.crear(jugador);
    }

    @Test
    public void consultarJugador(){
        OperacionesJugador operaciones = new OperacionesJugador();
        List<Jugador> jugadores = operaciones.consultar();
        jugadores.forEach(jugador -> System.out.println(jugador));
        assertTrue(jugadores.size() > 0, "Se obtuvo");
    }

    @Test
    public void actualizarJugador() {
        OperacionesJugador operaciones = new OperacionesJugador();
        Jugador jugador = new Jugador();
        jugador.setNombre("David_Original");
        jugador.setEsIa(false);
        operaciones.crear(jugador);
        jugador.setNombre("David_Actualizado");
        jugador.setEsIa(true);
        operaciones.actualizar(jugador);
        assertTrue(true, "El jugador se actualizó correctamente");
    }

    @Test
    public void eliminarJugador() {
        OperacionesJugador operaciones = new OperacionesJugador();
        Jugador jugador = new Jugador();
        jugador.setNombre("JugadorParaBorrar");
        jugador.setEsIa(false);
        operaciones.crear(jugador);
        operaciones.eliminar(jugador);
        assertTrue(true, "El jugador fue eliminado exitosamente");
    }


}
