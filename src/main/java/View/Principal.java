package View;

import Controller.Carreras_Controller;
import java.awt.Frame;
import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Principal extends javax.swing.JFrame {

    public static Principal instancia;
    Carreras_Controller Carreras_controller = new Carreras_Controller();
    private String stateFilter = "todos";
    String statusFilter = "Pendiente";

    public Principal() {
        initComponents();
        this.setExtendedState(MAXIMIZED_BOTH);
        pnlNotificaciones.setVisible(false);
        rbtnPendiente.setSelected(true);
//        ImageIcon icono = new ImageIcon("src/Images/notificacion.png");
//        btnNotificaciones.setIcon(icono);
        showCarrerasInPrincipal("", statusFilter);
        notificacion();
        File file2 = new File("src/main/resources/Images/icono5.png");
        String absolutePath2 = file2.getAbsolutePath();

        ImageIcon iconoOriginal2 = new ImageIcon(absolutePath2);
        Image imagenOriginal2 = iconoOriginal2.getImage();
        this.setIconImage(imagenOriginal2);
    }

    public void showCarrerasInPrincipal(String search, String statusFilter) {
        try {
            DefaultTableModel model;
            model = Carreras_controller.showCarrerasInPrincipal(search, statusFilter);
            tblPrincipal.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void notificacion() {
        // Convertir ruta relativa a ruta absoluta
        File file = new File("src/main/resources/Images/notificacion.png");
        String absolutePath = file.getAbsolutePath();

        ImageIcon iconoOriginal = new ImageIcon(absolutePath);
        Image imagenOriginal = iconoOriginal.getImage();

        // Redimensionar la imagen
        int nuevoAncho = 53;
        int nuevoAlto = 38;
        Image imagenRedimensionada = imagenOriginal.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);

        // Crea un nuevo ImageIcon con la imagen redimensionada
        ImageIcon iconoRedimensionado = new ImageIcon(imagenRedimensionada);
        lblIcono.setIcon(iconoRedimensionado);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rbtngPrincipal = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPrincipal = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        rbtnPendiente = new javax.swing.JRadioButton();
        rbtnFinalizado = new javax.swing.JRadioButton();
        rbtnTodos = new javax.swing.JRadioButton();
        pnlNotificaciones = new javax.swing.JPanel();
        lblCantidadNotificaciones = new javax.swing.JLabel();
        lblIcono = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuCarreras = new javax.swing.JMenu();
        mnuApuestas = new javax.swing.JMenu();
        mnuBalance = new javax.swing.JMenu();
        mnuApostadores = new javax.swing.JMenu();
        mnuCaballos = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblPrincipal = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblPrincipal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblPrincipal);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        rbtngPrincipal.add(rbtnPendiente);
        rbtnPendiente.setText("Pendiente");
        rbtnPendiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnPendienteActionPerformed(evt);
            }
        });

        rbtngPrincipal.add(rbtnFinalizado);
        rbtnFinalizado.setText("Finalizado");
        rbtnFinalizado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnFinalizadoActionPerformed(evt);
            }
        });

        rbtngPrincipal.add(rbtnTodos);
        rbtnTodos.setText("Todos");
        rbtnTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnTodosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtnFinalizado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtnPendiente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtnTodos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtnFinalizado)
                    .addComponent(rbtnPendiente)
                    .addComponent(rbtnTodos))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlNotificaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlNotificacionesMouseClicked(evt);
            }
        });
        pnlNotificaciones.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblCantidadNotificaciones.setBackground(new java.awt.Color(236, 33, 39));
        lblCantidadNotificaciones.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblCantidadNotificaciones.setForeground(new java.awt.Color(255, 255, 255));
        lblCantidadNotificaciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCantidadNotificaciones.setText("99");
        lblCantidadNotificaciones.setName(""); // NOI18N
        lblCantidadNotificaciones.setOpaque(true);
        pnlNotificaciones.add(lblCantidadNotificaciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 5, 15, 15));

        lblIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/notificacion.png"))); // NOI18N
        pnlNotificaciones.add(lblIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 38));

        mnuCarreras.setText("Carreras");
        mnuCarreras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuCarrerasMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuCarreras);

        mnuApuestas.setText("Apuestas");
        mnuApuestas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuApuestasMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuApuestas);

        mnuBalance.setText("Balance");
        mnuBalance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuBalanceMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuBalance);

        mnuApostadores.setText("Apostadores");
        mnuApostadores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuApostadoresMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuApostadores);

        mnuCaballos.setText("Caballos");
        mnuCaballos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuCaballosMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuCaballos);

        jMenu1.setText("Ajustes");
        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 956, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(pnlNotificaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlNotificaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuApuestasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuApuestasMouseClicked
        Apuestas dialog = new Apuestas(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_mnuApuestasMouseClicked

    private void mnuApostadoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuApostadoresMouseClicked
        Apostadores dialog = new Apostadores(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_mnuApostadoresMouseClicked

    private void mnuBalanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuBalanceMouseClicked
        Balance dialog = new Balance(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_mnuBalanceMouseClicked

    private void mnuCarrerasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuCarrerasMouseClicked
        Carreras dialog = new Carreras(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_mnuCarrerasMouseClicked

    private void mnuCaballosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuCaballosMouseClicked
        Caballos dialog = new Caballos(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_mnuCaballosMouseClicked

    private void rbtnFinalizadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnFinalizadoActionPerformed
        showCarrerasInPrincipal("", "Finalizado");
    }//GEN-LAST:event_rbtnFinalizadoActionPerformed

    private void rbtnPendienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnPendienteActionPerformed
        showCarrerasInPrincipal("", "Pendiente");
    }//GEN-LAST:event_rbtnPendienteActionPerformed

    private void rbtnTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnTodosActionPerformed
        showCarrerasInPrincipal("", "Todos");
    }//GEN-LAST:event_rbtnTodosActionPerformed

    private void pnlNotificacionesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlNotificacionesMouseClicked
        Notificacion dialog = new Notificacion(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_pnlNotificacionesMouseClicked

    Frame f = JOptionPane.getFrameForComponent(this);

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
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JLabel lblCantidadNotificaciones;
    private javax.swing.JLabel lblIcono;
    private javax.swing.JMenu mnuApostadores;
    private javax.swing.JMenu mnuApuestas;
    private javax.swing.JMenu mnuBalance;
    private javax.swing.JMenu mnuCaballos;
    private javax.swing.JMenu mnuCarreras;
    public static javax.swing.JPanel pnlNotificaciones;
    private javax.swing.JRadioButton rbtnFinalizado;
    private javax.swing.JRadioButton rbtnPendiente;
    private javax.swing.JRadioButton rbtnTodos;
    private javax.swing.ButtonGroup rbtngPrincipal;
    private javax.swing.JTable tblPrincipal;
    // End of variables declaration//GEN-END:variables

}
