package Services;

import Model.Apuestas_Model;
import Model.ApuestasParaVista_DTO;
import Model.EstadoApuesta;
import Model.Vencidos_Model;
import Repository.Apuestas_Repository;
import Services.Exceptions.ServiceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

/**
 * Contiene la lógica de negocio relacionada con las Apuestas.
 */
public class Apuestas_Services {

    private final Apuestas_Repository repository;

    // Se usa Inyección de Dependencias para facilitar las pruebas.
    public Apuestas_Services(Apuestas_Repository repository) {
        this.repository = repository;
    }

    /**
     * Crea una nueva apuesta en la base de datos.
     *
     * @param model El objeto Apuestas_Model con los datos a insertar.
     * @throws ServiceException Si ocurre un error de base de datos.
     */
    public void crearApuesta(Apuestas_Model model) throws ServiceException {
        try {
            repository.insert(model);
        } catch (SQLException e) {
            throw new ServiceException("Error al crear la apuesta: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza una apuesta existente.
     *
     * @param model El objeto Apuestas_Model con los datos a actualizar.
     * @throws ServiceException Si ocurre un error de base de datos.
     */
    public void actualizarApuesta(Apuestas_Model model) throws ServiceException {
        try {
            repository.update(model);
        } catch (SQLException e) {
            throw new ServiceException("Error al actualizar la apuesta: " + e.getMessage(), e);
        }
    }

    /**
     * Consulta las apuestas para una carrera específica, listas para ser
     * mostradas en la vista.
     *
     * @param idCarrera El ID de la carrera a consultar.
     * @return Una lista de DTOs con la información de las apuestas.
     * @throws ServiceException Si ocurre un error de base de datos.
     */
    public List<ApuestasParaVista_DTO> consultarApuestasParaVista(int idCarrera) throws ServiceException {
        try {
            return repository.findApuestasParaVista(idCarrera);
        } catch (SQLException e) {
            throw new ServiceException("Error al consultar las apuestas: " + e.getMessage(), e);
        }
    }

    /**
     * Determina el estado de una apuesta basándose en sus montos y fecha
     * límite. Esta es una función de lógica de negocio pura.
     *
     * @param apuesta El objeto Apuestas_Model a evaluar.
     * @return El Enum EstadoApuesta correspondiente.
     */
    public EstadoApuesta determinarEstado(Apuestas_Model apuesta) {
        // El código ahora usa LocalDate, es más limpio y seguro.
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaLimite = apuesta.getFechaLimite();

        if (fechaLimite == null) {
            return EstadoApuesta.ERROR; // No se puede determinar el estado sin fecha límite.
        }

        long diasPasados = ChronoUnit.DAYS.between(fechaLimite, fechaActual);

        if (diasPasados > 7) {
            return EstadoApuesta.ARCHIVADO;
        }
        if (fechaLimite.isBefore(fechaActual) && apuesta.getAbonado() < apuesta.getMonto()) {
            return EstadoApuesta.VENCIDO;
        }
        if (apuesta.getAbonado() < apuesta.getMonto()) {
            return EstadoApuesta.PENDIENTE;
        }

        return EstadoApuesta.PAGADO;
    }

    /**
     * Obtiene los datos necesarios para poblar los ComboBox de la UI.
     *
     * @param tableName El nombre de la tabla (ej. "apostadores", "carreras").
     * @param idColumn El nombre de la columna del ID.
     * @param nameColumn El nombre de la columna del nombre/descripción.
     * @return Un HashMap para poblar un ComboBox.
     * @throws ServiceException Si ocurre un error de base de datos.
     */
    public HashMap<String, Integer> getDatosParaCombo(String tableName, String idColumn, String nameColumn) throws ServiceException {
        try {
            return repository.fillCombos(tableName, idColumn, nameColumn);
        } catch (SQLException e) {
            throw new ServiceException("Error al cargar datos para ComboBox de " + tableName + ": " + e.getMessage(), e);
        }
    }

    /**
     * Consulta todas las apuestas vencidas en el sistema.
     *
     * @return Una lista de DTOs con la información de las apuestas vencidas.
     * @throws ServiceException si ocurre un error en la consulta.
     */
    public List<Vencidos_Model> consultarApuestasVencidas() throws ServiceException {
        try {
            return repository.findVencidos();
        } catch (SQLException e) {
            throw new ServiceException("Error al consultar las apuestas vencidas: " + e.getMessage(), e);
        }
    }    
}
