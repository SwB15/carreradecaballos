package View;

import Config.ApuestasRenderer;
import Config.Run;
import Controller.Apuestas_Controller;
import Controller.State_Controller;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Apuestas extends javax.swing.JDialog {

    Apuestas_Controller apuestas_controller = new Apuestas_Controller();
    DecimalFormat formateador14 = new DecimalFormat("#,###.###");
    LocalDate localDate = LocalDate.now();
    private HashMap<String, String> ApostadoresMap, CarrerasMap, CaballosMap;
    private String id = "";
    private int idestado;
    private String initialState = "", finalState = "", stateFilter = "todos";
    Date startDate = null, endDate = null;
    Date currentDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    public Apuestas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        txtIdapostadores.setVisible(false);
        txtIdcarreras.setVisible(false);
        txtIdapuestas.setVisible(false);
        txtIdcaballos.setVisible(false);
        dchFecha.setDate(currentDate);
        dchFechalimite.setDate(currentDate);
        txtNumero.setText(String.valueOf(apuestas_controller.getMaxCodigo()));
        txtNumero.setEditable(false);
        txtNumero.transferFocus();
        txtNombre.requestFocus();
        apostadoresCombobox();
        carrerasCombobox();
        showApuestas("", stateFilter, startDate, endDate);
    }

    private void showApuestas(String search, String stateFilter, Date startDate, Date endDate) {
        try {
            DefaultTableModel model;
            model = apuestas_controller.showApuestas(search, stateFilter, startDate, endDate);
            tblApuestas.setModel(model);
            ocultar_columnas(tblApuestas);
            for (int i = 0; i < tblApuestas.getColumnCount(); i++) {
                tblApuestas.getColumnModel().getColumn(i).setCellRenderer(new ApuestasRenderer());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void ocultar_columnas(JTable table) {
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        table.getColumnModel().getColumn(9).setMaxWidth(0);
        table.getColumnModel().getColumn(9).setMinWidth(0);
        table.getColumnModel().getColumn(9).setPreferredWidth(0);
    }

    private void limpiar() {
        txtIdapuestas.setText("");
        txtNombre.setText("");
        txtMonto.setText("");
        txtAbonado.setText("");
        atxtObservacion.setText("");

        cmbApostadores.setSelectedItem(0);
        cmbCarreras.setSelectedItem(0);
    }

    // Método para cargar los apostadores en el ComboBox
    private void apostadoresCombobox() {
        // Obtenemos el HashMap con los Apostadores (ID -> nombre)
        ApostadoresMap = apuestas_controller.fillApostadoresCombobox();

        cmbApostadores.removeAllItems();

        // Agregamos los nombres de los Apostadores al ComboBox
        for (String nombre : ApostadoresMap.values()) {
            cmbApostadores.addItem(nombre);
        }

        // Si el ComboBox tiene elementos, seleccionamos el primero automáticamente y actualizamos el ID
        if (cmbApostadores.getItemCount() > 0) {
            String firstNombre = (String) cmbApostadores.getItemAt(0); // Primer caballo en el ComboBox
            for (Map.Entry<String, String> entry : ApostadoresMap.entrySet()) {
                if (entry.getValue().equals(firstNombre)) {
                    // Actualizamos el TextField con el ID del primer caballo
                    txtIdapostadores.setText(entry.getKey());
                    break;
                }
            }
        }

        // Listener para cmbApostadores (para actualizar el ID al seleccionar un nuevo caballo)
        cmbApostadores.addActionListener((ActionEvent e) -> {
            String selectedNombre = (String) cmbApostadores.getSelectedItem();
            if (selectedNombre != null) {
                // Buscamos el ID del caballo seleccionado en el HashMap
                for (Map.Entry<String, String> entry : ApostadoresMap.entrySet()) {
                    if (entry.getValue().equals(selectedNombre)) {
                        // Actualizamos el TextField con el ID del caballo
                        txtIdapostadores.setText(entry.getKey());
                        System.out.println("ID Caballo: " + entry.getKey());
                        break;
                    }
                }
            }
        });
    }

    // Método para cargar las carreras en el ComboBox
    private void carrerasCombobox() {
        // Obtenemos el HashMap con los Carreras (ID -> nombre)
        CarrerasMap = apuestas_controller.fillCarrerasCombobox();

        cmbCarreras.removeAllItems();
        // Agregamos los nombres de los Carreras al ComboBox
        for (String nombre : CarrerasMap.values()) {
            cmbCarreras.addItem(nombre);
        }

        // Si el ComboBox tiene elementos, seleccionamos el primero automáticamente y actualizamos el ID
        if (cmbCarreras.getItemCount() > 0) {
            String firstNombre = (String) cmbCarreras.getItemAt(0);
            for (Map.Entry<String, String> entry : CarrerasMap.entrySet()) {
                if (entry.getValue().equals(firstNombre)) {
                    txtIdcarreras.setText(entry.getKey());
                    caballosCombobox(Integer.parseInt(entry.getKey()));
                    break;
                }
            }
        }

        // Listener para cmbCarreras (para actualizar el ID al seleccionar un nuevo caballo)
        cmbCarreras.addActionListener((ActionEvent e) -> {
            String selectedNombre = (String) cmbCarreras.getSelectedItem();
            if (selectedNombre != null) {
                for (Map.Entry<String, String> entry : CarrerasMap.entrySet()) {
                    if (entry.getValue().equals(selectedNombre)) {
                        txtIdcarreras.setText(entry.getKey());
                        caballosCombobox(Integer.parseInt(entry.getKey()));
                        System.out.println("ID Caballo: " + entry.getKey());
                        break;
                    }
                }
            }
        });
    }

    // Método para cargar las carreras en el ComboBox
    private void caballosCombobox(int idcarreras) {
        CaballosMap = apuestas_controller.fillCaballosCombobox(idcarreras);

        cmbCaballos.removeAllItems();

        // Agregamos los nombres de los Caballos al ComboBox
        for (String nombre : CaballosMap.values()) {
            cmbCaballos.addItem(nombre);
        }

        // Si el ComboBox tiene elementos, seleccionamos el primero automáticamente y actualizamos el ID
        if (cmbCaballos.getItemCount() > 0) {
            String firstNombre = (String) cmbCaballos.getItemAt(0); // Primer caballo en el ComboBox
            for (Map.Entry<String, String> entry : CaballosMap.entrySet()) {
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
                for (Map.Entry<String, String> entry : CaballosMap.entrySet()) {
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

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        dchFecha = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtMonto = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cmbApostadores = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cmbCarreras = new javax.swing.JComboBox<>();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        atxtObservacion = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        chbActive = new javax.swing.JCheckBox();
        txtIdapostadores = new javax.swing.JTextField();
        txtIdcarreras = new javax.swing.JTextField();
        txtIdapuestas = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cmbCaballos = new javax.swing.JComboBox<>();
        txtIdcaballos = new javax.swing.JTextField();
        txtAbonado = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        dchFechalimite = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblApuestas = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        txtBuscar = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        dchHasta = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        dchDesde = new com.toedter.calendar.JDateChooser();
        cmbEstado = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        btnMostrar = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Apuestas");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setText("Número:");

        jLabel1.setText("Fecha:");

        dchFecha.setDateFormatString("dd/MM/yyyy");

        jLabel3.setText("Apuesta:");

        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });

        txtMonto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMontoActionPerformed(evt);
            }
        });
        txtMonto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMontoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtMontoKeyTyped(evt);
            }
        });

        jLabel4.setText("Monto:");

        jLabel5.setText("Apostador:");

        cmbApostadores.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Apostador 1", "Apostador2", "Apostador 3", "Apostador 4", "Apostador 5", "Apostador 6" }));

        jLabel6.setText("Carrera:");

        cmbCarreras.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Carrera 1", "Carrera 2", "Carrera 3", "Carrera 4", "Carrera 5", "Carrera 6" }));

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

        atxtObservacion.setColumns(20);
        atxtObservacion.setRows(5);
        atxtObservacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                atxtObservacionKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(atxtObservacion);

        jLabel7.setText("Observación:");

        chbActive.setText("Activo");
        chbActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbActiveActionPerformed(evt);
            }
        });

        jLabel8.setText("Caballos:");

        cmbCaballos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Caballo 1", "Caballo 2", "Caballo 3", "Caballo 4", "Caballo 5", "Caballo 6" }));

        txtAbonado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAbonadoActionPerformed(evt);
            }
        });
        txtAbonado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAbonadoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtAbonadoKeyTyped(evt);
            }
        });

        jLabel9.setText("Abonado:");

        jLabel15.setText("Fecha Limite:");

        dchFechalimite.setDateFormatString("dd/MM/yyyy");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txtIdapostadores, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIdcarreras, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIdcaballos, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(chbActive)
                                .addGap(26, 26, 26)
                                .addComponent(btnSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancel))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtAbonado, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel5)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cmbApostadores, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(txtIdapuestas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jLabel8)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(cmbCaballos, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jLabel6)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(cmbCarreras, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                                        .addComponent(jLabel7)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dchFechalimite, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dchFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(129, 129, 129))))
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dchFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(jLabel1)
                                .addComponent(jLabel6)
                                .addComponent(cmbCarreras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dchFechalimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cmbCaballos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdapuestas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbApostadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(jLabel7))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAbonado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnSave)
                    .addComponent(chbActive)
                    .addComponent(txtIdapostadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdcarreras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdcaballos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tblApuestas = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblApuestas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblApuestas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblApuestasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblApuestas);

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        jLabel11.setText("Buscar:");

        dchHasta.setDateFormatString("dd/MM/yyyy");

        jLabel12.setText("Hasta:");

        jLabel13.setText("Desde:");

        dchDesde.setDateFormatString("dd/MM/yyyy");

        cmbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Activo", "Inactivo" }));
        cmbEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbEstadoActionPerformed(evt);
            }
        });

        jLabel14.setText("Estado:");

        btnMostrar.setText("Mostrar");
        btnMostrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMostrarActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dchDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dchHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMostrar)
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnMostrar)
                                .addComponent(jLabel12)
                                .addComponent(dchHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(dchDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11)
                                .addComponent(jLabel14)
                                .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaStr = sdf.format(dchFecha.getDate());
        String fechalimiteStr = sdf.format(dchFechalimite.getDate());

        // Formateadores para convertir los Strings a LocalDate y LocalTime
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Convertir los Strings a LocalDate y LocalTime
        LocalDate fecha = LocalDate.parse(fechaStr, formatoFecha);
        LocalDate fechalimite = LocalDate.parse(fechalimiteStr, formatoFecha);

        // Formatearlo a String en formato compatible con MySQL (yyyy-MM-dd HH:mm:ss)
        DateTimeFormatter formatoMySQL = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fechaFinal = fecha.format(formatoMySQL);
        String fechaLimitefinal = fechalimite.format(formatoMySQL);
        System.out.println("fechalimite: " + fechaLimitefinal);

        if (validateFields()) {
            if (txtIdapuestas.getText().length() == 0) {
                save(Integer.parseInt(txtNumero.getText()), txtNombre.getText(), Integer.parseInt(txtMonto.getText().replace(".", "")), Integer.parseInt(txtAbonado.getText().replace(".", "")), fechaFinal, fechaLimitefinal, atxtObservacion.getText(), Integer.parseInt(txtIdcarreras.getText()), Integer.parseInt(txtIdcaballos.getText()), Integer.parseInt(txtIdapostadores.getText()));
            } else {
                update(Integer.parseInt(txtIdapuestas.getText()), txtNombre.getText(), Integer.parseInt(txtMonto.getText().replace(".", "")), Integer.parseInt(txtAbonado.getText().replace(".", "")), fechaFinal, fechaLimitefinal, atxtObservacion.getText(), Integer.parseInt(txtIdcarreras.getText()), Integer.parseInt(txtIdcaballos.getText()), Integer.parseInt(txtIdapostadores.getText()));
            }
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void tblApuestasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblApuestasMouseClicked
        int select = tblApuestas.rowAtPoint(evt.getPoint());
        tblApuestas.setRowSelectionInterval(select, select);

        id = tblApuestas.getValueAt(select, 0).toString();
        txtNumero.setText(String.valueOf(tblApuestas.getValueAt(select, 0)));
        txtIdapuestas.setText(String.valueOf(tblApuestas.getValueAt(select, 0)));
        txtNombre.setText(String.valueOf(tblApuestas.getValueAt(select, 1)));

        if (tblApuestas.getValueAt(select, 2).toString().length() != 0) {
            txtMonto.setText(formateador14.format(Integer.parseInt(tblApuestas.getValueAt(select, 2).toString())));
        } else {
            txtMonto.setText("0");
        }

        if (tblApuestas.getValueAt(select, 3).toString().length() != 0) {
            txtAbonado.setText(formateador14.format(Integer.parseInt(tblApuestas.getValueAt(select, 3).toString())));
        } else {
            txtAbonado.setText("0");
        }

        String fecha = tblApuestas.getValueAt(select, 4) != null ? tblApuestas.getValueAt(select, 4).toString() : "";
        String fechalimite = tblApuestas.getValueAt(select, 5) != null ? tblApuestas.getValueAt(select, 5).toString() : "";

        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

        try {
            // Si fecha es nula o vacía, usa la fecha actual
            Date fecha2 = (!fecha.isEmpty()) ? formatoFecha.parse(fecha) : currentDate;
            dchFecha.setDate(fecha2);

            // Si fechalimite es nula o vacía, usa la fecha actual
            Date fechalimite2 = (!fechalimite.isEmpty()) ? formatoFecha.parse(fechalimite) : currentDate;
            dchFechalimite.setDate(fechalimite2);

        } catch (ParseException ex) {
            Logger.getLogger(Carreras.class.getName()).log(Level.SEVERE, null, ex);
        }

        atxtObservacion.setText(String.valueOf(tblApuestas.getValueAt(select, 6)));
        cmbApostadores.setSelectedItem(String.valueOf(tblApuestas.getValueAt(select, 7)));
        cmbCarreras.setSelectedItem(String.valueOf(tblApuestas.getValueAt(select, 8)));
        cmbCaballos.setSelectedItem(String.valueOf(tblApuestas.getValueAt(select, 9)));

        if ("activo".equals(tblApuestas.getValueAt(select, 10).toString())) {
            chbActive.setSelected(true);
            initialState = tblApuestas.getValueAt(select, 10).toString();
        } else {
            chbActive.setSelected(false);
            initialState = tblApuestas.getValueAt(select, 10).toString();
        }

        showApuestas("", stateFilter, startDate, endDate);
    }//GEN-LAST:event_tblApuestasMouseClicked

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

    private void txtMontoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMontoKeyReleased
        if (txtMonto.getText().length() > 3) {
            String cadena = txtMonto.getText().replace(".", "");
            txtMonto.setText(formateador14.format(Integer.parseInt(cadena)));
        }
    }//GEN-LAST:event_txtMontoKeyReleased

    private void txtAbonadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAbonadoKeyReleased
        if (txtAbonado.getText().length() > 3) {
            String cadena = txtAbonado.getText().replace(".", "");
            txtAbonado.setText(formateador14.format(Integer.parseInt(cadena)));
        }
    }//GEN-LAST:event_txtAbonadoKeyReleased

    private void txtAbonadoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAbonadoKeyTyped
        char validar = evt.getKeyChar();
        if (Character.isLetter(validar)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(null, "Ingrese solo números", "Advertencia!", JOptionPane.WARNING_MESSAGE);
        }

        int numerocaracteres = 10;
        if (txtAbonado.getText().length() > numerocaracteres) {
            evt.consume();
            JOptionPane.showMessageDialog(null, "No ingrese tantos números", "Advertencia!", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_txtAbonadoKeyTyped

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        showApuestas(txtBuscar.getText(), stateFilter, startDate, endDate);
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void cmbEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEstadoActionPerformed
        stateFilter = cmbEstado.getSelectedItem().toString().toLowerCase();
        showApuestas(txtBuscar.getText(), stateFilter, startDate, endDate);
    }//GEN-LAST:event_cmbEstadoActionPerformed

    private void btnMostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarActionPerformed
        txtBuscar.setText("");
        cmbEstado.setSelectedItem("Todos");

        SimpleDateFormat formatoBD = new SimpleDateFormat("yyyy/MM/dd");
        Date fechaInicioChooser = dchDesde.getDate();
        Date fechaFinChooser = dchHasta.getDate();

        if (fechaInicioChooser != null && fechaFinChooser != null) {
            if (fechaInicioChooser.compareTo(fechaFinChooser) > 0) {
                JOptionPane.showMessageDialog(null, "La fecha de inicio no puede ser mayor que la fecha de fin.", "Error de Fecha", JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    startDate = formatoBD.parse(formatoBD.format(fechaInicioChooser));
                    endDate = formatoBD.parse(formatoBD.format(fechaFinChooser));
                    showApuestas(txtBuscar.getText(), stateFilter, startDate, endDate);
                } catch (ParseException ex) {
                    Logger.getLogger(Apuestas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnMostrarActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        txtNombre.transferFocus();
        txtMonto.requestFocus();
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtMontoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMontoActionPerformed
        txtMonto.transferFocus();
        txtAbonado.requestFocus();
    }//GEN-LAST:event_txtMontoActionPerformed

    private void txtAbonadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAbonadoActionPerformed
        txtAbonado.transferFocus();
        atxtObservacion.requestFocus();
    }//GEN-LAST:event_txtAbonadoActionPerformed

    private void atxtObservacionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_atxtObservacionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            atxtObservacion.transferFocus();
            btnSave.requestFocus();
        }
    }//GEN-LAST:event_atxtObservacionKeyPressed

    private void chbActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbActiveActionPerformed
        if (chbActive.isSelected()) {
            finalState = "activo";
        }
        if (!chbActive.isSelected()) {
            finalState = "inactivo";
        }
    }//GEN-LAST:event_chbActiveActionPerformed

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
            java.util.logging.Logger.getLogger(Apuestas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Apuestas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Apuestas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Apuestas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Apuestas dialog = new Apuestas(new javax.swing.JFrame(), true);
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
        if (cmbCarreras.getItemCount() == 0) {
            JOptionPane.showMessageDialog(null, "Cargue una carrera antes de cargar las apuestas", "Advertencia!", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (cmbCaballos.getItemCount() == 0) {
            JOptionPane.showMessageDialog(null, "Cargue un caballo antes de cargar las apuestas", "Advertencia!", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (cmbApostadores.getItemCount() == 0) {
            JOptionPane.showMessageDialog(null, "Cargue un apostador antes de cargar las apuestas", "Advertencia!", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtNombre.getText().length() == 0) {
            txtNombre.setText("Sin datos");
        }

        if (txtMonto.getText().length() == 0) {
            txtMonto.setText("0");
        }

        if (txtAbonado.getText().length() == 0) {
            txtAbonado.setText("0");
        }

        if (atxtObservacion.getText().length() == 0) {
            atxtObservacion.setText("Sin datos");
        }
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea atxtObservacion;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnMostrar;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chbActive;
    private javax.swing.JComboBox<String> cmbApostadores;
    private javax.swing.JComboBox<String> cmbCaballos;
    private javax.swing.JComboBox<String> cmbCarreras;
    private javax.swing.JComboBox<String> cmbEstado;
    private com.toedter.calendar.JDateChooser dchDesde;
    private com.toedter.calendar.JDateChooser dchFecha;
    private com.toedter.calendar.JDateChooser dchFechalimite;
    private com.toedter.calendar.JDateChooser dchHasta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable tblApuestas;
    private javax.swing.JTextField txtAbonado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtIdapostadores;
    private javax.swing.JTextField txtIdapuestas;
    private javax.swing.JTextField txtIdcaballos;
    private javax.swing.JTextField txtIdcarreras;
    private javax.swing.JTextField txtMonto;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNumero;
    // End of variables declaration//GEN-END:variables

    private void save(int id, String apuesta, int monto, int abonado, String fecha, String fechalimite, String observacion, int fk_carreras, int fk_caballos, int fk_apostadores) {
        finalState = "activo";
        idestado = State_Controller.getEstadoId(finalState, Run.model);

        apuestas_controller.createApuestas(apuesta, monto, abonado, fecha, fechalimite, observacion, fk_carreras, fk_caballos, fk_apostadores, idestado);

        JOptionPane.showMessageDialog(null, "Apuesta ingresada exitosamente!", "Hecho!", JOptionPane.INFORMATION_MESSAGE);
        limpiar();
        txtNumero.setText(String.valueOf(apuestas_controller.getMaxCodigo()));
        showApuestas("", stateFilter, startDate, endDate);
    }

    private void update(int id, String apuesta, int monto, int abonado, String fecha, String fechalimite, String observacion, int fk_carreras, int fk_caballos, int fk_apostadores) {
        if (initialState.equals("activo") && finalState.equals("inactivo")) {
            if (txtIdapuestas.getText().length() == 0) {
                JOptionPane.showMessageDialog(null, "Seleccione una apuesta para desactivar.", "Advertencia!", JOptionPane.WARNING_MESSAGE);
            } else {
                String[] opciones = {"Sí", "No"};
                int respuesta = JOptionPane.showOptionDialog(this, "La apuesta será desactivada", "Desactivar?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, opciones, opciones[0]
                );

                if (respuesta == JOptionPane.YES_OPTION) {
                    idestado = State_Controller.getEstadoId(finalState, Run.model);
                }
            }
        } else if (initialState.equals("inactivo") && finalState.equals("activo")) {
            if (txtIdapuestas.getText().length() == 0) {
                JOptionPane.showMessageDialog(null, "Seleccione una apuesta para activar.", "Advertencia!", JOptionPane.WARNING_MESSAGE);
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

        apuestas_controller.updateApuestas(id, apuesta, monto, abonado, fecha, fechalimite, observacion, fk_carreras, fk_caballos, fk_apostadores, idestado);

        JOptionPane.showMessageDialog(null, "Apuesta actualizada exitosamente!", "Hecho!", JOptionPane.INFORMATION_MESSAGE);
        limpiar();
        txtNumero.setText(String.valueOf(apuestas_controller.getMaxCodigo()));
        showApuestas("", stateFilter, startDate, endDate);
    }
}
