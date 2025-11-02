package View;

import Config.AppPaths;
import Controller.Abonos_Controller;
import Controller.Apostadores_Controller;
import Model.Detalle_Pendientes;
import Model.Resultado_Pendientes;
import Services.Exceptions.ServiceException;
import Utils.Export_Excel;
import java.awt.BorderLayout;
import java.awt.Color;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
public class Pendientes extends javax.swing.JDialog {

    private final Apostadores_Controller apostadoresController;
    private final Abonos_Controller abonosController;
    private final int idApostador;
    private boolean seHizoUnCambio = false; // Flag para notificar a la ventana padre si debe actualizarse
    private List<Detalle_Pendientes> listaActualPendientes = new ArrayList<>();

    public Pendientes(Frame parent, boolean modal, int idApostador, String nombre, String cedula) {
        super(parent, modal);
        setTitle("Deudas Pendientes de " + nombre);
        initComponents(); // Método que crea los componentes de Swing.
        this.setLocationRelativeTo(null);

        // Rellenamos la info del apostador{
        txtId.setText(String.valueOf(idApostador));
        txtNombre.setText(nombre);
        txtCedula.setText(cedula);

        txtId.transferFocus();
        txtNombre.requestFocus();

        txtId.setEditable(false);

        txtCedula.setEditable(false);
        txtCedula.setBackground(Color.white);

        txtNombre.setEditable(false);
        txtNombre.setBackground(Color.white);

        txtCarreras.setEditable(false);
        txtCarreras.setBackground(Color.white);

        this.apostadoresController = new Apostadores_Controller();
        this.abonosController = new Abonos_Controller();
        this.idApostador = idApostador;

        // Rellenar info del apostador
        txtId.setText(String.valueOf(idApostador));
        txtNombre.setText(nombre);
        txtCedula.setText(cedula);

        // Cargar los datos de las deudas
        cargarDatosPendientes();
    }

    /**
     * Devuelve true si se realizó al menos un abono, para que la ventana que lo
     * llamó sepa que debe refrescar sus datos.
     *
     * @return
     */
    public boolean seHizoUnCambio() {
        return seHizoUnCambio;
    }

    private void limpiar() {
        txtCarreras.setText("");
        txtAbonar.setText("");
    }

    private void cargarDatosPendientes() {
        try {
            Resultado_Pendientes resultado = apostadoresController.consultarDeudas(this.idApostador);
            this.listaActualPendientes = resultado.getDetalles();

            String[] columnNames = {"ID Apuesta", "Carrera", "Caballo", "Apostado", "Abonado", "Pendiente", "EstadoDeuda"};
            DefaultTableModel model = new DefaultTableModel(null, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex >= 3 && columnIndex <= 5) {
                        return Number.class; // Se definen las columnas numéricas
                    }
                    return String.class;
                }
            };

            for (Detalle_Pendientes detalle : resultado.getDetalles()) {
                model.addRow(new Object[]{
                    detalle.getIdApuesta(),
                    detalle.getNombreCarrera(),
                    detalle.getNombreCaballo(),
                    detalle.getMontoApostado(),
                    detalle.getMontoAbonado(),
                    detalle.getMontoPendiente(),
                    detalle.getEstadoDeuda()
                });
            }

            tblPendientes.setModel(model);

            // Se le dice a la JTable que use nuestro renderer específicamente
            // para las columnas que marcamos como Number.
            Pendientes_TableCellRenderer renderer = new Pendientes_TableCellRenderer();
            tblPendientes.setDefaultRenderer(Number.class, renderer);
            tblPendientes.setDefaultRenderer(Object.class, renderer);
            
            ocultar_columnas(tblPendientes);
            // Formatear el total.
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            DecimalFormat formateador = new DecimalFormat("#,###", symbols);
            txtTotal.setText(formateador.format(resultado.getTotalPendiente()));

        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las deudas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ocultar_columnas(JTable table) {
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setPreferredWidth(0);
    }

