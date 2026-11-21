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
    public void concultarJugador(){
        OperacionesJugador operaciones = new OperacionesJugador();
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.forEach(jugador -> System.out.println(jugador));
        assertFalse(jugadores.size() > 0, "Se obtuvo");
    }
}
