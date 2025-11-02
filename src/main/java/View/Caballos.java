package View;

import Config.AppPaths;
import Controller.Caballos_Controller;
import Model.Caballos_Model;
import Services.Exceptions.ServiceException;
import Utils.ExcelExporter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.swing.JRViewerToolbar;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author SwichBlade15
 */
public class Caballos extends javax.swing.JDialog {

    private final Caballos_Controller controller;
    private String stateFilter = "todos"; // Mantenemos el estado del filtro

    public Caballos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);

        // Se inicializa el controlador.
        this.controller = new Caballos_Controller();

        // Configuración inicial de la UI
        txtIdcaballos.setVisible(false);
        txtNumero.setEditable(false);
        txtNumero.setText("Nuevo");
        txtCaballos.requestFocus();

        // Se llama al nuevo método para cargar los datos iniciales.
        actualizarTabla();

        txtIdcaballos.setVisible(false);
        txtNumero.setEditable(false);
        txtNumero.transferFocus();
        txtCaballos.requestFocus();
    }

    private void actualizarTabla() {
        String search = txtBuscar.getText();

        try {
            // 1. Se llama al controlador para obtener los DATOS (una Lista de Modelos).
            List<Caballos_Model> listaCaballos = controller.listarCaballos(search, stateFilter);

            // 2. Se crea un DefaultTableModel VACÍO.
            String[] titles = {"Id", "Caballos", "Jinetes", "Observacion", "Estado"};
            DefaultTableModel model = new DefaultTableModel(null, titles) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // 3. Se itera la lista y se llena el modelo fila por fila.
            for (Caballos_Model caballo : listaCaballos) {
                model.addRow(new Object[]{
                    caballo.getIdcaballos(),
                    caballo.getCaballos(),
                    caballo.getJinete(),
                    caballo.getObservacion(),
                    caballo.getFk_estados() == 1 ? "Activo" : "Inactivo"
                });
            }

            // 4. Se asigna el modelo a la JTable.
            tblCaballos.setModel(model);
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            tblCaballos.setRowSorter(sorter);

            // 5. Se ocultan las columnas necesarias.
            ocultar_columnas();

        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los caballos:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtIdcaballos.setText("");
        txtNumero.setText("Nuevo");
        txtCaballos.setText("");
        txtJinete.setText("");
        atxtObservacion.setText("");
        chbActive.setSelected(false);
        btnSave.setText("Guardar");
        txtCaballos.requestFocus();
    }

    private void ocultar_columnas() {
        tblCaballos.getColumnModel().getColumn(0).setMaxWidth(0);
        tblCaballos.getColumnModel().getColumn(0).setMinWidth(0);
        tblCaballos.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        txtNumero = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtJinete = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtCaballos = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        atxtObservacion = new javax.swing.JTextArea();
        chbActive = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        txtIdcaballos = new javax.swing.JTextField();
        btnImprimir = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtBuscar = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cmbEstado = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCaballos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Caballos");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Número:");

        jLabel3.setText("Jinete:");

        txtJinete.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtJineteKeyPressed(evt);
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

        jLabel4.setText("Caballo:");

        txtCaballos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCaballosKeyPressed(evt);
            }
        });

        jLabel5.setText("Observación:");

        atxtObservacion.setColumns(20);
        atxtObservacion.setRows(5);
        atxtObservacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                atxtObservacionKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(atxtObservacion);

        chbActive.setText("Activo");

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/print_icon16.png"))); // NOI18N
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/excel_icon16.png"))); // NOI18N
        btnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(4, 4, 4)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel3))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtCaballos, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                        .addComponent(txtJinete)))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtIdcaballos, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(chbActive)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnSave)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnCancel))
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(240, 240, 240)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5)
                            .addComponent(txtIdcaballos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCaballos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtJinete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSave)
                        .addComponent(btnCancel)
                        .addComponent(chbActive))
                    .addComponent(btnImprimir)
                    .addComponent(btnExcel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        jLabel6.setText("Buscar:");

        cmbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Activo", "Inactivo" }));
        cmbEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbEstadoActionPerformed(evt);
            }
        });

        jLabel14.setText("Estado:");

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14)
                        .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addComponent(jSeparator2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tblCaballos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblCaballos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblCaballos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCaballosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCaballos);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (txtCaballos.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del caballo no puede estar vacío.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = txtCaballos.getText().trim();
        String jinete = txtJinete.getText().trim();
        String observacion = atxtObservacion.getText().trim();

        try {
            if (txtIdcaballos.getText().trim().isEmpty()) {
                // CREAR
                int fk_estados = 1;
                controller.crearCaballo(nombre, jinete, observacion, fk_estados);
                JOptionPane.showMessageDialog(this, "Caballo creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // ACTUALIZAR
                int fk_estados = chbActive.isSelected() ? 1 : 2;
                int id = Integer.parseInt(txtIdcaballos.getText());
                controller.actualizarCaballo(id, nombre, jinete, observacion, fk_estados);
                JOptionPane.showMessageDialog(this, "Caballo actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            limpiarFormulario();
            actualizarTabla();
        } catch (ServiceException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error de Operación", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tblCaballosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCaballosMouseClicked
        int filaVista = tblCaballos.getSelectedRow();
        if (filaVista == -1) {
            return;
        }

        int filaModelo = tblCaballos.convertRowIndexToModel(filaVista);
        DefaultTableModel model = (DefaultTableModel) tblCaballos.getModel();

        // Se obtienen los valores de forma segura, verificando si son nulos.
        Object idObj = model.getValueAt(filaModelo, 0);
        Object caballoObj = model.getValueAt(filaModelo, 1);
        Object jineteObj = model.getValueAt(filaModelo, 2);
        Object obsObj = model.getValueAt(filaModelo, 3);
        Object estadoObj = model.getValueAt(filaModelo, 4);

        // Se asignan a los campos de texto, usando "" si el valor es nulo.
        txtIdcaballos.setText(idObj != null ? idObj.toString() : "");
        txtNumero.setText(idObj != null ? idObj.toString() : "");
        txtCaballos.setText(caballoObj != null ? caballoObj.toString() : "");
        txtJinete.setText(jineteObj != null ? jineteObj.toString() : "");
        atxtObservacion.setText(obsObj != null ? obsObj.toString() : "");

        String estadoStr = (estadoObj != null) ? estadoObj.toString() : "";
        chbActive.setSelected("Activo".equalsIgnoreCase(estadoStr));

        btnSave.setText("Actualizar");
    }//GEN-LAST:event_tblCaballosMouseClicked

    private void txtCaballosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCaballosKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtCaballos.transferFocus();
            txtJinete.requestFocus();
        }
    }//GEN-LAST:event_txtCaballosKeyPressed

    private void txtJineteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtJineteKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtJinete.transferFocus();
            atxtObservacion.requestFocus();
        }
    }//GEN-LAST:event_txtJineteKeyPressed

    private void atxtObservacionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_atxtObservacionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            atxtObservacion.transferFocus();
            btnSave.requestFocus();
        }
    }//GEN-LAST:event_atxtObservacionKeyPressed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        actualizarTabla();
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void cmbEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEstadoActionPerformed
        // Se guarda el estado del filtro seleccionado.
        this.stateFilter = cmbEstado.getSelectedItem().toString().toLowerCase();
        // Se actualiza la tabla con el nuevo filtro.
        actualizarTabla();
    }//GEN-LAST:event_cmbEstadoActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String currentdate = LocalDate.now().format(fmt);

            URL logoURL = getClass().getResource("/Images/icono5.png");
            String rutaLogo = "";
            if (logoURL == null) {
                throw new FileNotFoundException("No se encontró /Images/icono5.png");
            } else {
                rutaLogo = logoURL.toString();
            }

            URL camaraURL = getClass().getResource("/Images/camara16.png");
            String rutaAbsoluta = "";
            if (camaraURL == null) {
                throw new FileNotFoundException("No se encontró /Images/camara16.png");
            } else {
                rutaAbsoluta = camaraURL.toString();
            }

            String ruta = AppPaths.REPORTS_DIR + File.separator + "Caballos.jrxml";
            File jrxmlFile = new File(ruta);
            InputStream is = new FileInputStream(jrxmlFile);
            if (!jrxmlFile.exists()) {
                throw new FileNotFoundException("No se encontró el .jrxml en: " + ruta);
            }

            DefaultTableModel original = (DefaultTableModel) tblCaballos.getModel();
            int[] colsNoPermitidas = {};
            List<Integer> colsPermitidas = new ArrayList<>();
            outer:
            for (int col = 0; col < original.getColumnCount(); col++) {
                for (int no : colsNoPermitidas) {
                    if (col == no) {
                        continue outer;
                    }
                }
                colsPermitidas.add(col);
            }
            DefaultTableModel filtrado = new DefaultTableModel();
            for (int colIndex : colsPermitidas) {
                filtrado.addColumn(original.getColumnName(colIndex));
            }
            for (int row = 0; row < original.getRowCount(); row++) {
                Object[] fila = new Object[colsPermitidas.size()];
                for (int i = 0; i < colsPermitidas.size(); i++) {

                    // Se obtiene el valor de la celda
                    Object valor = original.getValueAt(row, colsPermitidas.get(i));

                    // CORRECCIÓN: Si el valor es nulo, se usa una cadena vacía "", si no, se usa el valor original.
                    fila[i] = (valor == null) ? "" : valor;
                }
                filtrado.addRow(fila);
            }

            JRTableModelDataSource datasource = new JRTableModelDataSource(filtrado);
            JasperReport jr = JasperCompileManager.compileReport(is);

            Map<String, Object> params = new HashMap<>();
            params.put("Logo", rutaLogo);
            params.put("Currentdate", currentdate);

            final JasperPrint jasperPrint = JasperFillManager.fillReport(jr, params, datasource);

            if (jasperPrint.getPages() == null || jasperPrint.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No hay nada en la tabla para mostrar",
                        "Tabla vacía",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            Container viewerContent = jasperViewer.getContentPane();
            JPanel mainPanel = (JPanel) viewerContent.getComponent(0);
            JRViewer jrViewer = (JRViewer) mainPanel.getComponent(0);
            JRViewerToolbar toolbar = (JRViewerToolbar) jrViewer.getComponent(0);

            JButton btnSavee = (JButton) toolbar.getComponent(0);
            JButton btnPrint = (JButton) toolbar.getComponent(1);
            btnSavee.setText("Guardar");
            btnSavee.setPreferredSize(new Dimension(75, 30));
            btnPrint.setText("Imprimir");
            btnPrint.setPreferredSize(new Dimension(75, 30));

            ImageIcon camaraIcon = new ImageIcon(rutaAbsoluta);
            JButton btnImagen = new JButton("PNG");
            btnImagen.setToolTipText("Guardar reporte como imagen");
            btnImagen.setIcon(camaraIcon);
            btnImagen.setPreferredSize(new Dimension(75, 30));
            btnImagen.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Guardar reporte como PNG");
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); //Sólo archivos
                chooser.setAcceptAllFileFilterUsed(false); //No permitir “Todos los archivos”
                FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG (*.png)", "png"); // Filtro de sólo PNG
                chooser.addChoosableFileFilter(pngFilter);
                chooser.setFileFilter(pngFilter);
                chooser.setApproveButtonText("Guardar"); //Texto del botón en español
                chooser.setApproveButtonToolTipText("Guardar reporte en imágenes PNG");

                if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File selected = chooser.getSelectedFile();
                    String path = selected.getAbsolutePath();
                    if (!path.toLowerCase().endsWith(".png")) {
                        path += ".png";
                        selected = new File(path);
                    }

                    //Generar y guardar cada pagina
                    int total = jasperPrint.getPages().size();
                    float zoomm = 1.0f;
                    for (int i = 0; i < total; i++) {
                        try {
                            Image img = JasperPrintManager.printPageToImage(jasperPrint, i, zoomm);
                            BufferedImage bimg = new BufferedImage(
                                    img.getWidth(null), img.getHeight(null),
                                    BufferedImage.TYPE_INT_ARGB
                            );
                            Graphics2D g = bimg.createGraphics();
                            g.drawImage(img, 0, 0, null);
                            g.dispose();

                            String baseName = selected.getName();
                            baseName = baseName.replaceAll("(?i)\\.png$", "");
                            File outFile = new File(
                                    selected.getParentFile(),
                                    baseName + "_" + (i + 1) + ".png"
                            );

                            ImageIO.write(bimg, "png", outFile);
                        } catch (JRException | IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

            Component[] originales = toolbar.getComponents();

            toolbar.removeAll();

            toolbar.add(btnPrint);
            toolbar.add(Box.createRigidArea(new Dimension(3, 0)));
            toolbar.add(btnSavee);
            toolbar.add(Box.createRigidArea(new Dimension(3, 0)));
            toolbar.add(btnImagen);
            toolbar.add(Box.createRigidArea(new Dimension(3, 0)));

            for (Component c : originales) {
                if (c == btnPrint || c == btnSavee /*|| c == btnImagen?*/) {
                    continue;
                }
                toolbar.add(c);
                toolbar.add(Box.createRigidArea(new Dimension(2, 0)));
            }

            toolbar.revalidate();
            toolbar.repaint();

            JDialog dialog = new JDialog((Frame) null, "Caballos", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.getContentPane().add(
                    jasperViewer.getContentPane(), BorderLayout.CENTER
            );

            dialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

        } catch (JRException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Caballos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        try {
            // 1. Obtener los datos FRESCOS desde el controlador.
            List<Caballos_Model> datos = controller.listarCaballos(txtBuscar.getText(), stateFilter);

            if (datos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay datos para exportar.", "Tabla Vacía", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Pedir al usuario la ubicación del archivo.
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Guardar como Excel");
            chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
            chooser.setSelectedFile(new File("Reporte_Caballos.xlsx"));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return; // El usuario canceló.
            }

            // 3. Preparar los datos en el formato genérico que el exportador necesita.
            List<String> headers = List.of("Caballo", "Jinete", "Observación", "Estado");
            List<List<Object>> dataRows = new ArrayList<>();
            for (Caballos_Model caballo : datos) {
                dataRows.add(Arrays.asList(
                        caballo.getIdcaballos(),
                        caballo.getCaballos(),
                        caballo.getJinete(),
                        caballo.getObservacion(),
                        caballo.getFk_estados() == 1 ? "Activo" : "Inactivo"
                ));
            }

            // 4. Llamar al exportador genérico.
            ExcelExporter.export(headers, dataRows, "Caballos", chooser.getSelectedFile().getAbsolutePath());

            JOptionPane.showMessageDialog(this, "Excel exportado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (ServiceException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error al exportar a Excel:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnExcelActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

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
            java.util.logging.Logger.getLogger(Caballos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Caballos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Caballos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Caballos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Caballos dialog = new Caballos(new javax.swing.JFrame(), true);
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
    private javax.swing.JTextArea atxtObservacion;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnSave;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chbActive;
    private javax.swing.JComboBox<String> cmbEstado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable tblCaballos;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCaballos;
    private javax.swing.JTextField txtIdcaballos;
    private javax.swing.JTextField txtJinete;
    private javax.swing.JTextField txtNumero;
    // End of variables declaration//GEN-END:variables

}
