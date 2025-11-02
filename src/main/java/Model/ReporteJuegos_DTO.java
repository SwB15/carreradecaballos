package Model;

import java.time.LocalDate;

public class ReporteJuegos_DTO {

    private String nombre;
    private LocalDate fecha;
    private String observacion;
    private String estado;
    private String ganador;

    public ReporteJuegos_DTO(String nombre, LocalDate fecha, String observacion, String estado, String ganador) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.observacion = observacion;
        this.estado = estado;
        this.ganador = ganador;
    }

    // JasperReports necesita los Getters públicos
    public String getNombre() {
        return nombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public String getEstado() {
        return estado;
    }

    public String getGanador() {
        return ganador;
    }
}
