package datos;

import estructuras.Cola;
import estructuras.Pila;
import modelo.EquipoRed;
import servicios.ColaEsperaService;
import servicios.ConfiguracionService;
import servicios.HistorialService;
import servicios.InventarioService;
import servicios.PrestamoService;
import servicios.TurnoService;

/** Pruebas directas del modulo de cola y deshacer. Autor: Gabriel. */
public final class PruebasGabriel {
    private static int verificaciones;

    private PruebasGabriel() { }

    public static void main(String[] args) {
        probarEstructuras();
        probarColaEspera();
        probarDeshacer();
        System.out.println("Gabriel: " + verificaciones + " verificaciones correctas.");
    }

    private static void probarEstructuras() {
        Cola<String> cola = new Cola<>();
        comprobar(cola.desencolar() == null, "desencolar cola vacia");
        cola.encolar("primero");
        cola.encolar("segundo");
        cola.encolar("tercero");
        comprobar("primero".equals(cola.consultarFrente()), "frente FIFO");
        comprobar("segundo".equals(cola.extraerPrimero("segundo"::equals)), "extraer intermedio");
        comprobar("primero".equals(cola.desencolar()), "desencolar primero");
        comprobar("tercero".equals(cola.desencolar()) && cola.estaVacia(), "conservar orden y vaciar");
        cola.encolar("nuevo");
        comprobar("nuevo".equals(cola.desencolar()), "reutilizar cola vacia");

        Cola<String> colaConCallback = new Cola<>();
        colaConCallback.encolar("viejo");
        int retirados = colaConCallback.eliminarSi("viejo"::equals,
                descartado -> colaConCallback.encolar("reemplazo"));
        comprobar(retirados == 1 && colaConCallback.tamanio() == 1
                && "reemplazo".equals(colaConCallback.consultarFrente()),
                "eliminar mantiene enlaces si el callback encola");
        comprobar("reemplazo".equals(colaConCallback.desencolar())
                && colaConCallback.estaVacia(), "cola consistente tras el callback");

        Pila<Integer> pila = new Pila<>();
        comprobar(pila.desapilar() == null, "desapilar vacia");
        pila.apilar(1);
        pila.apilar(2);
        comprobar(pila.consultarTope() == 2, "consultar tope");
        comprobar(pila.desapilar() == 2 && pila.desapilar() == 1, "orden LIFO");
        comprobar(pila.estaVacia(), "pila vacia tras desapilar");
    }

    private static void probarColaEspera() {
        HistorialService historial = new HistorialService();
        ColaEsperaService cola = new ColaEsperaService(historial);
        InventarioService inventario = new InventarioService(historial, cola);
        PrestamoService prestamos = new PrestamoService(inventario, cola, historial);
        ConfiguracionService config = new ConfiguracionService(inventario, historial);
        TurnoService turnos = new TurnoService(historial);
        DatosPrueba.cargar(inventario, prestamos, cola, config, turnos, historial);

        EquipoRed ocupado = inventario.buscar("RED002");
        comprobar(cola.consultarFrente().getResponsable().equals("Luis Paredes"), "frente inicial");
        comprobar(!cola.encolar("Luis Paredes", ocupado).isExito(), "rechazar duplicado");
        comprobar(!cola.encolar("Eva Mora", inventario.buscar("RED003")).isExito(),
                "mantenimiento no entra a la cola");
        comprobar(!cola.yaEspera(null, "RED002") && cola.contarPara(null) == 0,
                "consultas invalidas sin excepcion");
        comprobar(cola.yaEspera(" Luis Paredes ", " red002 "), "consulta normalizada");
        comprobar(cola.contarPara(" red002 ") == 2, "conteo normalizado");
        comprobar(cola.depurarEquipo("RED002", "prueba") == 2 && cola.estaVacia(),
                "depurar solicitudes por mantenimiento");
    }

    private static void probarDeshacer() {
        HistorialService historial = new HistorialService();
        ColaEsperaService cola = new ColaEsperaService(historial);
        InventarioService inventario = new InventarioService(historial, cola);
        ConfiguracionService config = new ConfiguracionService(inventario, historial);
        comprobar(inventario.registrar("RED010", modelo.TipoEquipo.ROUTER, 4).isExito(),
                "registrar equipo de prueba");
        comprobar(config.aplicar("RED010", "R-UNO", "192.168.50.10", 24, 50).isExito(),
                "primera configuracion");
        comprobar(!config.aplicar("RED010", "R-DOS", "192.168.050.11", 24, 50).isExito(),
                "rechazar octetos con ceros iniciales");
        comprobar(config.aplicar("RED010", "R-DOS", "192.168.50.11", 24, 50).isExito(),
                "segunda configuracion");
        comprobar(config.revertirUltimo().isExito()
                && inventario.buscar("RED010").getConfiguracion().getIp().equals("192.168.50.10"),
                "deshacer restaura primer cambio");
        comprobar(config.revertirUltimo().isExito()
                && inventario.buscar("RED010").getConfiguracion() == null,
                "deshacer restaura estado sin configurar");
        comprobar(!config.revertirUltimo().isExito(), "pila vacia informa error");
    }

    private static void comprobar(boolean condicion, String caso) {
        if (!condicion) {
            throw new AssertionError("Fallo: " + caso);
        }
        verificaciones++;
    }
}
