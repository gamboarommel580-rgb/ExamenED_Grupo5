package estructuras;

/**
 * Nodo generico de enlace simple.
 * Se reutiliza en ListaSimple, Cola y Pila.
 * Autor: Sebastian
 */
public class Nodo<T> {
    T dato;
    Nodo<T> siguiente;

    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}
