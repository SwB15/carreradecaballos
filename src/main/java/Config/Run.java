package Config;

import Controller.Fecha_Controller;
import Controller.State_Controller;
import View.Principal;
import com.formdev.flatlaf.FlatLightLaf;
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

    public static void main(String[] args) {
        try {
            // Configurar el Look and Feel
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Run.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Cargar el modelo de estados (u otra información inicial)
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

        // Realizar la comprobación de apuestas vencidas cuando se inicia el sistema
        DefaultTableModel updatedModel = Fecha_Controller.checkPending();

        // Obtener la cantidad de apuestas vencidas
        int cantidadVencidas = Fecha_Controller.getTotalPendientes();
        System.out.println("Cantidad de apuestas vencidas al iniciar: " + cantidadVencidas);

        // Actualizar la interfaz en el EDT
        SwingUtilities.invokeLater(() -> {
            Principal.pnlNotificaciones.setVisible(cantidadVencidas > 0);
            Principal.lblCantidadNotificaciones.setText(String.valueOf(cantidadVencidas));
        });
    }
}
