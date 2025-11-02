package Controller;

import Model.Caballos_Model;
import Repository.Caballos_Repository;
import Services.Caballos_Services;
import Services.Exceptions.ServiceException;
import java.util.List;

/**
 * Controlador que gestiona las interacciones entre la Vista y los Servicios
 * para Caballos.
 */
public class Caballos_Controller {

    private final Caballos_Services services;

    public Caballos_Controller() {
        // El controlador construye la cadena de dependencias.
        Caballos_Repository repository = new Caballos_Repository();
        this.services = new Caballos_Services(repository);
    }

    /**
     * Procesa la solicitud de la Vista para crear un nuevo caballo.
     *
     * @param nombre El nombre del caballo.
     * @param jinete El nombre del jinete.
     * @param observacion Notas adicionales.
     * @param fk_estados El estado inicial.
     * @throws ServiceException Si ocurre un error durante la creación.
     */
    public void crearCaballo(String nombre, String jinete, String observacion, int fk_estados) throws ServiceException {
        Caballos_Model nuevoCaballo = new Caballos_Model(0, nombre, jinete, observacion, fk_estados);
        services.crearCaballo(nuevoCaballo);
    }

    /**
     * Procesa la solicitud de la Vista para actualizar un caballo.
     *
     * @param id El ID del caballo a modificar.
     * @param nombre El nuevo nombre.
     * @param jinete El nuevo jinete.
     * @param observacion La nueva observación.
     * @param fk_estados El nuevo estado.
     * @throws ServiceException Si ocurre un error durante la actualización.
     */
    public void actualizarCaballo(int id, String nombre, String jinete, String observacion, int fk_estados) throws ServiceException {
        Caballos_Model caballoActualizado = new Caballos_Model(id, nombre, jinete, observacion, fk_estados);
        services.actualizarCaballo(caballoActualizado);
    }

    /**
     * Pide al servicio la lista de caballos para mostrar en la vista.
     *
     * @param search Término de búsqueda.
     * @param stateFilter Filtro de estado.
     * @return Una lista de objetos Caballos_Model.
     * @throws ServiceException Si la consulta falla.
     */
    public List<Caballos_Model> listarCaballos(String search, String stateFilter) throws ServiceException {
        return services.consultarCaballos(search, stateFilter);
    }
}
