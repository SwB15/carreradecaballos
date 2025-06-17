package View;

import Config.AppPaths;
import Config.Export_Excel;
import Controller.Carreras_Controller;
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
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
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
public class Principal extends javax.swing.JFrame {

    public static Principal instancia;
    Carreras_Controller Carreras_controller = new Carreras_Controller();
    private String stateFilter = "todos";
    public String statusFilter = "Pendiente", datefrom = "", dateto = "", desdeStr = "", hastaStr = "";

    public Principal() {
        initComponents();
        this.setExtendedState(MAXIMIZED_BOTH);
        pnlNotificaciones.setVisible(false);
        rbtnPendiente.setSelected(true);
        txtDesde.setVisible(false);
        txtHasta.setVisible(false);

        // Calcular el primer y último día del mes
        LocalDate hoy = LocalDate.now();
        LocalDate primer = hoy.withDayOfMonth(1);
        LocalDate ultimo = hoy.withDayOfMonth(hoy.lengthOfMonth());

        // Asignar esas fechas a los JDateChooser
        dchDesde.setDate(java.sql.Date.valueOf(primer));
        dchHasta.setDate(java.sql.Date.valueOf(ultimo));

        // Llenar datefrom/dateto y cargar la tabla inicial
        fechas();
        instancia = this;
        showCarrerasInPrincipal("", stateFilter, statusFilter, datefrom, dateto);

        // -------- Aquí se añaden los listeners --------
        // Cuando cambie la fecha "Desde"
        dchDesde.addPropertyChangeListener("date", evt -> {
            fechas();  // recalcula datefrom y dateto
            showCarrerasInPrincipal("", stateFilter, statusFilter, datefrom, dateto);
        });

        // Cuando cambie la fecha "Hasta"
        dchHasta.addPropertyChangeListener("date", evt -> {
            fechas();
            showCarrerasInPrincipal("", stateFilter, statusFilter, datefrom, dateto);
        });

        // -----------------------------------------------
        notificacion();

        // Icono de la aplicación
        File file2 = new File("src/main/resources/Images/icono5.png");
        //ImageIcon icon = new ImageIcon(getClass().getResource("/recursos/LogoPanaderia.png"));
        String absolutePath2 = file2.getAbsolutePath();
        ImageIcon iconoOriginal2 = new ImageIcon(absolutePath2);
        Image imagenOriginal2 = iconoOriginal2.getImage();
        this.setIconImage(imagenOriginal2);

        // Ocultar menús hasta login
        mnuApuestas.setVisible(false);
        mnuCarreras.setVisible(false);
        mnuListaApuestas.setVisible(false);
        mnuMicuenta.setVisible(false);
    }

    public void showCarrerasInPrincipal(String search, String stateFilter, String statusFilter, String datefrom, String dateto) {
        try {
            DefaultTableModel model;
            model = Carreras_controller.showCarrerasInPrincipal(search, stateFilter, statusFilter, datefrom, dateto);
            tblPrincipal.setModel(model);
            ocultar_columnas(tblPrincipal);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void ocultar_columnas(JTable table) {
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        table.getColumnModel().getColumn(2).setMaxWidth(0);
        table.getColumnModel().getColumn(2).setMinWidth(0);
        table.getColumnModel().getColumn(2).setPreferredWidth(0);

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

    private void fechas() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        desdeStr = sdf.format(dchDesde.getDate());
        hastaStr = sdf.format(dchHasta.getDate());

        System.out.println("desdeStr: " + desdeStr);
        System.out.println("hastaStr: " + hastaStr);

        // Formateadores para convertir los Strings a LocalDate y LocalTime
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Convertir los Strings a LocalDate y LocalTime
        LocalDate desde = LocalDate.parse(desdeStr, formatoFecha);
        LocalDate hasta = LocalDate.parse(hastaStr, formatoFecha);

        // Formatearlo a String en formato compatible con MySQL (yyyy-MM-dd HH:mm:ss)
        DateTimeFormatter formatoMySQL = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        datefrom = desde.format(formatoMySQL);
        dateto = hasta.format(formatoMySQL);

        txtDesde.setText("");
        txtHasta.setText("");

        txtDesde.setText(datefrom);
        txtHasta.setText(dateto);
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
        mnuCarreras = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        mnuApuestas = new javax.swing.JMenu();
        mnuBalance = new javax.swing.JMenu();
        mnuApostadores = new javax.swing.JMenu();
        mnuCaballos = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        mnuListaApuestas = new javax.swing.JMenu();
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

        mnuCarreras.setText("Carreras");
        mnuCarreras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuCarrerasMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuCarreras);

        jMenu2.setText("Juegos");
        jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu2MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu2);

        mnuApuestas.setText("Apuestas");
        mnuApuestas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuApuestasMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuApuestas);

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

