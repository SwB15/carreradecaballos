package Services;

import Model.Detallecarreras_Model;
import Repository.Detallecarreras_Repository;

public class Detallecarreras_Services {

    private final Detallecarreras_Repository Detallecarreras_repository = new Detallecarreras_Repository();

    public Detallecarreras_Services() {
    }

    public boolean addDetallecarreras(Detallecarreras_Model model) {
        return Detallecarreras_repository.insert(model);
    }

    public boolean updateDetallecarreras(Detallecarreras_Model model) {
        return Detallecarreras_repository.update(model);
    }
}
