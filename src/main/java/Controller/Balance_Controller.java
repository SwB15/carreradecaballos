package Controller;

import Model.ResultadosCarrera_DTO;
import Repository.Balance_Repository;
import Services.Balance_Services;
import Services.Exceptions.ServiceException;
import java.util.HashMap;
import java.util.List;

/**
 * Controlador que gestiona las interacciones entre la Vista y los Servicios
 * para Balances.
 */
public class Balance_Controller {

    private final Balance_Services services;

    public Balance_Controller() {
        // El controlador construye la cadena de dependencias.
        Balance_Repository repository = new Balance_Repository();
        this.services = new Balance_Services(repository);
    }

    /**
     * Pide al servicio los resultados procesados de una carrera.
     *
     * @param idCarrera El ID de la carrera a consultar.
     * @return Una lista de DTOs con los resultados listos para mostrar.
     * @throws ServiceException Si la consulta falla.
     */
    public List<ResultadosCarrera_DTO> obtenerResultadosCarrera(int idCarrera) throws ServiceException {
        return services.consultarResultados(idCarrera);
    }

    /**
     * Pide al servicio la lista de carreras finalizadas para un ComboBox.
     *
     * @return Un HashMap con los datos para el ComboBox.
     * @throws ServiceException Si la consulta falla.
     */
    public HashMap<String, Integer> obtenerCarrerasFinalizadasParaCombo() throws ServiceException {
        return services.getCarrerasFinalizadasParaCombo();
    }

    /**
     * Pide al servicio la comisión de una carrera.
     *
     * @param idCarrera El ID de la carrera.
     * @return El porcentaje de comisión.
     * @throws ServiceException Si la consulta falla.
     */
    public int obtenerComisionDeCarrera(int idCarrera) throws ServiceException {
        return services.consultarComisionDeCarrera(idCarrera);
    }
}