        jMenu1.setText("Ajustes");
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu1MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu1);

        mnuListaApuestas.setText("Lista Apuestas");
        mnuListaApuestas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuListaApuestasMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuListaApuestas);

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
        statusFilter = "Finalizado";
        showCarrerasInPrincipal("", stateFilter, "Finalizado", datefrom, dateto);
    }//GEN-LAST:event_rbtnFinalizadoActionPerformed

    private void rbtnPendienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnPendienteActionPerformed
        statusFilter = "Pendiente";
        showCarrerasInPrincipal("", stateFilter, statusFilter, datefrom, dateto);
    }//GEN-LAST:event_rbtnPendienteActionPerformed

    private void rbtnTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnTodosActionPerformed
        statusFilter = "Todos";
        showCarrerasInPrincipal("", stateFilter, statusFilter, datefrom, dateto);
    }//GEN-LAST:event_rbtnTodosActionPerformed

    private void pnlNotificacionesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlNotificacionesMouseClicked
        Notificacion dialog = new Notificacion(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_pnlNotificacionesMouseClicked

    private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
        Configuraciones dialog = new Configuraciones(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_jMenu1MouseClicked

    private void jMenuBar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuBar1MouseClicked

    }//GEN-LAST:event_jMenuBar1MouseClicked

    private void jMenu2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseClicked
        NewApuestas dialog = new NewApuestas(f, true);
        dialog.setVisible(true);

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
        if (SwingUtilities.isRightMouseButton(evt)) {
            int row = tblPrincipal.rowAtPoint(evt.getPoint());
            if (row != -1) {
                tblPrincipal.setRowSelectionInterval(row, row);

                JPopupMenu popup = new JPopupMenu();
                JMenuItem edit = new JMenuItem("Editar");
                popup.add(edit);

                edit.addActionListener(e -> {
                    int colCount = tblPrincipal.getColumnCount();
                    List<String> rowData = new ArrayList<>(colCount);
                    for (int col = 0; col < colCount; col++) {
                        Object val = tblPrincipal.getValueAt(row, col);
                        rowData.add(val != null ? val.toString() : "");
                    }

                    NewApuestas dialog = new NewApuestas(
                            (Frame) SwingUtilities.getWindowAncestor(tblPrincipal),
                            true
                    );
                    dialog.loadRowData(rowData);
                    dialog.setVisible(true);
                });

                // Mostrar el popup en la posición del clic
                popup.show(tblPrincipal, evt.getX(), evt.getY());
            }
        }
    }//GEN-LAST:event_tblPrincipalMouseClicked

    private void mnuListaApuestasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuListaApuestasMouseClicked
        ListaApuestas dialog = new ListaApuestas(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_mnuListaApuestasMouseClicked

    private void mnuApuestasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuApuestasMouseClicked
        Apuestas dialog = new Apuestas(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_mnuApuestasMouseClicked

    private void mnuCarrerasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuCarrerasMouseClicked
        Carreras dialog = new Carreras(f, true);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
    }//GEN-LAST:event_mnuCarrerasMouseClicked

    private void cmbEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEstadoActionPerformed
        switch (cmbEstado.getSelectedIndex()) {
            case 0 ->
                stateFilter = "Todos";
            case 1 ->
                stateFilter = "activo";
            case 2 ->
                stateFilter = "inactivo";
            default -> {
            }
        }

        showCarrerasInPrincipal("", stateFilter, statusFilter, datefrom, dateto);
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

            String ruta = AppPaths.REPORTS_DIR + File.separator + "Juegos.jrxml";
            File jrxmlFile = new File(ruta);
            InputStream is = new FileInputStream(jrxmlFile);
            if (!jrxmlFile.exists()) {
                throw new FileNotFoundException("No se encontró el .jrxml en: " + ruta);
            }

            DefaultTableModel original = (DefaultTableModel) tblPrincipal.getModel();
            int[] colsNoPermitidas = {0, 2, 4, 7, 8};
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
                    fila[i] = original.getValueAt(row, colsPermitidas.get(i));
                }
                filtrado.addRow(fila);
            }

            JRTableModelDataSource datasource = new JRTableModelDataSource(filtrado);
            JasperReport jr = JasperCompileManager.compileReport(is);
            Map<String, Object> params = new HashMap<>();
            params.put("Logo", rutaLogo);
            params.put("Desde", desdeStr);
            params.put("Hasta", hastaStr);
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
        DefaultTableModel model = (DefaultTableModel) tblPrincipal.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "La tabla está vacía. No se puede exportar un Excel sin datos.",
                    "Tabla vacía",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Set<Integer> columnsToSkip = Set.of(0, 2, 4, 7, 8);

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
    private javax.swing.JMenu jMenu1;
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
    private javax.swing.JMenu mnuApostadores;
    private javax.swing.JMenu mnuApuestas;
    private javax.swing.JMenu mnuBalance;
    private javax.swing.JMenu mnuCaballos;
    private javax.swing.JMenu mnuCarreras;
    private javax.swing.JMenu mnuListaApuestas;
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
