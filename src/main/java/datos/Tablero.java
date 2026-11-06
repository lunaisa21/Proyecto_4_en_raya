package datos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tablero {
    private int filas ;
    private int columnas  ;
    private Ficha[][] casillas;
}
