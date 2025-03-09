package Model;

public class Caballos_Model{
    private int idcaballos;
    private String caballos;
    private String jinete;
    private String observacion;
    private int fk_estados;

    public Caballos_Model() {
    }

    public Caballos_Model(int idcaballos, String caballos, String jinete, String observacion, int fk_estados) {
        this.idcaballos = idcaballos;
        this.caballos = caballos;
        this.jinete = jinete;
        this.observacion = observacion;
        this.fk_estados = fk_estados;
    }

    public int getIdcaballos() {
        return idcaballos;
    }

    public void setIdcaballos(int idcaballos) {
        this.idcaballos = idcaballos;
    }

    public String getCaballos() {
        return caballos;
    }

    public void setCaballos(String caballos) {
        this.caballos = caballos;
    }

    public String getJinete() {
        return jinete;
    }

    public void setJinete(String jinete) {
        this.jinete = jinete;
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