package Controller;

import Config.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class State_Controller {

    static int totalRegistros = 0;

    public static DefaultTableModel states() {
        String[] titulos = {"Codigo", "Estado"};
        String[] registros = new String[2];
        totalRegistros = 0;
        DefaultTableModel modelo = new DefaultTableModel(null, titulos);

        String sql = "SELECT idestados, estados FROM estados ORDER BY idestados DESC";

        try (Connection cn = DataSource.getConnection()) {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                registros[0] = rs.getString("idestados");
                registros[1] = rs.getString("estados");

                totalRegistros++;
                modelo.addRow(registros);
            }
            return modelo;
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, e);
            return null;
        }
    }
    
    // Método para obtener el ID del estado
    public static int getEstadoId(String estado, DefaultTableModel model) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 1).toString().equalsIgnoreCase(estado)) {
                return Integer.parseInt(model.getValueAt(i, 0).toString());
            }
        }
        return -1; // Si no encuentra el estado
    }
}


