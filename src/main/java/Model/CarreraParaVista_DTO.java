package Model;

import java.util.List;

/**
 * DTO para transportar los datos de una carrera con información enriquecida,
 * incluyendo la lista de caballos que participan en ella.
 */
public class CarreraParaVista_DTO {

    private final Carreras_Model carrera;
    private final String nombreEstado;
    private final String nombreGanador;
    private final List<Caballos_Model> participantes;
    private final String observacion;

    public CarreraParaVista_DTO(Carreras_Model carrera, String nombreEstado, String nombreGanador, List<Caballos_Model> participantes, String observacion) {
        this.carrera = carrera;
        this.nombreEstado = nombreEstado;
        this.nombreGanador = nombreGanador;
        this.participantes = participantes;
        this.observacion = observacion;
    }

    public Carreras_Model getCarrera() {
        return carrera;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public String getNombreGanador() {
        return nombreGanador;
    }

    public List<Caballos_Model> getParticipantes() {
        return participantes;
    }

    public String getObservacion() {
        return observacion;
    }
}
