package Config;

import Controller.Fecha_Controller;
import Controller.State_Controller;
import Model.Vencidos_Model;
import Services.SQLiteBackupManager;
import View.Principal;
import com.formdev.flatlaf.FlatLightLaf;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Run {

    public static DefaultTableModel model;
    public static List<Vencidos_Model> apuestasVencidas;

    public static void main(String[] args) {
        // 1) Look & Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 2) Crear BD y Backups
        DataSource.createDatabase();
        SQLiteBackupManager.crearBackup();
        SQLiteBackupManager.limpiarBackups();

        // 3) Extraer plantillas Jasper a AppData (con fallback)
        initReports();

        // 4) Cargar modelo de estados
        model = State_Controller.states();
        if (model != null) {
            System.out.println("Estados cargados:");
            for (int i = 0; i < model.getRowCount(); i++) {
                System.out.printf("ID: %s, Estado: %s%n",
                        model.getValueAt(i, 0),
                        model.getValueAt(i, 1));
            }
        } else {
            System.out.println("Error al cargar los estados.");
        }

        // 5) Levantar GUI
        Principal form = new Principal();
        form.setVisible(true);

        // 6) Notificaciones de apuestas vencidas
        apuestasVencidas = Fecha_Controller.checkPending();
        SwingUtilities.invokeLater(() -> {
            if (!apuestasVencidas.isEmpty()) {
                Principal.pnlNotificaciones.setVisible(true);
                Principal.lblCantidadNotificaciones
                        .setText(String.valueOf(apuestasVencidas.size()));
            }
        });
    }

    private static void initReports() {
        // Carpeta destino en AppData
        Path appDataReports = Paths.get(
                System.getProperty("user.home"),
                "AppData", "Local", "CarreraDeCaballos", "Reports"
        );
        try {
            Files.createDirectories(appDataReports);
        } catch (IOException e) {
            System.err.println("No pudo crear AppDataReports: " + e.getMessage());
            return;
        }

        List<String> templates = List.of(
                "Juegos.jrxml", "Perfil.jrxml", "Balance.jrxml",
                "Caballos.jrxml", "Movimientos.jrxml",
                "Apuestas.jrxml", "Apostadores.jrxml"
        );

        for (String tpl : templates) {
            Path target = appDataReports.resolve(tpl);
            if (Files.exists(target)) {
                continue;
            }

            // 1) Intento extraer desde el JAR
            try (InputStream in = Run.class.getResourceAsStream("/Reports/" + tpl)) {
                if (in != null) {
                    Files.copy(in, target);
                    continue;  // plantilla desplegada
                }
            } catch (IOException ioe) {
                System.err.println("Error extrayendo del JAR: " + tpl + " → " + ioe.getMessage());
            }

            // 2) Fallback: copiar desde la carpeta de instalación (Program Files\... \Reports)
            Path installReports = Paths.get(System.getProperty("user.dir"), "Reports", tpl);
            if (Files.exists(installReports)) {
                try {
                    Files.copy(installReports, target);
                    System.out.println("Fallback: copiado " + tpl + " desde carpeta de instalación");
                } catch (IOException ioe) {
                    System.err.println("Error copiando fallback " + tpl + ": " + ioe.getMessage());
                }
            } else {
                System.err.println("No se encontró plantilla ni en JAR ni en instalación: " + tpl);
            }
        }
    }

}
