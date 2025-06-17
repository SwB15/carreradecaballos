package Controller;

import Model.Detallecarreras_Model;
import Services.Detallecarreras_Services;

public class Detallecarreras_Controller {

    private final Detallecarreras_Services services = new Detallecarreras_Services();
    private final Detallecarreras_Model model = new Detallecarreras_Model();

    public Detallecarreras_Controller() {
    }

    public boolean createDetallecarreras(int foreignKey, int foreignKey2) {
        model.setFk_carreras(foreignKey);
        model.setFk_caballos(foreignKey2);
        return services.addDetallecarreras(model);
    }

    public boolean updateDetallecarreras(int id, int foreignKey, int foreignKey2) {
        model.setIddetallecarreras(id);
        model.setFk_carreras(foreignKey);
        model.setFk_caballos(foreignKey2);
        return services.updateDetallecarreras(model);
    }

    public boolean deleteDetallecarreras(int id) {
        model.setFk_carreras(id);
        return services.deleteDetallecarreras(model);
    }
}
