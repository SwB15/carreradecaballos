package Model;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) para representar una fila en el reporte del
 * historial de un apostador. Contiene una vista simplificada y denormalizada de
 * las apuestas pasadas.
 */
public class ReportesHistorialApostador_Model {

    private int idApuesta;
    private String apuesta;
    private int monto;
    private LocalDate fecha; // Se usa LocalDate para el manejo correcto de la fecha.
    private String caballo;
    private String resultado;

    /**
     * Constructor por defecto.
     */
    public ReportesHistorialApostador_Model() {
    }

    /**
     * Constructor para inicializar el DTO con todos sus datos.
     *
     * @param idApuesta El ID de la apuesta.
     * @param apuesta El nombre o descripción de la carrera/apuesta.
     * @param monto El monto de la apuesta.
     * @param fecha La fecha en que se realizó la apuesta.
     * @param caballo El nombre del caballo apostado.
     * @param resultado El resultado de la apuesta (ej. "Ganada", "Perdida").
     */
    public ReportesHistorialApostador_Model(int idApuesta, String apuesta, int monto,
            LocalDate fecha, String caballo, String resultado) {
        this.idApuesta = idApuesta;
        this.apuesta = apuesta;
        this.monto = monto;
        this.fecha = fecha;
        this.caballo = caballo;
        this.resultado = resultado;
    }

    public int getIdApuesta() {
        return idApuesta;
    }

    public void setIdApuesta(int idApuesta) {
        this.idApuesta = idApuesta;
    }

    public String getApuesta() {
        return apuesta;
    }

    public void setApuesta(String apuesta) {
        this.apuesta = apuesta;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getCaballo() {
        return caballo;
    }

    public void setCaballo(String caballo) {
        this.caballo = caballo;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
