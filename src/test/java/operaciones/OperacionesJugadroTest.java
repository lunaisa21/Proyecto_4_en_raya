package operaciones;

import datos.Jugador;
import org.junit.jupiter.api.Test;

public class OperacionesJugadroTest {
    @Test
    public void crearJuagdor(){
        OperacionesJugador operaciones = new OperacionesJugador();
        Jugador jugador = new Jugador();
        jugador.setNombre("David");
        jugador.setIdJugador(1564);

        operaciones.crear(jugador);
    }
}
