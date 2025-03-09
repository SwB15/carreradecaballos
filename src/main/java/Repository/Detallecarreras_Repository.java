package Repository;

import Config.DataSource;
import Model.Detallecarreras_Model;
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

public class Detallecarreras_Repository{
    
    String sql = "";
    PreparedStatement pst = null;

//********************************Begin of Insert, Update, Disable********************************
    public boolean insert(Detallecarreras_Model model) {
        sql = "INSERT INTO detallecarreras(fk_carreras, fk_caballos) VALUES(?,?)";
        try (Connection cn = DataSource.getConnection()) {
            pst = cn.prepareStatement(sql);
            pst.setInt(1, model.getFk_carreras());
            pst.setInt(2, model.getFk_caballos());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Detallecarreras_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean update(Detallecarreras_Model model) {
        sql = "UPDATE Detallecarreras SET fk_carreras = ?, fk_caballos = ? WHERE iddetallecarreras = ?";
        try (Connection cn = DataSource.getConnection()) {
            pst = cn.prepareStatement(sql);
            pst.setInt(1, model.getFk_carreras());
            pst.setInt(2, model.getFk_caballos());
            pst.setInt(3, model.getIddetallecarreras());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Detallecarreras_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
//******************************** End of Insert, Update, Disable ********************************  
    
//********************************Begin of Display Methods********************************
//    public DefaultTableModel showDetallecarreras(String search, String stateFilter) {
//        DefaultTableModel model;
//        String[] titles = {"Id", "Carreras", "Caballos", "Estado"};
//        String[] records = new String[5];
//        int totalRecords = 0;
//        model = new DefaultTableModel(null, titles);
//
//        String sSQL = "SELECT a.idDetallecarreras, a.fk_carreras, a.fk_caballos, a.observacion, e.estados "
//                + "FROM detallecarreras a "
//                + "JOIN carreras e ON s.fk_apuestas = e.idestados "
//                + "JOIN estados e ON s.fk_apuestas = e.idestados "
//                + "JOIN estados e ON s.fk_apuestas = e.idestados "
//                + "WHERE a.fk_caballos LIKE ?";
//
//        if (stateFilter.equals("activo")) {
//            sSQL += " AND e.estados = 'activo'";
//        } else if (stateFilter.equals("inactivo")) {
//            sSQL += " AND e.estados = 'inactivo'";
//        }
//
//        sSQL += " ORDER BY a.idDetallecarreras DESC";
//
//        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
//            pst.setString(1, "%" + search + "%");
//
//            ResultSet rs = pst.executeQuery();
//
//            while (rs.next()) {
//                records[0] = rs.getString("iddetallecarreras");
//                records[1] = rs.getString("fk_carreras");
//                records[2] = rs.getString("fk_caballos");
//                records[3] = rs.getString("observacion");
//                records[4] = rs.getString("estados");
//
//                totalRecords++;
//                model.addRow(records);
//            }
//            return model;
//        } catch (SQLException e) {
//            JOptionPane.showConfirmDialog(null, e);
//            return null;
//        }
//    }

//    public HashMap<String, List<String>> fillCategoryCombos() {
//        HashMap<String, List<String>> categoryMap = new HashMap<>();
//        String sSQL = "SELECT idcategorias, codigo, categorias FROM categorias";
//
//        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL); ResultSet rs = pst.executeQuery()) {
//
//            while (rs.next()) {
//                String id = rs.getString("idcategorias");
//                String code = rs.getString("codigo");
//                String name = rs.getString("categorias");
//
//                List<String> values = new ArrayList<>();
//                values.add(code);
//                values.add(id);
//
//                categoryMap.put(name, values);
//            }
//
//        } catch (SQLException e) {
//            // Mostrar mensaje de error para el usuario
//            JOptionPane.showMessageDialog(null, "Error al obtener las categorías: " + e.getMessage());
//
//            // Registrar el error para el análisis posterior (opcional)
//            Logger.getLogger(Detallecarreras_Repository.class.getName()).log(Level.SEVERE, "Error en fillCategoryCombos", e);
//        }
//        return categoryMap;
//    }
//********************************End of Display Methods********************************
}