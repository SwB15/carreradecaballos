package Services;

import Model.Caballos_Model;
import Repository.Caballos_Repository;
import Services.Exceptions.ServiceException;
import java.sql.SQLException;
import java.util.List;

/**
 * Contiene la lógica de negocio relacionada con los Caballos.
 */
public class Caballos_Services {

    private final Caballos_Repository repository;

    public Caballos_Services(Caballos_Repository repository) {
        this.repository = repository;
    }

    /**
     * Crea un nuevo caballo.
     *
     * @param model El modelo con los datos del nuevo caballo.
     * @throws ServiceException si ocurre un error al guardar.
     */
    public void crearCaballo(Caballos_Model model) throws ServiceException {
        try {
            // Aquí se podría añadir lógica de negocio, como validar que el nombre no esté duplicado.
            repository.insert(model);
        } catch (SQLException e) {
            throw new ServiceException("Error al crear el caballo: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza los datos de un caballo existente.
     *
     * @param model El modelo con los datos a actualizar.
     * @throws ServiceException si ocurre un error al actualizar.
     */
    public void actualizarCaballo(Caballos_Model model) throws ServiceException {
        try {
            repository.update(model);
        } catch (SQLException e) {
            throw new ServiceException("Error al actualizar el caballo: " + e.getMessage(), e);
        }
    }

    /**
     * Consulta la lista de caballos.
     *
     * @param search Término de búsqueda.
     * @param stateFilter Filtro de estado ('activo', 'inactivo').
     * @return Una lista de objetos Caballos_Model.
     * @throws ServiceException si ocurre un error en la consulta.
     */
    public List<Caballos_Model> consultarCaballos(String search, String stateFilter) throws ServiceException {
        try {
            return repository.findCaballos(search, stateFilter);
        } catch (SQLException e) {
            throw new ServiceException("Error al consultar los caballos: " + e.getMessage(), e);
        }
    }
}
