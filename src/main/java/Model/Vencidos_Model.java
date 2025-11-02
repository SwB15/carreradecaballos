package Model;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) para representar una apuesta vencida. Diseñado
 * para ser usado en notificaciones o reportes de deudas que han pasado su fecha
 * límite.
 */
public class Vencidos_Model {

    // Se declaran todos los campos como privados para asegurar la encapsulación.
    private int idApuesta;
    private int montoApuesta;
    private int montoAbonado;
    private LocalDate fechaApuesta; // Se usa el tipo de dato correcto para la fecha.
    private LocalDate fechaLimite;  // Se usa el tipo de dato correcto para la fecha.
    private String nombreApostador;
    private String nombreCarrera;

    /**
     * Constructor para inicializar el objeto con todos sus datos.
     *
     * @param idApuesta El ID de la apuesta vencida.
     * @param montoApuesta El monto total de la apuesta.
     * @param montoAbonado El monto que ha sido pagado hasta la fecha.
     * @param fechaApuesta La fecha en que se realizó la apuesta.
     * @param fechaLimite La fecha límite que ya ha pasado.
     * @param nombreApostador El nombre del deudor.
     * @param nombreCarrera El nombre de la carrera asociada.
     */
    public Vencidos_Model(int idApuesta, int montoApuesta, int montoAbonado,
            LocalDate fechaApuesta, LocalDate fechaLimite,
            String nombreApostador, String nombreCarrera) {
        this.idApuesta = idApuesta;
        this.montoApuesta = montoApuesta;
        this.montoAbonado = montoAbonado;
        this.fechaApuesta = fechaApuesta;
        this.fechaLimite = fechaLimite;
        this.nombreApostador = nombreApostador;
        this.nombreCarrera = nombreCarrera;
    }

    // --- GETTERS ---
    // Se proveen getters para acceder a los datos de forma controlada.
    public int getIdApuesta() {
        return idApuesta;
    }

    public int getMontoApuesta() {
        return montoApuesta;
    }

    public int getMontoAbonado() {
        return montoAbonado;
    }

    public LocalDate getFechaApuesta() {
        return fechaApuesta;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public String getNombreApostador() {
        return nombreApostador;
    }

    public String getNombreCarrera() {
        return nombreCarrera;
    }
}
