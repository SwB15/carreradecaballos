package View;

import Config.AppPaths;
import Controller.Apostadores_Controller;
import Model.ReportesHistorialApostador_Model;
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
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
public class PerfilApostador extends javax.swing.JDialog {

    private final Apostadores_Controller controller;
    private final int idApostador;

    public PerfilApostador(java.awt.Frame parent, boolean modal, int idApostador, String nombre, String cedula) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        txtNumero.setEditable(false);
        txtNumero.setBackground(Color.white);
        txtCedula.setEditable(false);
        txtCedula.setBackground(Color.white);
        txtNombre.setEditable(false);
        txtNombre.setBackground(Color.white);

        this.controller = new Apostadores_Controller();
        this.idApostador = idApostador;

        // Se llenan los campos de texto con los datos recibidos.
        txtNumero.setText(String.valueOf(idApostador));
        txtNombre.setText(nombre);
        txtCedula.setText(cedula);

        // 1. Se añaden los listeners a los JDateChooser.
        //    Cualquier cambio en las fechas llamará a actualizarTabla().
        PropertyChangeListener dateChangeListener = evt -> {
            if ("date".equals(evt.getPropertyName())) {
                actualizarTabla();
            }
        };
        dchDesde.addPropertyChangeListener(dateChangeListener);
        dchHasta.addPropertyChangeListener(dateChangeListener);

        // Carga inicial de datos
        actualizarTabla();
    }

    private void actualizarTabla() {
        // 1. Obtener fechas de los JDateChoosers de forma segura.
        Date fechaDesdeDate = dchDesde.getDate();
        Date fechaHastaDate = dchHasta.getDate();
        LocalDate fechaDesde = null;
        LocalDate fechaHasta = null;

        if (fechaDesdeDate != null) {
            fechaDesde = fechaDesdeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (fechaHastaDate != null) {
            fechaHasta = fechaHastaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        // 2. Validar el rango de fechas. Si es inválido, se limpia la tabla y se detiene.
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            DefaultTableModel model = (DefaultTableModel) tblPerfil.getModel();
            if (model != null) {
                model.setRowCount(0);
            }
            return;
        }

        try {
            // 1. Pedir la lista de DTOs al controlador
            List<ReportesHistorialApostador_Model> historial = controller.obtenerHistorial(this.idApostador, fechaDesde, fechaHasta);

            // 2. Construir el TableModel
            String[] titles = {"ID", "Carrera", "Caballo", "Fecha", "Monto", "Resultado"};
            DefaultTableModel model = new DefaultTableModel(null, titles) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (ReportesHistorialApostador_Model item : historial) {
                model.addRow(new Object[]{
                    item.getIdApuesta(),
                    item.getApuesta(), // Nombre de la carrera
                    item.getCaballo(),
                    item.getFecha(),
                    item.getMonto(),
                    item.getResultado()
                });
            }

            // 3. Asignar modelo y renderizador a la tabla
            tblPerfil.setModel(model);
            tblPerfil.setDefaultRenderer(Object.class, new PerfilApostador_TableCellRenderer());

            // 4. Llenar los campos de texto si hay historial
            if (!historial.isEmpty()) {
                // Aquí podrías obtener los datos del apostador de la primera fila si es necesario
                // o pasarlos a través del constructor.
            }
            
            ocultar_columnas(tblPerfil);

        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el historial: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            // Si hay un error al cargar, es buena idea cerrar el diálogo.
            this.dispose();
        }
    }

    private void ocultar_columnas(JTable table) {
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtNumero = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCedula = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        btnImprimir = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();
        dchDesde = new com.toedter.calendar.JDateChooser();
        dchHasta = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPerfil = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Historial de Apuestas");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Número:");

        jLabel2.setText("Nombre:");

        jLabel3.setText("Cedula:");

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

        dchDesde.setDateFormatString("dd/MM/yyyy");

        dchHasta.setDateFormatString("dd/MM/yyyy");

        jLabel4.setText("Desde:");

        jLabel5.setText("Hasta:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel5))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 392, Short.MAX_VALUE)
                                        .addComponent(jLabel4))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dchDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dchHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 438, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(dchDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(dchHasta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnImprimir)
                    .addComponent(btnExcel))
                .addContainerGap())
        );

        tblPerfil.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblPerfil);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1143, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
