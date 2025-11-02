package Model;

import java.util.List;

/**
 * DTO (Data Transfer Object) que encapsula el resultado de una consulta de
 * saldos pendientes. Contiene tanto la lista detallada de deudas como el monto
 * total pendiente.
 */
public class Resultado_Pendientes {

    /**
     * Una lista de objetos Detalle_Pendientes, cada uno representando una deuda
     * individual.
     */
    private final List<Detalle_Pendientes> detalles;

    /**
     * La suma total de los montos pendientes de todos los detalles en la lista.
     */
    private final int totalPendiente;

    /**
     * Constructor para crear el objeto de resultado.
     *
     * @param detalles La lista de deudas detalladas.
     * @param totalPendiente El monto total de todas las deudas.
     */
    public Resultado_Pendientes(List<Detalle_Pendientes> detalles, int totalPendiente) {
        this.detalles = detalles;
        this.totalPendiente = totalPendiente;
    }

    // --- Getters ---
    public List<Detalle_Pendientes> getDetalles() {
        return detalles;
    }

    public int getTotalPendiente() {
        return totalPendiente;
    }
}
