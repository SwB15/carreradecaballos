package View;

import Services.SQLiteBackupManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Configuraciones extends javax.swing.JDialog {

    public Configuraciones(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        cargarBackupsEnTabla();
    }

    public void cargarBackupsEnTabla() {
        System.out.println("BACKUP_DIR en tiempo de ejecución: " + SQLiteBackupManager.BACKUP_DIR);

        File backupDir = new File(SQLiteBackupManager.BACKUP_DIR);

        // 1) Imprime info de la carpeta
        System.out.println("backupDir.getAbsolutePath() = " + backupDir.getAbsolutePath());
        System.out.println("backupDir.exists() = " + backupDir.exists());
        System.out.println("backupDir.isDirectory() = " + backupDir.isDirectory());

        // 2) Imprime todos los archivos (sin filtrar)
        File[] allFiles = backupDir.listFiles();
        if (allFiles == null) {
            System.out.println("allFiles es null -> La carpeta no existe o no se pudo leer.");
        } else {
            System.out.println("Cantidad de archivos sin filtrar: " + allFiles.length);
            for (File f : allFiles) {
                System.out.println(" - Archivo encontrado (sin filtrar): " + f.getName());
            }
        }

        // 3) Aplica el filtro backup_*.db
        File[] backups = backupDir.listFiles((dir, name) -> name.startsWith("backup_") && name.endsWith(".db"));
        if (backups == null) {
            System.out.println("backups es null -> Error al leer con el filtro o la carpeta no existe.");
        } else {
            System.out.println("Cantidad de backups filtrados: " + backups.length);
            for (File backup : backups) {
                System.out.println(" - Archivo filtrado: " + backup.getName());
            }
        }

        // 4) Finalmente, si backups no es null, carga en la tabla
        if (backups != null && backups.length > 0) {
            DefaultTableModel model = (DefaultTableModel) tblBackups.getModel();
            model.setRowCount(0); // Limpiar tabla
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (File backup : backups) {
                model.addRow(new Object[]{
                    backup.getName(),
                    sdf.format(new Date(backup.lastModified())),
                    String.format("%.2f MB", backup.length() / (1024.0 * 1024.0))
                });
            }
        }

        tblBackups.revalidate();
        tblBackups.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtRestaurar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBackups = new javax.swing.JTable();
        BtnCargar = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ajustes");

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setText("Restaurar:");

        tblBackups = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblBackups.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nombre", "Fecha", "Tamaño"
            }
        ));
        tblBackups.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBackupsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBackups);

        BtnCargar.setText("Cargar");
        BtnCargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCargarActionPerformed(evt);
            }
        });

        btnDelete.setText("Eliminar");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRestaurar, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnCargar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtRestaurar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnCargar)
                    .addComponent(btnDelete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblBackupsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBackupsMouseClicked
        int fila = tblBackups.getSelectedRow();
        if (fila != -1) {
            String nombreBackup = tblBackups.getValueAt(fila, 0).toString();
            txtRestaurar.setText(nombreBackup);
        }
    }//GEN-LAST:event_tblBackupsMouseClicked

    private void BtnCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCargarActionPerformed
        String backupSeleccionado = txtRestaurar.getText().trim();
        if (backupSeleccionado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un backup de la tabla primero.");
            return;
        }

        String[] opciones = {"Sí", "No"};
        int confirm = JOptionPane.showOptionDialog(this,
                "¿Estás seguro que quieres restaurar el backup?\nSe sobrescribirá la base de datos actual.",
                "Confirmar restauración",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[1]); // Por defecto "No"

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                File backupFile = new File(SQLiteBackupManager.BACKUP_DIR + File.separator + backupSeleccionado);
                File dbFile = new File(SQLiteBackupManager.DB_PATH);

                // Sobrescribe la base de datos actual
                Files.copy(backupFile.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                JOptionPane.showMessageDialog(this, "Backup restaurado con éxito.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al restaurar el backup.");
            }
        }

    }//GEN-LAST:event_BtnCargarActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        String backupSeleccionado = txtRestaurar.getText().trim();
        if (backupSeleccionado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un backup de la tabla primero.");
            return;
        }

        String[] opciones = {"Sí", "No"};
        int confirm = JOptionPane.showOptionDialog(this,
                "¿Estás seguro que quieres eliminar el backup seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[1]);  // Por defecto "No"

        if (confirm == JOptionPane.YES_OPTION) {
            File backupFile = new File(SQLiteBackupManager.BACKUP_DIR + File.separator + backupSeleccionado);
            if (backupFile.exists() && backupFile.delete()) {
                JOptionPane.showMessageDialog(this, "Backup eliminado con éxito.");
                cargarBackupsEnTabla();  // Vuelve a cargar la tabla actualizada
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el backup.");
            }
        }

    }//GEN-LAST:event_btnDeleteActionPerformed

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
            java.util.logging.Logger.getLogger(Configuraciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Configuraciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Configuraciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Configuraciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Configuraciones dialog = new Configuraciones(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton BtnCargar;
    private javax.swing.JButton btnDelete;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblBackups;
    private javax.swing.JTextField txtRestaurar;
    // End of variables declaration//GEN-END:variables

}
