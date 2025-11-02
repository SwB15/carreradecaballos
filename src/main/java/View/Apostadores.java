package View;

import Config.AppPaths;
import Controller.Apostadores_Controller;
import Model.ApostadorParaVista_DTO;
import Services.Exceptions.ServiceException;
import Utils.ExcelExporter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import java.util.Arrays;
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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
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
public class Apostadores extends javax.swing.JDialog {

    private final Apostadores_Controller controller;
    private String stateFilter = "todos";
    private final DecimalFormat formateador;

    public Apostadores(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        // Se inicializa el controlador.
        this.controller = new Apostadores_Controller();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        this.formateador = new DecimalFormat("#,###", symbols);

        // Configuración inicial de la UI
        txtIdapostadores.setVisible(false);
        txtSaldo.setEditable(false);
        txtSaldo.setBackground(Color.white);
        txtNumero.setEditable(false);
        txtNumero.setText("Nuevo"); // Es mejor no pre-cargar IDs desde la UI.
        txtNombre.requestFocus();
        btnModificar.setEnabled(false);

        // Se llama al nuevo método para cargar los datos iniciales.
        actualizarTabla();

        tblApostadores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = tblApostadores.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tblApostadores.getRowCount()) {
                    // Seleccionar la fila bajo el cursor, sea clic derecho o izquierdo
                    tblApostadores.setRowSelectionInterval(row, row);
                } else {
                    tblApostadores.clearSelection();
                }

