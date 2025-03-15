package View;

import Config.Run;
import Model.Vencidos_Model;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 *
 * @author SwichBlade15
 */
public class Notificacion extends javax.swing.JDialog {

    public Notificacion(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        cargarNotificaciones();
    }

    private void cargarNotificaciones() {
        DecimalFormat formateador14 = new DecimalFormat("#,###.###");
        pnlContenido.removeAll(); // Limpiar antes de agregar nuevos
        pnlContenido.setLayout(new BoxLayout(pnlContenido, BoxLayout.Y_AXIS)); // Apilar en columna

        List<Vencidos_Model> apuestas = Run.apuestasVencidas;
        System.out.println("Apuestas vencidas encontradas: " + apuestas.size());

        if (apuestas.isEmpty()) {
            JLabel lblNoData = new JLabel("No hay apuestas vencidas.");
            lblNoData.setHorizontalAlignment(SwingConstants.CENTER);
            pnlContenido.add(lblNoData);
        } else {
            for (Vencidos_Model apuesta : apuestas) {
                JPanel panelApuesta = new JPanel();
                panelApuesta.setPreferredSize(null); // Permitir que se ajuste automáticamente
                panelApuesta.setMaximumSize(new Dimension(700, 120)); // Limitar el ancho, pero permitir que se expanda en altura
                panelApuesta.setLayout(new BoxLayout(panelApuesta, BoxLayout.Y_AXIS));
                panelApuesta.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

                String montoapuesta = formateador14.format(apuesta.montoApuesta);
                String montoabonado = formateador14.format(apuesta.montoAbonado);

                SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd"); // Formato original
                SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy"); // Formato deseado

                String fecha = "", fechalimite = "";

                try {
                    // Convertir fechaApuesta
                    Date fechaApuestaDate = formatoEntrada.parse(apuesta.fechaApuesta);
                    fecha = formatoSalida.format(fechaApuestaDate);

                    // Convertir fechaLimite
                    Date fechaLimiteDate = formatoEntrada.parse(apuesta.fechaLimite);
                    fechalimite = formatoSalida.format(fechaLimiteDate);

                    System.out.println("Fecha: " + fecha);
                    System.out.println("Fecha Límite: " + fechalimite);

                } catch (ParseException ex) {
                    Logger.getLogger(Carreras.class.getName()).log(Level.SEVERE, null, ex);
                }

                // Mensaje de notificación
                String mensaje = "<html><b>La apuesta realizada por " + apuesta.nombreApostador
                        + " en la carrera " + apuesta.nombreCarrera + " ha vencido en fecha: " + fechalimite
                        + ".</b><br>Datos de la apuesta:<br>"
                        + "Fecha: " + fecha + " <br> Fecha Límite: " + fechalimite
                        + " <br> Monto: " + montoapuesta + " <br> Abonado: " + montoabonado + "</html>";

                JLabel lblMensaje = new JLabel(mensaje);
                lblMensaje.setPreferredSize(null);
                lblMensaje.setMaximumSize(new Dimension(700, 120));
                lblMensaje.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                panelApuesta.add(lblMensaje, BorderLayout.CENTER);

                pnlContenido.add(panelApuesta);
                pnlContenido.add(Box.createVerticalStrut(10)); // Espacio entre notificaciones
            }
        }

        // Buscar el JScrollPane y actualizar su viewport con el nuevo contenido
        Container parent = pnlContenido.getParent();
        while (parent != null && !(parent instanceof JScrollPane)) {
            parent = parent.getParent();
        }

        if (parent instanceof JScrollPane scrollPane) {
            scrollPane.getViewport().setView(pnlContenido); // Actualizar el contenido
        }

        // Forzar actualización de la interfaz
        pnlContenido.revalidate();
        pnlContenido.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        pnlContenido = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Notificaciones");

        pnlContenido.setLayout(new javax.swing.BoxLayout(pnlContenido, javax.swing.BoxLayout.LINE_AXIS));
        jScrollPane1.setViewportView(pnlContenido);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Notificacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Notificacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Notificacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Notificacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Notificacion dialog = new Notificacion(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlContenido;
    // End of variables declaration//GEN-END:variables
}
