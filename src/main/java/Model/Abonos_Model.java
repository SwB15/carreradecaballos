package Model;

import java.time.LocalDate;

/**
 * Representa un abono o pago realizado para una apuesta específica.
 */
public class Abonos_Model {

    private int idAbono;
    private LocalDate fecha; // Se usa LocalDate para un manejo de fechas robusto.
    private Integer monto;
    private String origen;
    private Integer fk_apuestas;  // Se mantiene la nomenclatura para claves foráneas.
    private Integer fk_apostadores; // Se mantiene la nomenclatura para claves foráneas.

    // Asumo que tienes los getters y setters para todos estos campos.
    public Abonos_Model() {
    }

    public Abonos_Model(int idAbono, LocalDate fecha, Integer monto, String origen, Integer fk_apuestas, Integer fk_apostadores) {
        this.idAbono = idAbono;
        this.fecha = fecha;
        this.monto = monto;
        this.origen = origen;
        this.fk_apuestas = fk_apuestas;
        this.fk_apostadores = fk_apostadores;
    }

    public int getIdAbono() {
        return idAbono;
    }

    public void setIdAbono(int idAbono) {
        this.idAbono = idAbono;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getMonto() {
        return monto;
    }

    public void setMonto(Integer monto) {
        this.monto = monto;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public Integer getFk_apuestas() {
        return fk_apuestas;
    }

    public void setFk_apuestas(Integer fk_apuestas) {
        this.fk_apuestas = fk_apuestas;
    }

    public Integer getFk_apostadores() {
        return fk_apostadores;
    }

    public void setFk_apostadores(Integer fk_apostadores) {
        this.fk_apostadores = fk_apostadores;
    }
}
