package operaciones;

import java.util.List;

public interface GenericDAO <T>{
    void crear(T t);
    List<T> consultar();
    void actualizar(T t);
    void eliminar(T t);
}
