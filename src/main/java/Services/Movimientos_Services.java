package Services;

import Model.Movimientos_Model;
import Repository.Movimientos_Repository;
import java.sql.SQLException;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Movimientos_Services {

    private final Movimientos_Repository movimientos_repository = new Movimientos_Repository();

    public Movimientos_Services() {
    }

    public boolean add(Movimientos_Model model) {
        return movimientos_repository.insert(model);
    }

    public boolean update(Movimientos_Model model) {
        return movimientos_repository.update(model);
    }

    public int deleteMovimientosporcarrera(int raceId) throws SQLException {
        return movimientos_repository.deleteMovimientosporcarrera(raceId);
    }

    public DefaultTableModel showMovimientos(String search, String stateFilter, String datefrom, String dateto) {
        return movimientos_repository.showMovimientos(search, stateFilter, datefrom, dateto);
    }

    public List<Movimientos_Model> calcularMovimientosPorCarrera(int raceId, String fecha) throws SQLException {
        return movimientos_repository.calcularMovimientosPorCarrera(raceId, fecha);
    }
    
    public List<Movimientos_Model> getMovimientosPorCarrera(int idCarrera) throws SQLException {
        return movimientos_repository.getMovimientosPorCarrera(idCarrera);
    }
    
    public int getsaldoapostador(int idApostador) throws SQLException {
        return movimientos_repository.getsaldoapostador(idApostador);
    }
}
