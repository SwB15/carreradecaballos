package View;

import Controller.Balance_Controller;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Balance extends javax.swing.JDialog {

    Balance_Controller controller = new Balance_Controller();
    DecimalFormat formateador14 = new DecimalFormat("#,###.###");
    private HashMap<String, String> CarrerasMap;

    public Balance(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        txtIdcarreras.setVisible(false);
        txtApuestas.setEditable(false);
        txtApuestas.setBackground(Color.white);
        txtTotalApostado.setEditable(false);
        txtTotalApostado.setBackground(Color.white);
        txtGanadores.setEditable(false);
        txtGanadores.setBackground(Color.white);
        txtPerdedores.setEditable(false);
        txtPerdedores.setBackground(Color.white);
        txtTotalPagado.setEditable(false);
        txtTotalPagado.setBackground(Color.white);
        carrerasCombobox();
        btnPrint.setEnabled(false);
    }

    private void showResultados(int idcarreras) {
        try {
            DefaultTableModel model = controller.showResultados(idcarreras);
            model.addColumn("Monto Neto");

            // Recorrer filas y calcular el monto neto para los ganadores
            for (int i = 0; i < model.getRowCount(); i++) {
                String resultado = model.getValueAt(i, 5).toString();
                if (resultado.equalsIgnoreCase("Ganador")) {
                    int monto = Integer.parseInt(model.getValueAt(i, 3).toString());
                    int montoNeto = (monto * 90) / 100; // Calcula 90% sin usar double
                    model.setValueAt(montoNeto, i, 6);
                } else {
                    model.setValueAt("", i, 6); // Dejar vacío si es perdedor
                }
            }

            tblBalance.setModel(model);
            calcularResultados();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Método para cargar las carreras en el ComboBox
    private void carrerasCombobox() {
        // Obtenemos el HashMap con los Carreras (ID -> nombre)
        CarrerasMap = controller.fillCarrerasCombobox();

        cmbCarreras.removeAllItems();

        // Agregamos los nombres de los Carreras al ComboBox
        for (String nombre : CarrerasMap.values()) {
            cmbCarreras.addItem(nombre);
        }

        // Si el ComboBox tiene elementos, seleccionamos el primero automáticamente y actualizamos el ID
        if (cmbCarreras.getItemCount() > 0) {
            String firstNombre = (String) cmbCarreras.getItemAt(0); // Primer caballo en el ComboBox
            for (Map.Entry<String, String> entry : CarrerasMap.entrySet()) {
                if (entry.getValue().equals(firstNombre)) {
                    // Actualizamos el TextField con el ID del primer caballo
                    txtIdcarreras.setText(entry.getKey());
                    showResultados(Integer.parseInt(txtIdcarreras.getText()));
                    break;
                }
            }
        }

        // Listener para cmbCarreras (para actualizar el ID al seleccionar un nuevo caballo)
        cmbCarreras.addActionListener((ActionEvent e) -> {
            String selectedNombre = (String) cmbCarreras.getSelectedItem();
            if (selectedNombre != null) {
                // Buscamos el ID del caballo seleccionado en el HashMap
                for (Map.Entry<String, String> entry : CarrerasMap.entrySet()) {
                    if (entry.getValue().equals(selectedNombre)) {
                        // Actualizamos el TextField con el ID del caballo
                        txtIdcarreras.setText(entry.getKey());
                        showResultados(Integer.parseInt(entry.getKey()));
                        System.out.println("ID Caballo: " + entry.getKey());
                        break;
                    }
                }
            }
        });
    }

    private void calcularResultados() {
        int ganadores = 0;
        int perdedores = 0;
        int totalGanancias = 0;
        int totalApostado = 0;
        int totalPagado = 0;
        int apuestas = 0;

        DefaultTableModel model = (DefaultTableModel) tblBalance.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            apuestas++;
            String resultado = model.getValueAt(i, 5).toString(); // Columna "Resultado"
            int monto = Integer.parseInt(model.getValueAt(i, 3).toString()); // Columna "Monto"

            totalApostado += monto; // Suma total de apuestas

            if (resultado.equalsIgnoreCase("Ganador")) {
                ganadores++;
                totalGanancias += monto * 0.1; // Comisión del 10%
                totalPagado += monto - totalGanancias;
            } else if (resultado.equalsIgnoreCase("Perdedor")) {
                perdedores++;
            }
        }

        // Cargar los valores en los JTextField
        txtApuestas.setText(formateador14.format(apuestas));
        txtGanadores.setText(formateador14.format(ganadores));
        txtPerdedores.setText(formateador14.format(perdedores));
        txtGanancias.setText(formateador14.format(totalGanancias));
        txtTotalApostado.setText(formateador14.format(totalApostado));
        txtTotalPagado.setText(formateador14.format(totalPagado));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        txtGanancias = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtPerdedores = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtGanadores = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cmbCarreras = new javax.swing.JComboBox<>();
        txtIdcarreras = new javax.swing.JTextField();
        txtTotalApostado = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtTotalPagado = new javax.swing.JTextField();
        txtApuestas = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnPrint = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBalance = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Balance");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnCancel.setText("Cancel");

        jButton1.setText("Guardar");

        txtGanancias.setBackground(new java.awt.Color(153, 255, 0));
        txtGanancias.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtGanancias.setForeground(new java.awt.Color(0, 102, 0));
        txtGanancias.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtGanancias.setText("0");

        jLabel3.setText("Mis Ganancias:");

        jLabel4.setText("Perdedores:");

        jLabel5.setText("Ganadores:");

        jLabel6.setText("Carrera:");

        cmbCarreras.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel7.setText("Tot. Apost.:");

        jLabel8.setText("Tot. Pagado:");

        jLabel9.setText("Apuestas:");

        btnPrint.setText("Imprimir");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPerdedores, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtGanadores, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbCarreras, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtApuestas, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnPrint)
                                .addGap(166, 166, 166)
                                .addComponent(txtIdcarreras, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTotalApostado, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTotalPagado, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(134, 134, 134))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtGanancias, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cmbCarreras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalApostado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtApuestas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtTotalPagado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtGanadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPerdedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)))
                    .addComponent(txtGanancias, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(btnCancel)
                    .addComponent(txtIdcarreras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPrint))
                .addContainerGap())
        );

        tblBalance = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblBalance.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tblBalance);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
            java.util.logging.Logger.getLogger(Balance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Balance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Balance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Balance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Balance dialog = new Balance(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnPrint;
    private javax.swing.JComboBox<String> cmbCarreras;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tblBalance;
    private javax.swing.JTextField txtApuestas;
    private javax.swing.JTextField txtGanadores;
    private javax.swing.JTextField txtGanancias;
    private javax.swing.JTextField txtIdcarreras;
    private javax.swing.JTextField txtPerdedores;
    private javax.swing.JTextField txtTotalApostado;
    private javax.swing.JTextField txtTotalPagado;
    // End of variables declaration//GEN-END:variables

}
