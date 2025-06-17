package Model;

public class Apuestas_Model {

    private int idapuestas;
    private String apuesta;
    private int monto;
    private String fecha;
    private String fechalimite;
    private String observacion;
    private String saldousado;
    private int fk_carreras;
    private int fk_caballos;
    private int fk_apostadores;
    private int fk_estadopago;
    private int fk_estados;

    public Apuestas_Model() {
    }

    public Apuestas_Model(int idapuestas, String apuesta, int monto, String fecha, String fechalimite, String observacion, String saldousado, int fk_carreras, int fk_caballos, int fk_apostadores, int fk_estadopago, int fk_estados) {
        this.idapuestas = idapuestas;
        this.apuesta = apuesta;
        this.monto = monto;
        this.fecha = fecha;
        this.fechalimite = fechalimite;
        this.observacion = observacion;
        this.saldousado = saldousado;
        this.fk_carreras = fk_carreras;
        this.fk_caballos = fk_caballos;
        this.fk_apostadores = fk_apostadores;
        this.fk_estadopago = fk_estadopago;
        this.fk_estados = fk_estados;
    }

    public int getIdapuestas() {
        return idapuestas;
    }

    public void setIdapuestas(int idapuestas) {
        this.idapuestas = idapuestas;
    }

    public String getApuesta() {
        return apuesta;
    }

    public void setApuesta(String apuesta) {
        this.apuesta = apuesta;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFechalimite() {
        return fechalimite;
    }

    public void setFechalimite(String fechalimite) {
        this.fechalimite = fechalimite;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getSaldousado() {
        return saldousado;
    }

    public void setSaldousado(String saldousado) {
        this.saldousado = saldousado;
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

    public int getFk_apostadores() {
        return fk_apostadores;
    }

    public void setFk_apostadores(int fk_apostadores) {
        this.fk_apostadores = fk_apostadores;
    }

    public int getFk_estadopago() {
        return fk_estadopago;
    }

    public void setFk_estadopago(int fk_estadopago) {
        this.fk_estadopago = fk_estadopago;
    }

    public int getFk_estados() {
        return fk_estados;
    }

    public void setFk_estados(int fk_estados) {
        this.fk_estados = fk_estados;
    }
}
