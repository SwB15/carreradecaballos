package Model;

public class ApostadorParaVista_DTO {
    private final Apostadores_Model apostador;
    private final String estadoDeuda; // "ROJO", "AMARILLO", "VERDE", or null

    public ApostadorParaVista_DTO(Apostadores_Model apostador, String estadoDeuda) {
        this.apostador = apostador;
        this.estadoDeuda = estadoDeuda;
    }

    public Apostadores_Model getApostador() { return apostador; }
    public String getEstadoDeuda() { return estadoDeuda; }
}