package Model;

public class Detalleapuestas_Model{
    private int iddetalleapuestas;
    private int fk_apuestas;
    private int fk_carreras;

    public Detalleapuestas_Model() {
    }

    public Detalleapuestas_Model(int iddetalleapuestas, int fk_apuestas, int fk_carreras) {
        this.iddetalleapuestas = iddetalleapuestas;
        this.fk_apuestas = fk_apuestas;
        this.fk_carreras = fk_carreras;
    }

    public int getIddetalleapuestas() {
        return iddetalleapuestas;
    }

    public void setIddetalleapuestas(int iddetalleapuestas) {
        this.iddetalleapuestas = iddetalleapuestas;
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