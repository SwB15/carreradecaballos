package View;

import Config.AppPaths;
import Controller.Movimientos_Controller;
import Model.MovimientoParaVista_DTO;
import Services.Exceptions.ServiceException;
import Utils.ExcelExporter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class Movimientos extends javax.swing.JDialog {

    private final Movimientos_Controller controller;

    public Movimientos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        this.controller = new Movimientos_Controller();
        initializeView();

    }

    private void initializeView() {
        // Configuración inicial de fechas al mes actual
        LocalDate hoy = LocalDate.now();
        dchDesde.setDate(Date.from(hoy.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dchHasta.setDate(Date.from(hoy.withDayOfMonth(hoy.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // Listeners para los filtros. Todos llaman al mismo método central.
        dchDesde.addPropertyChangeListener("date", evt -> actualizarTabla());
        dchHasta.addPropertyChangeListener("date", evt -> actualizarTabla());
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                actualizarTabla();
            }
        });
        cmbTipomovimientos.addActionListener(e -> actualizarTabla());

        // Carga inicial de datos
        actualizarTabla();
    }

    /**
     * Método central para cargar y refrescar los datos de la tabla.
     */
    private void actualizarTabla() {
        System.out.println("\n--- VISTA (Movimientos): Iniciando actualización de tabla ---");

        // 1. Recoger filtros de la UI
        String apostadorSearch = txtBuscar.getText();
        String tipoMovimientoSearch = cmbTipomovimientos.getSelectedItem().toString();
        LocalDate dateFrom = (dchDesde.getDate() != null) ? dchDesde.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate dateTo = (dchHasta.getDate() != null) ? dchHasta.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        System.out.println("1. Filtros aplicados:");
        System.out.println("   - Búsqueda Apostador: '" + apostadorSearch + "'");
        System.out.println("   - Tipo Movimiento: '" + tipoMovimientoSearch + "'");
        System.out.println("   - Fecha Desde: " + dateFrom);
        System.out.println("   - Fecha Hasta: " + dateTo);

        try {
            // 2. Obtener la lista de DTOs del controlador
            System.out.println("2. Llamando al controlador: controller.listarMovimientos...");
            List<MovimientoParaVista_DTO> listaMovimientos = controller.listarMovimientos(apostadorSearch, tipoMovimientoSearch, dateFrom, dateTo);
            System.out.println("3. Datos recibidos del controlador. Número de movimientos: " + listaMovimientos.size());

            // 3. Construir el TableModel
            String[] titles = {"Id", "Fecha", "Tipo Movimiento", "Monto", "Apostador", "Observacion", "Id Apuesta", "Carrera"};
            DefaultTableModel model = new DefaultTableModel(null, titles) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            System.out.println("4. Llenando el TableModel...");
            for (MovimientoParaVista_DTO dto : listaMovimientos) {
                model.addRow(new Object[]{
                    dto.getMovimiento().getIdMovimiento(),
                    dto.getMovimiento().getFecha(),
                    dto.getDescripcionTipoMovimiento(),
                    dto.getMovimiento().getMonto(),
                    dto.getNombreApostador(),
                    dto.getMovimiento().getDescripcion(),
                    dto.getMovimiento().getFk_apuestas(),
                    dto.getNombreCarrera()
                });
            }

            // 4. Asignar modelo y renderizador a la tabla
            tblMovimientos.setModel(model);
            tblMovimientos.setDefaultRenderer(Object.class, new Movimientos_TableCellRenderer());
            tblMovimientos.setRowSorter(new TableRowSorter<>(model));
            ocultar_columnas(tblMovimientos);

            System.out.println("5. Tabla actualizada en la UI.");

        } catch (ServiceException e) {
            System.err.println("!!! ERROR en la vista al cargar movimientos: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error al cargar los movimientos:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("--- VISTA (Movimientos): Fin de la actualización ---");
    }

    private void ocultar_columnas(JTable table) {
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setPreferredWidth(0);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        txtBuscar = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cmbTipomovimientos = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        dchDesde = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        dchHasta = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMovimientos = new javax.swing.JTable();
        btnImprimir = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Movimientos");

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        jLabel6.setText("Buscar:");

        cmbTipomovimientos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Depósito", "Retiro", "Apuesta parcial", "Apuesta completa", "Apuesta ganada", "Devolución" }));
        cmbTipomovimientos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTipomovimientosActionPerformed(evt);
            }
        });

        jLabel14.setText("Tipo:");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        dchDesde.setDateFormatString("dd/MM/yyyy");

        jLabel2.setText("Desde:");

        jLabel4.setText("Hasta:");

        dchHasta.setDateFormatString("dd/MM/yyyy");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dchDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dchHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbTipomovimientos, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(dchHasta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dchDesde, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6)
                                .addComponent(jLabel14)
                                .addComponent(cmbTipomovimientos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tblMovimientos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblMovimientos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblMovimientos);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1097, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnImprimir)
                        .addComponent(btnExcel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        // Simplemente se notifica que un filtro cambió.
        actualizarTabla();
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void cmbTipomovimientosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTipomovimientosActionPerformed
        // Simplemente se notifica que un filtro cambió.
        actualizarTabla();
    }//GEN-LAST:event_cmbTipomovimientosActionPerformed

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

            String ruta = AppPaths.REPORTS_DIR + File.separator + "Movimientos.jrxml";
            File jrxmlFile = new File(ruta);
            InputStream is = new FileInputStream(jrxmlFile);
            if (!jrxmlFile.exists()) {
                throw new FileNotFoundException("No se encontró el .jrxml en: " + ruta);
            }

            DefaultTableModel original = (DefaultTableModel) tblMovimientos.getModel();
            int[] colsNoPermitidas = {0, 6, 7};
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
            // Se crea un formateador para los números
            DecimalFormat formateadorNumero = new DecimalFormat("#,###");
            // Se crea un formateador para las fechas
            DateTimeFormatter formateadorFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (int row = 0; row < original.getRowCount(); row++) {
                Object[] fila = new Object[colsPermitidas.size()];
                for (int i = 0; i < colsPermitidas.size(); i++) {
                    int originalColIndex = colsPermitidas.get(i);
                    Object valor = original.getValueAt(row, originalColIndex);

                    // --- INICIO DE LA CORRECCIÓN ---
                    if (valor instanceof LocalDate) {
                        // Si el valor es una fecha, se formatea a "dd/MM/yyyy".
                        fila[i] = ((LocalDate) valor).format(formateadorFecha);
                    } else if (valor instanceof Number) {
                        // Si es un número, se formatea con separador de miles.
                        fila[i] = formateadorNumero.format(valor);
                    } else {
                        // Para todo lo demás, se convierte a String (manejando nulos).
                        fila[i] = (valor == null) ? "" : valor.toString();
                    }
                    // --- FIN DE LA CORRECCIÓN ---
                }
                filtrado.addRow(fila);
            }

            JRTableModelDataSource datasource = new JRTableModelDataSource(filtrado);
            JasperReport jr = JasperCompileManager.compileReport(is);

            String apostador = "";
            if (txtBuscar.getText().length() != 0) {
                apostador = "Apostador: " + txtBuscar.getText();
            }

            // Se crea un formateador con el patrón deseado.
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            String parametroDesde;
            String parametroHasta;

            // --- Procesar Fecha Desde ---
            Date fechaDesde = dchDesde.getDate();
            if (fechaDesde != null) {
                parametroDesde = formatter.format(fechaDesde);
            } else {
                parametroDesde = "Todos"; // Valor por defecto si no hay fecha
            }

            // --- Procesar Fecha Hasta ---
            Date fechaHasta = dchHasta.getDate();
            if (fechaHasta != null) {
                parametroHasta = formatter.format(fechaHasta);
            } else {
                parametroHasta = "Todos";
            }

            Map<String, Object> params = new HashMap<>();
            params.put("Logo", rutaLogo);
            params.put("Currentdate", currentdate);
            params.put("Apostador", apostador);
            params.put("TipoMovimiento", cmbTipomovimientos.getSelectedItem());
            params.put("Desde", parametroDesde);
            params.put("Hasta", parametroHasta);

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

            JDialog dialog = new JDialog((Frame) null, "Movimientos", true);
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
            Logger.getLogger(Movimientos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
//        DefaultTableModel model = (DefaultTableModel) tblMovimientos.getModel();
//        if (model.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this,
//                    "La tabla está vacía. No se puede exportar un Excel sin datos.",
//                    "Tabla vacía",
//                    JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        List<Object[]> resumen = List.of(
//                new Object[]{"Tipo de movimiento: ", cmbTipomovimientos.getSelectedItem()},
//                new Object[]{" ", ""}
//        );
//
//        Set<Integer> columnsToSkip = Set.of(0, 6, 7);
//
//        JFileChooser chooser = new JFileChooser();
//        chooser.setDialogTitle("Guardar Excel");
//        chooser.setApproveButtonText("Guardar");
//        // Filtro opcional para que solo muestre .xlsx
//        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
//        chooser.setSelectedFile(new File("Movimientos.xlsx"));
//
//        int opcion = chooser.showSaveDialog(this);
//        if (opcion != JFileChooser.APPROVE_OPTION) {
//            return;  // cancelado
//        }
//
//        File destino = chooser.getSelectedFile();
//        if (!destino.getName().toLowerCase().endsWith(".xlsx")) {
//            destino = new File(destino.getParentFile(), destino.getName() + ".xlsx");
//        }
//
//        try {
//            Export_Excel.export(tblMovimientos,
//                    "Movimientos",
//                    destino.getAbsolutePath(),
//                    columnsToSkip, resumen);
//            // Abrir automáticamente
//            if (Desktop.isDesktopSupported()) {
//                Desktop.getDesktop().open(destino);
//            }
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this,
//                    "Error al exportar:\n" + ex.getMessage(),
//                    "Error", JOptionPane.ERROR_MESSAGE);
//            ex.printStackTrace();
//        }

        try {
            // 1. Obtener los datos FRESCOS del controlador
            String apostadorSearch = txtBuscar.getText();
            String tipoMovimientoSearch = cmbTipomovimientos.getSelectedItem().toString();
            LocalDate dateFrom = (dchDesde.getDate() != null) ? dchDesde.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
            LocalDate dateTo = (dchHasta.getDate() != null) ? dchHasta.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
            List<MovimientoParaVista_DTO> datos = controller.listarMovimientos(apostadorSearch, tipoMovimientoSearch, dateFrom, dateTo);

            if (datos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay datos para exportar.", "Tabla Vacía", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Pedir al usuario la ubicación del archivo
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Guardar como Excel");
            chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
            chooser.setSelectedFile(new File("Reporte_Movimientos.xlsx"));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            // 3. Preparar los datos y formatear la fecha
            List<String> headers = List.of("Fecha", "Tipo Movimiento", "Monto", "Apostador", "Observación", "Carrera");
            List<List<Object>> dataRows = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (MovimientoParaVista_DTO dto : datos) {
                dataRows.add(Arrays.asList(
                        dto.getMovimiento().getFecha() != null ? dto.getMovimiento().getFecha().format(formatter) : "", // <-- Fecha formateada
                        dto.getDescripcionTipoMovimiento(),
                        dto.getMovimiento().getMonto(),
                        dto.getNombreApostador(),
                        dto.getMovimiento().getDescripcion(),
                        dto.getNombreCarrera()
                ));
            }

            // 4. Preparar el resumen
            List<Object[]> resumen = List.of(
                    new Object[]{"Filtro Tipo de movimiento:", cmbTipomovimientos.getSelectedItem()},
                    new Object[]{"", ""} // Fila vacía como separador
            );

            // 5. Llamar al exportador genérico
            ExcelExporter.export(headers, dataRows, "Movimientos", chooser.getSelectedFile().getAbsolutePath(), resumen);

            JOptionPane.showMessageDialog(this, "Excel exportado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (ServiceException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error al exportar a Excel:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnExcelActionPerformed

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
            java.util.logging.Logger.getLogger(Movimientos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Movimientos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Movimientos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Movimientos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Movimientos dialog = new Movimientos(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JComboBox<String> cmbTipomovimientos;
    public static com.toedter.calendar.JDateChooser dchDesde;
    public static com.toedter.calendar.JDateChooser dchHasta;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable tblMovimientos;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables

}