                // Si es un clic DERECHO (PopupTrigger) y hay una fila seleccionada
                if (e.isPopupTrigger() && tblApostadores.getSelectedRowCount() != 0) {
                    // Muestra el JPopupMenu que ya tienes diseñado (pmnuApostadores)
                    pmnuApostadores.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Si es un clic IZQUIERDO normal (no doble clic)
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                    cargarDatosEnFormulario();
                }
            }
        });
    }

    private void cargarDatosEnFormulario() {
        int filaSeleccionada = tblApostadores.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }

        // Se convierten los índices de la vista al modelo por si la tabla está ordenada.
        int filaModelo = tblApostadores.convertRowIndexToModel(filaSeleccionada);

        // Se obtienen los datos del TableModel.
        String id = tblApostadores.getModel().getValueAt(filaModelo, 0).toString();
        String cedula = (String) tblApostadores.getModel().getValueAt(filaModelo, 1);
        String nombre = (String) tblApostadores.getModel().getValueAt(filaModelo, 2);
        String saldo = tblApostadores.getModel().getValueAt(filaModelo, 3).toString();
        String observacion = (String) tblApostadores.getModel().getValueAt(filaModelo, 4);
        String estado = (String) tblApostadores.getModel().getValueAt(filaModelo, 5);

        // Se rellenan los campos del formulario.
        txtIdapostadores.setText(id);
        txtNumero.setText(id);
        txtCedula.setText(cedula);
        txtNombre.setText(nombre);
        txtSaldo.setText(saldo);
        atxtObservacion.setText(observacion);
        chbActive.setSelected("activo".equalsIgnoreCase(estado));

        // Se habilitan los botones correspondientes.
        btnModificar.setEnabled(true);
        btnSave.setText("Actualizar");
    }

    private void actualizarTabla() {
        String search = txtBuscar.getText();

        try {
            List<ApostadorParaVista_DTO> listaApostadores = controller.listarApostadores(search, stateFilter);

            String[] titles = {"Id", "Cedula", "Apostador", "Saldo", "Observacion", "Estado", "Estado Deuda"};
            DefaultTableModel model = new DefaultTableModel(null, titles) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

                // Se le dice a la tabla qué columnas son numéricas para el ordenamiento.
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 3) { // Columna Saldo
                        return Number.class;
                    }
                    return String.class;
                }
            };

            // Se añaden los NÚMEROS PUROS al modelo, sin formatear.
            for (ApostadorParaVista_DTO dto : listaApostadores) {
                model.addRow(new Object[]{
                    dto.getApostador().getIdapostadores(),
                    dto.getApostador().getCedula(),
                    dto.getApostador().getNombre(),
                    dto.getApostador().getSaldo(), // Se pasa el número puro
                    dto.getApostador().getObservacion(),
                    dto.getApostador().getFk_estados() == 1 ? "Activo" : "Inactivo",
                    dto.getEstadoDeuda()
                });
            }

            tblApostadores.setModel(model);
            tblApostadores.setRowSorter(new TableRowSorter<>(model));

            // Se crea una sola instancia del renderizador para reutilizarla.
            Apostadores_TableCellRenderer renderer = new Apostadores_TableCellRenderer();

            // Se le dice a la tabla que use nuestro renderer para las columnas de tipo Número.
            tblApostadores.setDefaultRenderer(Number.class, renderer);
            // Y también se le dice que lo use para todas las demás columnas de tipo Objeto (como los Strings).
            tblApostadores.setDefaultRenderer(Object.class, renderer);

            ocultar_columnas(tblApostadores); // Se asume que este método existe y está correcto

        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los apostadores:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ocultar_columnas(JTable table) {
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        table.getColumnModel().getColumn(5).setMaxWidth(80);
        table.getColumnModel().getColumn(5).setMinWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);

        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setPreferredWidth(0);
    }

    private void openHistorial(int idApostador) {
        try {
            // Llama al método del controlador con fechas nulas para verificar si existe CUALQUIER historial.
            if (controller.obtenerHistorial(idApostador, null, null).isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay historial para este apostador.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Se obtienen los datos de la fila seleccionada (código sin cambios)
            int filaSeleccionada = tblApostadores.getSelectedRow();
            int filaModelo = tblApostadores.convertRowIndexToModel(filaSeleccionada);

            Object nombreObj = tblApostadores.getModel().getValueAt(filaModelo, 2);
            String nombre = (nombreObj == null) ? "" : nombreObj.toString();

            Object cedulaObj = tblApostadores.getModel().getValueAt(filaModelo, 1);
            String cedula = (cedulaObj == null) ? "" : cedulaObj.toString();

            // Se pasan los datos al nuevo constructor (código sin cambios)
            PerfilApostador dialog = new PerfilApostador((Frame) this.getParent(), true, idApostador, nombre, cedula);
            dialog.setVisible(true);

            // Refresca la tabla después de que el diálogo se cierra.
            actualizarTabla();

        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error al abrir el historial:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtIdapostadores.setText("");
        txtNumero.setText("Nuevo");
        txtCedula.setText("");
        txtNombre.setText("");
        txtSaldo.setText("");
        atxtObservacion.setText("");
        chbActive.setSelected(false);
        btnSave.setText("Guardar");
        btnModificar.setEnabled(false);
        txtNombre.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pmnuApostadores = new javax.swing.JPopupMenu();
        pmnuHistorial = new javax.swing.JMenuItem();
        pmnuPendientes = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        txtNumero = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCedula = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        chbActive = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        txtIdapostadores = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        atxtObservacion = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        txtSaldo = new javax.swing.JTextField();
        btnModificar = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txtBuscar = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cmbEstado = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblApostadores = new javax.swing.JTable();

        pmnuHistorial.setText("Historial");
        pmnuHistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmnuHistorialActionPerformed(evt);
            }
        });
        pmnuApostadores.add(pmnuHistorial);

        pmnuPendientes.setText("Pendientes");
        pmnuPendientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmnuPendientesActionPerformed(evt);
            }
        });
        pmnuApostadores.add(pmnuPendientes);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Apostadores");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Número:");

        jLabel2.setText("Nombre:");

        txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNombreKeyPressed(evt);
            }
        });

        jLabel3.setText("Cédula:");

        txtCedula.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCedulaKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCedulaKeyTyped(evt);
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

        jLabel4.setText("Observación:");

        chbActive.setText("Activo");

        atxtObservacion.setColumns(20);
        atxtObservacion.setRows(5);
        atxtObservacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                atxtObservacionKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(atxtObservacion);

        jLabel5.setText("Saldo");

        txtSaldo.setText("0");
        txtSaldo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSaldoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSaldoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSaldoKeyTyped(evt);
            }
        });

        btnModificar.setText("Modificar");
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(57, 57, 57)
                                .addComponent(txtIdapostadores, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 364, Short.MAX_VALUE)
                                .addComponent(chbActive)
                                .addGap(23, 23, 23)
                                .addComponent(btnSave)
                                .addGap(6, 6, 6)
                                .addComponent(btnCancel)))
                        .addGap(8, 8, 8))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5)
                                    .addComponent(btnModificar)))))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtIdapostadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnImprimir)
                            .addComponent(btnExcel)
                            .addComponent(chbActive)))
                    .addComponent(btnSave)
                    .addComponent(btnCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14)
                        .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tblApostadores = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblApostadores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblApostadores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblApostadoresMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblApostadores);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(191, 191, 191)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // 1. Validar datos de entrada.
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del apostador no puede estar vacío.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Recoger datos del formulario.
        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String observacion = atxtObservacion.getText().trim();

        try {
            // 3. Decidir si es una creación o una actualización.
            if (txtIdapostadores.getText().trim().isEmpty()) {
                // CREAR nuevo apostador
                int fk_estados = 1;
                controller.crearApostador(cedula, nombre, observacion, fk_estados);
                JOptionPane.showMessageDialog(this, "Apostador creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // ACTUALIZAR apostador existente
                int fk_estados = chbActive.isSelected() ? 1 : 2;
                int id = Integer.parseInt(txtIdapostadores.getText());
                controller.actualizarApostador(id, cedula, nombre, observacion, fk_estados);
                JOptionPane.showMessageDialog(this, "Apostador actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            // 4. Limpiar el formulario y refrescar la tabla.
            limpiarFormulario();
            actualizarTabla();

        } catch (ServiceException e) {
            // 5. Manejar errores del servicio.
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error de Operación", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: El ID del apostador es inválido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void tblApostadoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblApostadoresMouseClicked
        int filaSeleccionada = tblApostadores.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }

        // Se convierten los índices de la vista al modelo por si la tabla está ordenada.
        int filaModelo = tblApostadores.convertRowIndexToModel(filaSeleccionada);

        // Se obtienen los datos del TableModel.
        String id = tblApostadores.getModel().getValueAt(filaModelo, 0).toString();
        String cedula = (String) tblApostadores.getModel().getValueAt(filaModelo, 1);
        String nombre = (String) tblApostadores.getModel().getValueAt(filaModelo, 2);
        Object saldo = tblApostadores.getModel().getValueAt(filaModelo, 3).toString();
        String observacion = (String) tblApostadores.getModel().getValueAt(filaModelo, 4);
        String estado = (String) tblApostadores.getModel().getValueAt(filaModelo, 5);

        // Se rellenan los campos del formulario.
        txtIdapostadores.setText(id);
        txtNumero.setText(id);
        txtCedula.setText(cedula);
        txtNombre.setText(nombre);
        if (saldo instanceof Number) {
            // Si el saldo es un número, se formatea.
            txtSaldo.setText(formateador.format(saldo));
        } else {
            // Si no, se muestra tal cual (o vacío si es nulo).
            txtSaldo.setText(saldo == null ? "0" : saldo.toString());
        }
        atxtObservacion.setText(observacion);
        chbActive.setSelected("activo".equalsIgnoreCase(estado));

        // Se habilitan los botones correspondientes.
        btnModificar.setEnabled(true);
        btnSave.setText("Actualizar");
    }//GEN-LAST:event_tblApostadoresMouseClicked

    private void txtCedulaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCedulaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtCedula.transferFocus();
            atxtObservacion.requestFocus();
        }
    }//GEN-LAST:event_txtCedulaKeyPressed

    private void txtNombreKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtNombre.transferFocus();
            txtCedula.requestFocus();
        }
    }//GEN-LAST:event_txtNombreKeyPressed

    private void txtCedulaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCedulaKeyTyped
        char validar = evt.getKeyChar();
        if (Character.isLetter(validar)) {
            getToolkit().beep();
            evt.consume();
            JOptionPane.showMessageDialog(null, "Ingrese solo números", "Advertencia!", JOptionPane.WARNING_MESSAGE);
        }

        int numerocaracteres = 8;
        if (txtCedula.getText().length() > numerocaracteres) {
            evt.consume();
            JOptionPane.showMessageDialog(null, "No ingrese tantos números", "Advertencia!", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_txtCedulaKeyTyped

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        // Se llama al nuevo método centralizado para actualizar la tabla.
        actualizarTabla();
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void cmbEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEstadoActionPerformed
        // Se actualiza la variable de estado del filtro.
        this.stateFilter = cmbEstado.getSelectedItem().toString().toLowerCase();
        // Se llama al nuevo método centralizado.
        actualizarTabla();
    }//GEN-LAST:event_cmbEstadoActionPerformed

    private void atxtObservacionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_atxtObservacionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            atxtObservacion.transferFocus();
            btnSave.requestFocus();
        }
    }//GEN-LAST:event_atxtObservacionKeyPressed

    private void txtSaldoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSaldoKeyPressed

    }//GEN-LAST:event_txtSaldoKeyPressed

    private void txtSaldoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSaldoKeyTyped

    }//GEN-LAST:event_txtSaldoKeyTyped

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        if (txtIdapostadores.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un apostador de la tabla.", "Acción Requerida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int idApostador = Integer.parseInt(txtIdapostadores.getText());
        String nombreApostador = txtNombre.getText();

        // Se abre el diálogo de AgregarSaldo de la forma correcta, pasando los datos.
        AgregarSaldo dialogo = new AgregarSaldo((Frame) this.getParent(), true, idApostador, nombreApostador);
        dialogo.setVisible(true);

        // Si la operación en el diálogo fue exitosa, se actualiza la tabla.
        if (dialogo.isGuardadoExitoso()) {
            actualizarTabla();
            limpiarFormulario();
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void txtSaldoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSaldoKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            txtSaldo.transferFocus();
            atxtObservacion.requestFocus();
        }
    }//GEN-LAST:event_txtSaldoKeyReleased

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

            String ruta = AppPaths.REPORTS_DIR + File.separator + "Apostadores.jrxml";
            File jrxmlFile = new File(ruta);
            InputStream is = new FileInputStream(jrxmlFile);
            if (!jrxmlFile.exists()) {
                throw new FileNotFoundException("No se encontró el .jrxml en: " + ruta);
            }

            DefaultTableModel original = (DefaultTableModel) tblApostadores.getModel();
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

            // 2. Se crean los símbolos de formato personalizados
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();

            // 3. Se establece explícitamente el punto como separador de miles
            symbols.setGroupingSeparator('.');

            // 4. Se crea el formateador usando ese juego de símbolos
            DecimalFormat formateador = new DecimalFormat("#,###", symbols);

            DefaultTableModel filtrado = new DefaultTableModel();
            for (int colIndex : colsPermitidas) {
                filtrado.addColumn(original.getColumnName(colIndex));
            }
            for (int row = 0; row < original.getRowCount(); row++) {
                Object[] fila = new Object[colsPermitidas.size()];
                for (int i = 0; i < colsPermitidas.size(); i++) {

                    int originalColIndex = colsPermitidas.get(i);
                    Object valor = original.getValueAt(row, originalColIndex);

                    // Se verifica si la columna es "Saldo" y si el valor es un número
                    if (original.getColumnName(originalColIndex).equalsIgnoreCase("Saldo") && valor instanceof Number) {
                        // Se formatea el número a String con separador de miles
                        fila[i] = formateador.format(valor);
                    } else {
                        // Para las demás columnas, se copia el valor tal cual
                        fila[i] = (valor == null) ? "" : valor;
                    }
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

            JDialog dialog = new JDialog((Frame) null, "Apostadores", true);
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
//        try {
//            // 1. Obtener los datos FRESCOS desde el controlador.
//            List<ApostadorParaVista_DTO> datos = controller.listarApostadores(txtBuscar.getText(), stateFilter);
//
//            if (datos.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "No hay datos para exportar.", "Tabla Vacía", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//
//            // 2. Pedir al usuario la ubicación del archivo.
//            JFileChooser chooser = new JFileChooser();
//            chooser.setDialogTitle("Guardar como Excel");
//            chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
//            chooser.setSelectedFile(new File("Reporte_Apostadores.xlsx"));
//            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
//                return; // El usuario canceló.
//            }
//
//            // 3. Preparar los datos en el formato genérico que el exportador necesita.
//            List<String> headers = List.of("Cédula", "Apostador", "Saldo", "Observación", "Estado de Deuda");
//            List<List<Object>> dataRows = new ArrayList<>();
//            for (ApostadorParaVista_DTO dto : datos) {
//                dataRows.add(Arrays.asList(
//                        dto.getApostador().getCedula(),
//                        dto.getApostador().getNombre(),
//                        dto.getApostador().getSaldo(),
//                        dto.getApostador().getObservacion(),
//                        dto.getEstadoDeuda() != null ? dto.getEstadoDeuda() : "OK"
//                ));
//            }
//
//            // 4. Llamar al exportador genérico.
//            ExcelExporter.export(headers, dataRows, "Apostadores", chooser.getSelectedFile().getAbsolutePath());
//
//            JOptionPane.showMessageDialog(this, "Excel exportado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//
//        } catch (ServiceException | IOException e) {
//            JOptionPane.showMessageDialog(this, "Error al exportar a Excel:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }

        try {
            // 1. Obtener los datos FRESCOS desde el controlador.
            List<ApostadorParaVista_DTO> datos = controller.listarApostadores(txtBuscar.getText(), stateFilter);

            if (datos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay datos para exportar.", "Tabla Vacía", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Pedir al usuario la ubicación del archivo.
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Guardar como Excel");
            chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
            chooser.setSelectedFile(new File("Reporte_Apostadores.xlsx"));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            // 3. Preparar los datos en el formato genérico.
            // --- INICIO DE LA CORRECCIÓN ---
            List<String> headers = List.of("Cédula", "Apostador", "Saldo", "Observación", "Estado");
            List<List<Object>> dataRows = new ArrayList<>();

            for (ApostadorParaVista_DTO dto : datos) {
                dataRows.add(Arrays.asList(
                        dto.getApostador().getCedula(),
                        dto.getApostador().getNombre(),
                        dto.getApostador().getSaldo(),
                        dto.getApostador().getObservacion(),
                        // Se obtiene el estado (activo/inactivo) en lugar del estado de la deuda.
                        dto.getApostador().getFk_estados() == 1 ? "Activo" : "Inactivo"
                ));
            }
            // --- FIN DE LA CORRECCIÓN ---

            // 4. Llamar al exportador genérico.
            ExcelExporter.export(headers, dataRows, "Apostadores", chooser.getSelectedFile().getAbsolutePath());

            JOptionPane.showMessageDialog(this, "Excel exportado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (ServiceException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error al exportar a Excel:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnExcelActionPerformed

    private void pmnuHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pmnuHistorialActionPerformed
        int selectedRow = tblApostadores.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = tblApostadores.convertRowIndexToModel(selectedRow);
        int idApostador = Integer.parseInt(tblApostadores.getModel().getValueAt(modelRow, 0).toString());

        // Llamamos a tu método 'openHistorial' que ya tiene la lógica.
        openHistorial(idApostador);
    }//GEN-LAST:event_pmnuHistorialActionPerformed

    private void pmnuPendientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pmnuPendientesActionPerformed
        int selectedRow = tblApostadores.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un apostador.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tblApostadores.convertRowIndexToModel(selectedRow);
        int idApostador = (int) tblApostadores.getModel().getValueAt(modelRow, 0);
        String nombre = (String) tblApostadores.getModel().getValueAt(modelRow, 2);
        String cedula = (String) tblApostadores.getModel().getValueAt(modelRow, 1);

        // 1. Se crea y se muestra el diálogo. El código se detiene aquí hasta que se cierre.
        Pendientes dialogPendientes = new Pendientes(
                (Frame) SwingUtilities.getWindowAncestor(this),
                true,
                idApostador,
                nombre,
                cedula
        );
        dialogPendientes.setVisible(true);

        // 2. Cuando el diálogo se cierra, se le pregunta si se hizo algún cambio.
        if (dialogPendientes.seHizoUnCambio()) {
            // 3. Si hubo cambios (se pagó una deuda), se actualiza la tabla de apostadores.
            this.actualizarTabla();
        }
    }//GEN-LAST:event_pmnuPendientesActionPerformed

    Frame f = JOptionPane.getFrameForComponent(this);

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
            java.util.logging.Logger.getLogger(Apostadores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Apostadores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Apostadores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Apostadores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Apostadores dialog = new Apostadores(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chbActive;
    private javax.swing.JComboBox<String> cmbEstado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JPopupMenu pmnuApostadores;
    private javax.swing.JMenuItem pmnuHistorial;
    private javax.swing.JMenuItem pmnuPendientes;
    public static javax.swing.JTable tblApostadores;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCedula;
    private javax.swing.JTextField txtIdapostadores;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNumero;
    public static javax.swing.JTextField txtSaldo;
    // End of variables declaration//GEN-END:variables

}
