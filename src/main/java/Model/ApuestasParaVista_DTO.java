package Model;

import java.time.LocalDate;

/**
 * DTO para transportar los datos de una apuesta con información enriquecida
 * (nombres en lugar de IDs) para ser mostrada en una tabla o vista.
 */
public class ApuestasParaVista_DTO {

    private final Apuestas_Model apuesta; // El objeto de apuesta original
    private final String nombreApostador;
    private final int saldoApostador;
    private final String nombreCarrera;
    private final String nombreCaballo;
    private final String nombreEstadoApuesta;
    private final String nombreEstadoPago;

    public ApuestasParaVista_DTO(Apuestas_Model apuesta, String nombreApostador, int saldoApostador, String nombreCarrera, String nombreCaballo, String nombreEstadoApuesta, String nombreEstadoPago) {
        this.apuesta = apuesta;
        this.nombreApostador = nombreApostador;
        this.saldoApostador = saldoApostador;
        this.nombreCarrera = nombreCarrera;
        this.nombreCaballo = nombreCaballo;
        this.nombreEstadoApuesta = nombreEstadoApuesta;
        this.nombreEstadoPago = nombreEstadoPago;
    }

    public Apuestas_Model getApuesta() {
        return apuesta;
    }

    public String getNombreApostador() {
        return nombreApostador;
    }

    public int getSaldoApostador() {
        return saldoApostador;
    }

    public String getNombreCarrera() {
        return nombreCarrera;
    }

    public String getNombreCaballo() {
        return nombreCaballo;
    }

    public String getNombreEstadoApuesta() {
        return nombreEstadoApuesta;
    }

    public String getNombreEstadoPago() {
        return nombreEstadoPago;
    }
}
