package Model;

public class Apostadores_Model{
    private int idapostadores;
    private String cedula;
    private String nombre;
    private String observacion;
    private int fk_estados;

    public Apostadores_Model() {
    }

    public Apostadores_Model(int idapostadores, String cedula, String nombre, String observacion, int fk_estados) {
        this.idapostadores = idapostadores;
        this.cedula = cedula;
        this.nombre = nombre;
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