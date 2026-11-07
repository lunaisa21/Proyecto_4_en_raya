package operaciones;

import org.junit.jupiter.api.Test;
import datos.Tablero;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import lombok.NoArgsConstructor;
class LogicaiaTest {
    @Test
    void debeCrearLogicaIaCorrectamente() {

        Tablero tablero = new Tablero();

        Logicaia logica = new Logicaia(tablero);

        assertNotNull(logica);
        assertEquals(tablero, logica.getTablero());
    }

    @Test
    void debePermitirCambiarTablero() {

        Tablero tablero1 = new Tablero();
        Tablero tablero2 = new Tablero();

        Logicaia logica = new Logicaia(tablero1);

        logica.setTablero(tablero2);

        assertEquals(tablero2, logica.getTablero());
    }

    @Test
    void calcularJugadaDebeRetornarColumnaValida() {

        Tablero tablero = new Tablero();

        Logicaia logica = new Logicaia(tablero);

        int columna = logica.calcularJugada();

        assertTrue(columna >= 0 && columna <= 6);
    }

    @Test
    void calcularJugadaDebeRetornarSiempreUnaColumnaValida() {

        Tablero tablero = new Tablero();

        Logicaia logica = new Logicaia(tablero);

        for (int i = 0; i < 100; i++) {

            int columna = logica.calcularJugada();

            assertTrue(columna >= 0 && columna <= 6);
        }
    }
}

