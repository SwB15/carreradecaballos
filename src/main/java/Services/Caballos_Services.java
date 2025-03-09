package Services;

import Model.Caballos_Model;
import Repository.Caballos_Repository;
import javax.swing.table.DefaultTableModel;

public class Caballos_Services {

    private final Caballos_Repository Caballos_repository = new Caballos_Repository();

    public Caballos_Services() {
    }

    public boolean addCaballos(Caballos_Model model) {
        return Caballos_repository.insert(model);
    }

    public boolean updateCaballos(Caballos_Model model) {
        return Caballos_repository.update(model);
    }

    public DefaultTableModel showCaballos(String search, String stateFilter) {
        return Caballos_repository.showCaballos(search, stateFilter);
    }
    
    public int getMaxCodigo(){
        return Caballos_repository.getMaxCodigo();
    }
}
