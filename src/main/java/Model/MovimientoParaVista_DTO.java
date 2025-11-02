package Model;

/**
 * DTO para transportar los datos de un movimiento con información enriquecida
 * para ser mostrada en una tabla o vista.
 */
public class MovimientoParaVista_DTO {

    private final Movimientos_Model movimiento;
    private final String nombreApostador;
    private final String descripcionTipoMovimiento;
    private final String nombreCarrera;

    public MovimientoParaVista_DTO(Movimientos_Model movimiento, String nombreApostador, String descripcionTipoMovimiento, String nombreCarrera) {
        this.movimiento = movimiento;
        this.nombreApostador = nombreApostador;
        this.descripcionTipoMovimiento = descripcionTipoMovimiento;
        this.nombreCarrera = nombreCarrera;
    }

    public Movimientos_Model getMovimiento() {
        return movimiento;
    }

    public String getNombreApostador() {
        return nombreApostador;
    }

    public String getDescripcionTipoMovimiento() {
        return descripcionTipoMovimiento;
    }

    public String getNombreCarrera() {
        return nombreCarrera;
    }
}
