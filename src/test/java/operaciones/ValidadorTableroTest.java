import datos.ColorFicha;
import datos.Ficha;
import datos.Tablero;
import operaciones.ValidadorTablero;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidadorTableroTest {

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

    @Test
    void validarTableroLleno(){
        Tablero tableroPrueba = new Tablero(6, 7, new Ficha[6][7]);
        Ficha[][] casillas = tableroPrueba.getCasillas();
        for (int fila=0; fila<6; fila++){
            for (int columna=0; columna<7; columna++){
                casillas[fila][columna] = new Ficha(ColorFicha.ROJO);
            }
        }
        ValidadorTablero validador = new ValidadorTablero(tableroPrueba);
        assertTrue(validador.tableroLleno());
    }

    @Test
    void validarTableroNoLleno(){
        Tablero tableroPrueba = new Tablero(6, 7, new Ficha[6][7]);
        Ficha[][] casillas = tableroPrueba.getCasillas();
        for (int fila=0; fila<6; fila++){
            for (int columna=0; columna<7; columna++){
                casillas[fila][columna] = null;
            }
        }
        ValidadorTablero validador = new ValidadorTablero(tableroPrueba);
        assertFalse(validador.tableroLleno());
    }

    @Test
    void validarFilaLibre(){
        Tablero tableroPrueba = new Tablero(6, 7, new Ficha[6][7]);
        Ficha[][] casillas = tableroPrueba.getCasillas();
        casillas[5][2] = new Ficha(ColorFicha.ROJO);
        casillas[4][2] = new Ficha(ColorFicha.ROJO);

        ValidadorTablero validador = new ValidadorTablero(tableroPrueba);
        assertEquals(3, validador.filaLibre(2));
    }

    @Test
    void validarFilaNoLibre(){
        Tablero tableroPrueba = new Tablero(6, 7, new Ficha[6][7]);
        Ficha[][] casillas = tableroPrueba.getCasillas();
        for (int fila=0; fila<6; fila++){
            casillas[fila][0] = new Ficha(ColorFicha.AZUL);
        }
        ValidadorTablero validador = new ValidadorTablero(tableroPrueba);
        assertEquals(-1, validador.filaLibre(0));
    }


    @Test
    void validarHayCuatroEnLineaHorizontal(){
        Tablero tableroPrueba = new Tablero(6, 7, new Ficha[6][7]);
        Ficha[][] casillas = tableroPrueba.getCasillas();
        casillas[0][0]= new  Ficha(ColorFicha.AZUL);
        casillas[0][1]= new Ficha(ColorFicha.AZUL);
        casillas[0][2]= new Ficha(ColorFicha.AZUL);
        casillas[0][3]= new Ficha(ColorFicha.AZUL);
        ValidadorTablero validador = new ValidadorTablero(tableroPrueba);
        assertTrue(validador.hayCuatroEnLinea());
    }

    @Test
    void validarHayCuatroEnLineaVertical(){
        Tablero tableroPrueba = new Tablero(6, 7, new Ficha[6][7]);
        Ficha[][] casillas = tableroPrueba.getCasillas();
        casillas[0][0]= new  Ficha(ColorFicha.AZUL);
        casillas[1][0]= new Ficha(ColorFicha.AZUL);
        casillas[2][0]= new Ficha(ColorFicha.AZUL);
        casillas[3][0]= new Ficha(ColorFicha.AZUL);
        ValidadorTablero validador = new ValidadorTablero(tableroPrueba);
        assertTrue(validador.hayCuatroEnLinea());
    }

    @Test
    void validarHayCuatroEnLineaDiagonalDescendente(){
        Tablero tableroPrueba = new Tablero(6, 7, new Ficha[6][7]);
        Ficha[][] casillas = tableroPrueba.getCasillas();
        casillas[0][0]= new  Ficha(ColorFicha.AZUL);
        casillas[1][1]= new Ficha(ColorFicha.AZUL);
        casillas[2][2]= new Ficha(ColorFicha.AZUL);
        casillas[3][3]= new Ficha(ColorFicha.AZUL);
        ValidadorTablero validador = new ValidadorTablero(tableroPrueba);
        assertTrue(validador.hayCuatroEnLinea());
    }

    @Test
    void validarHayCuatroEnLinealDiagonalAscendente(){
        Tablero tableroPrueba = new Tablero(6, 7, new Ficha[6][7]);
        Ficha[][] casillas = tableroPrueba.getCasillas();
        casillas[3][3]= new  Ficha(ColorFicha.AZUL);
        casillas[2][2]= new Ficha(ColorFicha.AZUL);
        casillas[1][1]= new Ficha(ColorFicha.AZUL);
        casillas[0][0]= new Ficha(ColorFicha.AZUL);
        ValidadorTablero validador = new ValidadorTablero(tableroPrueba);
        assertTrue(validador.hayCuatroEnLinea());
    }
}
