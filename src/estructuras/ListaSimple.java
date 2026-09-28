package estructuras;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Lista simplemente enlazada con nodos propios.
 * Uso en el sistema: prestamos activos por estudiante o grupo.
 * Se guarda referencia al ultimo nodo para insertar al final en O(1).
 * Autor: Sebastian
 */
public class ListaSimple<T> {
    private Nodo<T> cabeza;
    private Nodo<T> ultimo;
    private int tamanio;

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int tamanio() {
        return tamanio;
    }

    /** Inserta al final. O(1) gracias a la referencia 'ultimo'. */
    public void insertarAlFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
            ultimo = nuevo;
        } else {
            ultimo.siguiente = nuevo;
            ultimo = nuevo;
        }
        tamanio++;
    }

    /** Inserta al inicio. O(1). */
    public void insertarAlInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = cabeza;
        cabeza = nuevo;
        if (ultimo == null) {
            ultimo = nuevo;
        }
        tamanio++;
    }

    /** Busqueda secuencial. O(n). Devuelve null si no existe. */
    public T buscar(Predicate<T> criterio) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (criterio.test(actual.dato)) {
                return actual.dato;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    public boolean existe(Predicate<T> criterio) {
        return buscar(criterio) != null;
    }

    public int contar(Predicate<T> criterio) {
        int contador = 0;
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (criterio.test(actual.dato)) {
                contador++;
            }
            actual = actual.siguiente;
        }
        return contador;
    }

    /**
     * Elimina el primer elemento que cumple el criterio. O(n).
     * Reenlaza el nodo anterior con el siguiente y actualiza 'ultimo'.
     */
    public T eliminar(Predicate<T> criterio) {
        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;

        while (actual != null) {
            if (criterio.test(actual.dato)) {
                if (anterior == null) {
                    cabeza = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                if (actual == ultimo) {
                    ultimo = anterior;
                }
                tamanio--;
                return actual.dato;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return null;
    }

    /** Recorrido desde la cabeza hasta el final. */
    public void recorrer(Consumer<T> accion) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            accion.accept(actual.dato);
            actual = actual.siguiente;
        }
    }

    public void mostrar() {
        if (estaVacia()) {
            System.out.println("  (lista vacia)");
            return;
        }
        Nodo<T> actual = cabeza;
        int i = 1;
        while (actual != null) {
            System.out.println("  " + (i++) + ". " + actual.dato);
            actual = actual.siguiente;
        }
    }
}
