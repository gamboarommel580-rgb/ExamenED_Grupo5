package estructuras;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Cola FIFO con nodos propios (reutiliza Nodo).
 * Uso en el sistema: solicitudes en espera de equipos de alta demanda.
 * encolar y desencolar son O(1) porque se guardan frente y final.
 * Autor: Gabriel
 */
public class Cola<T> {
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;

    public boolean estaVacia() {
        return frente == null;
    }

    public int tamanio() {
        return tamanio;
    }

    /** Agrega al final de la cola. O(1). */
    public void encolar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("No se puede encolar un dato nulo.");
        }
        Nodo<T> nuevo = new Nodo<>(dato);
        if (fin == null) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.siguiente = nuevo;
            fin = nuevo;
        }
        tamanio++;
    }

    /** Retira el elemento del frente. O(1). Devuelve null si esta vacia. */
    public T desencolar() {
        if (estaVacia()) {
            return null;
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return dato;
    }

    /** Consulta el frente sin retirarlo. */
    public T consultarFrente() {
        return frente == null ? null : frente.dato;
    }

    /** Recorre del frente al final sin modificar la cola. */
    public void recorrer(Consumer<T> accion) {
        if (accion == pila) {
            throw new IllegalArgumentException("La accion de recorrido no puede ser nula.");
        }
        Nodo<T> actual = frente;
        while (actual != null) {
            accion.accept(actual.dato);
            actual = actual.siguiente;
        }
    }

    /**
     * Extrae la primera coincidencia sin alterar el orden de las demas.
     * Recorre como maximo n nodos: O(n).
     */
    public T extraerPrimero(Predicate<T> criterio) {
        if (criterio == null) {
            throw new IllegalArgumentException("El criterio no puede ser nulo.");
        }
        Nodo<T> anterior = null;
        Nodo<T> actual = frente;
        while (actual != null) {
            if (criterio.test(actual.dato)) {
                if (anterior == null) frente = actual.siguiente;
                else anterior.siguiente = actual.siguiente;
                if (actual == fin) fin = anterior;
                tamanio--;
                return actual.dato;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return null;
    }

    /** Elimina las coincidencias y comunica cada dato retirado. O(n). */
    public int eliminarSi(Predicate<T> criterio, Consumer<T> alEliminar) {
        if (criterio == null || alEliminar == null) {
            throw new IllegalArgumentException("Criterio y accion son obligatorios.");
        }
        Nodo<T> anterior = null;
        Nodo<T> actual = frente;
        int eliminados = 0;
        while (actual != null) {
            Nodo<T> siguiente = actual.siguiente;
            if (criterio.test(actual.dato)) {
                alEliminar.accept(actual.dato);
                if (anterior == null) frente = siguiente;
                else anterior.siguiente = siguiente;
                if (actual == fin) fin = anterior;
                tamanio--;
                eliminados++;
            } else {
                anterior = actual;
            }
            actual = siguiente;
        }
        return eliminados;
    }

    public void mostrar() {
        if (estaVacia()) {
            System.out.println("  (cola vacia)");
            return;
        }
        Nodo<T> actual = frente;
        int posicion = 1;
        while (actual != null) {
            String marca = (actual == frente) ? "  <- FRENTE" : "";
            System.out.println("  " + (posicion++) + ". " + actual.dato + marca);
            actual = actual.siguiente;
        }
    }
}
