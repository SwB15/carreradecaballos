
package Controller;

import Services.Balance_Services;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Balance_Controller {
    
    Balance_Services services = new Balance_Services();
    
    public DefaultTableModel showResultados(int idcarreras) {
        return services.showResultados(idcarreras);
    }
    
    public HashMap<String, String> fillCarrerasCombobox() {
        return services.fillCarrerasCombobox();
    }
}
