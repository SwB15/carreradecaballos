package Services;

import Repository.Balance_Repository;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Balance_Services {

    Balance_Repository repository = new Balance_Repository();
    
    public DefaultTableModel showResultados(int idcarreras) {
        return repository.showResultados(idcarreras);
    }

    public HashMap<String, String> fillCarrerasCombobox() {
        return repository.fillCarrerasCombos();
    }
}
