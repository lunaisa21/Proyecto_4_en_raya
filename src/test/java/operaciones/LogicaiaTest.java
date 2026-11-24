package operaciones;

import datos.Ficha;
import datos.Tablero;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogicaiaTest {

    @Test
    void debeCrearLogicaIaCorrectamente() {
        Tablero tablero = new Tablero();
        // ¡Faltaba inicializar la variable logica!
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

    @Test
    void laIaDebeGanarSiTieneTresEnLinea() {
        Tablero tablero = new Tablero();

        Ficha fichaIA = new Ficha();
        Ficha fichaHumano = new Ficha();


        Logicaia logica = new Logicaia(tablero, fichaIA, fichaHumano);


        tablero.getCasillas()[5][1] = fichaIA;
        tablero.getCasillas()[5][2] = fichaIA;
        tablero.getCasillas()[5][3] = fichaIA;

        int columnaElegida = logica.calcularJugada();

        assertTrue(columnaElegida == 0 || columnaElegida == 4,
                "La IA debería haber elegido la columna 0 o 4 para ganar, pero eligió la: " + columnaElegida);
    }

    @Test
    void laIaDebeBloquearAlHumanoSiTieneTresEnLinea() {
        Tablero tablero = new Tablero();
        Ficha fichaIA = new Ficha();
        Ficha fichaHumano = new Ficha();

        Logicaia logica = new Logicaia(tablero, fichaIA, fichaHumano);


        tablero.getCasillas()[5][2] = fichaHumano;
        tablero.getCasillas()[5][3] = fichaHumano;
        tablero.getCasillas()[5][4] = fichaHumano;

        int columnaElegida = logica.calcularJugada();

        assertTrue(columnaElegida == 1 || columnaElegida == 5,
                "La IA debería haber bloqueado en la columna 1 o 5, pero eligió la: " + columnaElegida);
    }
}
