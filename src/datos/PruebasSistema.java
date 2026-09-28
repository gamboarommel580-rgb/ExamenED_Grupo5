package datos;

import modelo.EquipoRed;
import modelo.EstadoEquipo;
import modelo.Movimiento;
import modelo.TipoEquipo;
import servicios.ColaEsperaService;
import servicios.ConfiguracionService;
import servicios.HistorialService;
import servicios.InventarioService;
import servicios.PrestamoService;
import servicios.TurnoService;
import util.Resultado;

/**
 * Casos de prueba automaticos. Crea un sistema nuevo (no altera la
 * sesion del menu) y verifica cada regla con PASA / FALLA.
 * Autor: Rommel
 */
public final class PruebasSistema {
    private static int pasan;
    private static int fallan;

    private PruebasSistema() {
    }

    public static void ejecutar() {
        pasan = 0;
        fallan = 0;
        HistorialService historial = new HistorialService();
        ColaEsperaService cola = new ColaEsperaService(historial);
        InventarioService inventario = new InventarioService(historial, cola);
        PrestamoService prestamos = new PrestamoService(inventario, cola, historial);
        ConfiguracionService config = new ConfiguracionService(inventario, historial);
        TurnoService turnos = new TurnoService(historial);
        DatosPrueba.cargar(inventario, prestamos, cola, config, turnos, historial);

        System.out.println("\n=========== CASOS DE PRUEBA AUTOMATICOS ===========");

        titulo(1, "Lista secuencial: insertar, buscar y rechazar duplicado");
        verificar("Registrar RED006", inventario.registrar("RED006", TipoEquipo.ROUTER, 4).isExito());
        verificar("Buscar RED006", inventario.buscar("red006") != null);
        verificar("Rechaza codigo duplicado", !inventario.registrar("RED006", TipoEquipo.ROUTER, 4).isExito());
        verificar("Rechaza 60 puertos en un Router",
                !inventario.registrar("RED007", TipoEquipo.ROUTER, 60).isExito());

        titulo(2, "Lista simple: prestamo de equipo disponible");
        int antes = prestamos.cantidad();
        verificar("Prestar RED001", prestamos.prestar("RED001", "Carlos Mena").isExito());
        verificar("Estado cambia a Prestado", inventario.buscar("RED001").getEstado() == EstadoEquipo.PRESTADO);
        verificar("Lista de prestamos crece en 1", prestamos.cantidad() == antes + 1);

        titulo(3, "REGLA GRUPO 5: equipo en mantenimiento no se presta ni entra a cola");
        verificar("No se presta RED003", !prestamos.prestar("RED003", "Maria Lopez").isExito());
        verificar("No entra a la cola", !cola.encolar("Maria Lopez", inventario.buscar("RED003")).isExito());
        verificar("No aparece como disponible",
                inventario.buscarDisponiblePorTipo(TipoEquipo.ACCESS_POINT) == null);

        titulo(4, "Cola FIFO: la devolucion asigna al primero en llegar");
        Resultado dev = prestamos.devolver("RED002", false);
        System.out.println("    " + dev);
        verificar("RED002 asignado a Luis Paredes (primero)",
                prestamos.buscarPorEquipo("RED002").getResponsable().equals("Luis Paredes"));
        verificar("Queda Grupo Redes B al frente",
                cola.consultarFrente() != null && cola.consultarFrente().getResponsable().equals("Grupo Redes B"));

        titulo(5, "Devolucion con falla: mantenimiento y depuracion de la cola");
        Resultado falla = prestamos.devolver("RED002", true);
        System.out.println("    " + falla);
        verificar("RED002 en mantenimiento", inventario.buscar("RED002").getEstado() == EstadoEquipo.MANTENIMIENTO);
        verificar("Cola sin solicitudes de RED002", cola.contarPara("RED002") == 0);

        titulo(6, "Pila: deshacer respeta LIFO");
        EquipoRed r5 = inventario.buscar("RED005");
        String ipOriginal = r5.getConfiguracion().getIp();
        config.aplicar("RED005", "SW2-LAB", "192.168.10.50", 24, 10);
        config.aplicar("RED005", "SW2-CORE", "192.168.10.60", 24, 30);
        config.revertirUltimo();
        verificar("Primer deshacer vuelve a .50", r5.getConfiguracion().getIp().equals("192.168.10.50"));
        config.revertirUltimo();
        verificar("Segundo deshacer vuelve a la IP original", r5.getConfiguracion().getIp().equals(ipOriginal));

        titulo(7, "Validaciones de configuracion logica");
        verificar("Rechaza IP 300.1.1.1", !config.aplicar("RED005", "SW2", "300.1.1.1", 24, 10).isExito());
        verificar("Rechaza IP de red 192.168.10.0/24",
                !config.aplicar("RED005", "SW2", "192.168.10.0", 24, 10).isExito());
        verificar("Rechaza IP duplicada (la de RED001)",
                !config.aplicar("RED005", "SW2", "192.168.10.1", 24, 10).isExito());
        verificar("Rechaza VLAN 5000", !config.aplicar("RED005", "SW2", "192.168.10.70", 24, 5000).isExito());

        titulo(8, "Lista circular: la ronda vuelve al inicio");
        String inicial = turnos.consultarActual().getGrupo();
        turnos.avanzar();
        turnos.avanzar();
        turnos.avanzar();
        verificar("Tras 3 avances vuelve a " + inicial, turnos.consultarActual().getGrupo().equals(inicial));
        turnos.eliminarActual();
        verificar("Eliminar actual deja 2 turnos", turnos.cantidad() == 2);
        turnos.avanzar();
        turnos.avanzar();
        verificar("Sigue circulando tras eliminar", turnos.consultarActual() != null);

        titulo(9, "Lista doble: recorrido adelante y atras");
        Movimiento[] primeroYUltimo = new Movimiento[2];
        historial.recorrer(true, m -> {
            if (primeroYUltimo[0] == null) {
                primeroYUltimo[0] = m;
            }
        });
        historial.recorrer(false, m -> {
            if (primeroYUltimo[1] == null) {
                primeroYUltimo[1] = m;
            }
        });
        verificar("Adelante inicia con la carga inicial",
                primeroYUltimo[0].getDetalle().startsWith("Inicio de carga"));
        verificar("Atras inicia con el ultimo movimiento", primeroYUltimo[1] == historial.ultimo());

        titulo(10, "Validaciones de inventario");
        verificar("No elimina equipo prestado (RED001)", !inventario.eliminar("RED001").isExito());
        verificar("No cambia manualmente a Prestado",
                !inventario.modificarEstado("RED006", EstadoEquipo.PRESTADO).isExito());
        verificar("Finaliza mantenimiento de RED003", inventario.finalizarMantenimiento("RED003").isExito());
        verificar("RED003 ahora si puede prestarse", prestamos.prestar("RED003", "Maria Lopez").isExito());

        System.out.println("\n===================================================");
        System.out.println("RESULTADO: " + pasan + " verificaciones PASAN, " + fallan + " FALLAN");
        System.out.println("===================================================");
    }

    private static void titulo(int numero, String texto) {
        System.out.println("\nCaso " + numero + ": " + texto);
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            pasan++;
            System.out.println("  [PASA]  " + descripcion);
        } else {
            fallan++;
            System.out.println("  [FALLA] " + descripcion);
        }
    }
}
