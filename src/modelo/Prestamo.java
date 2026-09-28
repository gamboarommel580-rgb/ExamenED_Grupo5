package modelo;

/**
 * Prestamo activo de un equipo a un estudiante o grupo.
 * Autor: Sebastian
 */
public class Prestamo {
    private final String codigoEquipo;
    private final String responsable;
    private final String fecha;

    public Prestamo(String codigoEquipo, String responsable, String fecha) {
        this.codigoEquipo = codigoEquipo;
        this.responsable = responsable;
        this.fecha = fecha;
    }

    public String getCodigoEquipo() {
        return codigoEquipo;
    }

    public String getResponsable() {
        return responsable;
    }

    public String getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return "Equipo " + codigoEquipo + " | Responsable: " + responsable + " | Desde: " + fecha;
    }
}
