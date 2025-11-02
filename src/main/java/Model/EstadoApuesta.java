package Model;

import java.awt.Color;

public enum EstadoApuesta {
    // Se definen los posibles estados y su color asociado.
    PAGADO(new Color(76, 175, 80, 180)), // Verde
    PENDIENTE(new Color(255, 235, 59, 180)), // Amarillo
    VENCIDO(new Color(255, 87, 87, 180)), // Rojo
    ARCHIVADO(Color.WHITE), // Blanco para apuestas antiguas
    ERROR(new Color(255, 235, 59, 180));      // Amarillo para datos inválidos

    private final Color color;

    EstadoApuesta(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
