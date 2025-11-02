package Model;

/**
 * Representa la relación de detalle entre una apuesta y una carrera. (Nota:
 * Revisar si esta clase es necesaria, ya que Apuestas_Model ya contiene
 * fk_carreras).
 */
public class Detalleapuestas_Model {

    private int idDetalleApuesta;
    private int fk_apuestas;
    private int fk_carreras;

    /**
     * Constructor por defecto.
     */
    public Detalleapuestas_Model() {
    }

    /**
     * Constructor para inicializar el modelo con todos sus datos.
     *
     * @param idDetalleApuesta El ID único del registro de detalle.
     * @param fk_apuestas El ID de la apuesta relacionada.
     * @param fk_carreras El ID de la carrera relacionada.
     */
    public Detalleapuestas_Model(int idDetalleApuesta, int fk_apuestas, int fk_carreras) {
        this.idDetalleApuesta = idDetalleApuesta;
        this.fk_apuestas = fk_apuestas;
        this.fk_carreras = fk_carreras;
    }

    // --- Getters y Setters ---
    public int getIdDetalleApuesta() {
        return idDetalleApuesta;
    }

    public void setIdDetalleApuesta(int idDetalleApuesta) {
        this.idDetalleApuesta = idDetalleApuesta;
    }

    public int getFk_apuestas() {
        return fk_apuestas;
    }

    public void setFk_apuestas(int fk_apuestas) {
        this.fk_apuestas = fk_apuestas;
    }

    public int getFk_carreras() {
        return fk_carreras;
    }

    public void setFk_carreras(int fk_carreras) {
        this.fk_carreras = fk_carreras;
    }
}
