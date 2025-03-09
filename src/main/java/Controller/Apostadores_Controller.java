package Controller;

import Model.Apostadores_Model;
import Services.Apostadores_Services;
import javax.swing.table.DefaultTableModel;

public class Apostadores_Controller {

    private final Apostadores_Services services = new Apostadores_Services();
    private final Apostadores_Model model = new Apostadores_Model();

    public Apostadores_Controller() {
    }

    public boolean createApostadores(String cedula, String nombre, String observacion, int foreignKey) {
        model.setCedula(cedula);
        model.setNombre(nombre);
        model.setObservacion(observacion);
        model.setFk_estados(foreignKey);
        return services.addApostadores(model);
    }

    public boolean updateApostadores(int id, String cedula, String nombre, String observacion, int foreignKey) {
        model.setIdapostadores(id);
        model.setCedula(cedula);
        model.setNombre(nombre);
        model.setObservacion(observacion);
        model.setFk_estados(foreignKey);
        return services.updateApostadores(model);
    }

    public DefaultTableModel showApostadores(String search, String stateFilter) {
        return services.showApostadores(search, stateFilter);
    }
    
    public DefaultTableModel showHistorial(int id) {
        return services.showHistorial(id);
    }
    
    public int getMaxCodigo() {
        return services.getMaxCodigo();
    }
}
