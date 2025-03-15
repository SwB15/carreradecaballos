package View;

import Controller.Apostadores_Controller;
import Controller.Reportes_Controller;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.swing.JRViewerToolbar;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author SwichBlade15
 */
public class PerfilApostador extends javax.swing.JDialog {

    Apostadores_Controller controller = new Apostadores_Controller();
    private static final Logger logger = Logger.getLogger(PerfilApostador.class.getName());

    public PerfilApostador(java.awt.Frame parent, boolean modal, int idApostador) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        txtNumero.setEditable(false);
        txtNumero.setBackground(Color.white);
        txtCedula.setEditable(false);
        txtCedula.setBackground(Color.white);
        txtNombre.setEditable(false);
        txtNombre.setBackground(Color.white);
        showHistorial(idApostador);
        btnImprimir.setEnabled(false);
    }

    private void showHistorial(int id) {
        try {
            DefaultTableModel model;
            model = controller.showHistorial(id);
            tblPerfil.setModel(model);
            txtNumero.setText(String.valueOf(tblPerfil.getValueAt(0, 1)));
            txtNombre.setText(String.valueOf(tblPerfil.getValueAt(0, 2)));
            txtCedula.setText(String.valueOf(tblPerfil.getValueAt(0, 3)));
//            ocultar_columnas(tblPerfil);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void ocultar_columnas(JTable table) {
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(0);

        table.getColumnModel().getColumn(2).setMaxWidth(0);
        table.getColumnModel().getColumn(2).setMinWidth(0);
        table.getColumnModel().getColumn(2).setPreferredWidth(0);

        table.getColumnModel().getColumn(3).setMaxWidth(0);
        table.getColumnModel().getColumn(3).setMinWidth(0);
        table.getColumnModel().getColumn(3).setPreferredWidth(0);
    }

    public void mostrarHistorialApostador() {
        ImageIcon excelIcon = new ImageIcon("src/main/java/Images/excel_icon.png");
        Image image = excelIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);

        // Crear un nuevo ImageIcon con la imagen redimensionada
        ImageIcon resizedExcelIcon = new ImageIcon(image);

        Reportes_Controller reportesController = new Reportes_Controller();
        JasperPrint reporte = reportesController.generarReporte(txtCedula.getText(), txtNombre.getText(), tblPerfil);

        if (reporte != null) {
            // Crear el diálogo modal
            JDialog viewerDialog = new JDialog();
            viewerDialog.setModal(true);
            viewerDialog.setTitle("Reporte");
            viewerDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            // Establecer tamaño al máximo de la pantalla (SIN setExtendedState)
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            viewerDialog.setSize(screenSize);
            viewerDialog.setLocationRelativeTo(null); // Centrar la ventana

            // Crear el visor JasperViewer
            JasperViewer view = new JasperViewer(reporte, false);
            Container contentPane = view.getContentPane();
            JRViewerToolbar toolbar = (JRViewerToolbar) ((JRViewer) ((JPanel) contentPane.getComponents()[0]).getComponent(0)).getComponent(0);
            Component rigidArea = Box.createRigidArea(new Dimension(3, 0));
            Component rigidArea2 = Box.createRigidArea(new Dimension(3, 0));

            JButton btnSave = (JButton) toolbar.getComponent(0);
            btnSave.setText("Guardar");
            btnSave.setPreferredSize(new Dimension(75, 30));

            JButton btnPrint = (JButton) toolbar.getComponent(1);
            btnPrint.setText("Imprimir");
            btnPrint.setPreferredSize(new Dimension(75, 30));

            JButton btnExcel = (JButton) toolbar.getComponent(2);
            btnExcel.setText("Exportar");
            btnExcel.setIcon(resizedExcelIcon);
            btnExcel.setEnabled(true);
            btnExcel.setPreferredSize(new Dimension(75, 30));

            // Remueve los botones de la barra de herramientas
            toolbar.remove(btnSave);
            toolbar.remove(btnPrint);
            toolbar.remove(btnExcel);

            // Añade los botones en el orden deseado
            // Establecer un borde vacío de 5 píxeles en el lado izquierdo de la barra de herramientas
            toolbar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            toolbar.add(btnPrint, 0);
            toolbar.add(rigidArea, 1);
            toolbar.add(btnSave, 2);
            toolbar.add(rigidArea2, 3);
            toolbar.add(btnExcel, 4);
            toolbar.add(rigidArea2, 5);

            // Actualiza la barra de herramientas para reflejar los cambios
            toolbar.revalidate();
            toolbar.repaint();

            viewerDialog.getContentPane().add(view.getContentPane(), BorderLayout.CENTER);

            // Agregar botón "Cerrar"
            JButton btnCerrar = new JButton("Cerrar");
            btnCerrar.addActionListener(e -> viewerDialog.dispose());

            // Agregar el botón en la parte inferior
            JPanel panelBoton = new JPanel();
            panelBoton.add(btnCerrar);
            viewerDialog.getContentPane().add(panelBoton, BorderLayout.SOUTH);

            // Mostrar la ventana
            viewerDialog.setVisible(true);

            // Darle el foco al botón "Cerrar"
            SwingUtilities.invokeLater(() -> btnCerrar.requestFocusInWindow());

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtNumero = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCedula = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        btnImprimir = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPerfil = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Historial de Apuestas");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Número:");

        jLabel2.setText("Nombre:");

        jLabel3.setText("Cedula:");

        btnImprimir.setText("Imprimir");
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnImprimir))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnImprimir)
                .addContainerGap())
        );

        tblPerfil.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblPerfil);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        mostrarHistorialApostador();
    }//GEN-LAST:event_btnImprimirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnImprimir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPerfil;
    private javax.swing.JTextField txtCedula;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNumero;
    // End of variables declaration//GEN-END:variables

}
