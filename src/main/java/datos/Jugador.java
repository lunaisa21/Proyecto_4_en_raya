package datos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Jugador {
    private String nombre;
    private ColorFicha color;
    private boolean esIa;
    private int idJugador;
}
