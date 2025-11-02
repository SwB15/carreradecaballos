package View;

import Config.AppPaths;
import Controller.Carreras_Controller;
import Model.CarreraParaVista_DTO;
import Model.Vencidos_Model;
import Repository.Apuestas_Repository;
import Services.Apuestas_Services;
import Services.Exceptions.ServiceException;
import Utils.Export_Excel;
import java.awt.BorderLayout;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
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
public class Principal extends javax.swing.JFrame {

    private final Carreras_Controller carrerasController;
    private List<CarreraParaVista_DTO> listaActualDeCarreras;

    public Principal() {
        initComponents();
        this.setExtendedState(MAXIMIZED_BOTH);
        this.carrerasController = new Carreras_Controller();

        // Se llama a un método de inicialización para mantener el constructor limpio.
        initializeView();

        pnlNotificaciones.setVisible(false);
        rbtnPendiente.setSelected(true);
        txtDesde.setVisible(false);
        txtHasta.setVisible(false);

        mnuMicuenta.setVisible(false);
    }

    /**
     * Configura el estado inicial de la vista y carga los datos.
     */
    // En View/Principal.java
    private void initializeView() {
        pnlNotificaciones.setVisible(false);
        rbtnPendiente.setSelected(true);

        // Se establecen las fechas del mes actual
        LocalDate hoy = LocalDate.now();
        dchDesde.setDate(Date.from(hoy.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dchHasta.setDate(Date.from(hoy.withDayOfMonth(hoy.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // Se añaden listeners solo a los componentes de filtro que existen.
        // Cada vez que un filtro cambia, se actualiza la tabla.
        dchDesde.addPropertyChangeListener("date", evt -> actualizarTabla());
        dchHasta.addPropertyChangeListener("date", evt -> actualizarTabla());
        cmbEstado.addActionListener(e -> actualizarTabla());
        rbtnPendiente.addActionListener(e -> actualizarTabla());
        rbtnFinalizado.addActionListener(e -> actualizarTabla());
        rbtnTodos.addActionListener(e -> actualizarTabla()); // Asumiendo que tienes un rbtnTodos para la fase

        // Carga inicial de datos
        actualizarTabla();

        // Lógica para cargar las notificaciones al iniciar la aplicación
        initializeApplicationState();
        configurarIconoAplicacion();
        notificacion();
    }

    /**
     * Carga el ícono de la aplicación desde los recursos y lo establece en la
     * ventana.
     */
    private void configurarIconoAplicacion() {
        try {
            java.net.URL iconURL = getClass().getResource("/Images/icono5.png");

            if (iconURL != null) {
                ImageIcon icono = new ImageIcon(iconURL);
                this.setIconImage(icono.getImage());
            } else {
                // Este mensaje te ayudará a saber si el archivo no se está encontrando.
                System.err.println("Error: No se encontró el recurso del ícono en la ruta: /Images/icono5.png");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el ícono de la aplicación.");
            e.printStackTrace();
        }
    }

    /**
     * Carga las notificaciones de apuestas vencidas al iniciar. (Este es el
     * método que habíamos diseñado previamente para las notificaciones).
     */
    private void initializeApplicationState() {
        try {
            Apuestas_Services apuestasService = new Apuestas_Services(new Apuestas_Repository());
            List<Vencidos_Model> apuestasVencidas = apuestasService.consultarApuestasVencidas();

            if (!apuestasVencidas.isEmpty()) {
                pnlNotificaciones.setVisible(true);
                lblCantidadNotificaciones.setText(String.valueOf(apuestasVencidas.size()));

                // Se añade el listener para abrir el diálogo de notificaciones
                pnlNotificaciones.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        Notificacion dialogo = new Notificacion(Principal.this, true, apuestasVencidas);
                        dialogo.setVisible(true);
                    }
                });
            }
        } catch (ServiceException e) {
            System.err.println("Error al cargar notificaciones: " + e.getMessage());
            // Opcionalmente, mostrar un JOptionPane si el error es crítico.
        }
    }

    /**
     * El nuevo método central para cargar y refrescar los datos de la tabla
     * principal.
     */
    public void actualizarTabla() {
        try {
            // CORRECCIÓN: Se lee el filtro de estado desde el ComboBox.
            String statusFilter = cmbEstado.getSelectedItem().toString().toLowerCase();

            String faseFilter = rbtnPendiente.isSelected() ? "pendientes"
                    : rbtnFinalizado.isSelected() ? "finalizados"
                    : "todos";

            // 2. Llamar al controlador para obtener los datos
            List<CarreraParaVista_DTO> listaCarreras = carrerasController.listarCarreras("", faseFilter, statusFilter);
            this.listaActualDeCarreras = carrerasController.listarCarreras("", faseFilter, statusFilter);

            // 3. Construir el TableModel
            String[] titles = {"ID", "Nombre", "Fecha", "Fecha Límite", "Ganador", "Participantes", "Comisión", "Observacion", "Estado"};
            DefaultTableModel model = new DefaultTableModel(null, titles) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (CarreraParaVista_DTO dto : listaCarreras) {
                model.addRow(new Object[]{
                    dto.getCarrera().getIdCarrera(),
                    dto.getCarrera().getNombre(),
                    dto.getCarrera().getFecha(),
                    dto.getCarrera().getFechaLimite(),
                    dto.getNombreGanador() != null ? dto.getNombreGanador() : "Pendiente",
                    dto.getParticipantes(), // Se pasa la lista completa
                    dto.getCarrera().getComision(),
                    dto.getObservacion(),
                    dto.getNombreEstado()
                });
            }

            // 4. Asignar modelo y renderizador
            tblPrincipal.setModel(model);
            tblPrincipal.setDefaultRenderer(Object.class, new Principal_TableCellRenderer());

            // 5. Ordenador y columnas
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            tblPrincipal.setRowSorter(sorter);
//            ocultar_columnas(tblPrincipal);

        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las carreras: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Oculta las columnas deseadas con los índices actualizados.
     */
    private void ocultar_columnas(JTable table) {
        // --- ÍNDICES ACTUALIZADOS ---
        // 0: Id (Oculto)
        // 1: Nombre
        // 2: Lugar (Oculto)
        // 3: Fecha
        // 4: Fecha Limite
        // 5: Id Ganador (Oculto)
        // 6: Observacion
        // 7: Estado
        // 8: IDs Caballos (Oculto)
        // 9: Caballos (Oculto)
        // 10: Ganador
        // 11: Comision

        // Oculta "Id" (índice 0)
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
//
//        // Oculta "Lugar" (índice 2)
//        table.getColumnModel().getColumn(2).setMaxWidth(0);
//        table.getColumnModel().getColumn(2).setMinWidth(0);
//        table.getColumnModel().getColumn(2).setPreferredWidth(0);
//
//        // Oculta "Id Ganador" (ahora índice 5)
//        table.getColumnModel().getColumn(5).setMaxWidth(0);
//        table.getColumnModel().getColumn(5).setMinWidth(0);
//        table.getColumnModel().getColumn(5).setPreferredWidth(0);
//
//        // Oculta "IDs Caballos" (ahora índice 8)
//        table.getColumnModel().getColumn(8).setMaxWidth(0);
//        table.getColumnModel().getColumn(8).setMinWidth(0);
//        table.getColumnModel().getColumn(8).setPreferredWidth(0);
//
//        // Oculta "Caballos" (ahora índice 9)
//        table.getColumnModel().getColumn(9).setMaxWidth(0);
//        table.getColumnModel().getColumn(9).setMinWidth(0);
//        table.getColumnModel().getColumn(9).setPreferredWidth(0);
    }

    private void notificacion() {
        // Convertir ruta relativa a ruta absoluta
        File file = new File("src/main/resources/Images/notificacion.png");
        String absolutePath = file.getAbsolutePath();

        ImageIcon iconoOriginal = new ImageIcon(absolutePath);
        Image imagenOriginal = iconoOriginal.getImage();

        // Redimensionar la imagen
        int nuevoAncho = 53;
        int nuevoAlto = 38;
        Image imagenRedimensionada = imagenOriginal.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);

        // Crea un nuevo ImageIcon con la imagen redimensionada
        ImageIcon iconoRedimensionado = new ImageIcon(imagenRedimensionada);
        lblIcono.setIcon(iconoRedimensionado);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rbtngPrincipal = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        rbtnPendiente = new javax.swing.JRadioButton();
        rbtnFinalizado = new javax.swing.JRadioButton();
        rbtnTodos = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        cmbEstado = new javax.swing.JComboBox<>();
        jSeparator2 = new javax.swing.JSeparator();
        dchDesde = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        dchHasta = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        pnlNotificaciones = new javax.swing.JPanel();
        lblCantidadNotificaciones = new javax.swing.JLabel();
        lblIcono = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPrincipal = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        btnImprimir = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtDesde = new javax.swing.JTextField();
        txtHasta = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        mnuBalance = new javax.swing.JMenu();
        mnuApostadores = new javax.swing.JMenu();
        mnuCaballos = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        mnuAjustes = new javax.swing.JMenu();
        mnuMicuenta = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        rbtngPrincipal.add(rbtnPendiente);
        rbtnPendiente.setText("Pendiente");
        rbtnPendiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnPendienteActionPerformed(evt);
            }
        });

        rbtngPrincipal.add(rbtnFinalizado);
        rbtnFinalizado.setText("Finalizado");
        rbtnFinalizado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnFinalizadoActionPerformed(evt);
            }
        });

        rbtngPrincipal.add(rbtnTodos);
        rbtnTodos.setText("Todos");
        rbtnTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnTodosActionPerformed(evt);
            }
        });

        jLabel9.setText("Estado:");

        cmbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Activo", "Inactivo" }));
        cmbEstado.setSelectedIndex(1);
        cmbEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbEstadoActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        dchDesde.setDateFormatString("dd/MM/yyyy");

        jLabel2.setText("Desde:");

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        dchHasta.setDateFormatString("dd/MM/yyyy");

        jLabel4.setText("Hasta:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtnFinalizado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtnPendiente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtnTodos)
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
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dchHasta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dchDesde, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(rbtnFinalizado)
                                .addComponent(rbtnPendiente)
                                .addComponent(rbtnTodos)
                                .addComponent(jLabel9)
                                .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator3)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pnlNotificaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlNotificacionesMouseClicked(evt);
            }
        });
        pnlNotificaciones.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblCantidadNotificaciones.setBackground(new java.awt.Color(236, 33, 39));
        lblCantidadNotificaciones.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblCantidadNotificaciones.setForeground(new java.awt.Color(255, 255, 255));
        lblCantidadNotificaciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCantidadNotificaciones.setText("99");
        lblCantidadNotificaciones.setName(""); // NOI18N
        lblCantidadNotificaciones.setOpaque(true);
        pnlNotificaciones.add(lblCantidadNotificaciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 5, 15, 15));

        lblIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/notificacion.png"))); // NOI18N
        pnlNotificaciones.add(lblIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 38));

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tblPrincipal = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblPrincipal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblPrincipal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPrincipalMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPrincipal);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1043, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                .addContainerGap())
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

        jMenuBar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuBar1MouseClicked(evt);
            }
        });

        jMenu2.setText("Juegos");
        jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu2MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu2);

        mnuBalance.setText("Balance");
        mnuBalance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuBalanceMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuBalance);

        mnuApostadores.setText("Apostadores");
        mnuApostadores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuApostadoresMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuApostadores);

        mnuCaballos.setText("Caballos");
        mnuCaballos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuCaballosMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuCaballos);

        jMenu3.setText("Movimientos");
        jMenu3.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu3MenuSelected(evt);
            }
        });
        jMenu3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu3MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu3);

        mnuAjustes.setText("Ajustes");
        mnuAjustes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuAjustesMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuAjustes);

        mnuMicuenta.setText("Mi Cuenta");
        jMenuBar1.add(mnuMicuenta);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlNotificaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(pnlNotificaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnImprimir)
                                    .addComponent(btnExcel)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuApostadoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuApostadoresMouseClicked
        Apostadores dialog = new Apostadores(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_mnuApostadoresMouseClicked

    private void mnuBalanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuBalanceMouseClicked
        Balance dialog = new Balance(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_mnuBalanceMouseClicked

    private void mnuCaballosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuCaballosMouseClicked
        Caballos dialog = new Caballos(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_mnuCaballosMouseClicked

    private void rbtnFinalizadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnFinalizadoActionPerformed
        actualizarTabla();
    }//GEN-LAST:event_rbtnFinalizadoActionPerformed

    private void rbtnPendienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnPendienteActionPerformed
        actualizarTabla();
    }//GEN-LAST:event_rbtnPendienteActionPerformed

    private void rbtnTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnTodosActionPerformed
        actualizarTabla();
    }//GEN-LAST:event_rbtnTodosActionPerformed

    private void pnlNotificacionesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlNotificacionesMouseClicked
        try {
            // Se obtienen los datos de las notificaciones desde el servicio.
            // NOTA: Debes tener una instancia de Apuestas_Services disponible en tu clase Principal.
            Apuestas_Services apuestasService = new Apuestas_Services(new Apuestas_Repository());
            List<Vencidos_Model> apuestasVencidas = apuestasService.consultarApuestasVencidas();

            // Se pasan los datos al constructor del diálogo.
            Notificacion dialogo = new Notificacion(this, true, apuestasVencidas);
            dialogo.setVisible(true);

        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las notificaciones:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_pnlNotificacionesMouseClicked

    private void mnuAjustesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuAjustesMouseClicked
        Configuraciones dialog = new Configuraciones(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_mnuAjustesMouseClicked

    private void jMenuBar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuBar1MouseClicked

    }//GEN-LAST:event_jMenuBar1MouseClicked

    private void jMenu2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseClicked
//        NewApuestas dialog = new NewApuestas(f, true);
//        dialog.setVisible(true);

        // 1. Se crea y muestra el diálogo. El código se detiene aquí hasta que se cierre.
        NewApuestas dialog = new NewApuestas(this, true);
        dialog.setVisible(true);

        // 2. Cuando el diálogo se cierra, se comprueba si se hizo algún cambio.
        if (dialog.seHizoUnCambio()) {
            // 3. Si hubo cambios, se actualiza la tabla principal.
            actualizarTabla();
        }
        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);

    }//GEN-LAST:event_jMenu2MouseClicked

    private void jMenu3MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu3MenuSelected
