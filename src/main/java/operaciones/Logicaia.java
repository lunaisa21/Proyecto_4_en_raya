package operaciones;
import datos.Tablero;
import lombok.Getter;
import lombok.Setter;
import java.util.Random;

@Getter
@Setter
public class Logicaia {
    private Tablero tablero;

    public Logicaia(Tablero tablero) {
        this.tablero = tablero;
    }


    public int calcularJugada() {
        int columnaGanadora = buscarVictoria();
        if (columnaGanadora != -1) {
            return columnaGanadora;
        }

        int columnaBloqueo = buscarBloqueo();
        if (columnaBloqueo != -1) {
            return columnaBloqueo;
        }
        return buscarColumnaAleatoriaLibre();
    }

    private int buscarVictoria() {
        return -1;
    }


    private int buscarBloqueo() {
        return -1; //
    }
    private boolean esEmpate() {

        ValidadorTablero v = new ValidadorTablero(tablero);

        return v.tableroLleno();
    }


    private int buscarColumnaAleatoriaLibre() {
        Random random = new Random();
        int col;

        do {
            col = random.nextInt(7);
        } while (columnaLlenaEnTablero(col));

        return col;
    }

    private boolean columnaLlenaEnTablero(int col) {
        return false;
    }
    void main() {
    }
}






