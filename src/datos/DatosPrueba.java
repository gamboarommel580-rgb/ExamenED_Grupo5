package datos;

import modelo.TipoEquipo;
import modelo.TipoMovimiento;
import servicios.ColaEsperaService;
import servicios.ConfiguracionService;
import servicios.HistorialService;
import servicios.InventarioService;
import servicios.PrestamoService;
import servicios.TurnoService;

/**
 * Carga de datos iniciales. Se usan los servicios (no se escribe
 * directo en las estructuras) para que todo quede en el historial
 * y el estado sea coherente entre inventario, prestamos y cola.
 * Autor: Rommel
 */
public final class DatosPrueba {

    private DatosPrueba() {
    }

    public static void cargar(InventarioService inventario, PrestamoService prestamos,
                              ColaEsperaService cola, ConfiguracionService configuracion,
                              TurnoService turnos, HistorialService historial) {
        historial.registrar(TipoMovimiento.SISTEMA, "-", "Inicio de carga de datos de prueba");

        // Datos minimos del enunciado + dos equipos extra para las pruebas
        inventario.registrar("RED001", TipoEquipo.ROUTER, 4);
        inventario.registrar("RED002", TipoEquipo.SWITCH, 24);
        inventario.registrar("RED003", TipoEquipo.ACCESS_POINT, 2);
        inventario.registrar("RED004", TipoEquipo.ROUTER, 2);
        inventario.registrar("RED005", TipoEquipo.SWITCH, 48);

        // Plan de direccionamiento del laboratorio
        configuracion.aplicarInicial("RED001", "R1-LAB", "192.168.10.1", 24, 10);
        configuracion.aplicarInicial("RED002", "SW1-LAB", "192.168.10.2", 24, 10);
        configuracion.aplicarInicial("RED003", "AP1-LAB", "192.168.20.1", 24, 20);
        configuracion.aplicarInicial("RED004", "R2-LAB", "10.0.0.1", 30, 99);
        configuracion.aplicarInicial("RED005", "SW2-LAB", "192.168.10.3", 24, 10);

        // Estados del enunciado: RED002 Prestado, RED003 Mantenimiento
        prestamos.prestar("RED002", "Grupo Redes A");
        prestamos.prestar("RED004", "Ana Torres");
        inventario.enviarAMantenimiento("RED003", "antena danada (dato inicial)");

        // Cola de alta demanda para el switch prestado
        cola.encolar("Luis Paredes", inventario.buscar("RED002"));
        cola.encolar("Grupo Redes B", inventario.buscar("RED002"));

        // Ronda del banco de pruebas
        turnos.agregar("Grupo A", "Configuracion de VLAN");
        turnos.agregar("Grupo B", "Enrutamiento estatico");
        turnos.agregar("Grupo C", "OSPF de area unica");

        historial.registrar(TipoMovimiento.SISTEMA, "-", "Fin de carga de datos de prueba");
    }
}
