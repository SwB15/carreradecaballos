package Controller;

import Model.Carreras_Model;
import Services.Carreras_Services;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;

public class Carreras_Controller {

    private final Carreras_Services services = new Carreras_Services();
    private final Carreras_Model model = new Carreras_Model();

    public Carreras_Controller() {
    }

    public boolean createCarreras(String nombre, String lugar, String Fecha, Integer idganador, String observacion, int foreignKey) {
        model.setNombre(nombre);
        model.setLugar(lugar);
        model.setFecha(Fecha);
        model.setIdganador(idganador);
        model.setObservacion(observacion);
        model.setFk_estados(foreignKey);
        return services.addCarreras(model);
    }

    public boolean updateCarreras(int id, String nombre, String lugar, String Fecha, Integer idganador, String observacion, int foreignKey) {
        model.setIdcarreras(id);
        model.setNombre(nombre);
        model.setLugar(lugar);
        model.setFecha(Fecha);
        model.setIdganador(idganador);
        model.setObservacion(observacion);
        model.setFk_estados(foreignKey);
        return services.updateCarreras(model);
    }

    public int getMaxCodigo() {
        return services.getMaxCodigo();
    }
    
    public int getIdganador(Integer idcarreras) {
        return services.getIdganador(idcarreras);
    }

    public DefaultTableModel showCarreras(String search, String fase, String stateFilter) {
        return services.showCarreras(search, fase, stateFilter);
    }

    public DefaultTableModel showCarrerasInPrincipal(String search, String stateFilter, String statusFilter, String datefrom, String dateto) {
        return services.showCarrerasInPrincipal(search, stateFilter, statusFilter, datefrom, dateto);
    }

    public DefaultTableModel showCarrerasForEdit(String search, String statusFilter) {
        return services.showCarrerasForEdit(search, statusFilter);
    }

    public DefaultTableModel showCaballosInCarreras(String search) {
        return services.showCaballosInCarreras(search);
    }

    public HashMap<String, String> getCaballosPorCarrera(int idcarrera) {
        return services.getCaballosPorCarrera(idcarrera);
    }
}