//        Movimientos dialog = new Movimientos(f, true);
//        dialog.setVisible(true);
//
//        this.setLocationRelativeTo(null);
//        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_jMenu3MenuSelected

    private void jMenu3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu3MouseClicked
        Movimientos dialog = new Movimientos(f, true);
        dialog.setVisible(true);

        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_jMenu3MouseClicked

    private void tblPrincipalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPrincipalMouseClicked
        // Se verifica si es un clic derecho para el menú contextual
        if (SwingUtilities.isRightMouseButton(evt)) {
            int filaVista = tblPrincipal.rowAtPoint(evt.getPoint());
            if (filaVista != -1) {
                tblPrincipal.setRowSelectionInterval(filaVista, filaVista);

                // Se obtiene el DTO completo correspondiente a la fila seleccionada.
                int filaModelo = tblPrincipal.convertRowIndexToModel(filaVista);
                CarreraParaVista_DTO carreraSeleccionada = this.listaActualDeCarreras.get(filaModelo);

                // Se crea el menú popup
                JPopupMenu popup = new JPopupMenu();
                JMenuItem editItem = new JMenuItem("Editar Carrera");
                editItem.addActionListener(e -> {
                    // Se abre la ventana de edición, pasándole el objeto DTO completo.
                    NewApuestas dialog = new NewApuestas(
                            (Frame) SwingUtilities.getWindowAncestor(this),
                            true,
                            carreraSeleccionada // Se pasa el objeto, no una lista de Strings
                    );
                    dialog.setVisible(true);

                    // Después de que el diálogo se cierre, se actualiza la tabla por si hubo cambios.
                    actualizarTabla();
                });
                popup.add(editItem);

                // Se pueden añadir más opciones al menú aquí...
                // JMenuItem verDetallesItem = new JMenuItem("Ver Detalles");
                // popup.add(verDetallesItem);
                popup.show(tblPrincipal, evt.getX(), evt.getY());
            }
        }
    }//GEN-LAST:event_tblPrincipalMouseClicked

    private void cmbEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEstadoActionPerformed
        actualizarTabla();
    }//GEN-LAST:event_cmbEstadoActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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

            String ruta = AppPaths.REPORTS_DIR + File.separator + "Juegos.jrxml";
            File jrxmlFile = new File(ruta);
            InputStream is = new FileInputStream(jrxmlFile);
            if (!jrxmlFile.exists()) {
                throw new FileNotFoundException("No se encontró el .jrxml en: " + ruta);
            }

            DefaultTableModel original = (DefaultTableModel) tblPrincipal.getModel();
            int[] colsNoPermitidas = {0, 3, 5, 6};
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

                    // Si es la columna de la fecha (índice 2) y el valor es un LocalDate...
                    if (originalColIndex == 2 && valor instanceof LocalDate) {
                        // ...se formatea a String.
                        fila[i] = ((LocalDate) valor).format(formatter);
                    } else {
                        // Para todas las demás columnas, se copia el valor tal cual.
                        fila[i] = valor;
                    }
                }
                filtrado.addRow(fila);
            }

            JRTableModelDataSource datasource = new JRTableModelDataSource(filtrado);
            JasperReport jr = JasperCompileManager.compileReport(is);
            Map<String, Object> params = new HashMap<>();
            params.put("Logo", rutaLogo);
            // 1. Se define el formato deseado

            // 2. Se procesa la fecha "Desde"
            Date fechaDesde = dchDesde.getDate();
            if (fechaDesde != null) {
                String desdeStr = fechaDesde.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(formatter);
                params.put("Desde", desdeStr);
            } else {
                params.put("Desde", "N/A"); // O un texto por defecto si la fecha está vacía
            }

            // 3. Se procesa la fecha "Hasta"
            Date fechaHasta = dchHasta.getDate();
            if (fechaHasta != null) {
                String hastaStr = fechaHasta.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(formatter);
                params.put("Hasta", hastaStr);
            } else {
                params.put("Hasta", "N/A");
            }
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
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // Sólo archivos
                chooser.setAcceptAllFileFilterUsed(false); // No permitir “Todos los archivos”
                FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG (*.png)", "png"); // Filtro de sólo PNG
                chooser.addChoosableFileFilter(pngFilter);
                chooser.setFileFilter(pngFilter);
                chooser.setApproveButtonText("Guardar");
                chooser.setApproveButtonToolTipText("Guardar reporte en imágenes PNG");

                //Mostrar dialogo
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

            JDialog dialog = new JDialog((Frame) null, "Juegos", true);
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
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
//        try {
        // 1. Obtener los filtros y los datos FRESCOS del controlador.
