package operaciones;
import datos.Ficha;
import datos.Tablero;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Random;

@Getter
@Setter
public class Logicaia {
    private Tablero tablero;
    private Ficha fichaIA;
    private Ficha fichaHumano;

    // CONSTRUCTOR 1: Para que pasen los tests
    public Logicaia(Tablero tablero) {
        this.tablero = tablero;
    }

    // CONSTRUCTOR 2: Para tu juego real (le pasas las fichas)
    public Logicaia(Tablero tablero, Ficha fichaIA, Ficha fichaHumano) {
        this.tablero = tablero;
        this.fichaIA = fichaIA;
        this.fichaHumano = fichaHumano;
    }

    public int calcularJugada() {
        // Si por alguna razón las fichas no se han inicializado, usa un fallback
        if (fichaIA != null && fichaHumano != null) {
            int columnaGanadora = buscarVictoria();
            if (columnaGanadora != -1) {
                return columnaGanadora;
            }

            int columnaBloqueo = buscarBloqueo();
            if (columnaBloqueo != -1) {
                return columnaBloqueo;
            }
        }

        // Si no hay fichas configuradas o no hay jugadas de victoria/bloqueo, juega al azar
        return buscarColumnaAleatoriaLibre();
    }

    // ============ BUSCAR VICTORIA ============
    private int buscarVictoria() {
        for (int col = 0; col < tablero.getColumnas(); col++) {
            if (!columnaDisponible(col)) continue;
            int fila = obtenerFilaDisponible(col);
            if (produceCuatro(fila, col, fichaIA)) {
                return col;
            }
        }
        return -1;
    }

    // ============ BUSCAR BLOQUEO ============
    private int buscarBloqueo() {
        for (int col = 0; col < tablero.getColumnas(); col++) {
            if (!columnaDisponible(col)) continue;
            int fila = obtenerFilaDisponible(col);
            if (produceCuatro(fila, col, fichaHumano)) {
                return col;
            }
        }
        return -1;
    }

    // ============ COLUMNA ALEATORIA LIBRE ============
    private int buscarColumnaAleatoriaLibre() {
        Random rnd = new Random();
        for (int intento = 0; intento < 100; intento++) {
            int col = rnd.nextInt(tablero.getColumnas());
            if (columnaDisponible(col)) return col;
        }
        for (int col = 0; col < tablero.getColumnas(); col++) {
            if (columnaDisponible(col)) return col;
        }
        return -1;
    }

    // ============ HELPERS ============
    private int obtenerFilaDisponible(int col) {
        for (int fila = tablero.getFilas() - 1; fila >= 0; fila--) {
            if (tablero.getCasillas()[fila][col] == null) {
                return fila;
            }
        }
        return -1;
    }

    private boolean columnaDisponible(int col) {
        return obtenerFilaDisponible(col) != -1;
    }

    private boolean produceCuatro(int fila, int col, Ficha ficha) {
        if (ficha == null) return false;

        Ficha original = tablero.getCasillas()[fila][col];
        tablero.getCasillas()[fila][col] = ficha;
        boolean gana = comprobarCuatroEnLinea(fila, col, ficha);
        tablero.getCasillas()[fila][col] = original;

        return gana;
    }

    private boolean comprobarCuatroEnLinea(int fila, int col, Ficha ficha) {
        int[][] direcciones = { {0, 1}, {1, 0}, {1, 1}, {1, -1} };
        for (int[] dir : direcciones) {
            int total = 1
                    + contarDireccion(fila, col, dir[0], dir[1], ficha)
                    + contarDireccion(fila, col, -dir[0], -dir[1], ficha);
            if (total >= 4) return true;
        }
        return false;
    }

    private int contarDireccion(int fila, int col, int df, int dc, Ficha ficha) {
        int contador = 0;
        int f = fila + df;
        int c = col + dc;

        while (enTablero(f, c) && Objects.equals(ficha, tablero.getCasillas()[f][c])) {
            contador++;
            f += df;
            c += dc;
        }
        return contador;
    }

    private boolean enTablero(int fila, int col) {
        return fila >= 0 && fila < tablero.getFilas()
                && col >= 0 && col < tablero.getColumnas();
    }
}