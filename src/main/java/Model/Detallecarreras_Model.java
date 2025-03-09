package Model;

public class Detallecarreras_Model{
    private int iddetallecarreras;
    private int fk_carreras;
    private int fk_caballos;

    public Detallecarreras_Model() {
    }

    public Detallecarreras_Model(int iddetallecarreras, int fk_carreras, int fk_caballos) {
        this.iddetallecarreras = iddetallecarreras;
        this.fk_carreras = fk_carreras;
        this.fk_caballos = fk_caballos;
    }

    public int getIddetallecarreras() {
        return iddetallecarreras;
    }

    public void setIddetallecarreras(int iddetallecarreras) {
        this.iddetallecarreras = iddetallecarreras;
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