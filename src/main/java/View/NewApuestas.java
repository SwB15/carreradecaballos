package View;

import Config.AppPaths;
import Controller.Apostadores_Controller;
import Controller.Apuestas_Controller;
import Controller.Caballos_Controller;
import Controller.Carreras_Controller;
import Model.ApostadorParaVista_DTO;
import Model.ApuestasParaVista_DTO;
import Model.Apuestas_DTO;
import Model.Caballos_Model;
import Model.CarreraParaVista_DTO;
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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.NumberFormatter;
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
public class NewApuestas extends javax.swing.JDialog {

    private final Carreras_Controller carrerasController;
    private final Apostadores_Controller apostadoresController;
    private final Caballos_Controller caballosController;
    private final Apuestas_Controller apuestasController;
    private List<Apuestas_DTO> apuestasEnTabla = new ArrayList<>();

    private String finalState = "", stateFilter = "todos";
    int comision = 10;
    Date startDate = null, endDate = null;
    DecimalFormat formateador14 = new DecimalFormat("#,###.###");
    LocalDate localDate = LocalDate.now();
    Date currentDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    private boolean seHizoUnCambio = false;

    /**
     * Constructor para CREAR una nueva carrera.
     */
    public NewApuestas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);

        this.carrerasController = new Carreras_Controller();
        this.apostadoresController = new Apostadores_Controller();
        this.caballosController = new Caballos_Controller();
        this.apuestasController = new Apuestas_Controller();
        this.apuestasEnTabla = new ArrayList<>();

        txtNumero.setEditable(false);
        txtNumero.setBackground(Color.white);

        initializeViewForCreate();
    }

    /**
     * Constructor para EDITAR una carrera existente.
     */
    public NewApuestas(java.awt.Frame parent, boolean modal, CarreraParaVista_DTO carreraDTO) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);

        this.carrerasController = new Carreras_Controller();
        this.apostadoresController = new Apostadores_Controller();
        this.caballosController = new Caballos_Controller();
        this.apuestasController = new Apuestas_Controller();
        this.apuestasEnTabla = new ArrayList<>();

        txtNumero.setEditable(false);
        txtNumero.setBackground(Color.white);

        initializeViewForEdit(carreraDTO);
    }

    public boolean seHizoUnCambio() {
        return seHizoUnCambio;
    }

    /**
     * Configura la vista para el modo CREACIÓN.
     */
    private void initializeViewForCreate() {
        // Configuración común
        commonViewSetup();

        // Configuración específica de CREACIÓN
        setTitle("Nuevo Juego");
        try {
            int siguienteNumero = carrerasController.getSiguienteNumeroDeCarrera();
            txtNumero.setText(String.valueOf(siguienteNumero));
            txtNombre.setText("Juego N°: " + siguienteNumero);
        } catch (ServiceException e) {
            txtNumero.setText("Error");
        }
        dchFecha.setDate(new Date());
        dchFechalimite.setDate(new Date());
        spnComision.setValue(10);
        chbActive.setSelected(true);
        atxtObservacion.requestFocus();
    }

    /**
     * Configura la vista para el modo EDICIÓN.
     */
    private void initializeViewForEdit(CarreraParaVista_DTO carreraDTO) {
        // Configuración común
        commonViewSetup();

        // Configuración específica de EDICIÓN
        cargarDatosDeCarrera(carreraDTO);
        setTitle("Editar " + txtNombre.getText());
    }

    /**
     * Contiene la configuración de UI común para ambos modos.
     */
    private void commonViewSetup() {
        txtGanador.setEditable(false);
        txtGanador.setBackground(Color.white);
        txtIdganador.setVisible(false);
        txtIdcarreras.setVisible(false);

        configurarTablaElegidos();
        actualizarTablaApostadores();
        actualizarTablaCaballos();
        addDobleClickListeners();
    }

    /**
     * Añade los listeners de doble clic a las tablas de caballos y apostadores.
     */
    private void addDobleClickListeners() {
        tblCaballos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int fila = tblCaballos.getSelectedRow();
                    if (fila == -1) {
                        return;
                    }
                    int idCaballo = (int) tblCaballos.getValueAt(fila, 0);
                    String nombreCaballo = (String) tblCaballos.getValueAt(fila, 1);
                    addCaballoToElegidos(idCaballo, nombreCaballo);
                }
            }
        });

        tblApostadores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int fila = tblApostadores.getSelectedRow();
                    if (fila == -1) {
                        return;
                    }
                    int idApostador = (int) tblApostadores.getValueAt(fila, 0);
                    String nombreApostador = (String) tblApostadores.getValueAt(fila, 1);
                    int saldo = (int) tblApostadores.getValueAt(fila, 2);
                    addApostadorToElegidos(idApostador, nombreApostador, saldo);
                }
            }
        });
    }

    /**
     * Limpia todos los campos del formulario y la tabla de apuestas, preparando
     * la vista para una nueva entrada de carrera.
     */
    private void limpiarFormulario() {
        // Limpiar campos de la cabecera
        txtIdcarreras.setText("");
        txtNumero.setText("Nuevo");
        txtNombre.setText("Juego N°: "); // Puedes añadir el siguiente ID si lo deseas
        txtGanador.setText("");
        txtIdganador.setText("");
        atxtObservacion.setText("");

        // Resetear fechas y spinner
        dchFecha.setDate(new Date());
        dchFechalimite.setDate(new Date());
        spnComision.setValue(10);
        chbActive.setSelected(true);

        // Limpiar la lista de datos en memoria
        apuestasEnTabla.clear();

        // Refrescar la tabla de elegidos (que ahora estará vacía)
        refrescarTablaElegidos();

        // Poner el foco en el primer campo editable
        txtNombre.requestFocus();
    }

    /**
     * Añade un apostador a la primera fila disponible en la tabla de elegidos,
     * o crea una nueva fila si todas están completas.
     */
    private void addApostadorToElegidos(int id, String nombre, int saldo) {
        for (Apuestas_DTO apuesta : apuestasEnTabla) {
            if (apuesta.getIdApostador() == null || apuesta.getIdApostador() == 0) {
                apuesta.setIdApostador(id);
                apuesta.setNombreApostador(nombre);
                apuesta.setSaldo(saldo);
                apuesta.setSaldoOriginal(saldo);
                refrescarTablaElegidos();
                return;
            }
        }
        // Crea un nuevo DTO usando el constructor simplificado
        Apuestas_DTO nuevaApuesta = new Apuestas_DTO(null, null, null, 0, 0, id, nombre, saldo, saldo, 0);
        apuestasEnTabla.add(nuevaApuesta);
        refrescarTablaElegidos();
    }

    /**
     * Añade un caballo a la primera fila disponible en la tabla de elegidos, o
     * crea una nueva fila si todas están completas.
     */
    private void addCaballoToElegidos(int id, String nombre) {
        for (Apuestas_DTO apuesta : apuestasEnTabla) {
            if (apuesta.getIdCaballo() == null || apuesta.getIdCaballo() == 0) {
                apuesta.setIdCaballo(id);
                apuesta.setNombreCaballo(nombre);
                refrescarTablaElegidos();
                return;
            }
        }
        // Crea un nuevo DTO usando el constructor simplificado
        Apuestas_DTO nuevaApuesta = new Apuestas_DTO(null, id, nombre, 0, 0, null, null, 0, 0, 0);
        apuestasEnTabla.add(nuevaApuesta);
        refrescarTablaElegidos();
    }

    /**
     * Limpia y vuelve a llenar la tabla 'tblElegidos' usando la lista
     * 'apuestasEnTabla' como única fuente de datos.
     */
    private void refrescarTablaElegidos() {
        DefaultTableModel model = (DefaultTableModel) tblElegidos.getModel();
        model.setRowCount(0);

        System.out.println("\n--- VISTA (NewApuestas): Refrescando tabla 'tblElegidos' ---");
        System.out.println("Total de apuestas en la lista en memoria (apuestasEnTabla): " + apuestasEnTabla.size());

        for (Apuestas_DTO dto : apuestasEnTabla) {
            model.addRow(new Object[]{
                dto.getIdApuesta(),
                dto.getIdCaballo(),
                dto.getNombreCaballo(),
                dto.getMontoApostado(),
                dto.getMontoAbonado(),
                dto.getIdApostador(),
                dto.getNombreApostador(),
                dto.getSaldo()
            // Se omiten saldoOriginal y abonadoOriginal para que coincida con el modelo de 8 columnas
            });

            // Impresión detallada de cada DTO que se añade a la tabla
            System.out.println(String.format(
                    "   -> Añadiendo fila: [ID Apuesta: %s, ID Caballo: %s, Caballo: %s, Apostado: %d, Abonado: %d, ID Apostador: %s, Apostador: %s, Saldo: %d]",
                    dto.getIdApuesta(), dto.getIdCaballo(), dto.getNombreCaballo(),
                    dto.getMontoApostado(), dto.getMontoAbonado(), dto.getIdApostador(),
                    dto.getNombreApostador(), dto.getSaldo()
            ));
        }
        System.out.println("--- VISTA (NewApuestas): Fin del refresco de la tabla ---");
    }

    /**
     * Valida que todas las filas en la tabla de elegidos estén completas. DEBE
     * SER LLAMADO ANTES DE GUARDAR.
     *
     * @return true si todas las apuestas son válidas, false si alguna está
     * incompleta.
     */
    private boolean validarApuestasCompletas() {
        for (int i = 0; i < apuestasEnTabla.size(); i++) {
            Apuestas_DTO apuesta = apuestasEnTabla.get(i);
            if (apuesta.getIdCaballo() == null || apuesta.getIdCaballo() == 0) {
                JOptionPane.showMessageDialog(this, "La apuesta en la fila " + (i + 1) + " no tiene un caballo asignado.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (apuesta.getIdApostador() == null || apuesta.getIdApostador() == 0) {
                JOptionPane.showMessageDialog(this, "La apuesta en la fila " + (i + 1) + " no tiene un apostador asignado.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    /**
     * Carga los datos de una carrera existente en el formulario cuando se está
     * en modo de edición.
     */
    private void cargarDatosDeCarrera(CarreraParaVista_DTO dto) {
        txtIdcarreras.setText(String.valueOf(dto.getCarrera().getIdCarrera()));
        txtNumero.setText(String.valueOf(dto.getCarrera().getIdCarrera()));
        txtNombre.setText(dto.getCarrera().getNombre());

        if (dto.getCarrera().getFecha() != null) {
            dchFecha.setDate(Date.from(dto.getCarrera().getFecha().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if (dto.getCarrera().getFechaLimite() != null) {
            dchFechalimite.setDate(Date.from(dto.getCarrera().getFechaLimite().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

//        dchFecha.setDate(Date.from(dto.getCarrera().getFecha().atStartOfDay(ZoneId.systemDefault()).toInstant()));
//        dchFechalimite.setDate(Date.from(dto.getCarrera().getFechaLimite().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        spnComision.setValue(dto.getCarrera().getComision());
        atxtObservacion.setText(dto.getCarrera().getObservacion());
        chbActive.setSelected(dto.getCarrera().getFk_estados() == 1);

        if (dto.getNombreGanador() != null) {
            txtGanador.setText(dto.getNombreGanador());
            txtIdganador.setText(String.valueOf(dto.getCarrera().getFk_ganador()));
        }

        try {
            List<ApuestasParaVista_DTO> apuestasExistentes = apuestasController.listarApuestasPorCarrera(dto.getCarrera().getIdCarrera());
            for (ApuestasParaVista_DTO ap : apuestasExistentes) {
                apuestasEnTabla.add(new Apuestas_DTO(
                        ap.getApuesta().getIdApuesta(), ap.getApuesta().getFk_caballos(), ap.getNombreCaballo(),
                        ap.getApuesta().getMonto(), ap.getApuesta().getAbonado(), ap.getApuesta().getFk_apostadores(),
                        ap.getNombreApostador(), ap.getSaldoApostador(),
                        ap.getSaldoApostador(), ap.getApuesta().getAbonado()
                ));
            }
            refrescarTablaElegidos();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las apuestas de la carrera: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga o refresca la lista de apostadores disponibles.
     */
    private void actualizarTablaApostadores() {
        try {
            List<ApostadorParaVista_DTO> lista = apostadoresController.listarApostadores("", "activo");

            // Se definen los títulos de las columnas
            String[] titles = {"ID", "Apostador", "Saldo"};

            DefaultTableModel model = new DefaultTableModel(null, titles) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }

                // Se le dice a la tabla que la columna "Saldo" (índice 2) contiene números.
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 2) {
                        return Number.class;
                    }
                    return String.class;
                }
            };

            for (ApostadorParaVista_DTO dto : lista) {
                model.addRow(new Object[]{
                    dto.getApostador().getIdapostadores(),
                    dto.getApostador().getNombre(),
                    dto.getApostador().getSaldo() // Se pasa el número puro
                });
            }

            tblApostadores.setModel(model);
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            sorter.setComparator(2, (Number n1, Number n2) -> {
                return Double.compare(n1.doubleValue(), n2.doubleValue());
            });
            tblApostadores.setRowSorter(sorter);

            // --- SE APLICA EL RENDERIZADOR ---
            // Dentro del método actualizarTablaApostadores()
            tblApostadores.setDefaultRenderer(Number.class, new ApostadoresSeleccionRenderer());

            ocultarColumna(tblApostadores, 0); // Oculta la columna de ID

        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar apostadores: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga o refresca la lista de caballos disponibles.
     */
    private void actualizarTablaCaballos() {
        try {
            List<Caballos_Model> lista = caballosController.listarCaballos("", "activo");
            DefaultTableModel model = new DefaultTableModel(null, new String[]{"ID", "Caballo"}) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

            for (Caballos_Model caballo : lista) {
                model.addRow(new Object[]{
                    caballo.getIdcaballos(),
                    caballo.getCaballos()
                });
            }
            tblCaballos.setModel(model);
            tblCaballos.setRowSorter(new TableRowSorter<>(model));
            ocultarColumna(tblCaballos, 0);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar caballos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método auxiliar para ocultar una columna en una JTable.
     *
     * @param tabla La tabla a modificar.
     * @param indiceColumna El índice de la columna a ocultar.
     */
    private void ocultarColumna(JTable tabla, int indiceColumna) {
        tabla.getColumnModel().getColumn(indiceColumna).setMinWidth(0);
        tabla.getColumnModel().getColumn(indiceColumna).setMaxWidth(0);
        tabla.getColumnModel().getColumn(indiceColumna).setPreferredWidth(0);
    }

    /**
     * Configura el modelo de la tabla 'tblElegidos' con la estructura
     * simplificada.
     */
    private void configurarTablaElegidos() {
        String[] columnNames = {
            "ID Apuesta", "ID Caballo", "Caballo", "Apostado", "Abonado",
            "ID Apostador", "Apostador", "Saldo",
            "Saldo Original", "Abonado Original"
        };

        DefaultTableModel model = new DefaultTableModel(null, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3 || columnIndex == 4 || columnIndex == 7 || columnIndex == 8 || columnIndex == 9) {
                    return Integer.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Se puede editar "Apostado" (índice 3) en cualquier momento.
                if (column == 3) {
                    return true;
                }
                // Se puede editar "Abonado" (índice 4) SÓLO SI AÚN NO HAY UN GANADOR.
                if (column == 4) {
                    boolean hayGanador = !txtIdganador.getText().trim().isEmpty();
                    return !hayGanador; // Devuelve 'true' si NO hay ganador, 'false' si SÍ lo hay.
                }
                // Ninguna otra celda es editable.
                return false;
            }
        };

        tblElegidos.setModel(model);

        // 1. RENDERER: Cómo se MUESTRAN los números cuando NO se editan.
        DecimalFormat format = new DecimalFormat("#,###");
        DefaultTableCellRenderer numberRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Number) {
                    setHorizontalAlignment(RIGHT);
                    setText(format.format(value));
                }
                return c;
            }
        };

        // 2. EDITOR: Cómo se EDITAN los números.
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        JFormattedTextField editorField = new JFormattedTextField(formatter);
        editorField.setHorizontalAlignment(JTextField.RIGHT);

        // Se añade un FocusListener al campo de texto para seleccionar todo al entrar.
        editorField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                // Usamos invokeLater para asegurar que la selección ocurra después
                // de que el componente haya ganado el foco completamente.
                SwingUtilities.invokeLater(() -> {
                    JTextField tf = (JTextField) e.getSource();
                    tf.selectAll();
                });
            }
        });

        // Se crea el editor de celda con nuestro campo de texto ya configurado.
        DefaultCellEditor numberEditor = new DefaultCellEditor(editorField);

        // 3. Aplicar el renderer y el editor a las columnas de montos.
        tblElegidos.getColumnModel().getColumn(3).setCellRenderer(numberRenderer);
        tblElegidos.getColumnModel().getColumn(3).setCellEditor(numberEditor);
        tblElegidos.getColumnModel().getColumn(4).setCellRenderer(numberRenderer);
        tblElegidos.getColumnModel().getColumn(4).setCellEditor(numberEditor);

        // Ocultar columnas internas que no son para el usuario
        ocultarColumna(tblElegidos, 0); // ID Apuesta
        ocultarColumna(tblElegidos, 1); // ID Caballo
        ocultarColumna(tblElegidos, 5); // ID Apostador
        ocultarColumna(tblElegidos, 7); // Saldo (AHORA OCULTO)
        ocultarColumna(tblElegidos, 8); // Saldo Original
        ocultarColumna(tblElegidos, 9); // Abonado Original
    }

    /**
     * Sincroniza la lista en memoria 'apuestasEnTabla' con los datos que el
     * usuario pudo haber editado directamente en la JTable.
     */
    private void actualizarDTOsDesdeTabla() {
        DefaultTableModel model = (DefaultTableModel) tblElegidos.getModel();

        // Es crucial detener cualquier edición activa en la tabla antes de leer los valores.
        if (tblElegidos.isEditing()) {
            tblElegidos.getCellEditor().stopCellEditing();
        }

        for (int i = 0; i < apuestasEnTabla.size(); i++) {
            Apuestas_DTO dto = apuestasEnTabla.get(i);
            try {
                // Se leen los valores de las celdas editables (índices visuales)
                Object apostadoObj = model.getValueAt(i, 3); // Columna "Apostado"
                Object abonadoObj = model.getValueAt(i, 4);  // Columna "Abonado"

                // Se convierten los valores a Integer de forma segura, manejando nulos.
                int montoApostado = 0;
                if (apostadoObj != null && !apostadoObj.toString().isEmpty()) {
                    montoApostado = Integer.parseInt(apostadoObj.toString().replace(".", ""));
                }

                int montoAbonado = 0;
                if (abonadoObj != null && !abonadoObj.toString().isEmpty()) {
                    montoAbonado = Integer.parseInt(abonadoObj.toString().replace(".", ""));
                }

                // Se actualiza el DTO en la lista en memoria
                dto.setMontoApostado(montoApostado);
                dto.setMontoAbonado(montoAbonado);

            } catch (Exception e) {
                System.err.println("Error al leer datos editados de la tabla en la fila " + i + ": " + e.getMessage());
                // Opcional: mostrar un JOptionPane de error.
            }
        }
    }

    /**
     * Crea el JPopupMenu con las acciones para la fila seleccionada de la tabla
     * de elegidos.
     */
    private JPopupMenu crearPopupMenuParaElegidos(int filaVista) {
        JPopupMenu popupMenu = new JPopupMenu();
        int filaModelo = tblElegidos.convertRowIndexToModel(filaVista);

        // --- Acción de Eliminar Apuesta ---
        JMenuItem deleteItem = new JMenuItem("Eliminar Apuesta");
        deleteItem.addActionListener(e -> {
            // La acción ahora elimina el DTO de la lista en memoria
            apuestasEnTabla.remove(filaModelo);
            // Y luego refresca la tabla para que el cambio sea visible
            refrescarTablaElegidos();
        });
        popupMenu.add(deleteItem);

        // --- Acción de Agregar Saldo ---
        JMenuItem addSaldoItem = new JMenuItem("Agregar/Retirar Saldo");
        addSaldoItem.addActionListener(e -> {
            // Se obtiene el DTO de la lista en memoria
            Apuestas_DTO dtoSeleccionado = apuestasEnTabla.get(filaModelo);

            // Se abre el diálogo AgregarSaldo de la forma correcta
            AgregarSaldo dialogo = new AgregarSaldo(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    true,
                    dtoSeleccionado.getIdApostador(),
                    dtoSeleccionado.getNombreApostador()
            );
            dialogo.setVisible(true);

            // Si se guardó algo en el diálogo, se actualizan las tablas
            if (dialogo.isGuardadoExitoso()) {
                actualizarTablaApostadores();
                // Lógica para refrescar el saldo en la tabla de elegidos
                refrescarSaldosEnTablaElegidos();
            }
        });
        popupMenu.add(addSaldoItem);

        return popupMenu;
    }

    /**
     * Helper para refrescar los datos de la UI después de que se modifica un
     * saldo.
     */
    private void refrescarSaldosEnTablaElegidos() {
        try {
            // Se obtienen los datos actualizados de todos los apostadores
            List<ApostadorParaVista_DTO> apostadoresActualizados = apostadoresController.listarApostadores("", "activo");

            // Se actualiza el saldo en nuestra lista en memoria
            for (Apuestas_DTO dto : apuestasEnTabla) {
                for (ApostadorParaVista_DTO apostador : apostadoresActualizados) {
                    if (dto.getIdApostador().equals(apostador.getApostador().getIdapostadores())) {
                        dto.setSaldo(apostador.getApostador().getSaldo());
                        dto.setSaldoOriginal(apostador.getApostador().getSaldo()); // También el original
                        break;
                    }
                }
            }
            // Se redibuja la tabla con los saldos actualizados
            refrescarTablaElegidos();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, "Error al refrescar saldos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Prepara el formulario para una nueva inserción, obteniendo el siguiente
     * ID disponible para sugerir un nombre de carrera.
     */
    private void prepararSiguienteCarrera() {
        System.out.println("8. Preparando formulario para la siguiente carrera...");
        try {
            // Obtenemos el próximo ID desde el controlador
            int siguienteId = carrerasController.obtenerSiguienteIdCarrera();

            // Construimos el nombre sugerido
            String siguienteNombre = "Carrera Nro " + siguienteId;

            // Establecemos el nombre en el campo de texto.
            // IMPORTANTE: NO establecemos el ID en txtIdcarreras,
            // porque debe permanecer vacío para que la lógica de "CREACIÓN" funcione.
            txtNombre.setText(siguienteNombre);

            // Opcional: Ponemos el foco en el primer campo editable, por ej. la fecha.
            dchFecha.requestFocusInWindow();

            System.out.println("   - Siguiente nombre sugerido: " + siguienteNombre);

        } catch (ServiceException e) {
            // Si hay un error, simplemente lo mostramos en consola y dejamos el campo en blanco.
            System.err.println("Error al pre-cargar el siguiente nombre de carrera: " + e.getMessage());
            txtNombre.setText(""); // Dejar en blanco como fallback
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblCaballos = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblElegidos = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblApostadores = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        dchFecha = new com.toedter.calendar.JDateChooser();
        jLabel15 = new javax.swing.JLabel();
        dchFechalimite = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        atxtObservacion = new javax.swing.JTextArea();
        chbActive = new javax.swing.JCheckBox();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        txtIdcarreras = new javax.swing.JTextField();
        txtIdganador = new javax.swing.JTextField();
        btnSeleccionarGanador = new javax.swing.JButton();
        txtGanador = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnImprimir = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();
        spnComision = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Juegos");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tblCaballos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblCaballos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Caballos"
            }
        ));
        tblCaballos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCaballosMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblCaballosMousePressed(evt);
            }
        });
        jScrollPane4.setViewportView(tblCaballos);

        tblElegidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Elegidos"
            }
        ));
        tblElegidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblElegidosMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblElegidosMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblElegidosMouseReleased(evt);
            }
        });
        tblElegidos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblElegidosKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(tblElegidos);

        tblApostadores = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblApostadores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Apostadores"
            }
        ));
        tblApostadores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblApostadoresMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblApostadoresMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblApostadores);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Nombre:");

        txtNombre.setText("Juego N° 20");
        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });

        jLabel2.setText("Fecha:");

        dchFecha.setDateFormatString("dd/MM/yyyy");

        jLabel15.setText("Fecha Limite:");

        dchFechalimite.setDateFormatString("dd/MM/yyyy");

        jLabel7.setText("Observación:");

        atxtObservacion.setColumns(20);
        atxtObservacion.setRows(5);
        atxtObservacion.setText("Sin Observacion");
        atxtObservacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                atxtObservacionKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(atxtObservacion);

        chbActive.setText("Activo");
        chbActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbActiveActionPerformed(evt);
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

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Número:");

        txtNumero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumeroActionPerformed(evt);
            }
        });

        txtIdcarreras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdcarrerasActionPerformed(evt);
            }
        });

        btnSeleccionarGanador.setText("Seleccionar");
        btnSeleccionarGanador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarGanadorActionPerformed(evt);
            }
        });

        jLabel6.setText("Ganador:");

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

        SpinnerNumberModel modeloComision = new SpinnerNumberModel(10, 0, 100, 1);
        spnComision = new JSpinner(modeloComision);

        jLabel16.setText("Porcent. de Comisión:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtIdcarreras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtGanador, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSeleccionarGanador))
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 178, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dchFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel15)
                                            .addComponent(jLabel16))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(dchFechalimite, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                                            .addComponent(spnComision)))))
                            .addComponent(txtIdganador, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(146, 146, 146)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chbActive)
                        .addGap(26, 26, 26)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dchFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3)
                                .addComponent(txtIdcarreras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dchFechalimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1))
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSeleccionarGanador)
                            .addComponent(txtIdganador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtGanador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(spnComision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel16)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancel)
                        .addComponent(btnSave)
                        .addComponent(chbActive))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnImprimir)
                        .addComponent(btnExcel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void tblApostadoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblApostadoresMouseClicked
//        if (evt.getClickCount() == 2) {
//            int fila = tblApostadores.getSelectedRow();
//            if (fila == -1) {
//                return;
//            }
//
//            int idApostador = (int) tblApostadores.getValueAt(fila, 0);
//            String nombreApostador = (String) tblApostadores.getValueAt(fila, 1);
//            int saldo = (int) tblApostadores.getValueAt(fila, 2);
//            agregarApostadorAElegidos(idApostador, nombreApostador, saldo);
//        }
    }//GEN-LAST:event_tblApostadoresMouseClicked

    private void tblCaballosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCaballosMouseClicked
