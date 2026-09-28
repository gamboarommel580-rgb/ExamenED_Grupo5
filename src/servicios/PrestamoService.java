package servicios;

import estructuras.ListaSimple;
import modelo.EquipoRed;
import modelo.EstadoEquipo;
import modelo.Prestamo;
import modelo.Solicitud;
import modelo.TipoMovimiento;
import util.Fechas;
import util.Resultado;

/**
 * Prestamos activos sobre una lista simplemente enlazada.
 * Integra inventario (estado), cola (espera) e historial.
 * Autor: Sebastian
 */
public class PrestamoService {
    private final ListaSimple<Prestamo> prestamos = new ListaSimple<>();
    private final InventarioService inventario;
    private final ColaEsperaService colaEspera;
    private final HistorialService historial;

    public PrestamoService(InventarioService inventario, ColaEsperaService colaEspera,
                           HistorialService historial) {
        this.inventario = inventario;
        this.colaEspera = colaEspera;
        this.historial = historial;
    }

    public Resultado prestar(String codigo, String responsable) {
        EquipoRed equipo = inventario.buscar(codigo);
        if (equipo == null) {
            return Resultado.error("Equipo no encontrado.");
        }
        if (responsable == null || responsable.trim().length() < 3) {
            return Resultado.error("El responsable debe tener al menos 3 caracteres.");
        }
        if (equipo.getEstado() == EstadoEquipo.MANTENIMIENTO) {
            return Resultado.error("REGLA GRUPO 5: " + equipo.getCodigo()
                    + " esta en mantenimiento y no puede prestarse.");
        }
        if (equipo.getEstado() == EstadoEquipo.PRESTADO) {
            Prestamo actual = buscarPorEquipo(equipo.getCodigo());
            String quien = actual == null ? "otro usuario" : actual.getResponsable();
            return Resultado.error(equipo.getCodigo() + " ya esta prestado a " + quien
                    + ". Puede ingresar a la cola de espera.");
        }
        registrarPrestamo(equipo, responsable.trim(), "Prestamo registrado a ");
        return Resultado.ok("Prestamo registrado: " + equipo.getCodigo() + " -> " + responsable.trim());
    }

    private void registrarPrestamo(EquipoRed equipo, String responsable, String detalle) {
        prestamos.insertarAlFinal(new Prestamo(equipo.getCodigo(), responsable, Fechas.ahora()));
        inventario.actualizarEstado(equipo, EstadoEquipo.PRESTADO);
        historial.registrar(TipoMovimiento.PRESTAMO, equipo.getCodigo(), detalle + responsable);
    }

    /**
     * Devolucion. Si el equipo vuelve con falla pasa a mantenimiento
     * (y se depura la cola). Si vuelve bien, se asigna automaticamente
     * a la solicitud mas antigua que lo espera.
     */
    public Resultado devolver(String codigo, boolean conFalla) {
        EquipoRed equipo = inventario.buscar(codigo);
        if (equipo == null) {
            return Resultado.error("Equipo no encontrado.");
        }
        Prestamo prestamo = prestamos.eliminar(p -> p.getCodigoEquipo().equalsIgnoreCase(equipo.getCodigo()));
        if (prestamo == null) {
            return Resultado.error(equipo.getCodigo() + " no registra un prestamo activo.");
        }
        historial.registrar(TipoMovimiento.DEVOLUCION, equipo.getCodigo(),
                "Devuelto por " + prestamo.getResponsable() + (conFalla ? " (con falla)" : ""));

        if (conFalla) {
            Resultado r = inventario.aplicarMantenimiento(equipo, "devuelto con falla");
            return Resultado.ok("Devolucion registrada. " + r.getMensaje());
        }

        inventario.actualizarEstado(equipo, EstadoEquipo.DISPONIBLE);
        Solicitud siguiente = colaEspera.extraerPrimeraPara(equipo.getCodigo());
        if (siguiente == null) {
            return Resultado.ok("Devolucion registrada. " + equipo.getCodigo() + " esta Disponible.");
        }
        registrarPrestamo(equipo, siguiente.getResponsable(), "Asignado desde la cola a ");
        return Resultado.ok("Devolucion registrada. " + equipo.getCodigo()
                + " fue asignado automaticamente a " + siguiente.getResponsable() + " (cola FIFO).");
    }

    /** Atiende el frente de la cola si su equipo ya esta disponible. */
    public Resultado atenderFrenteCola() {
        Solicitud frente = colaEspera.consultarFrente();
        if (frente == null) {
            return Resultado.error("La cola de espera esta vacia.");
        }
        EquipoRed equipo = inventario.buscar(frente.getCodigoEquipo());
        if (equipo == null) {
            colaEspera.desencolar();
            return Resultado.error("El equipo solicitado ya no existe; la solicitud fue retirada.");
        }
        if (equipo.getEstado() != EstadoEquipo.DISPONIBLE) {
            return Resultado.error("El frente espera " + equipo.getCodigo() + ", que sigue "
                    + equipo.getEstado() + ". La solicitud permanece al frente.");
        }
        colaEspera.desencolar();
        registrarPrestamo(equipo, frente.getResponsable(), "Asignado desde la cola a ");
        return Resultado.ok("Solicitud atendida: " + equipo.getCodigo() + " -> " + frente.getResponsable());
    }

    public Prestamo buscarPorEquipo(String codigo) {
        return prestamos.buscar(p -> p.getCodigoEquipo().equalsIgnoreCase(codigo.trim()));
    }

    public int mostrarPorResponsable(String responsable) {
        int[] contador = {0};
        prestamos.recorrer(p -> {
            if (p.getResponsable().equalsIgnoreCase(responsable.trim())) {
                System.out.println("  - " + p);
                contador[0]++;
            }
        });
        return contador[0];
    }

    public void mostrar() {
        prestamos.mostrar();
    }

    public int cantidad() {
        return prestamos.tamanio();
    }
}
