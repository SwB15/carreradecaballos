package Repository;

import Config.DataSource;
import Model.Caballos_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Caballos_Repository{
    
    String sql = "";
    PreparedStatement pst = null;

//********************************Begin of Insert, Update, Disable********************************
    public boolean insert(Caballos_Model model) {
        sql = "INSERT INTO caballos(caballos, jinete, observacion, fk_estados) VALUES(?,?,?,?)";
        try (Connection cn = DataSource.getConnection()) {
            pst = cn.prepareStatement(sql);
            pst.setString(1, model.getCaballos());
            pst.setString(2, model.getJinete());
            pst.setString(3, model.getObservacion());
            pst.setInt(4, model.getFk_estados());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Caballos_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean update(Caballos_Model model) {
        sql = "UPDATE caballos SET caballos = ?, jinete = ?, observacion = ?, fk_estados = ? WHERE idcaballos = ?";
        try (Connection cn = DataSource.getConnection()) {
            pst = cn.prepareStatement(sql);
            pst.setString(1, model.getCaballos());
            pst.setString(2, model.getJinete());
            pst.setString(3, model.getObservacion());
            pst.setInt(4, model.getFk_estados());
            pst.setInt(5, model.getIdcaballos());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Caballos_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean disable(Caballos_Model model) {
        sql = "UPDATE caballos SET fk_estados = ? WHERE idcaballos = ?";
        try (Connection cn = DataSource.getConnection()) {
            pst = cn.prepareStatement(sql);
            pst.setInt(1, model.getFk_estados());
            pst.setInt(2, model.getIdcaballos());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Caballos_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
//******************************** End of Insert, Update, Disable ********************************  
    
//********************************Begin of Display Methods********************************
    public DefaultTableModel showCaballos(String search, String stateFilter) {
        DefaultTableModel model;
        String[] titles = {"Id", "Caballos", "Jinetes", "Observacion", "Estado"};
        String[] records = new String[5];
        int totalRecords = 0;
        model = new DefaultTableModel(null, titles);

        String sSQL = "SELECT c.idcaballos, c.caballos, c.jinete, c.observacion, e.estados "
                + "FROM caballos c "
                + "JOIN estados e ON c.fk_estados = e.idestados "
                + "WHERE c.caballos LIKE ?";

        if (stateFilter.equals("activo")) {
            sSQL += " AND e.estados = 'activo'";
        } else if (stateFilter.equals("inactivo")) {
            sSQL += " AND e.estados = 'inactivo'";
        }

        sSQL += " ORDER BY c.idcaballos DESC";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            pst.setString(1, "%" + search + "%");

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                records[0] = rs.getString("idcaballos");
                records[1] = rs.getString("caballos");
                records[2] = rs.getString("jinete");
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
    
    public int getMaxCodigo() {
        int maxcodigo = 0;
        String codigo;
        String sSQL = "SELECT MAX(idcaballos) AS max_id FROM caballos";

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