package Controller;

import Model.Apuestas_Model;
import Services.Apuestas_Services;
import java.util.Date;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;

public class Apuestas_Controller {

    private final Apuestas_Services services = new Apuestas_Services();
    private final Apuestas_Model model = new Apuestas_Model();

    public Apuestas_Controller() {
    }

    public boolean createApuestas(String apuesta, int monto, int abonado, String fecha, String fechalimite, String observacion, int foreignKey, int foreignKey2, int foreignKey3, int foreignKey4) {
        model.setApuesta(apuesta);
        model.setMonto(monto);
        model.setAbonado(abonado);
        model.setFecha(fecha);
        model.setFechalimite(fechalimite);
        model.setObservacion(observacion);
        model.setFk_carreras(foreignKey);
        model.setFk_caballos(foreignKey2);
        model.setFk_apostadores(foreignKey3);
        model.setFk_estados(foreignKey4);
        return services.addApuestas(model);
    }

    public boolean updateApuestas(int id, String apuesta, int monto, int abonado, String fecha, String fechalimite, String observacion, int foreignKey, int foreignKey2, int foreignKey3, int foreignKey4) {
        model.setIdapuestas(id);
        model.setApuesta(apuesta);
        model.setMonto(monto);
        model.setAbonado(abonado);
        model.setFecha(fecha);
        model.setFechalimite(fechalimite);
        model.setObservacion(observacion);
        model.setFk_carreras(foreignKey);
        model.setFk_caballos(foreignKey2);
        model.setFk_apostadores(foreignKey3);
        model.setFk_estados(foreignKey4);
        return services.updateApuestas(model);
    }

    public DefaultTableModel showApuestas(String search, String stateFilter, Date startDate, Date endDate) {
        return services.showApuestas(search, stateFilter, startDate, endDate);
    }

    public int getMaxCodigo() {
        return services.getMaxCodigo();
    }

    public HashMap<String, String> fillApostadoresCombobox() {
        return services.fillApostadoresCombobox();
    }

    public HashMap<String, String> fillCarrerasCombobox() {
        return services.fillCarrerasCombobox();
    }

    public HashMap<String, String> fillCaballosCombobox(int id) {
        return services.fillCaballosCombobox(id);
    }
}
