package Model;

/**
 *
 * @author SwichBlade15
 */
public class Movimientos_Model {

    private int idmovimientos;
    private String fecha;
    private int monto;
    private String observacion;
    private Integer fk_apostadores;
    private Integer fk_apuestas;
    private Integer fk_tipomovimientos;
    private Integer fk_carreras;

    public Movimientos_Model() {
    }

    public Movimientos_Model(int idmovimientos, String fecha, int monto, String observacion, Integer fk_apostadores, Integer fk_apuestas, Integer fk_tipomovimientos, Integer fk_carreras) {
        this.idmovimientos = idmovimientos;
        this.fecha = fecha;
        this.monto = monto;
        this.observacion = observacion;
        this.fk_apostadores = fk_apostadores;
        this.fk_apuestas = fk_apuestas;
        this.fk_tipomovimientos = fk_tipomovimientos;
        this.fk_carreras = fk_carreras;
    }

    public int getIdmovimientos() {
        return idmovimientos;
    }

    public void setIdmovimientos(int idmovimientos) {
        this.idmovimientos = idmovimientos;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Integer getFk_apostadores() {
        return fk_apostadores;
    }

    public void setFk_apostadores(Integer fk_apostadores) {
        this.fk_apostadores = fk_apostadores;
    }

    public Integer getFk_apuestas() {
        return fk_apuestas;
    }

    public void setFk_apuestas(Integer fk_apuestas) {
        this.fk_apuestas = fk_apuestas;
    }

    public Integer getFk_tipomovimientos() {
        return fk_tipomovimientos;
    }

    public void setFk_tipomovimientos(Integer fk_tipomovimientos) {
        this.fk_tipomovimientos = fk_tipomovimientos;
    }

    public Integer getFk_carreras() {
        return fk_carreras;
    }

    public void setFk_carreras(Integer fk_carreras) {
        this.fk_carreras = fk_carreras;
    }
}
