package Model;

import java.time.LocalDateTime;

public class Backup_DTO {
    private final String nombre;
    private final LocalDateTime fecha;
    private final double tamanoMb;

    public Backup_DTO(String nombre, LocalDateTime fecha, double tamanoMb) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.tamanoMb = tamanoMb;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public double getTamanoMb() {
        return tamanoMb;
    }
}