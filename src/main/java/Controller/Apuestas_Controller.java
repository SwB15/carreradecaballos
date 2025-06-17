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

    public boolean createApuestas(String apuesta, int monto, String fecha, String fechalimite, String observacion, String saldousado, int foreignKey, int foreignKey2, int foreignKey3, int foreignKey4, int foreignKey5) {
        model.setApuesta(apuesta);
        model.setMonto(monto);
        model.setFecha(fecha);
        model.setFechalimite(fechalimite);
        model.setObservacion(observacion);
        model.setSaldousado(saldousado);
        model.setFk_carreras(foreignKey);
        model.setFk_caballos(foreignKey2);
        model.setFk_apostadores(foreignKey3);
        model.setFk_estadopago(foreignKey4);
        model.setFk_estados(foreignKey5);
        return services.addApuestas(model);
    }

    public boolean updateApuestas(int id, String apuesta, int monto, String fecha, String fechalimite, String observacion, String saldousado, int foreignKey, int foreignKey2, int foreignKey3, int foreignKey4, int foreignKey5) {
        model.setIdapuestas(id);
        model.setApuesta(apuesta);
        model.setMonto(monto);
        model.setFecha(fecha);
        model.setFechalimite(fechalimite);
        model.setObservacion(observacion);
        model.setSaldousado(saldousado);
        model.setFk_carreras(foreignKey);
        model.setFk_caballos(foreignKey2);
        model.setFk_apostadores(foreignKey3);
        model.setFk_estadopago(foreignKey4);
        model.setFk_estados(foreignKey5);
        return services.updateApuestas(model);
    }

    public boolean disableApuestas(int id, int foreignKey, int foreignKey2) {
        model.setIdapuestas(id);
        model.setFk_estadopago(foreignKey);
        model.setFk_estados(foreignKey2);
        return services.disableApuestas(model);
    }

    public boolean delete(int id) {
        model.setIdapuestas(id);
        return services.delete(model);
    }

    public DefaultTableModel showApuestas(String search, String stateFilter, Date startDate, Date endDate) {
        return services.showApuestas(search, stateFilter, startDate, endDate);
    }

    public int getMaxCodigo() {
        return services.getMaxCodigo();
    }
    
    public int getMaxid() {
        return services.getMaxid();
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
