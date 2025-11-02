
import Config.DatabaseManager;
import Utils.SQLiteBackupManager;
import View.Principal;
import com.formdev.flatlaf.FlatLightLaf;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import Utils.ReportManager;

/**
 * Punto de entrada principal de la aplicación. Orquesta la secuencia de
 * inicialización y lanza la interfaz gráfica.
 */
public class Run {

    public static void main(String[] args) {
        // 1. Configurar el Look and Feel de la interfaz gráfica.
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, "Error al establecer el Look and Feel.", ex);
        }

        // 2. Inicializar la base de datos (crear esquema y aplicar migraciones).
        DatabaseManager.initializeDatabase();

        // 3. Gestionar backups de la base de datos.
        SQLiteBackupManager.crearBackup();
        SQLiteBackupManager.limpiarBackups();

        // 4. Asegurarse de que las plantillas de reportes existan.
        ReportManager.initializeReports();

        // 5. Lanzar la interfaz gráfica en el hilo de despacho de eventos de Swing.
        java.awt.EventQueue.invokeLater(() -> {
            new Principal().setVisible(true);
        });
    }
}
