package Model;

/**
 * Representa la entidad de un apostador en el sistema. Contiene la información
 * personal, el saldo y el estado del apostador.
 */
public class Apostadores_Model {

    private int idapostadores;
    private String cedula;
    private String nombre;
    private int saldo;
    private String observacion;
    private int fk_estados;

    /**
     * Constructor por defecto.
     */
    public Apostadores_Model() {
    }

    /**
     * Constructor para inicializar un objeto Apostadores_Model con todos sus
     * datos.
     *
     * @param idapostadores El identificador único del apostador.
     * @param cedula El número de cédula del apostador.
     * @param nombre El nombre completo del apostador.
     * @param saldo El saldo actual de la cuenta del apostador.
     * @param observacion Cualquier nota u observación relevante.
     * @param fk_estados El ID del estado actual (ej. 1 para activo, 2 para
     * inactivo).
     */
    public Apostadores_Model(int idapostadores, String cedula, String nombre, int saldo, String observacion, int fk_estados) {
        this.idapostadores = idapostadores;
        this.cedula = cedula;
        this.nombre = nombre;
        this.saldo = saldo;
        this.observacion = observacion;
        this.fk_estados = fk_estados;
    }

    public int getIdapostadores() {
        return idapostadores;
    }

    public void setIdapostadores(int idapostadores) {
        this.idapostadores = idapostadores;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public int getFk_estados() {
        return fk_estados;
    }

    public void setFk_estados(int fk_estados) {
        this.fk_estados = fk_estados;
    }
}
