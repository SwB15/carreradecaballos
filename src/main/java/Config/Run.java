package Config;

import Controller.Fecha_Controller;
import Controller.State_Controller;
import Model.Vencidos_Model;
import Services.SQLiteBackupManager;
import View.Principal;
import com.formdev.flatlaf.FlatLightLaf;
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
        try {
            // Configurar el Look and Feel
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Crear la base de datos antes de cualquier consulta
        DataSource.createDatabase();

        // Crear un backup diario y limpiar si excede el límite
        SQLiteBackupManager.crearBackup();
        SQLiteBackupManager.limpiarBackups();

        // Cargar el modelo de estados
        model = State_Controller.states();

        if (model != null) {
            System.out.println("Estados cargados:");
            for (int i = 0; i < model.getRowCount(); i++) {
                System.out.println("ID: " + model.getValueAt(i, 0) + ", Estado: " + model.getValueAt(i, 1));
            }
        } else {
            System.out.println("Error al cargar los estados.");
        }

        // Inicializar la ventana principal
        Principal form = new Principal();
        form.setVisible(true);

        // Cargar apuestas vencidas
        apuestasVencidas = Fecha_Controller.checkPending();

        // Mostrar notificación si hay apuestas vencidas
        SwingUtilities.invokeLater(() -> {
            if (!apuestasVencidas.isEmpty()) {
                Principal.pnlNotificaciones.setVisible(true);
                Principal.lblCantidadNotificaciones.setText(String.valueOf(apuestasVencidas.size()));
            }
        });
    }
}
