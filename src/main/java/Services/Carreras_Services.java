package Services;

import Model.Carreras_Model;
import Repository.Carreras_Repository;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;

public class Carreras_Services {

    private final Carreras_Repository Carreras_repository = new Carreras_Repository();

    public Carreras_Services() {
    }

    public boolean addCarreras(Carreras_Model model) {
        return Carreras_repository.insert(model);
    }

    public boolean updateCarreras(Carreras_Model model) {
        return Carreras_repository.update(model);
    }

    public int getMaxCodigo() {
        return Carreras_repository.getMaxCodigo();
    }

    public DefaultTableModel showCarreras(String search, String fase, String stateFilter) {
        return Carreras_repository.showCarreras(search, fase, stateFilter);
    }

    public DefaultTableModel showCarrerasInPrincipal(String search, String statusFilter) {
        return Carreras_repository.showCarrerasInPrincipal(search, statusFilter);
    }

    public DefaultTableModel showCaballosInCarreras(String search) {
        return Carreras_repository.showCaballosInCarreras(search);
    }

    public HashMap<String, String> getCaballosPorCarrera(int idcarrera) {
        return Carreras_repository.getCaballosPorCarrera(idcarrera);
    }
}
