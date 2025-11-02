package View;

import Model.Vencidos_Model;
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

/**
 *
 * @author SwichBlade15
 */
/**
 * JDialog que muestra una lista de notificaciones de apuestas vencidas.
 */
public class Notificacion extends javax.swing.JDialog {

    /**
     * Constructor modificado para recibir la lista de notificaciones a mostrar.
     *
     * @param parent El Frame padre.
     * @param modal Si el diálogo es modal.
     * @param apuestasVencidas La lista de DTOs con la información de las
     * apuestas vencidas.
     */
    public Notificacion(java.awt.Frame parent, boolean modal, List<Vencidos_Model> apuestasVencidas) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        cargarNotificaciones(apuestasVencidas);
    }

    /**
     * Construye la interfaz gráfica con la lista de notificaciones.
     *
     * @param apuestas La lista de DTOs de apuestas vencidas.
     */
    private void cargarNotificaciones(List<Vencidos_Model> apuestas) {
        DecimalFormat formateador = new DecimalFormat("#,###");
        DateTimeFormatter formatoSalida = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        pnlContenido.removeAll();
        pnlContenido.setLayout(new BoxLayout(pnlContenido, BoxLayout.Y_AXIS));

        if (apuestas == null || apuestas.isEmpty()) {
            pnlContenido.add(new JLabel("No hay apuestas vencidas."));
        } else {
            for (Vencidos_Model apuesta : apuestas) {
                // Se usan los tipos de datos correctos (LocalDate) del DTO refactorizado.
                String fecha = apuesta.getFechaApuesta().format(formatoSalida);
                String fechalimite = apuesta.getFechaLimite().format(formatoSalida);
                String montoApuesta = formateador.format(apuesta.getMontoApuesta());
                String montoAbonado = formateador.format(apuesta.getMontoAbonado());

                // Se construye el mensaje
                String mensaje = String.format(
                        "<html><b>La apuesta en %s por %s ha vencido el %s.</b><br>"
                        + "Monto: %s<br>Abonado: %s</html>",
                        apuesta.getNombreCarrera(),
                        apuesta.getNombreApostador(),
                        fechalimite,
                        montoApuesta,
                        montoAbonado
                );

                JLabel lblMensaje = new JLabel(mensaje);
                lblMensaje.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                lblMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);

                pnlContenido.add(lblMensaje);
                pnlContenido.add(Box.createVerticalStrut(10)); // Espacio entre notificaciones
            }
        }

        // Se fuerza la actualización de la UI
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlContenido;
    // End of variables declaration//GEN-END:variables
}
