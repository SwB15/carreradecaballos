package Services;

import Model.Apuestas_Model;
import Repository.Apuestas_Repository;
import java.util.Date;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;

public class Apuestas_Services {

    private final Apuestas_Repository Apuestas_repository = new Apuestas_Repository();

    public Apuestas_Services() {
    }

    public boolean addApuestas(Apuestas_Model model) {
        return Apuestas_repository.insert(model);
    }

    public boolean updateApuestas(Apuestas_Model model) {
        return Apuestas_repository.update(model);
    }

    public boolean disableApuestas(Apuestas_Model model) {
        return Apuestas_repository.disable(model);
    }

    public boolean delete(Apuestas_Model model) {
        return Apuestas_repository.delete(model);
    }

    public DefaultTableModel showApuestas(String search, String stateFilter, Date startDate, Date endDate) {
        return Apuestas_repository.showApuestas(search, stateFilter, startDate, endDate);
    }

    public int getMaxCodigo() {
        return Apuestas_repository.getMaxCodigo();
    }
    
    public int getMaxid() {
        return Apuestas_repository.getMaxid();
    }

    public HashMap<String, String> fillApostadoresCombobox() {
        return Apuestas_repository.fillApostadoresCombos();
    }

    public HashMap<String, String> fillCarrerasCombobox() {
        return Apuestas_repository.fillCarrerasCombos();
    }

    public HashMap<String, String> fillCaballosCombobox(int id) {
        return Apuestas_repository.fillCaballosCombos(id);
    }
}
