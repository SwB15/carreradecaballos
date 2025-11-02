package Model;

import java.time.LocalDate;

public class HistorialApuesta_DTO {
    private final int idApuesta;
    private final String nombreCarrera;
    private final LocalDate fecha;
    private final String nombreCaballo;
    private final int idCaballoApostado;
    private final Integer idCaballoGanador; // Can be null if race is pending
    private final int montoApostado;
    private final int comisionCarrera;
    private final long poolPerdedores; // Total bet by losers
    private final long totalApostadoAlGanador; // Total bet on the winning horse

    // Constructor, Getters...
    public HistorialApuesta_DTO(int idApuesta, String nombreCarrera, LocalDate fecha, String nombreCaballo, int idCaballoApostado, Integer idCaballoGanador, int montoApostado, int comisionCarrera, long poolPerdedores, long totalApostadoAlGanador) {
        this.idApuesta = idApuesta;
        this.nombreCarrera = nombreCarrera;
        this.fecha = fecha;
        this.nombreCaballo = nombreCaballo;
        this.idCaballoApostado = idCaballoApostado;
        this.idCaballoGanador = idCaballoGanador;
        this.montoApostado = montoApostado;
        this.comisionCarrera = comisionCarrera;
        this.poolPerdedores = poolPerdedores;
        this.totalApostadoAlGanador = totalApostadoAlGanador;
    }

    public int getIdApuesta() {
        return idApuesta;
    }

    public String getNombreCarrera() {
        return nombreCarrera;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getNombreCaballo() {
        return nombreCaballo;
    }

    public int getIdCaballoApostado() {
        return idCaballoApostado;
    }

    public Integer getIdCaballoGanador() {
        return idCaballoGanador;
    }

    public int getMontoApostado() {
        return montoApostado;
    }

    public int getComisionCarrera() {
        return comisionCarrera;
    }

    public long getPoolPerdedores() {
        return poolPerdedores;
    }

    public long getTotalApostadoAlGanador() {
        return totalApostadoAlGanador;
    }
}