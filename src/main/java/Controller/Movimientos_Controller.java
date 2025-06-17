package Controller;

import Model.Movimientos_Model;
import Services.Movimientos_Services;
import java.sql.SQLException;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Movimientos_Controller {

    private final Movimientos_Services services = new Movimientos_Services();
    private final Movimientos_Model model = new Movimientos_Model();

    public Movimientos_Controller() {
    }

    public boolean create(String fecha, int monto, String observacion, Integer foreignKey, Integer foreignKey2, Integer foreignKey3, Integer foreignKey4) {
        model.setFecha(fecha);
        model.setMonto(monto);
        model.setObservacion(observacion);
        model.setFk_apostadores(foreignKey);
        model.setFk_apuestas(foreignKey2);
        model.setFk_tipomovimientos(foreignKey3);
        model.setFk_carreras(foreignKey4);
        return services.add(model);
    }

    public boolean update(int id, String fecha, int monto, String observacion, Integer foreignKey, Integer foreignKey2, Integer foreignKey3, Integer foreignKey4) {
        model.setFecha(fecha);
        model.setMonto(monto);
        model.setObservacion(observacion);
        model.setFk_apostadores(foreignKey);
        model.setFk_apuestas(foreignKey2);
        model.setFk_tipomovimientos(foreignKey3);
        model.setFk_carreras(foreignKey4);
        return services.update(model);
    }

    public int deleteMovimientosporcarrera(int raceId) throws SQLException {
        return services.deleteMovimientosporcarrera(raceId);
    }

    public DefaultTableModel showMovimientos(String search, String stateFilter, String datefrom, String dateto) {
        return services.showMovimientos(search, stateFilter, datefrom, dateto);
    }

    public List<Movimientos_Model> calcularMovimientosPorCarrera(int raceId, String fecha) throws SQLException {
        return services.calcularMovimientosPorCarrera(raceId, fecha);
    }

    public List<Movimientos_Model> getMovimientosPorCarrera(int idCarrera) throws SQLException {
        return services.getMovimientosPorCarrera(idCarrera);
    }

    public int getsaldoapostador(int idApostador) throws SQLException {
        return services.getsaldoapostador(idApostador);
    }
}
