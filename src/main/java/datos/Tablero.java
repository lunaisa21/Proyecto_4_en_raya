package datos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tablero {
    private int filas = 6 ;
    private int columnas = 7 ;
    private Ficha[][] casillas;

    public Tablero() {
        this.casillas = new Ficha[filas][columnas];
    }
}
