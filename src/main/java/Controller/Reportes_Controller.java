package Controller;

import Model.ReportesHistorialApostador_Model;
import net.sf.jasperreports.engine.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.nio.file.Paths;
import java.util.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class Reportes_Controller {

    public JasperPrint generarReporte(String cedula, String nombre, JTable tblHistorial) {
        try {
            // Crear mapa de parámetros
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("Cedula", cedula);
            parametros.put("Nombre", nombre);

            // Extraer datos de la tabla
            List<ReportesHistorialApostador_Model> apuestasList = new ArrayList<>();
            DefaultTableModel modelo = (DefaultTableModel) tblHistorial.getModel();
            for (int i = 0; i < modelo.getRowCount(); i++) {
                int idApuesta = Integer.parseInt(modelo.getValueAt(i, 0).toString());
                String apuesta = modelo.getValueAt(i, 4).toString();
                int monto = Integer.parseInt(modelo.getValueAt(i, 5).toString());
                String fecha = modelo.getValueAt(i, 6).toString();
                String caballo = modelo.getValueAt(i, 7).toString();
                String resultado = modelo.getValueAt(i, 8).toString();

                apuestasList.add(new ReportesHistorialApostador_Model(idApuesta, apuesta, monto, fecha, caballo, resultado));
            }

            // Pasar la lista como JRBeanCollectionDataSource
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(apuestasList);
            parametros.put("ApuestasDataSource", dataSource);

            // Compilar y llenar el reporte
            final String rutaReporte = Paths.get("src/main/resources/Reports/HistorialApostador.jrxml").toAbsolutePath().toString();
            JasperReport reporte = JasperCompileManager.compileReport(rutaReporte);
            return JasperFillManager.fillReport(reporte, parametros, new JREmptyDataSource());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar el reporte: " + e.getMessage());
            return null;
        }
    }

}
