
package Services;

import Model.Abonos_Model;
import Repository.Abonos_Repository;


/**
 *
 * @author SwichBlade15
 */
public class Abonos_Services {
private final Abonos_Repository Abonos_repository = new Abonos_Repository();

    public Abonos_Services() {
    }

    public boolean addAbonos(Abonos_Model model) {
        return Abonos_repository.insert(model);
    }

//    public boolean updateAbonos(Abonos_Model model) {
//        return Abonos_repository.update(model);
//    }
}