//        try {
//            Reportes_Controller reportesController = new Reportes_Controller();
//            JFrame owner = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
//            reportesController.generarReporteHistorial(
//                    owner,
//                    this.idApostador,
//                    txtCedula.getText(),
//                    txtNombre.getText()
//            );
//        } catch (ServiceException e) {
//            JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + e.getMessage(), "Error de Reporte", JOptionPane.ERROR_MESSAGE);
//        }

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

            String ruta = AppPaths.REPORTS_DIR + File.separator + "Perfil.jrxml";
            File jrxmlFile = new File(ruta);
            InputStream is = new FileInputStream(jrxmlFile);
            if (!jrxmlFile.exists()) {
                throw new FileNotFoundException("No se encontró el .jrxml en: " + ruta);
            }

            DefaultTableModel original = (DefaultTableModel) tblPerfil.getModel();
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

            // Se crean los formateadores una sola vez fuera del bucle.
            DateTimeFormatter formateadorFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DecimalFormat formateadorNumero = new DecimalFormat("#,###");

            // Bucle que recorre las filas del modelo original
            for (int row = 0; row < original.getRowCount(); row++) {
                Object[] fila = new Object[colsPermitidas.size()];
                for (int i = 0; i < colsPermitidas.size(); i++) {

                    int originalColIndex = colsPermitidas.get(i);
                    Object valor = original.getValueAt(row, originalColIndex);
                    String nombreColumna = original.getColumnName(originalColIndex);

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
            String cedula = "";
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

            if ("null".equals(txtCedula.getText())) { // Es más seguro comparar así para evitar errores si el texto es nulo
                cedula = "0";
            }

            Map<String, Object> params = new HashMap<>();
            params.put("Logo", rutaLogo);
            params.put("Numero", txtNumero.getText());
            params.put("Cedula", cedula);
            params.put("Nombre", txtNombre.getText());
            params.put("Desde", parametroDesde);
            params.put("Hasta", parametroHasta);
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
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);//Sólo archivos
                chooser.setAcceptAllFileFilterUsed(false);//No permitir “Todos los archivos”
                FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG (*.png)", "png");//Filtro de sólo PNG
                chooser.addChoosableFileFilter(pngFilter);
                chooser.setFileFilter(pngFilter);

                chooser.setApproveButtonText("Guardar");//Texto del botón en español
                chooser.setApproveButtonToolTipText("Guardar reporte en imágenes PNG");

                //Mostrar el dialogo
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

            JDialog dialog = new JDialog((Frame) null, "Perfil de " + txtNombre.getText(), true);
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
            Logger.getLogger(PerfilApostador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        DefaultTableModel model = (DefaultTableModel) tblPerfil.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "La tabla está vacía. No se puede exportar un Excel sin datos.",
                    "Tabla vacía",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Object[]> resumen = List.of(
                new Object[]{"N° apostador: ", txtNumero.getText()},
                new Object[]{"Cedula: ", txtCedula.getText()},
                new Object[]{"Nombre: ", txtNombre.getText()},
                new Object[]{" ", ""}
        );

        Set<Integer> columnsToSkip = Set.of(0);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Excel");
        chooser.setApproveButtonText("Guardar");
        // Filtro opcional para que solo muestre .xlsx
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        chooser.setSelectedFile(new File("Perfil del Apostador.xlsx"));

        int opcion = chooser.showSaveDialog(this);
        if (opcion != JFileChooser.APPROVE_OPTION) {
            return;  // cancelado
        }

        File destino = chooser.getSelectedFile();
        // Asegúrate de que la extensión .xlsx esté
        if (!destino.getName().toLowerCase().endsWith(".xlsx")) {
            destino = new File(destino.getParentFile(), destino.getName() + ".xlsx");
        }

        try {
            DefaultTableModel originalModel = (DefaultTableModel) tblPerfil.getModel();

            // --- INICIO DE LA LÓGICA DE FORMATO ---
            // 1. Crear un modelo temporal para la exportación.
            DefaultTableModel modeloParaExportar = new DefaultTableModel();
            for (int i = 0; i < originalModel.getColumnCount(); i++) {
                modeloParaExportar.addColumn(originalModel.getColumnName(i));
            }

            // 2. Definir el formateador de fecha.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // 3. Copiar los datos, formateando la fecha en el proceso.
            for (int row = 0; row < originalModel.getRowCount(); row++) {
                Object[] fila = new Object[originalModel.getColumnCount()];
                for (int col = 0; col < originalModel.getColumnCount(); col++) {
                    Object valor = originalModel.getValueAt(row, col);

                    // Si la columna es la de la fecha y el valor es un LocalDate...
                    if (valor instanceof LocalDate) {
                        fila[col] = ((LocalDate) valor).format(formatter);
                    } else {
                        fila[col] = valor;
                    }
                }
                modeloParaExportar.addRow(fila);
            }
            // --- FIN DE LA LÓGICA DE FORMATO ---

            // 4. Se llama al exportador con el modelo ya formateado.
            Export_Excel.export(modeloParaExportar,
                    "Perfil del Apostador",
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnImprimir;
    private com.toedter.calendar.JDateChooser dchDesde;
    private com.toedter.calendar.JDateChooser dchHasta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tblPerfil;
    private javax.swing.JTextField txtCedula;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNumero;
    // End of variables declaration//GEN-END:variables

}
