package Utils;

import Config.AppPaths;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Utilidad genérica para inicializar, generar y mostrar reportes con
 * JasperReports.
 */
public final class ReportManager {

    private ReportManager() {
    }

    /**
     * Asegura que las plantillas de reportes (.jrxml) estén presentes en el
     * directorio de datos.
     */
    public static void initializeReports() {
        List<String> templates = List.of(
                "Juegos.jrxml", "Perfil.jrxml", "Balance.jrxml",
                "Caballos.jrxml", "Movimientos.jrxml",
                "Apuestas.jrxml", "Apostadores.jrxml", "Pendientes.jrxml"
        );

        for (String templateName : templates) {
            Path targetPath = Paths.get(AppPaths.REPORTS_DIR, templateName);
            if (Files.exists(targetPath)) {
                continue;
            }

            try (InputStream resourceStream = ReportManager.class.getResourceAsStream("/Reports/" + templateName)) {
                if (resourceStream != null) {
                    Files.copy(resourceStream, targetPath);
                } else {
                    System.err.println("No se encontró la plantilla en los recursos del JAR: " + templateName);
                }
            } catch (Exception e) {
                System.err.println("Error al desplegar la plantilla de reporte '" + templateName + "': " + e.getMessage());
            }
        }
    }

    /**
     * Genera y muestra un reporte de JasperReports en un visor.
     */
    public static void generarReporte(JFrame owner, String titulo, String nombreJrxml,
            Map<String, Object> parametros, Collection<?> coleccionDeDatos) {
        try {
            if (coleccionDeDatos == null || coleccionDeDatos.isEmpty()) {
                JOptionPane.showMessageDialog(owner, "No hay datos para generar el reporte.", "Tabla Vacía", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String rutaJrxml = Paths.get(AppPaths.REPORTS_DIR, nombreJrxml).toString();
            if (!Files.exists(Paths.get(rutaJrxml))) {
                throw new Exception("No se encontró el archivo de reporte: " + nombreJrxml);
            }
            JasperReport reporteCompilado = JasperCompileManager.compileReport(rutaJrxml);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(coleccionDeDatos);

            if (parametros == null) {
                parametros = new HashMap<>();
            }

            parametros.putIfAbsent("FECHA_ACTUAL", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            URL logoUrl = ReportManager.class.getResource("/Images/icono5.png");
            if (logoUrl != null) {
                parametros.putIfAbsent("LOGO_URL", logoUrl.toString());
            }

            JasperPrint jasperPrint = JasperFillManager.fillReport(reporteCompilado, parametros, dataSource);

            JDialog viewerDialog = new JDialog(owner, titulo, true);
            viewerDialog.setSize(1024, 768);
            viewerDialog.setLocationRelativeTo(owner);
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewerDialog.setContentPane(viewer.getContentPane());
            viewerDialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(owner, "Error al generar el reporte:\n" + e.getMessage(), "Error de Reporte", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
