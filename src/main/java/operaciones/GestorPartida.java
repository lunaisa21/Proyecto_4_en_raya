package operaciones;

import datos.ColorFicha;
import datos.Ficha;
import datos.Jugador;
import datos.Marcador;
import datos.Partida;
import datos.Tablero;
import java.util.Random;
import lombok.Getter;

public class GestorPartida {

    @Getter
    private final Partida partida;

    @Getter
    private final ValidadorTablero validador;

    private final Logicaia logicaia;
    private final Marcador marcadorJugador;

    @Getter
    private Jugador ganador;

    private final Random random = new Random();

    public GestorPartida(Jugador jugador, Jugador ia, Marcador marcadorJugador) {
        this.marcadorJugador = marcadorJugador;

        Tablero tablero = new Tablero();
        this.partida = new Partida();
        this.partida.setTablero(tablero);
        this.partida.setJugador(jugador);
        this.partida.setIa(ia);
        this.partida.setTerminada(true);

        this.validador = new ValidadorTablero(tablero);
        this.logicaia = new Logicaia(tablero);
    }

    public void iniciarPartida() {
        Tablero tablero = partida.getTablero();
        tablero.setCasillas(new Ficha[tablero.getFilas()][tablero.getColumnas()]);
        this.ganador = null;
        this.partida.setTerminada(false);

        asignarColores();
        this.partida.setTurnoActual(elegirJugadorInicial());
    }

    public Jugador elegirJugadorInicial() {
        return random.nextBoolean() ? partida.getJugador() : partida.getIa();
    }

    public void asignarColores() {
        Jugador jugador = partida.getJugador();
        Jugador ia = partida.getIa();

        jugador.setColor(ColorFicha.ROJO);
        ia.setColor(ColorFicha.AZUL);

        this.logicaia.setFichaHumano(new Ficha(jugador.getColor()));
        this.logicaia.setFichaIA(new Ficha(ia.getColor()));
    }

    public boolean colocarFicha(int columna) {
        Tablero tablero = partida.getTablero();
        Jugador actual = partida.getTurnoActual();

        if (partida.isTerminada() || actual == null || columna < 0 || columna >= tablero.getColumnas()) {
            return false;
        }
        if (validador.columnaLlena(columna)) {
            return false;
        }

        int fila = validador.filaLibre(columna);
        if (fila == -1) {
            return false;
        }

        tablero.getCasillas()[fila][columna] = new Ficha(actual.getColor());

        if (validador.hayCuatroEnLinea()) {
            registrarVictoria(actual);
        } else if (validador.tableroLleno()) {
            registrarEmpate();
        } else {
            alternarTurno();
        }

        return true;
    }

    public boolean ejecutarTurnoIA() {
        if (partida.isTerminada() || partida.getTurnoActual() != partida.getIa()) {
            return false;
        }
        int columnaElegida = logicaia.calcularJugada();
        return colocarFicha(columnaElegida);
    }

    public void alternarTurno() {
        Jugador siguiente = (partida.getTurnoActual() == partida.getJugador())
                ? partida.getIa()
                : partida.getJugador();
        partida.setTurnoActual(siguiente);
    }

    public void registrarVictoria(Jugador g) {
        this.ganador = g;
        this.partida.setTerminada(true);

        if (marcadorJugador != null) {
            if (g == partida.getJugador()) {
                marcadorJugador.setVictorias(marcadorJugador.getVictorias() + 1);
            } else if (g == partida.getIa()) {
                marcadorJugador.setDerrotas(marcadorJugador.getDerrotas() + 1);
            }
        }
    }

    public void registrarEmpate() {
        this.ganador = null;
        this.partida.setTerminada(true);

        if (marcadorJugador != null) {
            marcadorJugador.setEmpates(marcadorJugador.getEmpates() + 1);
        }
    }

    public void salir() {
        this.partida.setTerminada(true);
    }
}