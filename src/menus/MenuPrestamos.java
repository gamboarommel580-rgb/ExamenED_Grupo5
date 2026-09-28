package menus;

import modelo.EquipoRed;
import modelo.EstadoEquipo;
import modelo.Prestamo;
import servicios.ColaEsperaService;
import servicios.InventarioService;
import servicios.PrestamoService;
import util.Consola;
import util.Resultado;

/**
 * Submenu de prestamos y devoluciones.
 * Autor: Sebastian
 */
public class MenuPrestamos {
    private final PrestamoService prestamos;
    private final InventarioService inventario;
    private final ColaEsperaService colaEspera;

    public MenuPrestamos(PrestamoService prestamos, InventarioService inventario,
                         ColaEsperaService colaEspera) {
        this.prestamos = prestamos;
        this.inventario = inventario;
        this.colaEspera = colaEspera;
    }

    public void mostrar() {
        int opcion;
        do {
            Consola.titulo("PRESTAMOS (lista simplemente enlazada)");
            System.out.println("1. Registrar prestamo");
            System.out.println("2. Registrar devolucion");
            System.out.println("3. Mostrar prestamos activos");
            System.out.println("4. Buscar prestamo por equipo");
            System.out.println("5. Prestamos de un estudiante/grupo");
            System.out.println("0. Regresar");
            opcion = Consola.leerEntero("Opcion: ", 0, 5);
            switch (opcion) {
                case 1 -> registrarPrestamo();
                case 2 -> registrarDevolucion();
                case 3 -> {
                    prestamos.mostrar();
                    System.out.println("  Prestamos activos: " + prestamos.cantidad());
                }
                case 4 -> {
                    Prestamo p = prestamos.buscarPorEquipo(Consola.leerTexto("Codigo del equipo: "));
                    System.out.println(p == null ? "[ERROR] Ese equipo no tiene prestamo activo." : "  " + p);
                }
                case 5 -> {
                    int n = prestamos.mostrarPorResponsable(Consola.leerTexto("Estudiante o grupo: "));
                    System.out.println("  Prestamos encontrados: " + n);
                }
                default -> { }
            }
        } while (opcion != 0);
    }

    private void registrarPrestamo() {
        String codigo = Consola.leerTexto("Codigo del equipo: ");
        EquipoRed equipo = inventario.buscar(codigo);
        if (equipo == null) {
            System.out.println("[ERROR] Equipo no encontrado.");
            return;
        }
        String responsable = Consola.leerTexto("Estudiante o grupo: ");
        Resultado resultado = prestamos.prestar(codigo, responsable);
        Consola.mostrar(resultado);

        // Si el equipo esta ocupado, la solicitud puede pasar a la cola.
        if (!resultado.isExito() && equipo.getEstado() == EstadoEquipo.PRESTADO) {
            EquipoRed alternativo = inventario.buscarDisponiblePorTipo(equipo.getTipo());
            if (alternativo != null) {
                System.out.println("  Sugerencia: " + alternativo.getCodigo() + " (" + alternativo.getTipo()
                        + ") esta disponible.");
            }
            if (Consola.leerSiNo("Desea ingresar a la cola de espera de " + equipo.getCodigo() + "?")) {
                Consola.mostrar(colaEspera.encolar(responsable, equipo));
            }
        }
    }

    private void registrarDevolucion() {
        String codigo = Consola.leerTexto("Codigo del equipo: ");
        if (prestamos.buscarPorEquipo(codigo) == null) {
            System.out.println("[ERROR] Ese equipo no tiene prestamo activo.");
            return;
        }
        boolean conFalla = Consola.leerSiNo("El equipo presenta falla?");
        Consola.mostrar(prestamos.devolver(codigo, conFalla));
    }
}
