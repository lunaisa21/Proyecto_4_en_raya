package datos;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
@Getter
@Setter
@NoArgsConstructor

public class Partida {
    private Tablero tablero;
    private Jugador jugador;
    private Jugador ia;
    private Jugador turnoActual;
    private boolean terminada;
}
