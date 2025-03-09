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

    public boolean disable(Apostadores_Model model) {
        sql = "UPDATE apostadores SET fk_estados = ? WHERE idapostadores = ?";
        try (Connection cn = DataSource.getConnection()) {
            pst = cn.prepareStatement(sql);
            pst.setInt(1, model.getFk_estados());
            pst.setInt(2, model.getIdapostadores());

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
        DefaultTableModel model;
        String[] titles = {"Id", "Cedula", "Nombre", "Observacion", "Estado"};
        String[] records = new String[5];
        int totalRecords = 0;
        model = new DefaultTableModel(null, titles);

        String sSQL = "SELECT a.idapostadores, a.cedula, a.nombre, a.observacion, e.estados "
                + "FROM apostadores a "
                + "JOIN estados e ON a.fk_estados = e.idestados "
                + "WHERE a.nombre LIKE ?";

        if (stateFilter.equals("activo")) {
            sSQL += " AND e.estados = 'activo'";
        } else if (stateFilter.equals("inactivo")) {
            sSQL += " AND e.estados = 'inactivo'";
        }

        sSQL += " ORDER BY a.idapostadores DESC";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            pst.setString(1, "%" + search + "%");

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                records[0] = rs.getString("idapostadores");
                records[1] = rs.getString("cedula");
                records[2] = rs.getString("nombre");
                records[3] = rs.getString("observacion");
                records[4] = rs.getString("estados");

                totalRecords++;
                model.addRow(records);
            }
            return model;
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, e);
            return null;
        }
    }

    //Mostrar apuestas
    public DefaultTableModel showHistorial(int apostadorId) {
        DefaultTableModel model;
        String[] titles = {"ID Apuesta", "Id Apostador", "Apostador", "Cédula", "Apuesta", "Monto", "Fecha", "Hora", "Caballo", "Resultado"};
        model = new DefaultTableModel(null, titles);

        String sSQL = "SELECT a.idapuestas, ap.idapostadores, ap.nombre, ap.cedula, a.apuesta, a.monto, a.fecha, c.caballos, c.idcaballos, ca.idganador "
                + "FROM apuestas a "
                + "JOIN apostadores ap ON a.fk_apostadores = ap.idapostadores "
                + "JOIN caballos c ON a.fk_caballos = c.idcaballos "
                + "JOIN carreras ca ON a.fk_carreras = ca.idcarreras "
                + "WHERE a.fk_apostadores = ?";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            pst.setInt(1, apostadorId);
            ResultSet rs = pst.executeQuery();

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

            return model;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el historial de apuestas: " + e.getMessage());
            return null;
        }
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
