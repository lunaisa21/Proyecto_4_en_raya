package operaciones;

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
        return 0;
    }
    public boolean hayCuatroEnLinea(){
        return true;
    }
    public boolean tableroLleno(){
        return true;
    }
}
