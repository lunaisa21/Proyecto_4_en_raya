package datos;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
@Getter
@Setter
@NoArgsConstructor

public class Partida {
    private Tablero Tablero;
    private Jugador jugador;
    private Jugador IA;
    private Jugador turnoActual;
    private boolean terminada;
    private Marcador Marcador;



}
