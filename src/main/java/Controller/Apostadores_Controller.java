package Controller;

import Model.ApostadorParaVista_DTO;
import Model.Apostadores_Model;
import Model.ReportesHistorialApostador_Model;
import Model.Resultado_Pendientes;
import Repository.Apostadores_Repository;
import Services.Apostadores_Services;
import Services.Exceptions.ServiceException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador que gestiona las interacciones entre la Vista y los Servicios
 * para Apostadores.
 */
public class Apostadores_Controller {

    private final Apostadores_Services services;

    public Apostadores_Controller() {
        // El controlador es responsable de construir la cadena de dependencias.
        Apostadores_Repository repository = new Apostadores_Repository();
        this.services = new Apostadores_Services(repository);
    }

    /**
     * Pide al servicio la lista de deudas pendientes para un apostador
     * específico.
     *
     * @param idApostador El ID del apostador.
     * @return Un objeto Resultado_Pendientes con la lista de deudas y el total.
     * @throws ServiceException Si la consulta falla.
     */
    public Resultado_Pendientes consultarDeudas(int idApostador) throws ServiceException {
        System.out.println("\n--- CONTROLLER (Apostadores): Petición para consultar deudas del apostador ID: " + idApostador + " ---");
        System.out.println("1. Llamando al servicio...");

        Resultado_Pendientes resultado = services.consultarDeudas(idApostador);

        System.out.println("4. Controlador recibió " + resultado.getDetalles().size() + " deudas del servicio.");
        System.out.println("--- CONTROLLER (Apostadores): Finaliza consulta de deudas ---");
        return resultado;
    }

    /**
     * Procesa la solicitud de la Vista para crear un nuevo apostador.
     *
     * @param cedula La cédula del apostador.
     * @param nombre El nombre del apostador.
     * @param observacion Notas adicionales.
     * @param fk_estados El estado inicial.
     * @throws ServiceException Si ocurre un error durante la creación.
     */
    public void crearApostador(String cedula, String nombre, String observacion, int fk_estados) throws ServiceException {
        Apostadores_Model nuevoApostador = new Apostadores_Model(0, cedula, nombre, 0, observacion, fk_estados);
        services.crearApostador(nuevoApostador);
    }

    /**
     * Procesa la solicitud de la Vista para actualizar un apostador.
     *
     * @param id El ID del apostador a modificar.
     * @param cedula La nueva cédula.
     * @param nombre El nuevo nombre.
     * @param observacion La nueva observación.
     * @param fk_estados El nuevo estado.
     * @throws ServiceException Si ocurre un error durante la actualización.
     */
    public void actualizarApostador(int id, String cedula, String nombre, String observacion, int fk_estados) throws ServiceException {
        Apostadores_Model apostadorActualizado = new Apostadores_Model(id, cedula, nombre, 0, observacion, fk_estados);
        services.actualizarApostador(apostadorActualizado);
    }

    /**
     * Pide al servicio la lista de apostadores para mostrar en la vista
     * principal.
     *
     * @param search Término de búsqueda.
     * @param stateFilter Filtro de estado.
     * @return Una lista de DTOs con la información de los apostadores.
     * @throws ServiceException Si la consulta falla.
     */
    public List<ApostadorParaVista_DTO> listarApostadores(String search, String stateFilter) throws ServiceException {
        return services.consultarApostadoresParaVista(search, stateFilter);
    }

    /**
     * Pide al servicio el historial procesado de un apostador, ahora con filtro
     * de fecha.
     *
     * @param idApostador El ID del apostador.
     * @param desde La fecha de inicio del filtro (puede ser null).
     * @param hasta La fecha de fin del filtro (puede ser null).
     * @return Una lista de DTOs con el historial listo para mostrar.
     * @throws ServiceException Si la consulta falla.
     */
    public List<ReportesHistorialApostador_Model> obtenerHistorial(int idApostador, LocalDate desde, LocalDate hasta) throws ServiceException {
        // Se llama al método del servicio, ahora pasando las fechas.
        return services.consultarHistorial(idApostador, desde, hasta);
    }

    /**
     * Procesa la solicitud de la Vista para añadir o restar saldo a un
     * apostador.
     *
     * @param monto El monto a añadir (puede ser negativo para restar).
     * @param idApostador El ID del apostador a modificar.
     * @throws ServiceException Si ocurre un error durante la actualización.
     */
    public void addSaldo(int monto, int idApostador) throws ServiceException {
        // Llama al servicio para que maneje la lógica de negocio.
        services.actualizarSaldo(idApostador, monto);
    }
}
