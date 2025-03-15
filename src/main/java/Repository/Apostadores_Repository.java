package Repository;

import Config.DataSource;
import Model.Apostadores_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Apostadores_Repository {

    String sql = "";
    PreparedStatement pst = null;

//********************************Begin of Insert, Update, Disable********************************
    public boolean insert(Apostadores_Model model) {
        sql = "INSERT INTO apostadores(cedula, nombre, observacion, fk_estados) VALUES(?,?,?,?)";
        try (Connection cn = DataSource.getConnection()) {
            pst = cn.prepareStatement(sql);
            pst.setString(1, model.getCedula());
            pst.setString(2, model.getNombre());
            pst.setString(3, model.getObservacion());
            pst.setInt(4, model.getFk_estados());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Apostadores_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean update(Apostadores_Model model) {
        sql = "UPDATE apostadores SET cedula = ?, nombre = ?, observacion = ?, fk_estados = ? WHERE idapostadores = ?";
        try (Connection cn = DataSource.getConnection()) {
            pst = cn.prepareStatement(sql);
            pst.setString(1, model.getCedula());
            pst.setString(2, model.getNombre());
            pst.setString(3, model.getObservacion());
            pst.setInt(4, model.getFk_estados());
            pst.setInt(5, model.getIdapostadores());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Apostadores_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
//******************************** End of Insert, Update, Disable ********************************  

//********************************Begin of Display Methods********************************
    public DefaultTableModel showApostadores(String search, String stateFilter) {
        String[] titles = {"Id", "Cedula", "Nombre", "Observacion", "Estado"};
        DefaultTableModel model = new DefaultTableModel(null, titles);

        String sSQL = "SELECT a.idapostadores, a.cedula, a.nombre, a.observacion, e.estados "
                + "FROM apostadores a "
                + "INNER JOIN estados e ON a.fk_estados = e.idestados "
                + "WHERE a.nombre LIKE ? COLLATE NOCASE ";

        if ("activo".equals(stateFilter)) {
            sSQL += "AND e.estados = 'activo' ";
        } else if ("inactivo".equals(stateFilter)) {
            sSQL += "AND e.estados = 'inactivo' ";
        }

        sSQL += "ORDER BY a.idapostadores DESC";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {

            pst.setString(1, "%" + search + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("idapostadores"),
                        rs.getString("cedula"),
                        rs.getString("nombre"),
                        rs.getString("observacion"),
                        rs.getString("estados")
                    });
                }
            }

        } catch (SQLException e) {
            Logger.getLogger(Apostadores_Repository.class.getName()).log(Level.SEVERE, "Error al consultar apostadores", e);
        }

        return model;
    }

    //Mostrar apuestas
    public DefaultTableModel showHistorial(int apostadorId) {
        String[] titles = {"ID Apuesta", "Id Apostador", "Apostador", "Cédula", "Apuesta", "Monto", "Fecha", "Caballo", "Resultado"};
        DefaultTableModel model = new DefaultTableModel(null, titles);

        String sSQL = "SELECT a.idapuestas, ap.idapostadores, ap.nombre, ap.cedula, a.apuesta, a.monto, a.fecha, "
                + "c.caballos, c.idcaballos, ca.idganador "
                + "FROM apuestas a "
                + "INNER JOIN apostadores ap ON a.fk_apostadores = ap.idapostadores "
                + "INNER JOIN caballos c ON a.fk_caballos = c.idcaballos "
                + "INNER JOIN carreras ca ON a.fk_carreras = ca.idcarreras "
                + "WHERE a.fk_apostadores = ?";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {

            pst.setInt(1, apostadorId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[9];

                    row[0] = rs.getInt("idapuestas");
                    row[1] = rs.getInt("idapostadores");
                    row[2] = rs.getString("nombre");
                    row[3] = rs.getString("cedula");
                    row[4] = rs.getString("apuesta");
                    row[5] = rs.getInt("monto");
                    row[6] = rs.getString("fecha");
                    row[7] = rs.getString("caballos");

                    int idCaballoApostado = rs.getInt("idcaballos");
                    Integer idGanador = rs.getObject("idganador") != null ? rs.getInt("idganador") : null;

                    if (idGanador == null) {
                        row[8] = "Pendiente";
                    } else if (idGanador == idCaballoApostado) {
                        row[8] = "Ganador";
                    } else {
                        row[8] = "Perdedor";
                    }
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(Apostadores_Repository.class.getName()).log(Level.SEVERE, "Error al obtener el historial de apuestas", e);
        }
        return model;
    }

    public int getMaxCodigo() {
        int maxcodigo = 0;
        String codigo;
        String sSQL = "SELECT MAX(idapostadores) AS max_id FROM apostadores";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) { //Don't touch!

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                codigo = rs.getString("max_id");
                if (codigo != null) {
                    maxcodigo = Integer.parseInt(rs.getString("max_id"));
                } else {
                    maxcodigo = 0;
                }

            }
            return maxcodigo + 1;
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, e);
            return 0;
        }
    }
//********************************End of Display Methods********************************
}
