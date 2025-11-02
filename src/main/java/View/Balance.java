package View;

import Config.AppPaths;
import Controller.Balance_Controller;
import Model.ResultadosCarrera_DTO;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
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
public class Balance extends javax.swing.JDialog {

    private final Balance_Controller controller;
    private HashMap<String, Integer> carrerasMap; // Mapa para relacionar nombre con ID
    private final DecimalFormat formateador;

    public Balance(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        // Se configura el formateador una sola vez
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        this.formateador = new DecimalFormat("#,###", symbols);
        this.controller = new Balance_Controller();
        initializeView();
    }

    /**
     * Configura el estado inicial de la vista y carga los datos.
     */
    private void initializeView() {
        // Configuración de la UI
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
        txtGanancias.setEditable(false);
        txtComision.setEditable(false);
        txtComision.setBackground(Color.white);

        // Se asigna el listener al ComboBox
        cmbCarreras.addActionListener(e -> actualizarVista());

        // Se cargan las carreras en el ComboBox y se dispara la primera carga de datos.
        cargarCarrerasCombobox();
    }

//    private void ocultar_columnas(JTable table) {
//        // Ocultar: ID Apostador, Cédula, Monto Neto
//        int[] columnasOcultas = {0, 2};
//        for (int col : columnasOcultas) {
//            table.getColumnModel().getColumn(col).setMaxWidth(0);
//            table.getColumnModel().getColumn(col).setMinWidth(0);
//            table.getColumnModel().getColumn(col).setPreferredWidth(0);
//        }
//    }
    /**
     * Carga las carreras finalizadas en el ComboBox y selecciona la primera.
     */
    private void cargarCarrerasCombobox() {
        try {
            this.carrerasMap = controller.obtenerCarrerasFinalizadasParaCombo();

            // Para evitar que el listener se dispare múltiples veces al añadir items,
            // se puede desactivar temporalmente, aunque en este caso no es crítico.
            cmbCarreras.removeAllItems();
            for (String nombreCarrera : carrerasMap.keySet()) {
                cmbCarreras.addItem(nombreCarrera);
            }

            // Si no hay carreras, se limpia la tabla y los totales.
            if (cmbCarreras.getItemCount() == 0) {
                tblBalance.setModel(new DefaultTableModel());
                calcularYMostrarTotales(List.of()); // Pasa una lista vacía
            }

        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las carreras: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método central que actualiza la tabla y los totales según la carrera
     * seleccionada.
     */
    private void actualizarVista() {
        String nombreCarreraSeleccionada = (String) cmbCarreras.getSelectedItem();
        if (nombreCarreraSeleccionada == null || carrerasMap == null) {
            return;
        }

        int idCarrera = carrerasMap.get(nombreCarreraSeleccionada);
        txtIdcarreras.setText(String.valueOf(idCarrera));

        try {
            List<ResultadosCarrera_DTO> resultados = controller.obtenerResultadosCarrera(idCarrera);

            String[] titles = {"ID", "Apostador", "Cédula", "Total Apuesto", "Pool Perdedor", "Comision (10%)", "Monto Neto"};

            DefaultTableModel model = new DefaultTableModel(null, titles) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 1 || columnIndex == 2) {
                        return String.class;
                    } else {
                        return Integer.class; // Se le dice a la tabla que las demás son numéricas
                    }
                }
            };

            // --- CORRECCIÓN CLAVE ---
            // Se añaden los NÚMEROS PUROS al modelo, sin formatear.
            for (ResultadosCarrera_DTO dto : resultados) {
                model.addRow(new Object[]{
                    dto.getIdApostador(),
                    dto.getNombreApostador(),
                    dto.getCedulaApostador(),
                    dto.getTotalApostado(),
                    dto.getPoolPerdedor(),
                    dto.getComisionGestor(),
                    dto.getResultadoFinal()
                });
            }
            // --- FIN DE LA CORRECCIÓN ---

            tblBalance.setModel(model);
            tblBalance.setRowSorter(new TableRowSorter<>(model));

            // Se le dice a la JTable que use nuestro renderer para las columnas que marcamos como numéricas.
            tblBalance.setDefaultRenderer(Integer.class, new Balance_TableCellRenderer());

            ocultarColumnas();
            calcularYMostrarTotales(resultados);

        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ocultarColumnas() {
        int[] columnasOcultas = {0, 2}; // Ocultar: ID Apostador, Cédula
        for (int col : columnasOcultas) {
            tblBalance.getColumnModel().getColumn(col).setMaxWidth(0);
            tblBalance.getColumnModel().getColumn(col).setMinWidth(0);
            tblBalance.getColumnModel().getColumn(col).setPreferredWidth(0);
        }
    }

    /**
     * Calcula los totales a partir de la lista de DTOs y los muestra en los
     * campos de texto.
     */
    private void calcularYMostrarTotales(List<ResultadosCarrera_DTO> resultados) {
        long ganadores = resultados.stream().filter(r -> r.getResultadoFinal() > 0).count();
        long perdedores = resultados.size() - ganadores;
        long sumApostado = resultados.stream().mapToLong(ResultadosCarrera_DTO::getTotalApostado).sum();
        long sumComision = resultados.stream().mapToLong(ResultadosCarrera_DTO::getComisionGestor).sum();
        long sumPagado = resultados.stream().filter(r -> r.getResultadoFinal() > 0).mapToLong(ResultadosCarrera_DTO::getResultadoFinal).sum();

        // --- CORRECCIÓN CLAVE ---
        // Se obtiene la comisión de la primera fila si existe, ya que es la misma para toda la carrera.
        int comisionPorcentaje = 0;
        if (!resultados.isEmpty()) {
            try {
                // Se necesita un método en el controlador para esto
                comisionPorcentaje = controller.obtenerComisionDeCarrera(carrerasMap.get(cmbCarreras.getSelectedItem()));
            } catch (ServiceException e) {
                comisionPorcentaje = 10; // Valor por defecto en caso de error
            }
        }
        txtComision.setText(comisionPorcentaje + "%");
        // --- FIN DE LA CORRECCIÓN ---

        txtApuestas.setText(String.valueOf(resultados.size()));
        txtGanadores.setText(String.valueOf(ganadores));
        txtPerdedores.setText(String.valueOf(perdedores));
        txtTotalApostado.setText(formateador.format(sumApostado));
        txtGanancias.setText(formateador.format(sumComision));
        txtTotalPagado.setText(formateador.format(sumPagado));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnCancelar = new javax.swing.JButton();
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
        btnImprimir = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtComision = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBalance = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Balance");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        txtGanancias.setBackground(new java.awt.Color(153, 255, 0));
        txtGanancias.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtGanancias.setForeground(new java.awt.Color(0, 102, 0));
        txtGanancias.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtGanancias.setText("0");

        jLabel3.setText("Mis Ganancias:");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Perdedores:");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Ganadores:");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Carrera:");

        cmbCarreras.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel7.setText("Tot. Apost.:");

        jLabel8.setText("Tot. Pagado:");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Apuestas:");

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

        jLabel10.setText("Porcent. de Comisión:");

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
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtGanadores, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbCarreras, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtApuestas, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPerdedores, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(156, 156, 156)
                                .addComponent(txtIdcarreras, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTotalApostado, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTotalPagado, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtComision, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGanadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtComision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPerdedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(jLabel3)
                    .addComponent(txtGanancias, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancelar)
                        .addComponent(txtIdcarreras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnImprimir)
                    .addComponent(btnExcel))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

            String ruta = AppPaths.REPORTS_DIR + File.separator + "Balance.jrxml";
            File jrxmlFile = new File(ruta);
            InputStream is = new FileInputStream(jrxmlFile);
            if (!jrxmlFile.exists()) {
                throw new FileNotFoundException("No se encontró el .jrxml en: " + ruta);
            }

            DefaultTableModel original = (DefaultTableModel) tblBalance.getModel();
            int[] colsNoPermitidas = {0, 2};
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

                    int originalColIndex = colsPermitidas.get(i);
                    Object valor = original.getValueAt(row, originalColIndex);

                    // --- INICIO DE LA CORRECCIÓN ---
                    // Los índices 3, 4, 5 y 6 corresponden a las columnas numéricas
                    if ((originalColIndex >= 3 && originalColIndex <= 6) && valor instanceof Number) {
                        // Si el valor es un número en una de esas columnas, se formatea a String.
                        fila[i] = formateador.format(valor);
                    } else {
                        // Para las demás columnas (como el nombre del apostador), se copia el valor.
                        fila[i] = (valor == null) ? "" : valor.toString();
                    }
                    // --- FIN DE LA CORRECCIÓN ---
                }
                filtrado.addRow(fila);
            }

            String ganancias = "";
            String[] opciones = {"Sí", "No"};
            int respuesta = JOptionPane.showOptionDialog(
                    this,
                    "Mostrar también las ganancias como gestor?",
                    "Selecciona",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, opciones, opciones[1]
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                ganancias = "Mis ganancias: " + txtGanancias.getText() + " gs.";
            }

            JRTableModelDataSource datasource = new JRTableModelDataSource(filtrado);
            JasperReport jr = JasperCompileManager.compileReport(is);

            Map<String, Object> params = new HashMap<>();
            params.put("Logo", rutaLogo);
            params.put("Currentdate", currentdate);
            params.put("Carrera", cmbCarreras.getSelectedItem());
            params.put("Apuestas", txtApuestas.getText());
            params.put("Ganadores", txtGanadores.getText());
            params.put("Perdedores", txtPerdedores.getText());
            params.put("Totalapostado", txtTotalApostado.getText());
            params.put("Totalpagado", txtTotalPagado.getText());
            params.put("Misganancias", ganancias);

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

            JButton btnSave = (JButton) toolbar.getComponent(0);
            JButton btnPrint = (JButton) toolbar.getComponent(1);
            btnSave.setText("Guardar");
            btnSave.setPreferredSize(new Dimension(75, 30));
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
            toolbar.add(btnSave);
            toolbar.add(Box.createRigidArea(new Dimension(3, 0)));
            toolbar.add(btnImagen);
            toolbar.add(Box.createRigidArea(new Dimension(3, 0)));

            for (Component c : originales) {
                if (c == btnPrint || c == btnSave /*|| c == btnImagen?*/) {
                    continue;
                }
                toolbar.add(c);
                toolbar.add(Box.createRigidArea(new Dimension(2, 0)));
            }

            toolbar.revalidate();
            toolbar.repaint();

            JDialog dialog = new JDialog((Frame) null, "Balance", true);
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
            Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        DefaultTableModel model = (DefaultTableModel) tblBalance.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "La tabla está vacía. No se puede exportar un Excel sin datos.",
                    "Tabla vacía",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Object[]> resumen = List.of(
                new Object[]{"Carrera seleccionada: ", cmbCarreras.getSelectedItem()},
                new Object[]{"Cantidad de apuestas: ", txtApuestas.getText()},
                new Object[]{"Ganadores: ", txtGanadores.getText()},
                new Object[]{"Perdedores: ", txtPerdedores.getText()},
                new Object[]{"Total apostado : ", txtTotalApostado.getText()},
                new Object[]{"Total pagado: ", txtTotalPagado.getText()},
                new Object[]{"Mis ganancias: ", txtGanancias.getText()},
                new Object[]{" ", ""}
        );

        Set<Integer> columnsToSkip = Set.of(0, 2);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Excel");
        chooser.setApproveButtonText("Guardar");
        // Filtro opcional para que solo se muestre .xlsx
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        chooser.setSelectedFile(new File("Balance.xlsx"));

        int opcion = chooser.showSaveDialog(this);
        if (opcion != JFileChooser.APPROVE_OPTION) {
            return;  // cancelado
        }

        File destino = chooser.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".xlsx")) {
            destino = new File(destino.getParentFile(), destino.getName() + ".xlsx");
        }

        try {
            Export_Excel.export(tblBalance,
                    "Balance",
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

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

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
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JComboBox<String> cmbCarreras;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tblBalance;
    private javax.swing.JTextField txtApuestas;
    private javax.swing.JTextField txtComision;
    private javax.swing.JTextField txtGanadores;
    private javax.swing.JTextField txtGanancias;
    private javax.swing.JTextField txtIdcarreras;
    private javax.swing.JTextField txtPerdedores;
    private javax.swing.JTextField txtTotalApostado;
    private javax.swing.JTextField txtTotalPagado;
    // End of variables declaration//GEN-END:variables

}
