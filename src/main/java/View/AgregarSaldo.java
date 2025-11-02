package View;

import Controller.Movimientos_Controller;
import Services.Exceptions.ServiceException;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

/**
 *
 * @author SwichBlade15
 */
public class AgregarSaldo extends javax.swing.JDialog {

    private final Movimientos_Controller movimientosController;
    private final int idApostador;
    private final DecimalFormat formateador = new DecimalFormat("#,###");
    private boolean guardadoExitoso = false;

    public AgregarSaldo(java.awt.Frame parent, boolean modal, int idApostador, String nombreApostador) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);

        this.movimientosController = new Movimientos_Controller();
        this.idApostador = idApostador;

        // Configuración inicial de la UI
        txtApostadores.setText(nombreApostador);
        txtApostadores.setEditable(false);
        txtApostadores.setBackground(Color.white);
        txtMonto.requestFocus();
        txtMontoactual.setVisible(false);
        txtIdapostadores.setVisible(false);
        txtDestino.setVisible(false);
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtApostadores = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtMonto = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        cmbOperaciones = new javax.swing.JComboBox<>();
        txtMontoactual = new javax.swing.JTextField();
        txtIdapostadores = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        atxtObservacion = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        txtDestino = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Agregar Saldo");

        jLabel1.setText("Apostador:");

        btnSave.setText("Guardar");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancelar");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jLabel2.setText("Monto:");

        txtMonto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtMontoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMontoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtMontoKeyTyped(evt);
            }
        });

        cmbOperaciones.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Depositar", "Retirar" }));
        cmbOperaciones.setToolTipText("");

        atxtObservacion.setColumns(20);
        atxtObservacion.setRows(5);
        jScrollPane1.setViewportView(atxtObservacion);

        jLabel3.setText("Observación:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtMontoactual, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdapostadores, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbOperaciones, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(txtApostadores, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtApostadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbOperaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnCancel)
                    .addComponent(txtMontoactual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdapostadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // 1. VALIDAR Y RECOGER DATOS DE LA VISTA
        if (txtMonto.getText().trim().isEmpty() || txtMonto.getText().equals("0")) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto mayor a 0.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int monto;
        try {
            monto = Integer.parseInt(txtMonto.getText().replace(".", ""));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El monto ingresado no es un número válido.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String operacionStr = cmbOperaciones.getSelectedItem().toString();
        if ("Retirar".equalsIgnoreCase(operacionStr)) {
            monto = -monto; // Se convierte a negativo para retiros
        }

        String descripcion = atxtObservacion.getText().trim();
        if (descripcion.isEmpty()) {
            descripcion = operacionStr;
        }

        // 2. LLAMAR AL CONTROLADOR
        try {
            movimientosController.crearMovimientoDeSaldo(idApostador, monto, descripcion);

            // 3. MOSTRAR ÉXITO, MARCAR Y CERRAR
            this.guardadoExitoso = true; // Se marca que la operación fue exitosa
            JOptionPane.showMessageDialog(this, operacionStr + " realizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();

        } catch (ServiceException e) {
            // 4. MOSTRAR ERROR
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error en la Operación", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtMontoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMontoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtMonto.transferFocus();
            btnSave.requestFocus();
        }
    }//GEN-LAST:event_txtMontoKeyPressed

    private void txtMontoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMontoKeyReleased
        if (txtMonto.getText().length() > 3) {
            String cadena = txtMonto.getText().replace(".", "");
            txtMonto.setText(formateador.format(Integer.parseInt(cadena)));
        }
    }//GEN-LAST:event_txtMontoKeyReleased

    private void txtMontoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMontoKeyTyped
        char validar = evt.getKeyChar();
        if (Character.isLetter(validar)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(null, "Ingrese solo números", "Advertencia!", JOptionPane.WARNING_MESSAGE);
        }

        int numerocaracteres = 10;
        if (txtMonto.getText().length() > numerocaracteres) {
            evt.consume();
            JOptionPane.showMessageDialog(null, "No ingrese tantos números", "Advertencia!", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_txtMontoKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea atxtObservacion;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cmbOperaciones;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    public static javax.swing.JTextField txtApostadores;
    public static javax.swing.JTextField txtDestino;
    public static javax.swing.JTextField txtIdapostadores;
    public static javax.swing.JTextField txtMonto;
    public static javax.swing.JTextField txtMontoactual;
    // End of variables declaration//GEN-END:variables

}
