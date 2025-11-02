package Services;

import Model.Estados_Model;
import Repository.Estados_Repository;
import Services.Exceptions.ServiceException;
import java.sql.SQLException;
import java.util.List;

public class Estados_Services {

    private final Estados_Repository repository;

    public Estados_Services(Estados_Repository repository) {
        this.repository = repository;
    }

    public List<Estados_Model> consultarTodos() throws ServiceException {
        try {
            return repository.findAll();
        } catch (SQLException e) {
            throw new ServiceException("Error al consultar los estados: " + e.getMessage(), e);
        }
    }
}
