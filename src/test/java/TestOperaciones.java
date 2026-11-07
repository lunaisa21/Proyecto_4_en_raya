import datos.ColorFicha;
import datos.Ficha;
import datos.Tablero;
import operaciones.ValidadorTablero;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestOperaciones {

    @Test
    void validarColumnaLlena(){
        Tablero tableroPrueba = new Tablero(6, 7, new Ficha[6][7]);
        Ficha[][] casillas = tableroPrueba.getCasillas();
        for (int fila=0; fila<6; fila++){
            casillas[fila][0] = new Ficha(ColorFicha.ROJO);
        }
        ValidadorTablero validador = new ValidadorTablero(tableroPrueba);
        assertTrue(validador.columnaLlena(0));
    }

    @Test
    void validarColumnaNoLlena(){
        Tablero tableroPrueba = new Tablero(6, 7, new Ficha[6][7]);
        Ficha[][] casillas = tableroPrueba.getCasillas();
        casillas[2][0] = new Ficha(ColorFicha.ROJO);
        ValidadorTablero validador = new ValidadorTablero(tableroPrueba);
        assertFalse(validador.columnaLlena(0));
    }
}
