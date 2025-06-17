package View;

import Controller.Carreras_Controller;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author SwichBlade15
 */
public class CargarGanador extends javax.swing.JDialog {

    Carreras_Controller carreras_controller = new Carreras_Controller();

    public CargarGanador(java.awt.Frame parent, boolean modal, int idcarreras, String carrera, HashMap<String, String> caballosMap) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        txtCarreras.setText(carrera);
        txtCarreras.setEditable(false);
        txtCarreras.setBackground(Color.white);
        txtIdcarreras.setText(String.valueOf(idcarreras));
        txtIdcarreras.setVisible(false);
        txtIdcaballos.setVisible(false);
        txtCarreras.transferFocus();
        cmbCaballos.requestFocus();
        cargarCaballosComboBox(caballosMap);
    }

    public void cargarCaballosComboBox(HashMap<String, String> caballosMap) {
        cmbCaballos.removeAllItems();

        // Agregamos los nombres de los caballos al ComboBox
        for (String nombre : caballosMap.values()) {
            cmbCaballos.addItem(nombre);
        }

        // Si el ComboBox tiene elementos, seleccionamos el primero automáticamente y actualizamos el ID
        if (cmbCaballos.getItemCount() > 0) {
            String firstNombre = (String) cmbCaballos.getItemAt(0); // Primer caballo en el ComboBox
            for (Map.Entry<String, String> entry : caballosMap.entrySet()) {
                if (entry.getValue().equals(firstNombre)) {
                    // Actualizamos el TextField con el ID del primer caballo
                    txtIdcaballos.setText(entry.getKey());
                    break;
                }
            }
        }

        // Listener para cmbCaballos (para actualizar el ID al seleccionar un nuevo caballo)
        cmbCaballos.addActionListener((ActionEvent e) -> {
            String selectedNombre = (String) cmbCaballos.getSelectedItem();
            if (selectedNombre != null) {
                // Buscamos el ID del caballo seleccionado en el HashMap
                for (Map.Entry<String, String> entry : caballosMap.entrySet()) {
                    if (entry.getValue().equals(selectedNombre)) {
                        // Actualizamos el TextField con el ID del caballo
                        txtIdcaballos.setText(entry.getKey());
                        System.out.println("ID Caballo: " + entry.getKey());
                        break;
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtCarreras = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cmbCaballos = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        txtIdcarreras = new javax.swing.JTextField();
        txtIdcaballos = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cargar Ganador");

        jLabel1.setText("Carrera:");

        btnCancel.setText("Cancelar");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnSave.setText("Guardar");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jLabel2.setText("Caballo:");

        cmbCaballos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCaballos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCaballosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtIdcarreras, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdcaballos, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel))
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCarreras)
                            .addComponent(cmbCaballos, 0, 234, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCarreras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cmbCaballos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnCancel)
                    .addComponent(txtIdcarreras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdcaballos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (txtCarreras.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "Necesitas seleccionar o cargar una carrera primeramente", "Advertencia!", JOptionPane.WARNING_MESSAGE);
        } else {
            NewApuestas.txtIdganador.setText(txtIdcaballos.getText());
            NewApuestas.txtGanador.setText(cmbCaballos.getSelectedItem().toString());
            this.dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void cmbCaballosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCaballosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCaballosActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cmbCaballos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    public static javax.swing.JTextField txtCarreras;
    private javax.swing.JTextField txtIdcaballos;
    public static javax.swing.JTextField txtIdcarreras;
    // End of variables declaration//GEN-END:variables

}
