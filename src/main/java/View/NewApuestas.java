package View;

import Controller.Apostadores_Controller;
import Controller.Caballos_Controller;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class NewApuestas extends javax.swing.JDialog {

    Apostadores_Controller apostadorescontroller = new Apostadores_Controller();
    Caballos_Controller caballoscontroller = new Caballos_Controller();

    private Object[] apostadores = new Object[2];
    private Object[] caballos = new Object[2];

    public NewApuestas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        tblApostadores.setName("tblApostadores");
        tblCaballos.setName("tblCaballos");
        tblElegidos.setName("tblElegidos");
//        tblElegidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblElegidos();
        showApostadores("", "activo");
        showCaballos("", "activo");
    }

    private void showApostadores(String search, String stateFilter) {
        try {
            DefaultTableModel model;
            model = apostadorescontroller.showApostadores(search, stateFilter);
            tblApostadores.setModel(model);
            ocultar_columnas(tblApostadores);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void showCaballos(String search, String stateFilter) {
        try {
            DefaultTableModel model;
            model = caballoscontroller.showCaballos(search, stateFilter);
            tblCaballos.setModel(model);
            ocultar_columnas(tblCaballos);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Configuración de la tabla unificada tblElegidos con modelo personalizado
    private void tblElegidos() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Caballo", "Nombre Caballo", "Apostado", "Abonado", "ID Apostador", "Nombre Apostador"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Sólo las columnas 2 y 3 son editables.
                return column == 2 || column == 3;
            }
        };
        tblElegidos.setModel(model);
        tblElegidos.setShowGrid(true);
        tblElegidos.setGridColor(Color.GRAY);
        tblElegidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Listener para clic derecho que limpia la selección:
        tblElegidos.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (SwingUtilities.isRightMouseButton(evt)) {
                    tblElegidos.clearSelection();
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (SwingUtilities.isRightMouseButton(evt)) {
                    tblElegidos.clearSelection();
                }
            }
        });
        ocultar_columnas(tblElegidos);
    }

    private void addHorseToTable(Object horseId, Object horseName) {
        DefaultTableModel model = (DefaultTableModel) tblElegidos.getModel();
        int selectedRow = tblElegidos.getSelectedRow();

        // Si hay una fila seleccionada, se actualiza esa fila.
        if (selectedRow != -1) {
            model.setValueAt(horseId, selectedRow, 0);
            model.setValueAt(horseName, selectedRow, 1);
            return;
        }

        // Si no hay fila seleccionada, se busca la primera fila sin datos de caballo.
        for (int i = 0; i < model.getRowCount(); i++) {
            Object idCaballo = model.getValueAt(i, 0);
            Object nomCaballo = model.getValueAt(i, 1);
            if ((idCaballo == null || idCaballo.toString().trim().isEmpty())
                    && (nomCaballo == null || nomCaballo.toString().trim().isEmpty())) {
                model.setValueAt(horseId, i, 0);
                model.setValueAt(horseName, i, 1);
                tblElegidos.setRowSelectionInterval(i, i);
                return;
            }
        }

        // Si no se encontró fila incompleta, se agrega una nueva.
        model.addRow(new Object[]{
            horseId, // ID Caballo
            horseName, // Nombre Caballo
            "", // Apostado (editable)
            "", // Abonado (editable)
            null, // ID Apostador
            null // Nombre Apostador
        });
    }

    private void addApostadorToTable(Object bettorId, Object bettorName) {
        DefaultTableModel model = (DefaultTableModel) tblElegidos.getModel();
        int selectedRow = tblElegidos.getSelectedRow();

        // Si hay una fila seleccionada, se actualiza esa fila.
        if (selectedRow != -1) {
            model.setValueAt(bettorId, selectedRow, 4);
            model.setValueAt(bettorName, selectedRow, 5);
            return;
        }

        // Si no hay fila seleccionada, se busca la primera fila sin datos de apostador.
        for (int i = 0; i < model.getRowCount(); i++) {
            Object idApostador = model.getValueAt(i, 4);
            Object nomApostador = model.getValueAt(i, 5);
            if ((idApostador == null || idApostador.toString().trim().isEmpty())
                    && (nomApostador == null || nomApostador.toString().trim().isEmpty())) {
                model.setValueAt(bettorId, i, 4);
                model.setValueAt(bettorName, i, 5);
                tblElegidos.setRowSelectionInterval(i, i);
                return;
            }
        }

        // Si no se encontró fila incompleta, se agrega una nueva.
        model.addRow(new Object[]{
            null, // ID Caballo
            null, // Nombre Caballo
            "", // Apostado
            "", // Abonado
            bettorId, // ID Apostador
            bettorName // Nombre Apostador
        });
    }

    private void ocultar_columnas(JTable table) {
        if (table.getName().equals("tblApostadores")) {
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(0);

            table.getColumnModel().getColumn(1).setMaxWidth(0);
            table.getColumnModel().getColumn(1).setMinWidth(0);
            table.getColumnModel().getColumn(1).setPreferredWidth(0);

            table.getColumnModel().getColumn(3).setMaxWidth(0);
            table.getColumnModel().getColumn(3).setMinWidth(0);
            table.getColumnModel().getColumn(3).setPreferredWidth(0);

            table.getColumnModel().getColumn(4).setMaxWidth(0);
            table.getColumnModel().getColumn(4).setMinWidth(0);
            table.getColumnModel().getColumn(4).setPreferredWidth(0);
        }

        if (table.getName().equals("tblCaballos")) {
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(0);

            table.getColumnModel().getColumn(2).setMaxWidth(0);
            table.getColumnModel().getColumn(2).setMinWidth(0);
            table.getColumnModel().getColumn(2).setPreferredWidth(0);

            table.getColumnModel().getColumn(3).setMaxWidth(0);
            table.getColumnModel().getColumn(3).setMinWidth(0);
            table.getColumnModel().getColumn(3).setPreferredWidth(0);

            table.getColumnModel().getColumn(4).setMaxWidth(0);
            table.getColumnModel().getColumn(4).setMinWidth(0);
            table.getColumnModel().getColumn(4).setPreferredWidth(0);
        }

        if (table.getName().equals("tblElegidos")) {
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(0);

            table.getColumnModel().getColumn(4).setMaxWidth(0);
            table.getColumnModel().getColumn(4).setMinWidth(0);
            table.getColumnModel().getColumn(4).setPreferredWidth(0);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblApostadores = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblElegidos = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblCaballos = new javax.swing.JTable();
        jTextField6 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cargar Apuestas");

        tblApostadores = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblApostadores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Apostadores"
            }
        ));
        tblApostadores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblApostadoresMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblApostadoresMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblApostadores);

        tblElegidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Elegidos"
            }
        ));
        tblElegidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblElegidosMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblElegidosMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblElegidosMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(tblElegidos);

        tblCaballos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblCaballos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Caballos"
            }
        ));
        tblCaballos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCaballosMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblCaballosMousePressed(evt);
            }
        });
        jScrollPane4.setViewportView(tblCaballos);

        jTextField6.setText("Juego N° 20");
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jLabel1.setText("Juego:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 646, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(376, 376, 376)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE)
                        .addComponent(jScrollPane3))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 695, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void tblApostadoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblApostadoresMouseClicked
        // Limpiamos la selección de la otra tabla, pero no de tblElegidos
        tblCaballos.clearSelection();
        // No se limpia tblElegidos para permitir actualizar la fila seleccionada

        if (evt.getClickCount() == 2) {
            int row = tblApostadores.getSelectedRow();
            if (row != -1) {
                Object bettorId = tblApostadores.getValueAt(row, 0);
                Object bettorName = tblApostadores.getValueAt(row, 2);
                addApostadorToTable(bettorId, bettorName);
            }
        }
    }//GEN-LAST:event_tblApostadoresMouseClicked

    private void tblCaballosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCaballosMouseClicked
        tblApostadores.clearSelection();
        // No se limpia tblElegidos para permitir actualizar la fila seleccionada

        if (evt.getClickCount() == 2) {
            int row = tblCaballos.getSelectedRow();
            if (row != -1) {
                Object horseId = tblCaballos.getValueAt(row, 0);
                Object horseName = tblCaballos.getValueAt(row, 1);
                addHorseToTable(horseId, horseName);
            }
        }
    }//GEN-LAST:event_tblCaballosMouseClicked

    private void tblElegidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElegidosMouseClicked
        tblApostadores.clearSelection();
        tblCaballos.clearSelection();

        if (evt.getClickCount() == 2) {
            int row = tblElegidos.getSelectedRow();
            if (row != -1) {
                DefaultTableModel model = (DefaultTableModel) tblElegidos.getModel();
                model.removeRow(row);
            }
        }
    }//GEN-LAST:event_tblElegidosMouseClicked

    private void tblElegidosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElegidosMouseReleased
        if (SwingUtilities.isRightMouseButton(evt)) {
            tblElegidos.clearSelection();
        }
    }//GEN-LAST:event_tblElegidosMouseReleased

    private void tblElegidosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElegidosMousePressed
        if (SwingUtilities.isRightMouseButton(evt)) {
            tblElegidos.clearSelection();
        }
    }//GEN-LAST:event_tblElegidosMousePressed

    private void tblCaballosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCaballosMousePressed
        if (SwingUtilities.isRightMouseButton(evt)) {
            tblCaballos.clearSelection();
        }
    }//GEN-LAST:event_tblCaballosMousePressed

    private void tblApostadoresMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblApostadoresMouseReleased
        if (SwingUtilities.isRightMouseButton(evt)) {
            tblApostadores.clearSelection();
        }
    }//GEN-LAST:event_tblApostadoresMouseReleased

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
            java.util.logging.Logger.getLogger(NewApuestas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewApuestas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewApuestas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewApuestas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewApuestas dialog = new NewApuestas(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTable tblApostadores;
    private javax.swing.JTable tblCaballos;
    private javax.swing.JTable tblElegidos;
    // End of variables declaration//GEN-END:variables

}
