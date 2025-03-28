package View;

import Config.Run;
import Controller.Carreras_Controller;
import Controller.Detallecarreras_Controller;
import Controller.State_Controller;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Carreras extends javax.swing.JDialog {

    Carreras_Controller Carreras_controller = new Carreras_Controller();
    Detallecarreras_Controller Detallecarreras_controller = new Detallecarreras_Controller();
    DecimalFormat formateador14 = new DecimalFormat("#,###.###");
    LocalDate localDate = LocalDate.now();

    private String id = "";
    private String initialState = "", finalState = "", stateFilter = "todos", fase = "todos";
    private int idestado;
    public static List<String> idCaballosSeleccionados = new ArrayList<>();
    Date currentDate;

    public Carreras(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        txtIdcarreras.setVisible(false);
        txtIdganador.setVisible(false);
        txtNumero.setEditable(false);
        txtNumero.transferFocus();
        txtNombre.requestFocus();
        atxtCaballos.setEditable(false);
        atxtCaballos.setBackground(Color.white);
        currentDate();
        dchFecha.setDate(currentDate);
        txtNumero.setText(String.valueOf(Carreras_controller.getMaxCodigo()));
        txtGanador.setEditable(false);
        txtGanador.setBackground(Color.white);
        showCarreras("", "", stateFilter);
    }

    private void currentDate() {
        currentDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private void showCarreras(String search, String fase, String stateFilter) {
        try {
            DefaultTableModel model;
            model = Carreras_controller.showCarreras(search, fase, stateFilter);
            tblCarreras.setModel(model);
            ocultar_columnas(tblCarreras);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void ocultar_columnas(JTable table) {
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        table.getColumnModel().getColumn(4).setMaxWidth(0);
        table.getColumnModel().getColumn(4).setMinWidth(0);
        table.getColumnModel().getColumn(4).setPreferredWidth(0);

        table.getColumnModel().getColumn(7).setMaxWidth(0);
        table.getColumnModel().getColumn(7).setMinWidth(0);
        table.getColumnModel().getColumn(7).setPreferredWidth(0);

        table.getColumnModel().getColumn(8).setMaxWidth(0);
        table.getColumnModel().getColumn(8).setMinWidth(0);
        table.getColumnModel().getColumn(8).setPreferredWidth(0);
    }

    private void actualizarFiltro() {
        String search = txtBuscar.getText();
        stateFilter = cmbEstado.getSelectedItem().toString().toLowerCase();
        fase = cmbFase.getSelectedItem().toString().toLowerCase();

        System.out.println("Ejecutando showCarreras con:");
        System.out.println("  search: " + search);
        System.out.println("  stateFilter: " + stateFilter);
        System.out.println("  fase: " + fase);

        showCarreras(search, fase, stateFilter);
    }

    private void limpiar() {
        txtIdcarreras.setText("");
        txtNombre.setText("");
        atxtCaballos.setText("");
        txtGanador.setText("");
        txtIdganador.setText("");
        dchFecha.setDate(currentDate);
        txtLugar.setText("");
        atxtObservacion.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        dchFecha = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtLugar = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        atxtObservacion = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        atxtCaballos = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        btnSeleccionarCaballos = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnSeleccionarGanador = new javax.swing.JButton();
        chbActive = new javax.swing.JCheckBox();
        txtIdcarreras = new javax.swing.JTextField();
        txtGanador = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtIdganador = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCarreras = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtBuscar = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cmbFase = new javax.swing.JComboBox<>();
        cmbEstado = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carreras");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        dchFecha.setDateFormatString("dd/MM/yyyy");

        jLabel1.setText("Fecha:");

        jLabel2.setText("Número:");

        jLabel3.setText("Nombre:");

        txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNombreKeyPressed(evt);
            }
        });

        jLabel4.setText("Lugar:");

        txtLugar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtLugarKeyPressed(evt);
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

        jLabel5.setText("Observación:");

        atxtObservacion.setColumns(20);
        atxtObservacion.setRows(5);
        atxtObservacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                atxtObservacionKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(atxtObservacion);

        atxtCaballos.setColumns(20);
        atxtCaballos.setRows(5);
        jScrollPane3.setViewportView(atxtCaballos);

        jLabel6.setText("Ganador:");

        btnSeleccionarCaballos.setText("Seleccionar");
        btnSeleccionarCaballos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarCaballosActionPerformed(evt);
            }
        });

        btnSeleccionarGanador.setText("Seleccionar");
        btnSeleccionarGanador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarGanadorActionPerformed(evt);
            }
        });

        chbActive.setText("Activo");
        chbActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbActiveActionPerformed(evt);
            }
        });

        jLabel7.setText("Caballos:");

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
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel3)
                                .addComponent(jLabel6))
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtIdcarreras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(chbActive)
                                                .addGap(23, 23, 23)
                                                .addComponent(btnSave)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnCancel))
                                            .addComponent(dchFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtLugar, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane3)
                                    .addComponent(txtGanador))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btnSeleccionarGanador)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtIdganador, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btnSeleccionarCaballos)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtIdcarreras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dchFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLugar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSeleccionarCaballos)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGanador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSeleccionarGanador)
                    .addComponent(jLabel6)
                    .addComponent(txtIdganador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnCancel)
                    .addComponent(chbActive))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblCarreras = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblCarreras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblCarreras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCarrerasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCarreras);

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        jLabel8.setText("Buscar:");

        cmbFase.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Pendientes", "Finalizados" }));
        cmbFase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFaseActionPerformed(evt);
            }
        });

        cmbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Activo", "Inactivo" }));
        cmbEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbEstadoActionPerformed(evt);
            }
        });

        jLabel9.setText("Estado:");

        jLabel10.setText("Fase:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscar)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbFase, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbFase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSeleccionarGanadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarGanadorActionPerformed
        String idCarrera = txtNumero.getText().trim();

        if (idCarrera.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una carrera primero", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("idcarrera: " + idCarrera);

        CargarGanador dialog = new CargarGanador(f, true, Integer.parseInt(idCarrera), txtNombre.getText());
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
    }//GEN-LAST:event_btnSeleccionarGanadorActionPerformed

    private void btnSeleccionarCaballosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarCaballosActionPerformed
        CargarCaballos dialog = new CargarCaballos(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
    }//GEN-LAST:event_btnSeleccionarCaballosActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (validateFields()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = sdf.format(dchFecha.getDate());

            if (txtIdcarreras.getText().length() == 0) {
                save(Integer.parseInt(txtNumero.getText()), txtNombre.getText(), txtLugar.getText(), fecha, txtIdganador.getText(), atxtObservacion.getText(), idCaballosSeleccionados);
            } else {
                Integer idGanador = (txtIdganador.getText().trim().isEmpty()) ? null : Integer.valueOf(txtIdganador.getText().trim());
                update(Integer.parseInt(txtNumero.getText()), txtNombre.getText(), txtLugar.getText(), fecha, idGanador, atxtObservacion.getText(), idCaballosSeleccionados);
            }
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tblCarrerasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCarrerasMouseClicked
        int select = tblCarreras.rowAtPoint(evt.getPoint());
        tblCarreras.setRowSelectionInterval(select, select);

        id = tblCarreras.getValueAt(select, 0).toString();
        txtNumero.setText(String.valueOf(tblCarreras.getValueAt(select, 0)));
        txtIdcarreras.setText(String.valueOf(tblCarreras.getValueAt(select, 0)));
        txtNombre.setText(String.valueOf(tblCarreras.getValueAt(select, 1)));
        txtLugar.setText(String.valueOf(tblCarreras.getValueAt(select, 2)));

        String fecha = tblCarreras.getValueAt(select, 3).toString();
        java.util.Date fecha2 = null;
        try {
            fecha2 = new SimpleDateFormat("dd/MM/yyyy").parse(fecha);
        } catch (ParseException ex) {
            Logger.getLogger(Carreras.class.getName()).log(Level.SEVERE, null, ex);
        }
        dchFecha.setDate(fecha2);
        if ("null".equals(String.valueOf(tblCarreras.getValueAt(select, 4)))) {
            txtIdganador.setText("");
        } else {
            txtIdganador.setText(String.valueOf(tblCarreras.getValueAt(select, 4)));
        }

        atxtObservacion.setText(String.valueOf(tblCarreras.getValueAt(select, 5)));

        if ("activo".equals(tblCarreras.getValueAt(select, 6).toString())) {
            chbActive.setSelected(true);
            initialState = tblCarreras.getValueAt(select, 6).toString();
        } else {
            chbActive.setSelected(false);
            initialState = tblCarreras.getValueAt(select, 6).toString();
        }

        idCaballosSeleccionados = Arrays.asList(String.valueOf(tblCarreras.getValueAt(select, 7)).split("\n"));
        System.out.println("Caballos desde jtable" + idCaballosSeleccionados);
        System.out.println("ids: " + idCaballosSeleccionados);

        atxtCaballos.setText(String.valueOf(tblCarreras.getValueAt(select, 8)));
        txtGanador.setText(String.valueOf(tblCarreras.getValueAt(select, 9)));
    }//GEN-LAST:event_tblCarrerasMouseClicked

    private void txtNombreKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtNombre.transferFocus();
            txtLugar.requestFocus();
        }
    }//GEN-LAST:event_txtNombreKeyPressed

    private void txtLugarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLugarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtLugar.transferFocus();
            atxtObservacion.requestFocus();
        }
    }//GEN-LAST:event_txtLugarKeyPressed

    private void atxtObservacionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_atxtObservacionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            atxtObservacion.transferFocus();
            btnSave.requestFocus();
        }
    }//GEN-LAST:event_atxtObservacionKeyPressed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        actualizarFiltro();
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void cmbEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEstadoActionPerformed
        actualizarFiltro();
        System.out.println("cmbEstados: " + stateFilter);
    }//GEN-LAST:event_cmbEstadoActionPerformed

    private void cmbFaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFaseActionPerformed
        actualizarFiltro();
        System.out.println("cmbFase: " + fase);
    }//GEN-LAST:event_cmbFaseActionPerformed

    private void chbActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbActiveActionPerformed
        if (chbActive.isSelected()) {
            finalState = "activo";
        }
        if (!chbActive.isSelected()) {
            finalState = "inactivo";
        }
    }//GEN-LAST:event_chbActiveActionPerformed

    Frame f = JOptionPane.getFrameForComponent(this);

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Carreras dialog = new Carreras(new javax.swing.JFrame(), true);
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

    private boolean validateFields() {
        if (atxtCaballos.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "Cargue al menos un caballo", "Advertencia!", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtNombre.getText().length() == 0) {
            txtNombre.setText("Sin datos");
        }

        if (txtLugar.getText().length() == 0) {
            txtLugar.setText("Sin datos");
        }
        if (txtGanador.getText().length() == 0) {
            txtGanador.setText("Sin datos");
        }
        if (atxtObservacion.getText().length() == 0) {
            atxtObservacion.setText("Sin datos");
        }
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JTextArea atxtCaballos;
    private javax.swing.JTextArea atxtObservacion;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSeleccionarCaballos;
    private javax.swing.JButton btnSeleccionarGanador;
    private javax.swing.JCheckBox chbActive;
    private javax.swing.JComboBox<String> cmbEstado;
    private javax.swing.JComboBox<String> cmbFase;
    private com.toedter.calendar.JDateChooser dchFecha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tblCarreras;
    private javax.swing.JTextField txtBuscar;
    public static javax.swing.JTextField txtGanador;
    private javax.swing.JTextField txtIdcarreras;
    public static javax.swing.JTextField txtIdganador;
    private javax.swing.JTextField txtLugar;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNumero;
    // End of variables declaration//GEN-END:variables

    private void save(int idCarrera, String nombre, String lugar, String fecha, String idganador, String observacion, List<String> idCaballos) {
        finalState = "activo";
        idestado = State_Controller.getEstadoId(finalState, Run.model);

        Carreras_controller.createCarreras(nombre, lugar, fecha, observacion, idestado);

        for (int i = 0; i < idCaballos.size(); i++) {
            System.out.println("idcaballos: " + idCaballos.get(i));
            Detallecarreras_controller.createDetallecarreras(idCarrera, Integer.parseInt(idCaballos.get(i)));
        }

        JOptionPane.showMessageDialog(null, "Carrera ingresada exitosamente!", "Hecho!", JOptionPane.INFORMATION_MESSAGE);
        limpiar();
        txtNumero.setText(String.valueOf(Carreras_controller.getMaxCodigo()));
        txtNombre.requestFocus();
        actualizarFiltro();
    }

    private void update(int idCarrera, String nombre, String lugar, String fecha, Integer idganador, String observacion, List<String> idCaballos) {
        if (initialState.equals("activo") && finalState.equals("inactivo")) {
            if (txtIdcarreras.getText().length() == 0) {
                JOptionPane.showMessageDialog(null, "Seleccione una carrera para desactivar.", "Advertencia!", JOptionPane.WARNING_MESSAGE);
            } else {
                String[] opciones = {"Sí", "No"};
                int respuesta = JOptionPane.showOptionDialog(
                        this,
                        "La carrera será desactivada",
                        "Desactivar?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                );

                if (respuesta == JOptionPane.YES_OPTION) {
                    idestado = State_Controller.getEstadoId(finalState, Run.model);
                }
            }
        } else if (initialState.equals("inactivo") && finalState.equals("activo")) {
            if (txtIdcarreras.getText().length() == 0) {
                JOptionPane.showMessageDialog(null, "Seleccione una carrera para activar.", "Advertencia!", JOptionPane.WARNING_MESSAGE);
            } else {
                String[] opciones = {"Sí", "No"};
                int respuesta = JOptionPane.showOptionDialog(
                        this,
                        "La apuesta será activada",
                        "Activar?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                );

                if (respuesta == JOptionPane.YES_OPTION) {
                    idestado = State_Controller.getEstadoId(finalState, Run.model);
                }
            }
        } else {
            idestado = State_Controller.getEstadoId(initialState, Run.model);
        }

        Carreras_controller.updateCarreras(idCarrera, nombre, lugar, fecha, idganador, observacion, idestado);
        Detallecarreras_controller.deleteDetallecarreras(idCarrera);
        for (int i = 0; i < idCaballos.size(); i++) {
            System.out.println("idcaballos en for de update: " + idCaballos.get(i));

            Detallecarreras_controller.createDetallecarreras(idCarrera, Integer.parseInt(idCaballos.get(i)));
        }

        JOptionPane.showMessageDialog(null, "Carrera actualizada exitosamente!", "Hecho!", JOptionPane.INFORMATION_MESSAGE);
        limpiar();
        txtNumero.setText(String.valueOf(Carreras_controller.getMaxCodigo()));
        txtNombre.requestFocus();
        actualizarFiltro();
    }
}
