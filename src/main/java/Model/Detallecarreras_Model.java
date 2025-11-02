package Model;

/**
 * Modela la relación "muchos a muchos" entre Carreras y Caballos. Cada registro
 * en esta tabla significa que un caballo específico está participando en una
 * carrera específica.
 */
public class Detallecarreras_Model {

    private int idDetalleCarrera;
    private int fk_carreras;
    private int fk_caballos;

    /**
     * Constructor por defecto.
     */
    public Detallecarreras_Model() {
    }

    /**
     * Constructor para inicializar el modelo con todos sus datos.
     *
     * @param idDetalleCarrera El ID único de esta entrada de detalle.
     * @param fk_carreras El ID de la carrera.
     * @param fk_caballos El ID del caballo participante.
     */
    public Detallecarreras_Model(int idDetalleCarrera, int fk_carreras, int fk_caballos) {
        this.idDetalleCarrera = idDetalleCarrera;
        this.fk_carreras = fk_carreras;
        this.fk_caballos = fk_caballos;
    }

    // --- Getters y Setters ---
    public int getIdDetalleCarrera() {
        return idDetalleCarrera;
    }

    public void setIdDetalleCarrera(int idDetalleCarrera) {
        this.idDetalleCarrera = idDetalleCarrera;
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
}
