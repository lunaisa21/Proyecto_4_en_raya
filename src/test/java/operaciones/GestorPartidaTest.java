package operaciones;

import datos.ColorFicha;
import datos.Ficha;
import datos.Jugador;
import datos.Marcador;
import datos.Partida;
import datos.Tablero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GestorPartidaTest {

    private Jugador humano;
    private Jugador ia;
    private Marcador marcador;
    private GestorPartida gestor;
    private Partida partida;

    @BeforeEach
    void preparar() {
        humano = new Jugador();
        ia = new Jugador();
        marcador = new Marcador();
        gestor = new GestorPartida(humano, ia, marcador);
        partida = gestor.getPartida();
    }

    // ---------- helpers ----------

    private void iniciarConTurno(Jugador quienEmpieza) {
        gestor.iniciarPartida();
        partida.setTurnoActual(quienEmpieza); // evita la aleatoriedad
    }

    private int contarFichas() {
        Ficha[][] c = partida.getTablero().getCasillas();
        int n = 0;
        for (Ficha[] fila : c) {
            for (Ficha f : fila) {
                if (f != null) n++;
            }
        }
        return n;
    }

    // ---------- constructor ----------

    @Test
    void constructor_dejaLaPartidaTerminadaHastaIniciar() {
        assertNotNull(partida);
        assertNotNull(partida.getTablero());
        assertSame(humano, partida.getJugador());
        assertSame(ia, partida.getIa());
        assertTrue(partida.isTerminada());
        assertNull(gestor.getGanador());
    }

    @Test
    void colocarFicha_antesDeIniciar_devuelveFalse() {
        assertFalse(gestor.colocarFicha(0));
    }

    // ---------- iniciarPartida ----------

    @Test
    void iniciarPartida_preparaTodo() {
        gestor.iniciarPartida();

        assertFalse(partida.isTerminada());
        assertNull(gestor.getGanador());
        assertEquals(0, contarFichas());
        assertEquals(ColorFicha.ROJO, humano.getColor());
        assertEquals(ColorFicha.AZUL, ia.getColor());
        assertNotNull(partida.getTurnoActual());
        assertTrue(partida.getTurnoActual() == humano || partida.getTurnoActual() == ia);
    }

    @Test
    void elegirJugadorInicial_siempreDevuelveUnoDeLosDos() {
        for (int i = 0; i < 50; i++) {
            Jugador j = gestor.elegirJugadorInicial();
            assertTrue(j == humano || j == ia);
        }
    }

    @Test
    void iniciarPartida_despuesDeJugar_limpiaTablero() {
        iniciarConTurno(humano);
        gestor.colocarFicha(3);
        assertEquals(1, contarFichas());

        gestor.iniciarPartida();

        assertEquals(0, contarFichas());
        assertFalse(partida.isTerminada());
    }

    // ---------- colocarFicha ----------

    @Test
    void colocarFicha_columnaInvalida_devuelveFalse() {
        iniciarConTurno(humano);
        Tablero t = partida.getTablero();

        assertFalse(gestor.colocarFicha(-1));
        assertFalse(gestor.colocarFicha(t.getColumnas()));
        assertEquals(0, contarFichas());
        assertSame(humano, partida.getTurnoActual());
    }

    @Test
    void colocarFicha_caeAlFondoConElColorDelJugador() {
        iniciarConTurno(humano);
        Tablero t = partida.getTablero();

        assertTrue(gestor.colocarFicha(2));

        Ficha ficha = t.getCasillas()[t.getFilas() - 1][2];
        assertNotNull(ficha);
        assertEquals(ColorFicha.ROJO, ficha.getColor());
    }

    @Test
    void colocarFicha_apilaFichasEnLaMismaColumna() {
        iniciarConTurno(humano);
        Tablero t = partida.getTablero();

        gestor.colocarFicha(0);
        gestor.colocarFicha(0);

        assertNotNull(t.getCasillas()[t.getFilas() - 1][0]);
        assertNotNull(t.getCasillas()[t.getFilas() - 2][0]);
    }

    @Test
    void colocarFicha_alternaElTurno() {
        iniciarConTurno(humano);

        gestor.colocarFicha(0);
        assertSame(ia, partida.getTurnoActual());

        gestor.colocarFicha(1);
        assertSame(humano, partida.getTurnoActual());
    }

    @Test
    void colocarFicha_columnaLlena_devuelveFalseYNoCambiaTurno() {
        iniciarConTurno(humano);
        int filas = partida.getTablero().getFilas();

        // Llenar la columna 0 alternando colores (no puede haber 4 seguidas del mismo)
        for (int i = 0; i < filas; i++) {
            assertTrue(gestor.colocarFicha(0), "ficha " + i);
        }
        assertFalse(partida.isTerminada());

        Jugador turno = partida.getTurnoActual();
        int antes = contarFichas();

        assertFalse(gestor.colocarFicha(0));
        assertEquals(antes, contarFichas());
        assertSame(turno, partida.getTurnoActual());
    }

    // ---------- victorias / derrotas ----------

    @Test
    void victoriaVertical_delHumano_sumaVictoria() {
        iniciarConTurno(humano);

        gestor.colocarFicha(0); // H
        gestor.colocarFicha(1); // IA
        gestor.colocarFicha(0); // H
        gestor.colocarFicha(1); // IA
        gestor.colocarFicha(0); // H
        gestor.colocarFicha(1); // IA
        gestor.colocarFicha(0); // H -> 4 en linea

        assertTrue(partida.isTerminada());
        assertSame(humano, gestor.getGanador());
        assertEquals(1, marcador.getVictorias());
        assertEquals(0, marcador.getDerrotas());
        assertEquals(0, marcador.getEmpates());
    }

    @Test
    void victoriaHorizontal_delHumano() {
        iniciarConTurno(humano);

        gestor.colocarFicha(0); // H
        gestor.colocarFicha(0); // IA
        gestor.colocarFicha(1); // H
        gestor.colocarFicha(1); // IA
        gestor.colocarFicha(2); // H
        gestor.colocarFicha(2); // IA
        gestor.colocarFicha(3); // H -> 4 en fila inferior

        assertTrue(partida.isTerminada());
        assertSame(humano, gestor.getGanador());
    }

    @Test
    void victoriaDeLaIA_sumaDerrota() {
        iniciarConTurno(ia);

        gestor.colocarFicha(0); // IA
        gestor.colocarFicha(1); // H
        gestor.colocarFicha(0); // IA
        gestor.colocarFicha(1); // H
        gestor.colocarFicha(0); // IA
        gestor.colocarFicha(1); // H
        gestor.colocarFicha(0); // IA -> gana

        assertTrue(partida.isTerminada());
        assertSame(ia, gestor.getGanador());
        assertEquals(1, marcador.getDerrotas());
        assertEquals(0, marcador.getVictorias());
    }

    @Test
    void despuesDeGanar_noSePuedeSeguirJugando() {
        iniciarConTurno(humano);
        for (int i = 0; i < 3; i++) {
            gestor.colocarFicha(0);
            gestor.colocarFicha(1);
        }
        gestor.colocarFicha(0); // gana

        int fichas = contarFichas();
        assertFalse(gestor.colocarFicha(5));
        assertEquals(fichas, contarFichas());
    }

    // ---------- empate ----------

    @Test
    void registrarEmpate_sumaEmpateYTerminaLaPartida() {
        iniciarConTurno(humano);

        gestor.registrarEmpate();

        assertTrue(partida.isTerminada());
        assertNull(gestor.getGanador());
        assertEquals(1, marcador.getEmpates());
    }

    // ---------- marcador nulo ----------

    @Test
    void conMarcadorNulo_noLanzaExcepcion() {
        GestorPartida sinMarcador = new GestorPartida(new Jugador(), new Jugador(), null);
        sinMarcador.iniciarPartida();

        assertDoesNotThrow(() -> sinMarcador.registrarVictoria(sinMarcador.getPartida().getJugador()));
        assertDoesNotThrow(sinMarcador::registrarEmpate);
    }

    // ---------- IA ----------

    @Test
    void ejecutarTurnoIA_enTurnoDelHumano_devuelveFalse() {
        iniciarConTurno(humano);

        assertFalse(gestor.ejecutarTurnoIA());
        assertEquals(0, contarFichas());
    }

    @Test
    void ejecutarTurnoIA_enSuTurno_colocaUnaFichaAzulYPasaElTurno() {
        iniciarConTurno(ia);

        assertTrue(gestor.ejecutarTurnoIA());

        assertEquals(1, contarFichas());
        assertSame(humano, partida.getTurnoActual());

        Ficha[][] c = partida.getTablero().getCasillas();
        boolean hayAzul = false;
        for (Ficha[] fila : c) {
            for (Ficha f : fila) {
                if (f != null && f.getColor() == ColorFicha.AZUL) hayAzul = true;
            }
        }
        assertTrue(hayAzul);
    }

    @Test
    void ejecutarTurnoIA_conPartidaTerminada_devuelveFalse() {
        iniciarConTurno(ia);
        gestor.salir();

        assertFalse(gestor.ejecutarTurnoIA());
    }

    @Test
    void partidaCompletaHumanoContraIA_terminaSinErrores() {
        iniciarConTurno(humano);
        int jugadas = 0;
        int col = 0;

        while (!partida.isTerminada() && jugadas < 100) {
            if (partida.getTurnoActual() == humano) {
                // el humano prueba columnas hasta encontrar una libre
                int intentos = 0;
                while (!gestor.colocarFicha(col % partida.getTablero().getColumnas())
                        && intentos++ < partida.getTablero().getColumnas()) {
                    col++;
                }
                col++;
            } else {
                assertTrue(gestor.ejecutarTurnoIA());
            }
            jugadas++;
        }

        assertTrue(partida.isTerminada(), "la partida debe terminar");
        int total = marcador.getVictorias() + marcador.getDerrotas() + marcador.getEmpates();
        assertEquals(1, total, "debe registrarse exactamente un resultado");
    }

    // ---------- salir / alternar ----------

    @Test
    void salir_terminaLaPartida() {
        gestor.iniciarPartida();

        gestor.salir();

        assertTrue(partida.isTerminada());
        assertFalse(gestor.colocarFicha(0));
    }

    @Test
    void alternarTurno_cambiaEntreJugadorEIA() {
        iniciarConTurno(humano);

        gestor.alternarTurno();
        assertSame(ia, partida.getTurnoActual());

        gestor.alternarTurno();
        assertSame(humano, partida.getTurnoActual());
    }
}