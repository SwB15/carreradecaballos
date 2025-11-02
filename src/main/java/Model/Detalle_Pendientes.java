package Model;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) que representa el detalle de una apuesta con un
 * saldo pendiente. Esta clase está diseñada para ser usada en vistas o reportes
 * que listen deudas.
 */
public class Detalle_Pendientes {

    private int idApuesta;
    private String nombreCarrera;
    private String nombreCaballo;
    private int montoApostado;
    private int montoAbonado;
    /**
     * El monto pendiente, típicamente calculado como (montoApostado -
     * montoAbonado).
     */
    private int montoPendiente;
    private LocalDate fechaLimite;
    private String estadoDeuda;

    /**
     * Constructor por defecto.
     */
    public Detalle_Pendientes() {
    }

    /**
     * Constructor para inicializar el DTO con todos sus datos.
     *
     * @param idApuesta El ID de la apuesta original.
     * @param nombreCarrera El nombre de la carrera asociada.
     * @param nombreCaballo El nombre del caballo por el que se apostó.
     * @param montoApostado El monto total de la apuesta.
     * @param montoAbonado El monto que ya ha sido pagado.
     * @param montoPendiente El monto restante por pagar.
     * @param fechaLimite La fecha límite para saldar la deuda.
     * @param estadoDeuda
     */
    public Detalle_Pendientes(int idApuesta, String nombreCarrera, String nombreCaballo,
            int montoApostado, int montoAbonado, int montoPendiente,
            LocalDate fechaLimite, String estadoDeuda) {
        this.idApuesta = idApuesta;
        this.nombreCarrera = nombreCarrera;
        this.nombreCaballo = nombreCaballo;
        this.montoApostado = montoApostado;
        this.montoAbonado = montoAbonado;
        this.montoPendiente = montoPendiente;
        this.fechaLimite = fechaLimite;
        this.estadoDeuda = estadoDeuda;
    }

    public int getIdApuesta() {
        return idApuesta;
    }

    public void setIdApuesta(int idApuesta) {
        this.idApuesta = idApuesta;
    }

    public String getNombreCarrera() {
        return nombreCarrera;
    }

    public void setNombreCarrera(String nombreCarrera) {
        this.nombreCarrera = nombreCarrera;
    }

    public String getNombreCaballo() {
        return nombreCaballo;
    }

    public void setNombreCaballo(String nombreCaballo) {
        this.nombreCaballo = nombreCaballo;
    }

    public int getMontoApostado() {
        return montoApostado;
    }

    public void setMontoApostado(int montoApostado) {
        this.montoApostado = montoApostado;
    }

    public int getMontoAbonado() {
        return montoAbonado;
    }

    public void setMontoAbonado(int montoAbonado) {
        this.montoAbonado = montoAbonado;
    }

    public int getMontoPendiente() {
        return montoPendiente;
    }

    public void setMontoPendiente(int montoPendiente) {
        this.montoPendiente = montoPendiente;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getEstadoDeuda() {
        return estadoDeuda;
    }
}
