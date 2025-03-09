package Services;

import Model.Apostadores_Model;
import Repository.Apostadores_Repository;
import javax.swing.table.DefaultTableModel;

public class Apostadores_Services {

    private final Apostadores_Repository Apostadores_repository = new Apostadores_Repository();

    public Apostadores_Services() {
    }

    public boolean addApostadores(Apostadores_Model model) {
        return Apostadores_repository.insert(model);
    }

    public boolean updateApostadores(Apostadores_Model model) {
        return Apostadores_repository.update(model);
    }

    public DefaultTableModel showApostadores(String search, String stateFilter) {
        return Apostadores_repository.showApostadores(search, stateFilter);
    }
    
    public DefaultTableModel showHistorial(int id) {
        return Apostadores_repository.showHistorial(id);
    }
    
    public int getMaxCodigo() {
        return Apostadores_repository.getMaxCodigo();
    }
}
