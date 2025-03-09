package View;

import Config.Run;
import Controller.Apostadores_Controller;
import Controller.State_Controller;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Apostadores extends javax.swing.JDialog {

    Apostadores_Controller controller = new Apostadores_Controller();

    private String initialState = "", finalState = "", stateFilter = "todos";
    private String id = "";
    private int idestado;

    public Apostadores(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        txtIdapostadores.setVisible(false);
        txtNumero.setEditable(false);
        txtNumero.setText(String.valueOf(controller.getMaxCodigo()));
        txtNumero.setBackground(Color.white);
        txtNumero.transferFocus();
        txtCedula.requestFocus();
        showApostadores("", stateFilter);
    }

    private void showApostadores(String search, String stateFilter) {
        try {
            DefaultTableModel model;
            model = controller.showApostadores(search, stateFilter);
            tblApostadores.setModel(model);
//            ocultar_columnas(tblApostadores);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void limpiar() {
        txtIdapostadores.setText("");
        txtCedula.setText("");
        txtNombre.setText("");
        atxtObservacion.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblApostadores = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtNumero = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCedula = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        atxtObservacion = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        chbActive = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        txtIdapostadores = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        txtBuscar = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        rbtnAll = new javax.swing.JRadioButton();
        rbtnActive = new javax.swing.JRadioButton();
        rbtnInactive = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Apostadores");

        tblApostadores = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblApostadores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblApostadores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblApostadoresMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblApostadores);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Número:");

        jLabel2.setText("Nombre:");

        txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNombreKeyPressed(evt);
            }
        });

        jLabel3.setText("Cédula:");

        txtCedula.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCedulaKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCedulaKeyTyped(evt);
            }
        });

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

        atxtObservacion.setColumns(20);
        atxtObservacion.setRows(5);
        atxtObservacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                atxtObservacionKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(atxtObservacion);

        jLabel4.setText("Observación:");

        chbActive.setText("Activo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chbActive)
                        .addGap(23, 23, 23)
                        .addComponent(btnSave)
                        .addGap(6, 6, 6)
                        .addComponent(btnCancel))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jLabel3))
                            .addComponent(jLabel2))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(57, 57, 57)
                                .addComponent(txtIdapostadores, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdapostadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(12, 12, 12)
                                .addComponent(jLabel3)
                                .addGap(12, 12, 12)
                                .addComponent(jLabel2))
                            .addComponent(jLabel4))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(chbActive))
                    .addComponent(btnSave)
                    .addComponent(btnCancel))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        jLabel6.setText("Buscar:");

        rbtnAll.setText("Todos");
        rbtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnAllActionPerformed(evt);
            }
        });

        rbtnActive.setText("Activo");
        rbtnActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnActiveActionPerformed(evt);
            }
        });

        rbtnInactive.setText("Inactivo");
        rbtnInactive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnInactiveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(rbtnActive)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rbtnInactive)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rbtnAll)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtnAll)
                    .addComponent(rbtnActive)
                    .addComponent(rbtnInactive))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (txtIdapostadores.getText().length() == 0) {
            validateFields();
            save(txtCedula.getText(), txtNombre.getText(), atxtObservacion.getText());
        } else {
            validateFields();
            update(Integer.parseInt(txtIdapostadores.getText()), txtCedula.getText(), txtNombre.getText(), atxtObservacion.getText());
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void tblApostadoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblApostadoresMouseClicked
        int selectedRow = tblApostadores.getSelectedRow();
        if (selectedRow != -1) {
//            int idApostador = Integer.parseInt(tablaApostadores.getValueAt(selectedRow, 0).toString());

            if (evt.getClickCount() == 1) {
                int select = tblApostadores.rowAtPoint(evt.getPoint());
                tblApostadores.setRowSelectionInterval(select, select);

                id = tblApostadores.getValueAt(select, 0).toString();
                txtNumero.setText(String.valueOf(tblApostadores.getValueAt(select, 0)));
                txtCedula.setText(String.valueOf(tblApostadores.getValueAt(select, 1)));
                txtNombre.setText(String.valueOf(tblApostadores.getValueAt(select, 2)));
                atxtObservacion.setText(String.valueOf(tblApostadores.getValueAt(select, 3)));

                if ("activo".equals(tblApostadores.getValueAt(select, 4).toString())) {
                    chbActive.setSelected(true);
                    initialState = tblApostadores.getValueAt(select, 4).toString();
                } else {
                    chbActive.setSelected(false);
                    initialState = tblApostadores.getValueAt(select, 4).toString();
                }
            } else if (evt.getClickCount() == 2) {
                PerfilApostador dialog = new PerfilApostador(f, true, Integer.parseInt(txtNumero.getText()));
                dialog.setVisible(true);
            }
        }
    }//GEN-LAST:event_tblApostadoresMouseClicked

    private void txtCedulaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCedulaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtCedula.transferFocus();
            txtNombre.requestFocus();
        }
    }//GEN-LAST:event_txtCedulaKeyPressed

    private void txtNombreKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtNombre.transferFocus();
            atxtObservacion.requestFocus();
        }
    }//GEN-LAST:event_txtNombreKeyPressed

    private void atxtObservacionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_atxtObservacionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            atxtObservacion.transferFocus();
            btnSave.requestFocus();
        }
    }//GEN-LAST:event_atxtObservacionKeyPressed

    private void txtCedulaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCedulaKeyTyped
        char validar = evt.getKeyChar();
        if (Character.isLetter(validar)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(null, "Ingrese solo números", "Advertencia!", JOptionPane.WARNING_MESSAGE);
        }

        int numerocaracteres = 8;
        if (txtCedula.getText().length() > numerocaracteres) {
            evt.consume();
            JOptionPane.showMessageDialog(null, "No ingrese tantos números", "Advertencia!", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_txtCedulaKeyTyped

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        showApostadores(txtBuscar.getText(), stateFilter);
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void rbtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnAllActionPerformed
        stateFilter = "todos";
        showApostadores(txtBuscar.getText(), stateFilter);
    }//GEN-LAST:event_rbtnAllActionPerformed

    private void rbtnActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnActiveActionPerformed
        stateFilter = "activo";
        showApostadores(txtBuscar.getText(), stateFilter);
    }//GEN-LAST:event_rbtnActiveActionPerformed

    private void rbtnInactiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnInactiveActionPerformed
        stateFilter = "inactivo";
        showApostadores(txtBuscar.getText(), stateFilter);
    }//GEN-LAST:event_rbtnInactiveActionPerformed

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
            java.util.logging.Logger.getLogger(Apostadores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Apostadores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Apostadores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Apostadores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Apostadores dialog = new Apostadores(new javax.swing.JFrame(), true);
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

    private void validateFields() {
        if (txtCedula.getText().length() == 0) {
            txtCedula.setText("0");
        }

        if (txtNombre.getText().length() == 0) {
            txtNombre.setText("Sin datos");
        }

        if (atxtObservacion.getText().length() == 0) {
            atxtObservacion.setText("Sin datos");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea atxtObservacion;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chbActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JRadioButton rbtnActive;
    private javax.swing.JRadioButton rbtnAll;
    private javax.swing.JRadioButton rbtnInactive;
    private javax.swing.JTable tblApostadores;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCedula;
    private javax.swing.JTextField txtIdapostadores;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNumero;
    // End of variables declaration//GEN-END:variables

    private void save(String cedula, String nombre, String observacion) {
        finalState = "activo";
        idestado = State_Controller.getEstadoId(finalState, Run.model);

        controller.createApostadores(cedula, nombre, observacion, idestado);

        JOptionPane.showMessageDialog(null, "Apostador ingresado exitosamente!", "Hecho!", JOptionPane.INFORMATION_MESSAGE);
        limpiar();
        txtNumero.setText(String.valueOf(controller.getMaxCodigo()));
        txtCedula.requestFocus();
        showApostadores("", stateFilter);
    }

    private void update(int id, String cedula, String nombre, String observacion) {
        if (initialState.equals("activo") && finalState.equals("inactivo")) {
            if (txtIdapostadores.getText().length() == 0) {
                JOptionPane.showMessageDialog(null, "Seleccione un apostador para desactivar.", "Advertencia!", JOptionPane.WARNING_MESSAGE);
            } else {
                int respuesta = JOptionPane.showConfirmDialog(this, "El apostador será desactivado", "Desactivar?", JOptionPane.YES_NO_OPTION);
                if (respuesta == JOptionPane.YES_OPTION) {
                    idestado = State_Controller.getEstadoId(finalState, Run.model);
                }
            }
        } else if (initialState.equals("inactivo") && finalState.equals("activo")) {
            if (txtIdapostadores.getText().length() == 0) {
                JOptionPane.showMessageDialog(null, "Seleccione un apostador para activar.", "Advertencia!", JOptionPane.WARNING_MESSAGE);
            } else {
                int respuesta = JOptionPane.showConfirmDialog(this, "El apostador será activado", "Activar?", JOptionPane.YES_NO_OPTION);
                if (respuesta == JOptionPane.YES_OPTION) {
                    idestado = State_Controller.getEstadoId(finalState, Run.model);
                }
            }
        } else {
            idestado = State_Controller.getEstadoId(initialState, Run.model);
        }

        controller.updateApostadores(id, cedula, nombre, observacion, idestado);

        JOptionPane.showMessageDialog(null, "Apostador actualizado exitosamente!", "Hecho!", JOptionPane.INFORMATION_MESSAGE);
        limpiar();
        txtNumero.setText(String.valueOf(controller.getMaxCodigo()));
        txtCedula.requestFocus();
        showApostadores("", stateFilter);
    }
}
