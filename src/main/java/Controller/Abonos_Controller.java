
package Controller;

import Model.Abonos_Model;
import Services.Abonos_Services;


/**
 *
 * @author SwichBlade15
 */
public class Abonos_Controller {
private final Abonos_Services services = new Abonos_Services();
    private final Abonos_Model model = new Abonos_Model();

    public Abonos_Controller() {
    }

    public boolean createAbonos(String fecha, Integer monto, String origen, Integer foreignKey, Integer foreignKey2) {
        model.setFecha(fecha);
        model.setMonto(monto);
        model.setOrigen(origen);
        model.setFk_apuestas(foreignKey);
        model.setFk_apostadores(foreignKey2);
        return services.addAbonos(model);
    }

//    public boolean updateAbonos(int id, String fecha, Integer monto, String origen, Integer foreignKey, Integer foreignKey2) {
//        model.setIdabonos(id);
//        model.setFecha(fecha);
//        model.setMonto(monto);
//        model.setOrigen(origen);
//        model.setFk_apuestas(foreignKey);
//        model.setFk_apostadores(foreignKey2);
//        return services.updateAbonos(model);
//    }
}