//            String statusFilter = cmbEstado.getSelectedItem().toString().toLowerCase();
//            String faseFilter = rbtnPendiente.isSelected() ? "pendientes" : rbtnFinalizado.isSelected() ? "finalizados" : "todos";
//            List<CarreraParaVista_DTO> datos = carrerasController.listarCarreras("", faseFilter, statusFilter);
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
//            chooser.setSelectedFile(new File("Reporte_Juegos.xlsx"));
//            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
//                return; // El usuario canceló.
//            }
//
//            // 3. Preparar los datos en el formato genérico que el exportador necesita.
//            List<String> headers = List.of("ID", "Nombre", "Fecha", "Fecha Límite", "Ganador", "Comisión", "Observacion", "Estado");
//            List<List<Object>> dataRows = new ArrayList<>();
//
//            for (CarreraParaVista_DTO dto : datos) {
//                // --- CORRECCIÓN AQUÍ: Se usa Arrays.asList en lugar de List.of ---
//                dataRows.add(Arrays.asList(
//                        dto.getCarrera().getIdCarrera(),
//                        dto.getCarrera().getNombre(),
//                        dto.getCarrera().getFecha(),
//                        dto.getCarrera().getFechaLimite(),
//                        dto.getNombreGanador() != null ? dto.getNombreGanador() : "Pendiente",
//                        dto.getCarrera().getComision(),
//                        dto.getObservacion(),
//                        dto.getNombreEstado()
//                ));
//            }
//
//            ExcelExporter.export(headers, dataRows, "Juegos", chooser.getSelectedFile().getAbsolutePath());
//
//            JOptionPane.showMessageDialog(this, "Excel exportado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//
//        } catch (ServiceException | IOException e) {
//            JOptionPane.showMessageDialog(this, "Error al exportar a Excel:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }

        DefaultTableModel model = (DefaultTableModel) tblPrincipal.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "La tabla está vacía. No se puede exportar un Excel sin datos.",
                    "Tabla vacía",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Set<Integer> columnsToSkip = Set.of(0, 3, 5, 6);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Excel");
        chooser.setApproveButtonText("Guardar");
        // Filtro opcional para que solo muestre .xlsx
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        chooser.setSelectedFile(new File("Juegos.xlsx"));

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
            Export_Excel.export(tblPrincipal,
                    "Juegos",
                    destino.getAbsolutePath(),
                    columnsToSkip);
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
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnImprimir;
    public static javax.swing.JComboBox<String> cmbEstado;
    public static com.toedter.calendar.JDateChooser dchDesde;
    public static com.toedter.calendar.JDateChooser dchHasta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    public static javax.swing.JLabel lblCantidadNotificaciones;
    private javax.swing.JLabel lblIcono;
    private javax.swing.JMenu mnuAjustes;
    private javax.swing.JMenu mnuApostadores;
    private javax.swing.JMenu mnuBalance;
    private javax.swing.JMenu mnuCaballos;
    private javax.swing.JMenu mnuMicuenta;
    public static javax.swing.JPanel pnlNotificaciones;
    public static javax.swing.JRadioButton rbtnFinalizado;
    public static javax.swing.JRadioButton rbtnPendiente;
    public static javax.swing.JRadioButton rbtnTodos;
    private javax.swing.ButtonGroup rbtngPrincipal;
    private javax.swing.JTable tblPrincipal;
    public static javax.swing.JTextField txtDesde;
    public static javax.swing.JTextField txtHasta;
    // End of variables declaration//GEN-END:variables

}
