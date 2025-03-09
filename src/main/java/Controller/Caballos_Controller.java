package Controller;

import Model.Caballos_Model;
import Services.Caballos_Services;
import javax.swing.table.DefaultTableModel;

public class Caballos_Controller {

    private final Caballos_Services services = new Caballos_Services();
    private final Caballos_Model model = new Caballos_Model();

    public Caballos_Controller() {
    }

    public boolean createCaballos(String Caballos, String Jinete, String observacion, int foreignKey) {
        model.setCaballos(Caballos);
        model.setJinete(Jinete);
        model.setObservacion(observacion);
        model.setFk_estados(foreignKey);
        return services.addCaballos(model);
    }

    public boolean updateCaballos(int id, String Caballos, String Jinete, String observacion, int foreignKey) {
        model.setIdcaballos(id);
        model.setCaballos(Caballos);
        model.setJinete(Jinete);
        model.setObservacion(observacion);
        model.setFk_estados(foreignKey);
        return services.updateCaballos(model);
    }

    public DefaultTableModel showCaballos(String search, String stateFilter) {
        return services.showCaballos(search, stateFilter);
    }
    
    public int getMaxCodigo() {
        return services.getMaxCodigo();
    }
}
