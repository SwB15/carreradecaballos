package Model;

public class Carreras_Model{
    private int idcarreras;
    private String nombre;
    private String lugar;
    private String fecha;
    private Integer idganador;
    private String observacion;
    private int fk_estados;

    public Carreras_Model() {
    }

    public Carreras_Model(int idcarreras, String nombre, String lugar, String fecha, Integer idganador, String observacion, int fk_estados) {
        this.idcarreras = idcarreras;
        this.nombre = nombre;
        this.lugar = lugar;
        this.fecha = fecha;
        this.idganador = idganador;
        this.observacion = observacion;
        this.fk_estados = fk_estados;
    }

    public int getIdcarreras() {
        return idcarreras;
    }

    public void setIdcarreras(int idcarreras) {
        this.idcarreras = idcarreras;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Integer getIdganador() {
        return idganador;
    }

    public void setIdganador(Integer idganador) {
        this.idganador = idganador;
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