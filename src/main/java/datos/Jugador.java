package datos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Jugador {
    private String nombre;
    private ColorFicha color;
    private boolean esIa;
}
