package operaciones;

import datos.ColorFicha;
import datos.Ficha;
import datos.Tablero;

public class ValidadorTablero {

    private Tablero tablero;
     public ValidadorTablero(Tablero tablero){
         this.tablero = tablero;
     }

    public boolean columnaLlena(int columna){
        for (int fila=0; fila<6;  fila++){
            if (tablero.getCasillas()[fila][columna]==null){
                return false;
            }
        }
        return true;
    }

    public int filaLibre (int columna){
         for (int fila=5; fila>=0;  fila--){
             if  (tablero.getCasillas()[fila][columna]==null){
                 return fila;
             }
         }
         return -1;
    }

    public boolean hayCuatroEnLinea(){
         for (int fila=0; fila<6;  fila++){
             for (int columna=0; columna<6; columna++){
                  Ficha ficha = tablero.getCasillas()[fila][columna];

                  if (ficha==null){
                      continue;
                  }
                  ColorFicha colorF=ficha.getColor();
                  //verificar horizontal
                  if (columna+3<7 && mismaFicha(fila, columna, 0, 1, colorF)){
                      return true;
                  }
                  //verificar vertical
                 if (fila+3<6 && mismaFicha(fila, columna, 1, 0, colorF)){
                     return true;
                 }
                 //verificar diagonal descendente
                 if (fila+3<6 && columna+3<7 && mismaFicha(fila, columna, 1, 1, colorF)){
                     return true;
                 }
                 //varificar diagonal ascendente
                 if (fila-3>=0 && columna+3<7 && mismaFicha(fila, columna, -1, 1, colorF)){
                     return true;
                 }
             }
         }
         return false;
    }
    private boolean mismaFicha(int fila, int columna, int direccionFila, int direccionColumna, ColorFicha color) {
        for (int i = 0; i < 4; i++) {
            Ficha ficha = tablero.getCasillas()
                    [fila + i * direccionFila]
                    [columna + i * direccionColumna];
            if (ficha == null || ficha.getColor() != color) {
                return false;
            }
        }

        return true;
    }

    public boolean tableroLleno(){
         for (int fila=0; fila<6;  fila++){
             for  (int columna=0; columna<7; columna++){
                 if (tablero.getCasillas()[fila][columna]== null){
                     return false;
                 }
             }
         }
        return true;
    }
}