    // Método auxiliar para calcular el color
    private String calcularEstadoColor(LocalDate fechaLimite) {
        if (fechaLimite == null) {
            return ""; // Sin fecha, sin color
        }
        LocalDate hoy = LocalDate.now();
        long dias = ChronoUnit.DAYS.between(hoy, fechaLimite);

        if (dias < 0) {
            return "ROJO";
        } else if (dias <= 1) {
            return "AMARILLO";
        } else {
            return "VERDE";
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPendientes = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCedula = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        txtCarreras = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtAbonar = new javax.swing.JTextField();
        btnAbonar = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        btnImprimir = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tblPendientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblPendientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPendientesMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tblPendientesMouseEntered(evt);
            }
        });
        jScrollPane1.setViewportView(tblPendientes);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Id:");

        txtId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdActionPerformed(evt);
            }
        });

        jLabel2.setText("Nombre:");

        jLabel6.setText("Cedula:");

        txtCedula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCedulaActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        txtCarreras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCarrerasActionPerformed(evt);
            }
        });

        jLabel3.setText("Carrera:");

        jLabel4.setText("Total:");

        txtTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalActionPerformed(evt);
            }
        });

        jLabel5.setText("Abonar");

        txtAbonar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAbonarActionPerformed(evt);
            }
        });
        txtAbonar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAbonarKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAbonarKeyReleased(evt);
            }
        });

        btnAbonar.setText("Abonar");
        btnAbonar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbonarActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel1))
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(166, 166, 166)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(76, 76, 76)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtCarreras, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtAbonar, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAbonar))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(txtCarreras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtAbonar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5)
                                    .addComponent(btnAbonar)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6)))))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnImprimir)
                    .addComponent(btnExcel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdActionPerformed

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        DefaultTableModel model = (DefaultTableModel) tblPendientes.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "La tabla está vacía. No se puede exportar un Excel sin datos.",
                    "Tabla vacía",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        String fechaStr = sdf.format(dchFecha.getDate());
//        String fechalimiteStr = sdf.format(dchFechalimite.getDate());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String currentdate = LocalDate.now().format(fmt);

        List<Object[]> resumen = List.of(
                new Object[]{"Id N°: ", txtId.getText()},
                new Object[]{"Nombre: ", txtNombre.getText()},
                new Object[]{"Cedula: ", txtCedula.getText()},
                new Object[]{"Total: ", txtTotal.getText()}
        );

        Set<Integer> columnsToSkip = Set.of(0);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Excel");
        chooser.setApproveButtonText("Guardar");

        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        chooser.setSelectedFile(new File("Apuestas pendientes de cobro.xlsx"));

        int opcion = chooser.showSaveDialog(this);
        if (opcion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File destino = chooser.getSelectedFile();
        // Asegúrate de que la extensión .xlsx esté
        if (!destino.getName().toLowerCase().endsWith(".xlsx")) {
            destino = new File(destino.getParentFile(), destino.getName() + ".xlsx");
        }

        try {
            Export_Excel.export(tblPendientes,
                    "Apuestas pendientes de cobro",
                    destino.getAbsolutePath(),
                    columnsToSkip, resumen);
            // Abrir automáticamente
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(destino);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al exportar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnExcelActionPerformed

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

            String ruta = AppPaths.REPORTS_DIR + File.separator + "Pendientes.jrxml";
            File jrxmlFile = new File(ruta);
            InputStream is = new FileInputStream(jrxmlFile);
            if (!jrxmlFile.exists()) {
                throw new FileNotFoundException("No se encontró el .jrxml en: " + ruta);
            }

            DefaultTableModel original = (DefaultTableModel) tblPendientes.getModel();
            int[] colsNoPermitidas = {0};
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

            // Se crea un formateador para los números, forzando el punto como separador.
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            DecimalFormat formateador = new DecimalFormat("#,###", symbols);

// Bucle que recorre las filas del modelo original
            for (int row = 0; row < original.getRowCount(); row++) {
                Object[] fila = new Object[colsPermitidas.size()];
                for (int i = 0; i < colsPermitidas.size(); i++) {

                    int originalColIndex = colsPermitidas.get(i);
                    Object valor = original.getValueAt(row, originalColIndex);

                    // --- INICIO DE LA CORRECCIÓN ---
                    // Se comprueba si la columna es una de las que se deben formatear.
                    // Los índices 3, 4 y 5 corresponden a "Apostado", "Abonado" y "Pendiente" en tu tabla original.
                    if ((originalColIndex == 3 || originalColIndex == 4 || originalColIndex == 5) && valor instanceof Number) {
                        // Si es un número en una de esas columnas, se formatea.
                        fila[i] = formateador.format(valor);
                    } else {
                        // Si no, se convierte a String (convirtiendo null en vacío).
                        fila[i] = (valor == null) ? "" : valor.toString();
                    }
                    // --- FIN DE LA CORRECCIÓN ---
                }
                filtrado.addRow(fila);
            }

            JRTableModelDataSource datasource = new JRTableModelDataSource(filtrado);
            JasperReport jr = JasperCompileManager.compileReport(is);

            Map<String, Object> params = new HashMap<>();
            params.put("Logo", rutaLogo);
            params.put("Nombre", txtNombre.getText());
            params.put("Cedula", txtCedula.getText());
            params.put("Total", txtTotal.getText());
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
            JDialog dialog = new JDialog((Frame) null, "Pendientes", true);
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
            Logger.getLogger(NewApuestas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void txtCarrerasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCarrerasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCarrerasActionPerformed

    private void txtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalActionPerformed

    private void txtAbonarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAbonarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAbonarActionPerformed

    private void txtCedulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCedulaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCedulaActionPerformed

    private void tblPendientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPendientesMouseClicked
        int filaSeleccionada = tblPendientes.getSelectedRow();

        if (filaSeleccionada != -1) {
            String nombreCarrera = tblPendientes.getValueAt(filaSeleccionada, 0).toString();
            txtCarreras.setText(nombreCarrera);
        }
    }//GEN-LAST:event_tblPendientesMouseClicked

    private void btnAbonarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbonarActionPerformed
        // --- 1. Validar y obtener montos ---
        int montoParaAbonar;
        int montoTotalDeuda;
        try {
            montoParaAbonar = Integer.parseInt(txtAbonar.getText().replace(".", ""));
            montoTotalDeuda = Integer.parseInt(txtTotal.getText().replace(".", ""));
            if (montoParaAbonar <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido y positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- 2. Lógica de PAGO TOTAL ---
        if (montoParaAbonar == montoTotalDeuda) {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Desea saldar todas las deudas pendientes?", "Confirmar Pago Total", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    System.out.println("\n--- VISTA (Pendientes): Iniciando Pago Total ---");
                    System.out.println("1. Número de deudas en 'listaActualPendientes': " + this.listaActualPendientes.size());

                    if (this.listaActualPendientes.isEmpty()) {
                        System.err.println("!!! ERROR: La lista de deudas está vacía. No se puede continuar.");
                    } else {
                        System.out.println("2. Pasando la lista de deudas al controlador...");
                    }

                    abonosController.saldarTodasLasDeudas(this.idApostador, this.listaActualPendientes);

                    limpiar();
                    JOptionPane.showMessageDialog(this, "¡Todas las deudas han sido saldadas!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    this.seHizoUnCambio = true;
                    cargarDatosPendientes();

                } catch (ServiceException e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error de Operación", JOptionPane.ERROR_MESSAGE);
                }
            }
        } // --- Lógica de PAGO PARCIAL ---
        else if (montoParaAbonar < montoTotalDeuda) {
            int filaSeleccionada = tblPendientes.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Para un abono parcial, debe seleccionar una apuesta.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idApuesta = (int) tblPendientes.getModel().getValueAt(tblPendientes.convertRowIndexToModel(filaSeleccionada), 0);
            int montoPendiente = (int) tblPendientes.getModel().getValueAt(tblPendientes.convertRowIndexToModel(filaSeleccionada), 5);

            if (montoParaAbonar > montoPendiente) {
                JOptionPane.showMessageDialog(this, "El abono excede el pendiente de la apuesta seleccionada.", "Monto Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                abonosController.registrarAbonoDirecto(idApuesta, this.idApostador, montoParaAbonar);
                JOptionPane.showMessageDialog(this, "Abono registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiar();
                this.seHizoUnCambio = true;
                cargarDatosPendientes();
            } catch (ServiceException e) {
                JOptionPane.showMessageDialog(this, "Error al registrar el abono:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "El monto a abonar no puede ser mayor que la deuda total.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAbonarActionPerformed

    private void txtAbonarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAbonarKeyReleased
        final DecimalFormat formateador = new DecimalFormat("#,###");

        if (txtAbonar.getText().length() > 3) {
            String cadena = txtAbonar.getText().replace(".", "");
            txtAbonar.setText(formateador.format(Integer.parseInt(cadena)));
        }
    }//GEN-LAST:event_txtAbonarKeyReleased

    private void tblPendientesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPendientesMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tblPendientesMouseEntered

    private void txtAbonarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAbonarKeyPressed
        if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
            txtAbonar.transferFocus();
            btnAbonar.requestFocus();
        }
    }//GEN-LAST:event_txtAbonarKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbonar;
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable tblPendientes;
    private javax.swing.JTextField txtAbonar;
    private javax.swing.JTextField txtCarreras;
    private javax.swing.JTextField txtCedula;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables

}
