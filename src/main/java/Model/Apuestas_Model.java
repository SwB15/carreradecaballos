package Model;

import java.time.LocalDate;

/**
 * Representa la entidad de una apuesta realizada en una carrera específica.
 * Contiene todos los detalles de la apuesta, incluyendo montos y estado.
 */
public class Apuestas_Model {

    private int idApuesta;
    private String apuesta; // Un nombre o descripción para la apuesta
    private int monto;
    private LocalDate fecha; // Se usa LocalDate para un manejo seguro de la fecha.
    private LocalDate fechaLimite; // Se usa LocalDate para la fecha límite.
    private String observacion;
    private boolean saldoUsado; // Un booleano es más claro que un String.
    private int abonado;

    // Se mantiene la nomenclatura para las claves foráneas.
    private int fk_carreras;
    private int fk_caballos;
    private int fk_apostadores;
    private int fk_estadopago;
    private int fk_estados;

    /**
     * Constructor por defecto.
     */
    public Apuestas_Model() {
    }

    /**
     * Constructor completo para crear una nueva instancia de Apuesta.
     * @param idApuesta
     * @param apuesta
     * @param monto
     * @param fecha
     * @param fechaLimite
     * @param observacion
     * @param saldoUsado
     * @param abonado
     * @param fk_carreras
     * @param fk_caballos
     * @param fk_apostadores
     * @param fk_estadopago
     * @param fk_estados
     */
    public Apuestas_Model(int idApuesta, String apuesta, int monto, LocalDate fecha,
            LocalDate fechaLimite, String observacion, boolean saldoUsado,
            int abonado, int fk_carreras, int fk_caballos, int fk_apostadores,
            int fk_estadopago, int fk_estados) {
        this.idApuesta = idApuesta;
        this.apuesta = apuesta;
        this.monto = monto;
        this.fecha = fecha;
        this.fechaLimite = fechaLimite;
        this.observacion = observacion;
        this.saldoUsado = saldoUsado;
        this.abonado = abonado;
        this.fk_carreras = fk_carreras;
        this.fk_caballos = fk_caballos;
        this.fk_apostadores = fk_apostadores;
        this.fk_estadopago = fk_estadopago;
        this.fk_estados = fk_estados;
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

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public boolean isSaldoUsado() {
        return saldoUsado;
    }

    public void setSaldoUsado(boolean saldoUsado) {
        this.saldoUsado = saldoUsado;
    }

    public int getAbonado() {
        return abonado;
    }

    public void setAbonado(int abonado) {
        this.abonado = abonado;
    }

    public int getFk_carreras() {
        return fk_carreras;
    }

    public void setFk_carreras(int fk_carreras) {
        this.fk_carreras = fk_carreras;
    }

    public int getFk_caballos() {
        return fk_caballos;
    }

    public void setFk_caballos(int fk_caballos) {
        this.fk_caballos = fk_caballos;
    }

    public int getFk_apostadores() {
        return fk_apostadores;
    }

    public void setFk_apostadores(int fk_apostadores) {
        this.fk_apostadores = fk_apostadores;
    }

    public int getFk_estadopago() {
        return fk_estadopago;
    }

    public void setFk_estadopago(int fk_estadopago) {
        this.fk_estadopago = fk_estadopago;
    }

    public int getFk_estados() {
        return fk_estados;
    }

    public void setFk_estados(int fk_estados) {
        this.fk_estados = fk_estados;
    }
}
