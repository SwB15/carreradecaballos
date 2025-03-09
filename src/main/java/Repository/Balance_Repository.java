package Repository;

import Config.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Balance_Repository {

    public DefaultTableModel showResultados(int raceId) {
        DefaultTableModel model;
        String[] titles = {"ID Apuesta", "Apostador", "Cédula", "Monto", "Caballo", "Resultado"};
        model = new DefaultTableModel(null, titles);

        String sSQL = "SELECT a.idapuestas, ap.nombre, ap.cedula, a.monto, c.caballos, "
                + "CASE WHEN c.idcaballos = ca.idganador THEN 'Ganador' ELSE 'Perdedor' END AS resultado "
                + "FROM apuestas a "
                + "JOIN apostadores ap ON a.fk_apostadores = ap.idapostadores "
                + "JOIN carreras ca ON a.fk_carreras = ca.idcarreras "
                + "JOIN caballos c ON a.fk_caballos = c.idcaballos "
                + "WHERE ca.idcarreras = ?";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            pst.setInt(1, raceId);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getInt("idapuestas");
                row[1] = rs.getString("nombre");
                row[2] = rs.getString("cedula");
                row[3] = rs.getInt("monto");
                row[4] = rs.getString("caballos");
                row[5] = rs.getString("resultado");
                model.addRow(row);
            }

            return model;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener resultados: " + e.getMessage());
            return null;
        }
    }

    public HashMap<String, String> fillCarrerasCombos() {
        HashMap<String, String> carrerasMap = new HashMap<>();
        String sSQL = "SELECT idcarreras, nombre FROM carreras WHERE idganador IS NOT NULL ";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("idcarreras");
                String name = rs.getString("nombre");

                carrerasMap.put(id, name);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener las carreras: " + e.getMessage());
            Logger.getLogger(Apuestas_Repository.class.getName()).log(Level.SEVERE, "Error en fillCarrerasCombos", e);
        }
        return carrerasMap;
    }

    public int calcularGanancia(int raceId) {
        int totalGanancia = 0;

        String sSQL = "SELECT a.monto FROM apuestas a "
                + "JOIN detalleapuestas da ON a.idapuestas = da.fk_apuestas "
                + "JOIN carreras ca ON da.fk_carreras = ca.idcarreras "
                + "JOIN detallecarreras dc ON ca.idcarreras = dc.fk_carreras "
                + "JOIN caballos c ON dc.fk_caballos = c.idcaballos "
                + "WHERE ca.idcarreras = ? AND c.idcaballos = (SELECT fk_caballos FROM detallecarreras WHERE fk_carreras = ?)";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            pst.setInt(1, raceId);
            pst.setInt(2, raceId);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int monto = rs.getInt("monto");
                totalGanancia += monto * 0.1; // Aplica el descuento del 10%
            }
            return totalGanancia;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
            return 0;
        }
    }
}
