package Services;

import Model.ResultadosCarrera_DTO;
import Repository.Balance_Repository;
import Services.Exceptions.ServiceException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Contiene la lógica de negocio para los cálculos de balance y resultados de
 * carreras.
 */
public class Balance_Services {

    private final Balance_Repository repository;

    public Balance_Services(Balance_Repository repository) {
        this.repository = repository;
    }

    /**
     * Consulta los resultados finales de una carrera. En este caso, la lógica
     * de cálculo compleja ya está en la consulta SQL del repositorio, por lo
     * que el servicio actúa principalmente como un intermediario que maneja
     * errores.
     *
     * @param idCarrera El ID de la carrera a consultar.
     * @return Una lista de DTOs con los resultados de la carrera.
     * @throws ServiceException Si ocurre un error de base de datos.
     */
    public List<ResultadosCarrera_DTO> consultarResultados(int idCarrera) throws ServiceException {
        try {
            return repository.findResultadosPorCarrera(idCarrera);
        } catch (SQLException e) {
            throw new ServiceException("Error al consultar los resultados de la carrera: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene las carreras finalizadas para poblar un ComboBox.
     *
     * @return Un HashMap con los nombres e IDs de las carreras.
     * @throws ServiceException Si ocurre un error de base de datos.
     */
    public HashMap<String, Integer> getCarrerasFinalizadasParaCombo() throws ServiceException {
        try {
            return repository.findCarrerasFinalizadas();
        } catch (SQLException e) {
            throw new ServiceException("Error al cargar las carreras finalizadas: " + e.getMessage(), e);
        }
    }

    /**
     * Consulta la comisión de una carrera específica.
     *
     * @param idCarrera El ID de la carrera.
     * @return El porcentaje de comisión.
     * @throws ServiceException Si ocurre un error de base de datos.
     */
    public int consultarComisionDeCarrera(int idCarrera) throws ServiceException {
        try {
            return repository.findComisionPorCarrera(idCarrera);
        } catch (SQLException e) {
            throw new ServiceException("Error al obtener la comisión de la carrera: " + e.getMessage(), e);
        }
    }
}
