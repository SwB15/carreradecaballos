package Model;

import java.time.LocalDate;

public class ReporteCarreras_DTO {

    private int id;
    private String nombre;
    private LocalDate fecha;
    private String ganador;
    private int comision;
    private String participantes; // Los participantes como un solo String

    public ReporteCarreras_DTO(int id, String nombre, LocalDate fecha, String ganador, int comision, String participantes) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.ganador = ganador;
        this.comision = comision;
        this.participantes = participantes;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getGanador() {
        return ganador;
    }

    public int getComision() {
        return comision;
    }

    public String getParticipantes() {
        return participantes;
    }

}