//        if (evt.getClickCount() == 2) { // Doble clic para añadir
//            int fila = tblCaballos.getSelectedRow();
//            if (fila == -1) {
//                return;
//            }
//
//            // Se obtienen los datos del caballo de la tabla de origen
//            int idCaballo = (int) tblCaballos.getValueAt(fila, 0);
//            String nombreCaballo = (String) tblCaballos.getValueAt(fila, 1);
//
//            // Se llama al nuevo método de ayuda
//            agregarCaballoAElegidos(idCaballo, nombreCaballo);
//        }
    }//GEN-LAST:event_tblCaballosMouseClicked

    private void tblElegidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElegidosMouseClicked
        // Se verifica si es un clic derecho para mostrar el menú
        if (SwingUtilities.isRightMouseButton(evt)) {
            int filaVista = tblElegidos.rowAtPoint(evt.getPoint());
            if (filaVista != -1) {
                tblElegidos.setRowSelectionInterval(filaVista, filaVista);

                // Se crea y muestra el menú contextual
                JPopupMenu popupMenu = crearPopupMenuParaElegidos(filaVista);
                popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }//GEN-LAST:event_tblElegidosMouseClicked

    private void tblElegidosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElegidosMouseReleased
        if (SwingUtilities.isRightMouseButton(evt)) {
            tblElegidos.clearSelection();
        }
    }//GEN-LAST:event_tblElegidosMouseReleased

    private void tblElegidosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElegidosMousePressed
        if (SwingUtilities.isRightMouseButton(evt)) {
            tblElegidos.clearSelection();
        }
    }//GEN-LAST:event_tblElegidosMousePressed

    private void tblCaballosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCaballosMousePressed
        if (SwingUtilities.isRightMouseButton(evt)) {
            tblCaballos.clearSelection();
        }
    }//GEN-LAST:event_tblCaballosMousePressed

    private void tblApostadoresMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblApostadoresMouseReleased
        if (SwingUtilities.isRightMouseButton(evt)) {
            tblApostadores.clearSelection();
        }
    }//GEN-LAST:event_tblApostadoresMouseReleased

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

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        System.out.println("--- INICIO: btnSaveActionPerformed ---");

        if (dchFecha.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una Fecha para la carrera.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            return; // Detiene la ejecución
        }
        if (dchFechalimite.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una Fecha Límite para la carrera.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            return; // Detiene la ejecución
        }

        actualizarDTOsDesdeTabla();
        System.out.println("1. DTOs actualizados desde la tabla. Total de apuestas: " + apuestasEnTabla.size());

        // 2. VALIDAR que todas las apuestas estén completas.
        if (!validarApuestasCompletas()) {
            return; // Si la validación falla, detenemos el proceso.
        }

        var nombreCarrera = txtNombre.getText();
        var fecha = dchFecha.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        var fechaLimite = dchFechalimite.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        var comisionValor = (int) spnComision.getValue();
        var observacion = atxtObservacion.getText();
        var idGanador = txtIdganador.getText().isEmpty() ? null : Integer.parseInt(txtIdganador.getText());
        var esActivo = chbActive.isSelected();

        System.out.println("2. Datos de la cabecera recogidos:");
        System.out.println("   - Nombre: " + nombreCarrera);
        System.out.println("   - Fecha: " + fecha);
        System.out.println("   - Ganador ID: " + idGanador);
        System.out.println("   - Apuestas en lista: " + apuestasEnTabla.size());

        try {
            if (txtIdcarreras.getText().trim().isEmpty()) {
                System.out.println("3. Detectado modo CREACIÓN. Llamando al controlador...");
                carrerasController.crearCarreraConApuestas(nombreCarrera, fecha, fechaLimite, comisionValor, observacion, esActivo, idGanador, apuestasEnTabla);
                JOptionPane.showMessageDialog(this, "Carrera y apuestas guardadas exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Lógica de Actualización
                System.out.println("3. Detectado modo ACTUALIZACIÓN. Llamando al controlador...");
                var idCarrera = Integer.parseInt(txtIdcarreras.getText());
                carrerasController.actualizarCarreraConApuestas(idCarrera, nombreCarrera, fecha, fechaLimite, comisionValor, observacion, esActivo, idGanador, apuestasEnTabla);
                JOptionPane.showMessageDialog(this, "Carrera y apuestas actualizadas exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            System.out.println("7. Operación finalizada en la Vista.");
            this.seHizoUnCambio = true;
            limpiarFormulario();

            setTitle("Nuevo Juego");
            try {
                int siguienteNumero = carrerasController.getSiguienteNumeroDeCarrera();
                txtNumero.setText(String.valueOf(siguienteNumero));
                txtNombre.setText("Juego N°: " + siguienteNumero);
            } catch (ServiceException e) {
                txtNumero.setText("Error");
            }

        } catch (ServiceException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar los datos:\n" + e.getMessage(), "Error de Operación", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        System.out.println("--- FIN: btnSaveActionPerformed ---");
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void tblElegidosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblElegidosKeyReleased

    }//GEN-LAST:event_tblElegidosKeyReleased

    private void txtNumeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumeroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumeroActionPerformed

    private void txtIdcarrerasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdcarrerasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdcarrerasActionPerformed

    private void btnSeleccionarGanadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarGanadorActionPerformed
        // 1. Validar que haya apuestas en nuestra lista en memoria.
        if (apuestasEnTabla.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero debe agregar apuestas a la tabla.", "Tabla Vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Construir el mapa de caballos únicos a partir de nuestra lista de DTOs.
        //    El mapa será del tipo correcto: <ID, Nombre>
        HashMap<Integer, String> caballosParticipantes = new HashMap<>();
        for (Apuestas_DTO dto : apuestasEnTabla) {
            if (dto.getIdCaballo() != null && dto.getNombreCaballo() != null) {
                caballosParticipantes.put(dto.getIdCaballo(), dto.getNombreCaballo());
            }
        }

        if (caballosParticipantes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay caballos asignados en las apuestas para elegir un ganador.", "Faltan Datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCarrera = txtIdcarreras.getText().isEmpty() ? 0 : Integer.parseInt(txtIdcarreras.getText());
        String nombreCarrera = txtNombre.getText();

        // 3. Abrir el diálogo CargarGanador, pasándole el mapa correcto.
        CargarGanador dialogo = new CargarGanador(
                (Frame) SwingUtilities.getWindowAncestor(this),
                true,
                idCarrera,
                nombreCarrera,
                caballosParticipantes
        );
        dialogo.setVisible(true);

        // 4. Después de que el diálogo se cierra, obtener el resultado.
        Integer idGanador = dialogo.getIdCaballoSeleccionado();
        String nombreGanador = dialogo.getNombreCaballoSeleccionado();

        // 5. Si el usuario seleccionó un ganador (no canceló), actualizar los campos de texto.
        if (idGanador != null && nombreGanador != null) {
            txtIdganador.setText(String.valueOf(idGanador));
            txtGanador.setText(nombreGanador);
        }
    }//GEN-LAST:event_btnSeleccionarGanadorActionPerformed

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

            String ruta = AppPaths.REPORTS_DIR + File.separator + "Apuestas.jrxml";
            File jrxmlFile = new File(ruta);
            InputStream is = new FileInputStream(jrxmlFile);
            if (!jrxmlFile.exists()) {
                throw new FileNotFoundException("No se encontró el .jrxml en: " + ruta);
            }

//*****************************Inicio del Calculo del monto a entregar al ganador*****************************
            List<Object[]> apuestas = new ArrayList<>();
            String txtIdG = txtIdganador.getText();
            String resultadoGanador = "";
//            if (txtGanador.getText().length() != 0) {
//                resultadoGanador = calcularMontoGanadorYNombreApostadorDesdeUI(apuestas, txtIdG);
//            } else {
//                resultadoGanador = "Sin ganador";
//            }

            //TODO: Terminar el calculo del monto para el ganador en el reporte
//*****************************Fin del Calculo del monto a entregar al ganador*****************************
            Date fecha = dchFecha.getDate();
            Date fechalimite = dchFechalimite.getDate();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fechaStr = fecha == null ? "" : sdf.format(fecha);
            String fechalimiteStr = fechalimite == null ? "" : sdf.format(fechalimite);

            System.out.println(">>> fechaStr       = " + fechaStr);
            System.out.println(">>> fechalimiteStr = " + fechalimiteStr);

            DefaultTableModel original = (DefaultTableModel) tblElegidos.getModel();
            int[] colsNoPermitidas = {1, 5, 7, 8, 9, 10, 11};
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
                    int colIndex = colsPermitidas.get(i);
                    Object valor = original.getValueAt(row, colIndex);

                    String strVal = (valor == null)
                            ? ""
                            : valor.toString().trim();

                    if (colIndex == 0) {
                        fila[i] = (strVal.isEmpty() || strVal.equalsIgnoreCase("null"))
                                ? ""
                                : strVal;

                    } else if (colIndex == 3 || colIndex == 4) {
                        if (strVal.isEmpty()) {
                            fila[i] = "";
                        } else {
                            try {
                                int num = Integer.parseInt(strVal.replace(".", ""));
                                fila[i] = formateador14.format(num);
                            } catch (NumberFormatException ex) {
                                fila[i] = "";
                            }
                        }
                    } else {
                        fila[i] = strVal;
                    }
                }
                filtrado.addRow(fila);
            }

            JRTableModelDataSource datasource = new JRTableModelDataSource(filtrado);
            JasperReport jr = JasperCompileManager.compileReport(is);

            String ganador = "";
            if (txtGanador.getText().length() != 0) {
                ganador = txtGanador.getText();
            } else {
                ganador = "Sin ganador";
            }

            String estado = chbActive.isSelected() ? "Activo" : "Inactivo";
            System.out.println("estado: " + estado);

            Map<String, Object> params = new HashMap<>();
            params.put("Logo", rutaLogo);
            params.put("Estados", estado);
            params.put("Currentdate", currentdate);
            params.put("Juego", txtNombre.getText());
            params.put("Ganador", ganador);
            params.put("Fecha", fechaStr);
            params.put("Fechalimite", fechalimiteStr);
            params.put("Observacion", atxtObservacion.getText());
//            params.put("Resultadoganador", resultadoGanador);

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

            JDialog dialog = new JDialog((Frame) null, "Apuestas", true);
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

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        DefaultTableModel model = (DefaultTableModel) tblElegidos.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "La tabla está vacía. No se puede exportar un Excel sin datos.",
                    "Tabla vacía",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaStr = sdf.format(dchFecha.getDate());
        String fechalimiteStr = sdf.format(dchFechalimite.getDate());

        List<Object[]> resumen = List.of(
                new Object[]{"Juego N°: ", txtNumero.getText()},
                new Object[]{"Ganador: ", txtGanador.getText()},
                new Object[]{"Fecha: ", fechaStr},
                new Object[]{"Fecha limite: ", fechalimiteStr},
                new Object[]{"Observacion: ", atxtObservacion},
                new Object[]{" ", ""}
        );

        Set<Integer> columnsToSkip = Set.of(1, 5, 7, 8, 9, 10, 11);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Excel");
        chooser.setApproveButtonText("Guardar");
        // Filtro opcional para que solo muestre .xlsx
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        chooser.setSelectedFile(new File("Apuestas.xlsx"));

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
            Export_Excel.export(tblElegidos,
                    "Apuestas",
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
            java.util.logging.Logger.getLogger(NewApuestas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewApuestas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewApuestas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewApuestas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewApuestas dialog = new NewApuestas(new javax.swing.JFrame(), true);
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
        // 1. Validar que la lista en memoria no esté vacía.
        if (apuestasEnTabla.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe haber al menos una apuesta en la tabla.", "Datos Incompletos", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 2. Validar fechas.
        Date fechaDate = dchFecha.getDate();
        Date fechaLimiteDate = dchFechalimite.getDate();
        if (fechaDate == null || fechaLimiteDate == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha y una fecha límite.", "Error de Fechas", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (fechaLimiteDate.before(fechaDate)) {
            JOptionPane.showMessageDialog(this, "La fecha límite no puede ser anterior a la fecha de la carrera.", "Error de Fechas", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 3. Validar que cada apuesta en la lista tenga los datos mínimos.
        for (int i = 0; i < apuestasEnTabla.size(); i++) {
            Apuestas_DTO dto = apuestasEnTabla.get(i);
            if (dto.getIdCaballo() == null || dto.getIdApostador() == null) {
                JOptionPane.showMessageDialog(this, "La fila " + (i + 1) + " debe tener un caballo y un apostador asignado.", "Error de Datos", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true; // Si todas las validaciones pasan.
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea atxtObservacion;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSeleccionarGanador;
    private javax.swing.JCheckBox chbActive;
    private com.toedter.calendar.JDateChooser dchFecha;
    private com.toedter.calendar.JDateChooser dchFechalimite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner spnComision;
    private javax.swing.JTable tblApostadores;
    private javax.swing.JTable tblCaballos;
    private javax.swing.JTable tblElegidos;
    public static javax.swing.JTextField txtGanador;
    private javax.swing.JTextField txtIdcarreras;
    public static javax.swing.JTextField txtIdganador;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNumero;
    // End of variables declaration//GEN-END:variables
//
//    private void save(int idcarrera, String nombrecarrera, String lugar, String fecha, String fechalimite, Integer idganador, String observacion, List<Object[]> apuestas, String fechaactual, int comisionn) {
//        finalState = "activo";
//        idestado = State_Controller.getEstadoId(finalState, Run.model);
//
//        carrerascontroller.createCarreras(nombrecarrera, lugar, fecha, fechalimite, idganador, observacion, idestado, comisionn);
//        System.out.println("Pasó carrerascontroller.create en save");
//
//        // Obtenemos el ID de la carrera recién creada
//        idcarrera = carrerascontroller.getMaxCodigo() - 1;
//        System.out.println("ID de la carrera creada: " + idcarrera);
//
//        try {
//            for (Object[] datos : apuestas) {
//                // --- 1. Lectura de datos de la apuesta ---
//                int idCaballo = Integer.parseInt(datos[1].toString());
//                int apostado = Integer.parseInt(datos[3].toString());
//                int abonado = Integer.parseInt(datos[4].toString());
//                int idApostador = Integer.parseInt(datos[5].toString());
//                boolean origenpago = (boolean) datos[8];
//                String origen = origenpago ? "Si" : "No";
//
//                detallecontroller.createDetallecarreras(idcarrera, idCaballo);
//
//                // --- 2. Lógica de Victoria (CORRECCIÓN PRINCIPAL) ---
//                // Se calcula una sola vez y de forma segura
//                boolean tieneGanador = (idganador != null);
//                boolean apostadorGano = false;
//                if (tieneGanador) {
//                    apostadorGano = apuestas.stream().anyMatch(apuesta
//                            -> Integer.parseInt(apuesta[5].toString()) == idApostador
//                            && Integer.parseInt(apuesta[1].toString()) == idganador.intValue()
//                    );
//                }
//
//                // --- 3. Creación de la Apuesta y Movimientos ---
//                int fkEstadoPago = (apostado > abonado) ? 2 : 3; // 2: parcial, 3: pagada
//
//                apuestascontroller.createApuestas("Apuesta", apostado, fecha, fechalimite, "Sin observacion", origen, idcarrera, idCaballo, idApostador, fkEstadoPago, idestado, abonado);
//                int idApuesta = apuestascontroller.getMaxid();
//
//                if (abonado > 0 && !apostadorGano) {
//                    int tipoMovimiento = (apostado > abonado) ? 3 : 4; // 3: parcial, 4: completa
//                    String descMovimiento = (tipoMovimiento == 3) ? "Apuesta parcial realizada" : "Apuesta total realizada";
//                    movimientoscontroller.create(fechaactual, abonado, descMovimiento, idApostador, idApuesta, tipoMovimiento, idcarrera);
//                } else if (apostadorGano) {
//                    System.out.println("❗ Apuesta perdedora perdonada para apostador ID " + idApostador);
//                }
//            }
//        } catch (HeadlessException | NumberFormatException ex) {
//            JOptionPane.showMessageDialog(null, "Error procesando las apuestas: " + ex.getMessage());
//            ex.printStackTrace();
//        }
//
//        // --- 4. Reparto de Ganancias (si hay ganador) ---
//        if (idganador != null) {
//            System.out.println("🟢 Iniciando reparto de ganancias para carrera ID " + idcarrera);
//
//            Set<Integer> ganadores = apuestas.stream()
//                    .filter(f -> Integer.parseInt(f[1].toString()) == idganador)
//                    .map(f -> Integer.parseInt(f[5].toString()))
//                    .collect(Collectors.toSet());
//
//            int totalStakesPerdidos = apuestas.stream()
//                    .filter(f -> !ganadores.contains(Integer.parseInt(f[5].toString())))
//                    .mapToInt(f -> Integer.parseInt(f[3].toString()))
//                    .sum();
//
//            int comisionValor = (int) Math.round(totalStakesPerdidos * (comisionn / 100.0));
//            int gananciaNeta = totalStakesPerdidos - comisionValor;
//
//            System.out.println("Totales -> Stakes perdidos: " + totalStakesPerdidos + ", Comisión: " + comisionValor + ", Ganancia neta a repartir: " + gananciaNeta);
//
//            Map<Integer, Integer> aportesAlGanador = new HashMap<>();
//            apuestas.stream()
//                    .filter(f -> Integer.parseInt(f[1].toString()) == idganador)
//                    .forEach(f -> aportesAlGanador.merge(Integer.parseInt(f[5].toString()), Integer.parseInt(f[3].toString()), Integer::sum));
//
//            int totalApostadoAlGanador = aportesAlGanador.values().stream().mapToInt(Integer::intValue).sum();
//
//            if (totalApostadoAlGanador > 0) {
//                for (var entry : aportesAlGanador.entrySet()) {
//                    int idApostador = entry.getKey();
//                    int montoApostado = entry.getValue();
//                    int ganancia = (int) Math.round(gananciaNeta * (montoApostado / (double) totalApostadoAlGanador));
//
//                    apostadorescontroller.addSaldo(ganancia, idApostador);
//                    movimientoscontroller.create(fechaactual, ganancia, "Ganancia neta (solo lo ganado de otros)", idApostador, null, 5, idcarrera);
//                }
//            }
//
//            descontarPendientesPerdedores(apuestas, idganador, fechaactual, idcarrera);
//            System.out.println("✅ Reparto de ganancias completado.");
//        }
//
//        limpiar();
//
//        showApostadores(
//                "", "activo");
//        String datefrom = Principal.txtDesde.getText();
//        String dateto = Principal.txtHasta.getText();
//
//        if (Principal.rbtnFinalizado.isSelected()
//                == true) {
//            String state = "";
//            switch (cmbEstado.getSelectedIndex()) {
//                case 0 ->
//                    state = "Todos";
//                case 1 ->
//                    state = "activo";
//                case 2 ->
//                    state = "inactivo";
//                default -> {
//                }
//            }
//            Principal.instancia.showCarrerasInPrincipal("", state, "Finalizado", datefrom, dateto);
//        }
//
//        if (Principal.rbtnPendiente.isSelected()
//                == true) {
//            String state = "";
//            switch (cmbEstado.getSelectedIndex()) {
//                case 0 ->
//                    state = "Todos";
//                case 1 ->
//                    state = "activo";
//                case 2 ->
//                    state = "inactivo";
//                default -> {
//                }
//            }
//            Principal.instancia.showCarrerasInPrincipal("", state, "Pendiente", datefrom, dateto);
//        }
//
//        if (Principal.rbtnTodos.isSelected()
//                == true) {
//            String state = "";
//            switch (cmbEstado.getSelectedIndex()) {
//                case 0 ->
//                    state = "Todos";
//                case 1 ->
//                    state = "activo";
//                case 2 ->
//                    state = "inactivo";
//                default -> {
//                }
//            }
//            Principal.instancia.showCarrerasInPrincipal("", state, "Todos", datefrom, dateto);
//        }
//
//        txtNumero.setText(String.valueOf(carrerascontroller.getMaxCodigo()));
//        txtNombre.setText("Juego N°: " + String.valueOf(carrerascontroller.getMaxCodigo()));
//    }
//
//    private void update(
//            int idcarrera,
//            String nombrecarrera,
//            String lugar,
//            String fecha,
//            Integer idganador,
//            String fechalimite,
//            String observacion,
//            List<Object[]> apuestas,
//            String fechaactual,
//            int comisionn
//    ) {
//        System.out.println("=== ENTER update para carrera " + idcarrera
//                + ", ganador=" + idganador + " ===");
//        System.out.println("Parámetros de entrada:");
//        System.out.println("  nombrecarrera=" + nombrecarrera);
//        System.out.println("  lugar=" + lugar);
//        System.out.println("  fecha=" + fecha + ", fechalimite=" + fechalimite);
//        System.out.println("  observacion=" + observacion + ", comision=" + comisionn + "%");
//        System.out.println("  Número de apuestas: " + apuestas.size());
//        System.out.println("---------------------------------------------");
//        // 1) Gestión del cambio de estado (activo/inactivo)
//        if (initialState.equals("activo") && finalState.equals("inactivo")) {
//            if (txtIdcarreras.getText().isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Seleccione un juego para desactivar.",
//                        "Advertencia!", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            int resp = JOptionPane.showOptionDialog(this,
//                    "El juego será desactivado",
//                    "Desactivar?",
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.QUESTION_MESSAGE,
//                    null,
//                    new String[]{"Sí", "No"},
//                    "Sí");
//            if (resp == JOptionPane.YES_OPTION) {
//                idestado = State_Controller.getEstadoId(finalState, Run.model);
//            } else {
//                return;
//            }
//        } else if (initialState.equals("inactivo") && finalState.equals("activo")) {
//            if (txtIdcarreras.getText().isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Seleccione un juego para activar.",
//                        "Advertencia!", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            int resp = JOptionPane.showOptionDialog(this,
//                    "El juego será activado",
//                    "Activar?",
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.QUESTION_MESSAGE,
//                    null,
//                    new String[]{"Sí", "No"},
//                    "Sí");
//            if (resp == JOptionPane.YES_OPTION) {
//                idestado = State_Controller.getEstadoId(finalState, Run.model);
//            } else {
//                return;
//            }
//        } else {
//            idestado = State_Controller.getEstadoId(initialState, Run.model);
//        }
//
//        try {
//            // 2) Actualizo la carrera y limpio detalles previos
//            carrerascontroller.updateCarreras(idcarrera, nombrecarrera, lugar, fecha, fechalimite, idganador, observacion, idestado, comisionn);
//            detallecontroller.deleteDetallecarreras(idcarrera);
////            int idApostador = Integer.parseInt(datos[5].toString());
//
//            final boolean tieneGanador = (idganador != null);
//
//            // 3) Creo o actualizo cada apuesta
//            System.out.println("-- 3) Creación/actualización de apuestas --");
//            // -- 3) Creación/actualización de apuestas --
//            for (Object[] datos : apuestas) {
//                String idApuestaStr = datos[0] == null ? "" : datos[0].toString();
//                int idCaballo = Integer.parseInt(datos[1].toString());
//                int montoApostado = Integer.parseInt(datos[3].toString());
//                int nuevoTotalAbonado = Integer.parseInt(datos[4].toString());
//                int idApostador = Integer.parseInt(datos[5].toString());
//                boolean origenpago = (boolean) datos[8];
//                String origen = origenpago ? "Si" : "No";
//
//                detallecontroller.createDetallecarreras(idcarrera, idCaballo);
//
//                int idApuesta;
//                if (idApuestaStr.isEmpty()) {
//                    apuestascontroller.createApuestas("Apuesta", montoApostado, fecha, fechalimite, "Sin observacion", origen, idcarrera, idCaballo, idApostador, 2, idestado, nuevoTotalAbonado);
//                    idApuesta = apuestascontroller.getMaxid(); // Asume que esto te da el último ID
//                    System.out.println("  → Apuesta NUEVA creada, ID asignado: " + idApuesta);
//                } else {
//                    idApuesta = Integer.parseInt(idApuestaStr);
//                    apuestascontroller.updateApuestas(idApuesta, "Apuesta", montoApostado, fecha, fechalimite, "Sin observacion", origen, idcarrera, idCaballo, idApostador, 2, idestado, nuevoTotalAbonado);
//                    System.out.println("  → Apuesta ACTUALIZADA id=" + idApuesta);
//                }
//
//                // Generar movimiento si se abonó algo nuevo
//                int abonadoOriginalDB = apuestascontroller.getAbonado(idApuesta);
//                int diff = nuevoTotalAbonado - abonadoOriginalDB;
//                if (diff > 0) {
//                    int tipoMov = (nuevoTotalAbonado < montoApostado) ? 3 : 4;
//                    String desc = (tipoMov == 3) ? "Abono de apuesta (parcial)" : "Abono de apuesta (total)";
//                    movimientoscontroller.create(fechaactual, diff, desc, idApostador, idApuesta, tipoMov, idcarrera);
//                    apostadorescontroller.addSaldo(-diff, idApostador);
//                    if (tipoMov == 4) {
//                        apuestascontroller.updateEstadoPago(idApuesta, 3); // 3: pagada
//                    }
//                }
//            }
//
//            // 4) Si hay ganador, elimino reparto anterior (si existe) y recalculo
//            if (tieneGanador) {
//                System.out.println("\n>>> Iniciando recálculo de ganancias para ganador id=" + idganador);
//
//                // Revertir movimientos de reparto anteriores (si los hubo)
//                revertirRepartoAnterior(idcarrera);
//
//                // Calcular y aplicar nuevo reparto
//                calcularYRepartirGanancias(idcarrera, idganador, apuestas, fechaactual, comisionn);
//            }
//
//            System.out.println("=== FIN reparto para carrera " + idcarrera + " ===");
//
//            // 5) Refresco interfaz y listo…
//            limpiar();
//            showApostadores("", "activo");
//            String datefrom = Principal.txtDesde.getText();
//            String dateto = Principal.txtHasta.getText();
//            String filtroEstado = switch (cmbEstado.getSelectedIndex()) {
//                case 1 ->
//                    "activo";
//                case 2 ->
//                    "inactivo";
//                default ->
//                    "Todos";
//            };
//            String status = Principal.rbtnFinalizado.isSelected() ? "Finalizado"
//                    : Principal.rbtnPendiente.isSelected() ? "Pendiente"
//                    : /*else*/ "Todos";
//            Principal.instancia.showCarrerasInPrincipal("", filtroEstado, status, datefrom, dateto);
//
//            int maxCod = carrerascontroller.getMaxCodigo();
//            txtNumero.setText(String.valueOf(maxCod));
//            txtNombre.setText("Juego N°: " + maxCod);
//
//        } catch (NumberFormatException | SQLException e) {
//            JOptionPane.showMessageDialog(
//                    this,
//                    "Error al actualizar: " + e.getMessage(),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE
//            );
//            e.printStackTrace();
//        }
//    }
}
