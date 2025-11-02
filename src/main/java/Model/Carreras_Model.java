package Model;

import java.time.LocalDate;

/**
 * Representa la entidad de una carrera de caballos. Contiene la información
 * general de la carrera, incluyendo fechas y el resultado.
 */
public class Carreras_Model {

    private int idCarrera;
    private String nombre;
    private String lugar;
    private LocalDate fecha; // Se usa LocalDate para la fecha del evento.
    private LocalDate fechaLimite; // Se usa LocalDate para la fecha límite de apuestas.
    private String observacion;
    private int comision;

    // Se mantiene la nomenclatura para claves foráneas.
    private Integer fk_ganador; // El ID del caballo ganador. Es Integer para permitir nulos si no hay ganador.
    private int fk_estados;

    /**
     * Constructor por defecto.
     */
    public Carreras_Model() {
    }

    /**
     * Constructor para inicializar un objeto Carreras_Model con todos sus
     * datos.
     *
     * @param idCarrera El ID único de la carrera.
     * @param nombre El nombre de la carrera.
     * @param lugar El lugar donde se realiza la carrera.
     * @param fecha La fecha en que se corre la carrera.
     * @param fechaLimite La fecha límite para aceptar apuestas.
     * @param fk_ganador El ID del caballo ganador (puede ser null).
     * @param observacion Cualquier nota u observación relevante.
     * @param fk_estados El ID del estado actual de la carrera.
     * @param comision El porcentaje de comisión para el gestor.
     */
    public Carreras_Model(int idCarrera, String nombre, String lugar, LocalDate fecha,
            LocalDate fechaLimite, Integer fk_ganador, String observacion,
            int fk_estados, int comision) {
        this.idCarrera = idCarrera;
        this.nombre = nombre;
        this.lugar = lugar;
        this.fecha = fecha;
        this.fechaLimite = fechaLimite;
        this.fk_ganador = fk_ganador;
        this.observacion = observacion;
        this.fk_estados = fk_estados;
        this.comision = comision;
    }

    public int getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(int idCarrera) {
        this.idCarrera = idCarrera;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
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

    public int getComision() {
        return comision;
    }

    public void setComision(int comision) {
        this.comision = comision;
    }

    public Integer getFk_ganador() {
        return fk_ganador;
    }

    public void setFk_ganador(Integer fk_ganador) {
        this.fk_ganador = fk_ganador;
    }

    public int getFk_estados() {
        return fk_estados;
    }

    public void setFk_estados(int fk_estados) {
        this.fk_estados = fk_estados;
    }
}
