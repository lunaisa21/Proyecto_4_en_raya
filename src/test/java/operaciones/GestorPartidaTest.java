package operaciones;

import datos.Jugador;
import datos.ColorFicha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GestorPartidaTest {

    private GestorPartida gestor;
    private Jugador hum;
    private Jugador ia;

    @BeforeEach
    void setUp() {
        hum = new Jugador();
        ia = new Jugador();
        gestor = new GestorPartida(hum, ia, null);
    }

    @Test
    void testIniciarPartida() {
        gestor.iniciarPartida();
        assertFalse(gestor.getPartida().isTerminada(), "La partida debe estar en curso");
        assertEquals(ColorFicha.ROJO, hum.getColor(), "El humano debe ser ROJO");
        assertEquals(ColorFicha.AZUL, ia.getColor(), "La IA debe ser AZUL");
    }

    @Test
    void testColocarFicha() {
        gestor.iniciarPartida();
        // Si esto falla en rojo, es porque la clase Tablero tiene un error
        assertTrue(gestor.colocarFicha(0), "Debería dejar colocar ficha en la columna 0");
    }
}