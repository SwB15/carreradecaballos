package View;

import Config.AppPaths;
import Config.Export_Excel;
import Config.FormattedCellEditor;
import Config.Run;
import Controller.Abonos_Controller;
import Controller.Apostadores_Controller;
import Controller.Apuestas_Controller;
import Controller.Caballos_Controller;
import Controller.Carreras_Controller;
import Controller.Detallecarreras_Controller;
import Controller.Movimientos_Controller;
import Controller.State_Controller;
import Model.ApuestasData;
import Model.Movimientos_Model;
import static View.Principal.cmbEstado;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
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
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
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

    Carreras_Controller carrerascontroller = new Carreras_Controller();
    Detallecarreras_Controller detallecontroller = new Detallecarreras_Controller();
    Apostadores_Controller apostadorescontroller = new Apostadores_Controller();
    Movimientos_Controller movimientoscontroller = new Movimientos_Controller();
    Caballos_Controller caballoscontroller = new Caballos_Controller();
    Apuestas_Controller apuestascontroller = new Apuestas_Controller();
    Abonos_Controller abonoscontroller = new Abonos_Controller();

    private static final Logger logger = Logger.getLogger(PerfilApostador.class.getName());
    private int cantidadFilas = 0;
    private Object[] apostadores = new Object[2];
    private Object[] caballos = new Object[2];
    List<Object[]> apuestas = new ArrayList<>();
    private String initialState = "", finalState = "", stateFilter = "todos";
    private int idestado;
    Date startDate = null, endDate = null;
    DecimalFormat formateador14 = new DecimalFormat("#,###.###");
    LocalDate localDate = LocalDate.now();
    Date currentDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    public NewApuestas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        tblApostadores.setName("tblApostadores");
        chbActive.setSelected(true);
        tblCaballos.setName("tblCaballos");
        tblElegidos.setName("tblElegidos");
        dchFecha.setDate(currentDate);
        dchFechalimite.setDate(currentDate);
        txtNumero.setText(String.valueOf(carrerascontroller.getMaxCodigo()));
        txtNombre.setText("Juego N°: " + String.valueOf(carrerascontroller.getMaxCodigo()));
        txtGanador.setEditable(false);
        txtGanador.setBackground(Color.white);
        txtIdganador.setVisible(false);
        txtIdcarreras.setVisible(false);
        atxtObservacion.requestFocus();
        tblElegidos();
        showApostadores("", "activo");
        showCaballos("", "activo");
        btnActualizarsaldooculto.setVisible(false);
    }

    private void showApostadores(String search, String stateFilter) {
        try {
            DefaultTableModel model;
            model = apostadorescontroller.showApostadores(search, stateFilter);
            tblApostadores.setModel(model);
            ocultar_columnas(tblApostadores);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void showCaballos(String search, String stateFilter) {
        try {
            DefaultTableModel model;
            model = caballoscontroller.showCaballos(search, stateFilter);
            tblCaballos.setModel(model);
            ocultar_columnas(tblCaballos);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Configuración de la tabla unificada tblElegidos con modelo personalizado
    // Declarar la bandera a nivel de clase:
    private boolean ignoreTableEvents = false;

    private void tblElegidos() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Id", "ID Caballo", "Caballos", "Apostado", "Abonado", "ID Apostador", "Apostadores", "Saldo", "Usar Saldo", "Saldo Usado?", "Saldo Original", "Abonado Original"}
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Para que las columnas numéricas se traten como Integer y la de checkbox como Boolean
                if (columnIndex == 3 || columnIndex == 4 || columnIndex == 7) {
                    return Integer.class;
                }
                if (columnIndex == 8) {
                    return Boolean.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo "Apostado", "Abonado" y "Usar Saldo" son editables
                return column == 3 || column == 4 || column == 8;
            }
        };
        tblElegidos.setModel(model);
        tblElegidos.setShowGrid(true);
        tblElegidos.setGridColor(Color.GRAY);
        tblElegidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Renderer para columnas numéricas (Apostado, Abonado y Saldo)
        DefaultTableCellRenderer numRenderer = new DefaultTableCellRenderer() {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();

            {
                symbols.setGroupingSeparator('.');
            }
            DecimalFormat formateador14 = new DecimalFormat("#,###", symbols);

            @Override
            public void setValue(Object value) {
                if (value instanceof Number) {
                    setText(formateador14.format(((Number) value).intValue()));
                } else if (value != null && !value.toString().isEmpty()) {
                    try {
                        int num = Integer.parseInt(value.toString().replace(".", ""));
                        setText(formateador14.format(num));
                    } catch (NumberFormatException ex) {
                        setText(value.toString());
                    }
                } else {
                    setText("0");
                }
            }
        };

        tblElegidos.getColumnModel().getColumn(3).setCellRenderer(numRenderer);
        tblElegidos.getColumnModel().getColumn(4).setCellRenderer(numRenderer);
        tblElegidos.getColumnModel().getColumn(7).setCellRenderer(numRenderer);

        // Asignar el editor personalizado a las columnas "Apostado" y "Abonado"
        tblElegidos.getColumnModel().getColumn(3).setCellEditor(new FormattedCellEditor());
        tblElegidos.getColumnModel().getColumn(4).setCellEditor(new FormattedCellEditor());

        // Listener para clic derecho que limpia la selección:
        tblElegidos.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (SwingUtilities.isRightMouseButton(evt)) {
                    tblElegidos.clearSelection();
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (SwingUtilities.isRightMouseButton(evt)) {
                    tblElegidos.clearSelection();
                }
            }
        });

        model.addTableModelListener(e -> {
            if (ignoreTableEvents) {
                return;
            }
            int col = e.getColumn();
            int row = e.getFirstRow();
            if (col == 8) { // Columna "Usar Saldo"
                boolean isChecked = (Boolean) model.getValueAt(row, 8);
                Object apostadoObj = model.getValueAt(row, 3);
                Object saldoObj = model.getValueAt(row, 7);
                Object saldoOriginalObj = model.getValueAt(row, 10); // Columna extra: SaldoOriginal

                int saldoOriginal = 0;
                try {
                    if (saldoOriginalObj != null && !saldoOriginalObj.toString().isEmpty()) {
                        saldoOriginal = Integer.parseInt(saldoOriginalObj.toString().replace(".", ""));
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Formato numérico inválido en saldo original.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // En lugar de hacer un cast directo, convertimos el valor a entero
                int apostado = 0;
                try {
                    if (apostadoObj == null || apostadoObj.toString().isEmpty()
                            || Integer.parseInt(apostadoObj.toString().replace(".", "")) <= 0) {
                        if (isChecked) {
                            JOptionPane.showMessageDialog(null, "Ingrese un monto válido en 'Apostado' antes de usar el saldo.", "Error", JOptionPane.ERROR_MESSAGE);
                            ignoreTableEvents = true;
                            model.setValueAt(false, row, 8);
                            ignoreTableEvents = false;
                            return;
                        }
                    } else {
                        apostado = Integer.parseInt(apostadoObj.toString().replace(".", ""));
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Formato numérico inválido en 'Apostado'.", "Error", JOptionPane.ERROR_MESSAGE);
                    ignoreTableEvents = true;
                    model.setValueAt(false, row, 8);
                    ignoreTableEvents = false;
                    return;
                }

                int saldo = 0;
                try {
                    if (saldoObj != null && !saldoObj.toString().isEmpty()) {
                        saldo = Integer.parseInt(saldoObj.toString().replace(".", ""));
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Formato numérico inválido en 'Saldo'.", "Error", JOptionPane.ERROR_MESSAGE);
                    ignoreTableEvents = true;
                    model.setValueAt(false, row, 8);
                    ignoreTableEvents = false;
                    return;
                }

                if (isChecked) {
                    // Se descuenta el monto solo si el saldo actual es igual al original
                    if (saldo == saldoOriginal) {
                        if (saldo >= apostado) {
                            ignoreTableEvents = true;
                            model.setValueAt(apostado, row, 4);       // Actualiza "Abonado"
                            model.setValueAt(saldo - apostado, row, 7); // Actualiza "Saldo"
                            ignoreTableEvents = false;
                        } else {
                            int opcion = JOptionPane.showOptionDialog(
                                    null,
                                    "Saldo insuficiente. Acción:",
                                    "Advertencia",
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    new Object[]{"Pendiente", "Recargar Saldo", "Cancelar"},
                                    "Cancelar"
                            );
                            switch (opcion) {
                                case 0 -> {
                                    ignoreTableEvents = true;
                                    tblElegidos.editCellAt(row, 4);
                                    tblElegidos.getEditorComponent().requestFocusInWindow();
                                    model.setValueAt(false, row, 8);
                                    ignoreTableEvents = false;
                                }
                                case 1 -> {
                                    // Recargar Saldo
                                    AgregarSaldo dialog = new AgregarSaldo(f, true);
                                    AgregarSaldo.txtApostadores.setText(model.getValueAt(row, 6).toString());
                                    AgregarSaldo.txtMontoactual.setText(String.valueOf(saldo));
                                    AgregarSaldo.txtIdapostadores.setText(model.getValueAt(row, 5).toString());
                                    AgregarSaldo.txtDestino.setText("Apuestas");
                                    dialog.setVisible(true);
                                    // Actualizar saldo después de recargar
                                    int idApostador = Integer.parseInt(model.getValueAt(row, 5).toString());
                                    showApostadores("", "activo");
                                    for (int i = 0; i < tblApostadores.getRowCount(); i++) {
                                        if (idApostador == Integer.parseInt(tblApostadores.getValueAt(i, 0).toString())) {
                                            String nuevoSaldo = tblApostadores.getValueAt(i, 3).toString();
                                            ignoreTableEvents = true;
                                            model.setValueAt(nuevoSaldo, row, 7);
                                            model.setValueAt(nuevoSaldo, row, 4);
                                            // Actualizar también el saldo original
                                            model.setValueAt(nuevoSaldo, row, 10);
                                            ignoreTableEvents = false;
                                        }
                                    }
                                }
                                case 2 -> {
                                    ignoreTableEvents = true;
                                    model.setValueAt(false, row, 8);
                                    ignoreTableEvents = false;
                                }
                            }
                        }
                    }
                } else {
                    // Al desmarcar, se restaura el saldo original y se pone "Abonado" a cero
                    ignoreTableEvents = true;
                    model.setValueAt(saldoOriginal, row, 7);
                    model.setValueAt(0, row, 4);
                    ignoreTableEvents = false;
                }
            }
        });
        ocultar_columnas(tblElegidos);
    }

    private void addHorseToTable(Object horseId, Object horseName) {
        DefaultTableModel model = (DefaultTableModel) tblElegidos.getModel();
        int selectedRow = tblElegidos.getSelectedRow();

        // Si hay una fila seleccionada, se actualiza esa fila.
        if (selectedRow != -1) {
            model.setValueAt(horseId, selectedRow, 1); // ID Caballo
            model.setValueAt(horseName, selectedRow, 2); // Nombre Caballo
            return;
        }

        // Si no hay fila seleccionada, se busca la primera fila sin datos de caballo.
        for (int i = 0; i < model.getRowCount(); i++) {
            Object idCaballo = model.getValueAt(i, 1);
            Object nomCaballo = model.getValueAt(i, 2);
            if ((idCaballo == null || idCaballo.toString().trim().isEmpty())
                    && (nomCaballo == null || nomCaballo.toString().trim().isEmpty())) {
                model.setValueAt(horseId, i, 1);
                model.setValueAt(horseName, i, 2);
                tblElegidos.setRowSelectionInterval(i, i);
                return;
            }
        }

        // Si no se encontró fila incompleta, se agrega una nueva.
        model.addRow(new Object[]{
            null, //getNextApuestaId(), // Id Apuesta asignado automáticamente
            horseId, // ID Caballo
            horseName, // Nombre Caballo
            "", // Apostado (editable)
            "", // Abonado (editable)
            null, // ID Apostador
            null, // Nombre Apostador
            "", // Saldo
            false // Usar Saldo
        });
    }

    private void addApostadorToTable(Object bettorId, Object bettorName, Object bettorMoney) {
        DefaultTableModel model = (DefaultTableModel) tblElegidos.getModel();
        int selectedRow = tblElegidos.getSelectedRow();

        // 1) Determinar qué saldo se debe usar
        Object finalSaldo = getLastSaldoFromTable(bettorId, bettorMoney);

        // 2) Si hay una fila seleccionada, se actualiza esa fila
        if (selectedRow != -1) {
            model.setValueAt(bettorId, selectedRow, 5);       // ID Apostador
            model.setValueAt(bettorName, selectedRow, 6);     // Nombre Apostador
            model.setValueAt(finalSaldo, selectedRow, 7);     // Saldo
            model.setValueAt(finalSaldo, selectedRow, 10);    // Saldo Original
            return;
        }

        // 3) Buscar la primera fila sin datos de apostador
        for (int i = 0; i < model.getRowCount(); i++) {
            Object idApostador = model.getValueAt(i, 5);
            Object nomApostador = model.getValueAt(i, 6);
            Object saldoApostador = model.getValueAt(i, 7);

            boolean idVacio = (idApostador == null || idApostador.toString().trim().isEmpty());
            boolean nomVacio = (nomApostador == null || nomApostador.toString().trim().isEmpty());
            boolean saldoVacio = (saldoApostador == null || saldoApostador.toString().trim().isEmpty());

            if (idVacio && nomVacio && saldoVacio) {
                model.setValueAt(bettorId, i, 5);
                model.setValueAt(bettorName, i, 6);
                model.setValueAt(finalSaldo, i, 7);
                model.setValueAt(finalSaldo, i, 10); // Saldo Original
                tblElegidos.setRowSelectionInterval(i, i);
                return;
            }
        }

        // 4) Si no se encontró una fila libre, se agrega una nueva.
        model.addRow(new Object[]{
            null, //getNextApuestaId(), // Id Apuesta
            null, // ID Caballo
            null, // Nombre Caballo
            "", // Apostado
            "", // Abonado
            bettorId, // ID Apostador
            bettorName, // Nombre Apostador
            finalSaldo, // Saldo
            false, // Usar Saldo
            null, // Saldo Usado (o cualquier otra lógica)
            finalSaldo // Saldo Original (¡IMPORTANTE!)
        });
    }

    /**
     * Busca en la tabla "tblElegidos" si ya existe el apostador con ID
     * `bettorId`. - Si no existe, retorna `bettorMoneyParam`. - Si existe, toma
     * el saldo de la última aparición en la tabla. * Si ese saldo es distinto
     * de `bettorMoneyParam`, se usa el de la tabla. * Si es igual, se usa
     * `bettorMoneyParam`.
     */
    private Object getLastSaldoFromTable(Object bettorId, Object bettorMoneyParam) {
        DefaultTableModel model = (DefaultTableModel) tblElegidos.getModel();
        // Empezamos desde la última fila hacia la primera
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            Object existingId = model.getValueAt(i, 5); // Columna ID Apostador
            if (existingId != null && existingId.toString().equals(bettorId.toString())) {
                // Apostador encontrado
                Object existingSaldo = model.getValueAt(i, 7); // Columna Saldo
                if (existingSaldo != null && !existingSaldo.toString().isEmpty()) {
                    // Si el saldo en la tabla es distinto al que llega por parámetro,
                    // asumimos que el usuario ya lo cambió en alguna apuesta previa
                    if (!existingSaldo.toString().equals(bettorMoneyParam.toString())) {
                        return existingSaldo;
                    }
                }
                // Si llega aquí, o el saldo es igual, o no existe, usamos el del parámetro
                return bettorMoneyParam;
            }
        }
        // Si no se encontró el apostador, devolvemos el saldo que viene por parámetro
        return bettorMoneyParam;
    }

    private void ocultar_columnas(JTable table) {
        if (table.getName().equals("tblApostadores")) {
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(0);

            table.getColumnModel().getColumn(1).setMaxWidth(0);
            table.getColumnModel().getColumn(1).setMinWidth(0);
            table.getColumnModel().getColumn(1).setPreferredWidth(0);

            // Aumentar el ancho de la columna 2 (Nombre)
            table.getColumnModel().getColumn(2).setPreferredWidth(125);
            table.getColumnModel().getColumn(2).setMaxWidth(300);
            table.getColumnModel().getColumn(2).setMinWidth(125);

            // Reducir el ancho de la columna 3 (Monto)
            table.getColumnModel().getColumn(3).setPreferredWidth(75);
            table.getColumnModel().getColumn(3).setMaxWidth(100);
            table.getColumnModel().getColumn(3).setMinWidth(75);

            table.getColumnModel().getColumn(4).setMaxWidth(0);
            table.getColumnModel().getColumn(4).setMinWidth(0);
            table.getColumnModel().getColumn(4).setPreferredWidth(0);

            table.getColumnModel().getColumn(5).setMaxWidth(0);
            table.getColumnModel().getColumn(5).setMinWidth(0);
            table.getColumnModel().getColumn(5).setPreferredWidth(0);

        }

        if (table.getName().equals("tblCaballos")) {
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(0);

            table.getColumnModel().getColumn(2).setMaxWidth(0);
            table.getColumnModel().getColumn(2).setMinWidth(0);
            table.getColumnModel().getColumn(2).setPreferredWidth(0);

            table.getColumnModel().getColumn(3).setMaxWidth(0);
            table.getColumnModel().getColumn(3).setMinWidth(0);
            table.getColumnModel().getColumn(3).setPreferredWidth(0);

            table.getColumnModel().getColumn(4).setMaxWidth(0);
            table.getColumnModel().getColumn(4).setMinWidth(0);
            table.getColumnModel().getColumn(4).setPreferredWidth(0);
        }

        if (table.getName().equals("tblElegidos")) {
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(0);

            table.getColumnModel().getColumn(1).setMaxWidth(0);
            table.getColumnModel().getColumn(1).setMinWidth(0);
            table.getColumnModel().getColumn(1).setPreferredWidth(0);

            table.getColumnModel().getColumn(5).setMaxWidth(0);
            table.getColumnModel().getColumn(5).setMinWidth(0);
            table.getColumnModel().getColumn(5).setPreferredWidth(0);

            table.getColumnModel().getColumn(7).setMaxWidth(0);
            table.getColumnModel().getColumn(7).setMinWidth(0);
            table.getColumnModel().getColumn(7).setPreferredWidth(0);

            table.getColumnModel().getColumn(8).setMaxWidth(0);
            table.getColumnModel().getColumn(8).setMinWidth(0);
            table.getColumnModel().getColumn(8).setPreferredWidth(0);

            table.getColumnModel().getColumn(9).setMaxWidth(0);
            table.getColumnModel().getColumn(9).setMinWidth(0);
            table.getColumnModel().getColumn(9).setPreferredWidth(0);

            table.getColumnModel().getColumn(10).setMaxWidth(0);
            table.getColumnModel().getColumn(10).setMinWidth(0);
            table.getColumnModel().getColumn(10).setPreferredWidth(0);

            table.getColumnModel().getColumn(11).setMaxWidth(0);
            table.getColumnModel().getColumn(11).setMinWidth(0);
            table.getColumnModel().getColumn(11).setPreferredWidth(0);
        }
    }

    private void limpiar() {
        txtIdcarreras.setText("");
        txtNombre.setText("");
        atxtObservacion.setText("");
        txtGanador.setText("");
        txtIdganador.setText("");

        //Limpia la tabla sin eliminar nada de las configuraciones hechas
        DefaultTableModel model = (DefaultTableModel) tblElegidos.getModel();
        model.setRowCount(0);
    }

    public void showApuestas(String search, String stateFilter, Date startDate, Date endDate) {
        try {
            DefaultTableModel model;
            model = apuestascontroller.showApuestas(search, stateFilter, startDate, endDate);
            transferApuestasToElegidos(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void transferApuestasToElegidos(DefaultTableModel oldModel) {
        DefaultTableModel newModel = (DefaultTableModel) tblElegidos.getModel();
        ignoreTableEvents = true;
        newModel.setRowCount(0);  // limpia filas anteriores
        String fecha = "";
        String fechalimite = "";

        for (int i = 0; i < oldModel.getRowCount(); i++) {
            Object apuestaId = oldModel.getValueAt(i, 0);
            Object caballoId = oldModel.getValueAt(i, 11);
            Object caballoName = oldModel.getValueAt(i, 12);
            Object monto = oldModel.getValueAt(i, 2);
            Object abonadoTotal = oldModel.getValueAt(i, 14);
            Object apostadorId = oldModel.getValueAt(i, 7);
            Object apostadorName = oldModel.getValueAt(i, 8);
            Object saldoActual = oldModel.getValueAt(i, 9);
            String saldousadoStr = oldModel.getValueAt(i, 6).toString();

            boolean usarSaldo = "Si".equalsIgnoreCase(saldousadoStr);

            Object[] row = new Object[]{
                apuestaId,
                caballoId,
                caballoName,
                monto,
                abonadoTotal,
                apostadorId,
                apostadorName,
                saldoActual,
                usarSaldo,
                saldousadoStr,
                saldoActual, // saldo original
                abonadoTotal // abonado original
            };
            newModel.addRow(row);
        }

        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        Date fecha2 = null;
        Date fechalimite2 = null;
        try {
            fecha2 = (!fecha.isEmpty()) ? formatoFecha.parse(fecha) : currentDate;
            fechalimite2 = (!fechalimite.isEmpty()) ? formatoFecha.parse(fechalimite) : currentDate;
        } catch (ParseException ex) {
            Logger.getLogger(NewApuestas.class.getName()).log(Level.SEVERE, null, ex);
        }
        dchFecha.setDate(fecha2);
        dchFechalimite.setDate(fechalimite2);

        ignoreTableEvents = false;
    }

    public void loadRowData(List<String> rowData) {
        try {
            // Por ejemplo, si tu tabla principal tiene:
            // [0]=id, [1]=nombre, [2]=fecha, [3]=ganador, etc.
            txtNumero.setText(rowData.get(0));
            txtIdcarreras.setText(rowData.get(0));
            txtNombre.setText(rowData.get(1));

            String fecha = rowData.get(3);
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            Date fecha2 = (!fecha.isEmpty()) ? formatoFecha.parse(fecha) : currentDate;
            dchFecha.setDate(fecha2);

            txtIdganador.setText(!rowData.get(4).isEmpty() ? rowData.get(4) : "");
            txtGanador.setText(!rowData.get(9).isEmpty() ? rowData.get(9) : "");

            atxtObservacion.setText(!rowData.get(5).isEmpty() ? rowData.get(5) : "Sin Observacion");

            if ("activo".equals(rowData.get(6))) {
                chbActive.setSelected(true);
                initialState = rowData.get(6);
            } else {
                chbActive.setSelected(false);
                initialState = rowData.get(6);
            }

            DefaultTableModel oldModel = apuestascontroller.showApuestas(rowData.get(0), "activo", null, null);
            transferApuestasToElegidos(oldModel);
        } catch (ParseException ex) {
            Logger.getLogger(NewApuestas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Recorre las filas de tblElegidos y devuelve una lista de ApuestaData con
     * conversiones robustas.
     */
    private List<ApuestasData> extraerApuestasDeTabla(javax.swing.JTable tblElegidos) {
        List<ApuestasData> lista = new ArrayList<>();
        for (int fila = 0; fila < tblElegidos.getRowCount(); fila++) {
            // 0: idApuesta (puede ser null o vacío)
            Object objIdApuesta = tblElegidos.getValueAt(fila, 0);
            Integer idApuesta = null;
            if (objIdApuesta != null) {
                String s = objIdApuesta.toString().trim();
                if (!s.isEmpty()) {
                    try {
                        idApuesta = Integer.parseInt(s);
                    } catch (NumberFormatException ex) {
                        // Si falla parse, dejar null y quizá loggear
                        System.err.println("Warning: idApuesta no numérico en fila " + fila + ": " + s);
                    }
                }
            }

            // 1: idCaballo
            Integer idCaballo = null;
            Object objIdCaballo = tblElegidos.getValueAt(fila, 1);
            if (objIdCaballo != null) {
                if (objIdCaballo instanceof Number) {
                    idCaballo = ((Number) objIdCaballo).intValue();
                } else {
                    String s = objIdCaballo.toString().trim();
                    if (!s.isEmpty()) {
                        try {
                            idCaballo = Integer.parseInt(s);
                        } catch (NumberFormatException ex) {
                            System.err.println("Warning: idCaballo no numérico en fila " + fila + ": " + s);
                        }
                    }
                }
            }

            // 2: nombreCaballo
            Object objNombreCaballo = tblElegidos.getValueAt(fila, 2);
            String nombreCaballo = (objNombreCaballo != null) ? objNombreCaballo.toString().trim() : "";

            // 3: monto apostado
            int montoApostado = 0;
            Object objMontoApostado = tblElegidos.getValueAt(fila, 3);
            if (objMontoApostado != null) {
                String raw = objMontoApostado.toString().replace(".", "").trim();
                if (!raw.isEmpty()) {
                    try {
                        montoApostado = Integer.parseInt(raw);
                    } catch (NumberFormatException ex) {
                        System.err.println("Warning: montoApostado no numérico en fila " + fila + ": " + raw);
                    }
                }
            }

            // 4: monto abonado
            int montoAbonado = 0;
            Object objMontoAbonado = tblElegidos.getValueAt(fila, 4);
            if (objMontoAbonado != null) {
                String raw = objMontoAbonado.toString().replace(".", "").trim();
                if (!raw.isEmpty()) {
                    try {
                        montoAbonado = Integer.parseInt(raw);
                    } catch (NumberFormatException ex) {
                        System.err.println("Warning: montoAbonado no numérico en fila " + fila + ": " + raw);
                    }
                }
            }

            // 5: idApostador
            Integer idApostador = null;
            Object objIdApostador = tblElegidos.getValueAt(fila, 5);
            if (objIdApostador != null) {
                if (objIdApostador instanceof Number) {
                    idApostador = ((Number) objIdApostador).intValue();
                } else {
                    String s = objIdApostador.toString().trim();
                    if (!s.isEmpty()) {
                        try {
                            idApostador = Integer.parseInt(s);
                        } catch (NumberFormatException ex) {
                            System.err.println("Warning: idApostador no numérico en fila " + fila + ": " + s);
                        }
                    }
                }
            }

            // 6: nombreApostador
            Object objNombreApostador = tblElegidos.getValueAt(fila, 6);
            String nombreApostador = (objNombreApostador != null) ? objNombreApostador.toString().trim() : "";

            // 7: saldo actual
            int saldo = 0;
            Object objSaldo = tblElegidos.getValueAt(fila, 7);
            if (objSaldo != null) {
                String raw = objSaldo.toString().replace(".", "").trim();
                if (!raw.isEmpty()) {
                    try {
                        saldo = Integer.parseInt(raw);
                    } catch (NumberFormatException ex) {
                        System.err.println("Warning: saldo no numérico en fila " + fila + ": " + raw);
                    }
                }
            }

            // 8: origenPago (checkbox booleano)
            boolean origenPago = false;
            Object objOrigenPago = tblElegidos.getValueAt(fila, 8);
            if (objOrigenPago instanceof Boolean) {
                origenPago = (Boolean) objOrigenPago;
            } else if (objOrigenPago != null) {
                String s = objOrigenPago.toString().trim().toLowerCase();
                // interpretar "true"/"false", "si"/"no", "1"/"0" según tu UI
                if (s.equals("true") || s.equals("si") || s.equals("1")) {
                    origenPago = true;
                } else {
                    origenPago = false;
                }
            }

            // 9: saldoUsado
            Object objSaldoUsado = tblElegidos.getValueAt(fila, 9);
            String saldoUsado = (objSaldoUsado != null) ? objSaldoUsado.toString().trim() : "";

            // 10: saldoOriginal
            int saldoOriginal = 0;
            Object objSaldoOriginal = tblElegidos.getValueAt(fila, 10);
            if (objSaldoOriginal != null) {
                String raw = objSaldoOriginal.toString().replace(".", "").trim();
                if (!raw.isEmpty()) {
                    try {
                        saldoOriginal = Integer.parseInt(raw);
                    } catch (NumberFormatException ex) {
                        System.err.println("Warning: saldoOriginal no numérico en fila " + fila + ": " + raw);
                    }
                }
            }

            // 11: abonadoOriginal
            int abonadoOriginal = 0;
            Object objAbonadoOriginal = tblElegidos.getValueAt(fila, 11);
            if (objAbonadoOriginal != null) {
                String raw = objAbonadoOriginal.toString().replace(".", "").trim();
                if (!raw.isEmpty()) {
                    try {
                        abonadoOriginal = Integer.parseInt(raw);
                    } catch (NumberFormatException ex) {
                        System.err.println("Warning: abonadoOriginal no numérico en fila " + fila + ": " + raw);
                    }
                }
            }

            // Construir el objeto y agregarlo a la lista
            ApuestasData ad = new ApuestasData(
                    idApuesta, idCaballo, nombreCaballo,
                    montoApostado, montoAbonado,
                    idApostador, nombreApostador,
                    saldo, origenPago, saldoUsado,
                    saldoOriginal, abonadoOriginal
            );
            lista.add(ad);
        }
        return lista;
    }

    /**
     * Recalcula, a partir de la lista de apuestas en memoria y del id del
     * caballo ganador en texto, cuál(es) apostador(es) ha(n) ganado (es decir,
     * apostaron al caballo ganador) y cuánto debe(n) recibir, devolviendo un
     * String que incluya el nombre del apostador ganador y el monto
     * correspondiente.
     *
     * - Si txtIdGanador está vacío o no es número: devuelve "Sin ganador". - Si
     * no hay apuestas al ganador: devuelve "Sin apuestas al ganador". - Si hay
     * apuestas al ganador: • Si un solo apostador: "Apostador <Nombre>:
     * <monto>". • Si varios: "Apostador <Nombre1>: <monto1>; Apostador
     * <Nombre2>: <monto2>; ...".
     *
     * Indices esperados en cada Object[] fila (según tu tabla tblElegidos):
     * fila[1]: idCaballo (Integer o String) fila[2]: nombreCaballo (String) (no
     * es necesario aquí, pero se puede usar si se desea) fila[3]: monto
     * apostado (Integer o String) fila[5]: idApostador (Integer o String)
     * fila[6]: nombreapostador (String)
     *
     * @param apuestas Lista de Object[] con datos de cada apuesta, tal como la
     * construyes desde tblElegidos.
     * @param txtIdGanador Texto obtenido de txtGanador.getText().
     * @return String con el/los nombres del apostador ganador(es) y su(s)
     * monto(s), o "Sin ganador" / "Sin apuestas al ganador".
     */
    private String calcularMontoGanadorYNombreApostadorDesdeUI(List<Object[]> apuestas, String txtIdGanador) {
        // 1) Validar txtIdGanador
        if (txtIdGanador == null || txtIdGanador.trim().isEmpty()) {
            return "Sin ganador";
        }
        int idGanador;
        try {
            idGanador = Integer.parseInt(txtIdGanador.trim());
        } catch (NumberFormatException ex) {
            return "Sin ganador";
        }

        // 2) Agrupar apuestas por apostador y también guardar nombre de cada apostador
        Map<Integer, List<Object[]>> porApostador = new HashMap<>();
        Map<Integer, String> nombrePorApostador = new HashMap<>();
        for (Object[] fila : apuestas) {
            if (fila == null || fila.length <= 6) {
                continue;
            }
            // extraer idApostador de fila[5]
            Integer idApostador = null;
            Object objIdA = fila[5];
            if (objIdA instanceof Number) {
                idApostador = ((Number) objIdA).intValue();
            } else if (objIdA != null) {
                String s = objIdA.toString().trim();
                if (!s.isEmpty()) {
                    try {
                        idApostador = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        // inválido: saltar fila
                        continue;
                    }
                } else {
                    continue;
                }
            } else {
                continue;
            }
            // extraer nombreapostador de fila[6]
            Object objNombreA = fila[6];
            if (objNombreA != null) {
                String nombre = objNombreA.toString().trim();
                if (!nombre.isEmpty()) {
                    // si hay varias filas para el mismo idApostador, el nombre debería ser siempre igual
                    nombrePorApostador.putIfAbsent(idApostador, nombre);
                }
            }
            porApostador.computeIfAbsent(idApostador, k -> new ArrayList<>()).add(fila);
        }

        // 3) Calcular totales de la carrera: totalPool y totalGanador
        int totalPool = 0;
        int totalGanador = 0;
        for (Object[] fila : apuestas) {
            if (fila == null || fila.length <= 3) {
                continue;
            }
            // monto apostado en fila[3]
            int monto = 0;
            Object objMonto = fila[3];
            if (objMonto instanceof Number) {
                monto = ((Number) objMonto).intValue();
            } else if (objMonto != null) {
                String s = objMonto.toString().replace(".", "").trim();
                if (!s.isEmpty()) {
                    try {
                        monto = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        monto = 0;
                    }
                }
            }
            totalPool += monto;
            // idCaballo en fila[1]
            int idCab = -1;
            Object objCab = fila[1];
            if (objCab instanceof Number) {
                idCab = ((Number) objCab).intValue();
            } else if (objCab != null) {
                String s = objCab.toString().trim();
                if (!s.isEmpty()) {
                    try {
                        idCab = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            if (idCab == idGanador) {
                totalGanador += monto;
            }
        }

        // 4) Si no hay apuestas al ganador
        if (totalGanador == 0) {
            return "Sin apuestas al ganador";
        }

        // 5) Calcular ganancia neta: totalPerdido = totalPool - totalGanador; comisión 10%
        int totalPerdido = totalPool - totalGanador;
        int comision = (int) Math.round(totalPerdido * 0.10);
        int gananciaNeta = totalPerdido - comision;

        // 6) Para cada apostador que apostó al ganador, sumar stakes y calcular total a recibir
        Map<Integer, Integer> resultado = new LinkedHashMap<>(); // LinkedHashMap para orden predecible de inserción
        for (Map.Entry<Integer, List<Object[]>> entry : porApostador.entrySet()) {
            int idApostador = entry.getKey();
            List<Object[]> lista = entry.getValue();
            int stakeGanadora = 0;
            int stakePerdedores = 0;
            for (Object[] fila : lista) {
                // extraer idCaballo y monto
                int idCab = -1;
                Object objCab = fila[1];
                if (objCab instanceof Number) {
                    idCab = ((Number) objCab).intValue();
                } else if (objCab != null) {
                    try {
                        idCab = Integer.parseInt(objCab.toString().trim());
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
                int monto = 0;
                Object objM = fila[3];
                if (objM instanceof Number) {
                    monto = ((Number) objM).intValue();
                } else if (objM != null) {
                    try {
                        monto = Integer.parseInt(objM.toString().replace(".", "").trim());
                    } catch (NumberFormatException e) {
                        monto = 0;
                    }
                }
                if (idCab == idGanador) {
                    stakeGanadora += monto;
                } else {
                    stakePerdedores += monto;
                }
            }
            if (stakeGanadora > 0) {
                int totalARecibir = stakeGanadora + stakePerdedores + gananciaNeta;
                resultado.put(idApostador, totalARecibir);
            }
        }

        // 7) Construir el String de salida con nombre del apostador(es)
        if (resultado.isEmpty()) {
            return "Sin apuestas al ganador";
        } else {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Map.Entry<Integer, Integer> e : resultado.entrySet()) {
                if (!first) {
                    sb.append("; ");
                }
                first = false;
                int idApost = e.getKey();
                int monto = e.getValue();
                String nombreApost = nombrePorApostador.getOrDefault(idApost, "ID " + idApost);
                sb.append("Apostador ").append(nombreApost).append(": ").append(monto);
            }
            return sb.toString();
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
        btnActualizarsaldooculto = new javax.swing.JButton();

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

        btnActualizarsaldooculto.setText("actualizarsaldooculto");
        btnActualizarsaldooculto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarsaldoocultoActionPerformed(evt);
            }
        });

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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 231, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dchFechalimite, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dchFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(146, 146, 146)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIdganador, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(198, 198, 198)
                        .addComponent(btnActualizarsaldooculto)
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
                                .addComponent(jLabel6)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancel)
                        .addComponent(btnSave)
                        .addComponent(chbActive)
                        .addComponent(btnActualizarsaldooculto))
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
        // Limpiamos la selección de la otra tabla, pero no de tblElegidos
        tblCaballos.clearSelection();
        tblElegidos.clearSelection();
        // No se limpia tblElegidos para permitir actualizar la fila seleccionada

        if (evt.getClickCount() == 2) {
            int row = tblApostadores.getSelectedRow();
            if (row != -1) {
                Object bettorId = tblApostadores.getValueAt(row, 0);
                Object bettorName = tblApostadores.getValueAt(row, 2);
                Object bettorMoney = tblApostadores.getValueAt(row, 3);
                addApostadorToTable(bettorId, bettorName, bettorMoney);
            }
        }
    }//GEN-LAST:event_tblApostadoresMouseClicked

    private void tblCaballosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCaballosMouseClicked
        tblApostadores.clearSelection();
        tblElegidos.clearSelection();
        // No se limpia tblElegidos para permitir actualizar la fila seleccionada

        if (evt.getClickCount() == 2) {
            int row = tblCaballos.getSelectedRow();
            if (row != -1) {
                Object horseId = tblCaballos.getValueAt(row, 0);
                Object horseName = tblCaballos.getValueAt(row, 1);
                addHorseToTable(horseId, horseName);
            }
        }
    }//GEN-LAST:event_tblCaballosMouseClicked

    private void tblElegidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblElegidosMouseClicked
        tblApostadores.clearSelection();
        tblCaballos.clearSelection();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaactual = sdf.format(currentDate);

        if (SwingUtilities.isRightMouseButton(evt)) {
            // Obtener la fila bajo el cursor
            int row = tblElegidos.rowAtPoint(evt.getPoint());

            if (row != -1) { // Solo si se hizo clic en una fila válida
                tblElegidos.setRowSelectionInterval(row, row); // Seleccionar la fila

                // Crear menú contextual
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem deleteItem = new JMenuItem("Eliminar");
                JMenuItem addSaldo = new JMenuItem("Agregar Saldo");

                // Acción para eliminar la fila
                deleteItem.addActionListener((ActionEvent e) -> {
                    DefaultTableModel model = (DefaultTableModel) tblElegidos.getModel();
                    int columnCount = model.getColumnCount();
                    Object[] rowData = new Object[columnCount];

                    for (int i = 0; i < columnCount; i++) {
                        rowData[i] = model.getValueAt(row, i);
                    }

                    String[] opciones = {"Sí", "No"};
                    int respuesta = JOptionPane.showOptionDialog(this, "La apuesta será eliminada del juego definitivamente", "Desactivar?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                            null, opciones, opciones[0]
                    );

                    if (respuesta == JOptionPane.YES_OPTION) {
                        if (rowData[0] == null) {
                            model.removeRow(row);
                        } else {
                            finalState = "inactivo";
                            idestado = State_Controller.getEstadoId(finalState, Run.model);
                            movimientoscontroller.create(fechaactual, Integer.parseInt(rowData[4].toString()), "Devolución por apuesta eliminada", Integer.valueOf(rowData[5].toString()), Integer.valueOf(rowData[0].toString()), 6, null); //Tipomovimiento "devolucion" es 6
                            if (txtIdcarreras.getText().length() != 0) {
                                detallecontroller.deleteDetallecarreras(Integer.parseInt(txtIdcarreras.getText()));
                            }
                            apuestascontroller.disableApuestas(Integer.parseInt(rowData[0].toString()), 4, idestado); //Se pone el id de estadopago "cancelada" que es 4 y el id 
                            model.removeRow(row);
                        }
                    }

//                    updateDataWhenDelete(rowData, txtNumero.getText());
                    //reindexApuestas();
                });

                // Acción para agregar saldo
                addSaldo.addActionListener((ActionEvent e) -> {
                    Object apostador = tblElegidos.getValueAt(row, 6);
                    Object saldo = tblElegidos.getValueAt(row, 7);
                    Object idApostador = tblElegidos.getValueAt(row, 5);

                    if (apostador == null || saldo == null || idApostador == null) {
                        JOptionPane.showMessageDialog(this, "No hay un apostador válido seleccionado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    AgregarSaldo dialog = new AgregarSaldo(f, true);
                    AgregarSaldo.txtApostadores.setText(apostador.toString());
                    AgregarSaldo.txtMontoactual.setText(saldo.toString());
                    AgregarSaldo.txtIdapostadores.setText(idApostador.toString());
                    dialog.setVisible(true);
                });

                popupMenu.add(deleteItem);
                popupMenu.add(addSaldo);
                popupMenu.show(tblElegidos, evt.getX(), evt.getY()); // Mostrar menú
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaStr = sdf.format(dchFecha.getDate());
        String fechalimiteStr = sdf.format(dchFechalimite.getDate());
        String fechaactual = sdf.format(currentDate);

        // Formateadores para convertir los Strings a LocalDate y LocalTime
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Convertir los Strings a LocalDate y LocalTime
        LocalDate fecha = LocalDate.parse(fechaStr, formatoFecha);
        LocalDate fechalimite = LocalDate.parse(fechalimiteStr, formatoFecha);
        LocalDate fechaactual2 = LocalDate.parse(fechaactual, formatoFecha);

        // Formatearlo a String en formato compatible con MySQL (yyyy-MM-dd HH:mm:ss)
        DateTimeFormatter formatoMySQL = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fechaFinal = fecha.format(formatoMySQL);
        String fechaLimitefinal = fechalimite.format(formatoMySQL);
        String fechaactualfinal = fechaactual2.format(formatoMySQL);
        System.out.println("fechalimite: " + fechaLimitefinal);

        // Recorrer la tabla para crear un List<Objesct[]> con los datos de las apuestas
        for (int i = 0; i < tblElegidos.getRowCount(); i++) {
            Object idApuestas = tblElegidos.getValueAt(i, 0);
            Object idcaballo = tblElegidos.getValueAt(i, 1);
            Object nombrecaballo = tblElegidos.getValueAt(i, 2);

            // --- COLUMNA APOSTADO ---
            Object apostadoObj = tblElegidos.getValueAt(i, 3);
            String raw = (apostadoObj == null)
                    ? ""
                    : apostadoObj.toString().replace(".", "").trim();

            int apostado;
            try {
                apostado = raw.isEmpty()
                        ? 0
                        : Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                // Aquí también puedes loguear el error si quieres
                apostado = 0;
            }

            // --- COLUMNA ABONADO (con try/catch) ---
            Object abonadoObj = tblElegidos.getValueAt(i, 4);
            String rawAbonado = (abonadoObj == null)
                    ? ""
                    : abonadoObj.toString().replace(".", "").trim();

            int abonado;
            try {
                abonado = rawAbonado.isEmpty()
                        ? 0
                        : Integer.parseInt(rawAbonado);
            } catch (NumberFormatException ex) {
                // Si hay un formato incorrecto, le damos valor por defecto 0
                abonado = 0;
            }

            Object idapostador = tblElegidos.getValueAt(i, 5);
            Object nombreapostador = tblElegidos.getValueAt(i, 6);

            // --- COLUMNA SALDO ---
            Object saldoObj = tblElegidos.getValueAt(i, 7);
            String saldoStr = (saldoObj == null)
                    ? "0"
                    : saldoObj.toString().replace(".", "");
            int saldo = Integer.parseInt(saldoStr);

            // Checkbox (boolean) en la columna 8
            Boolean origenpago = (Boolean) tblElegidos.getValueAt(i, 8);

            // --- COLUMNA SALDO USADO (puede ser null según tu lógica) ---
            Object saldousadoObj = tblElegidos.getValueAt(i, 9);
            String saldousado = (saldousadoObj == null)
                    ? "No"
                    : saldousadoObj.toString();

            // --- COLUMNA SALDO ORIGINAL ---
            Object saldooriginalObj = tblElegidos.getValueAt(i, 10);
            String saldooriginalStr = (saldooriginalObj == null)
                    ? "0"
                    : saldooriginalObj.toString().replace(".", "");
            int saldooriginal = Integer.parseInt(saldooriginalStr);

            // --- COLUMNA ABONADO ORIGINAL ---
            Object abonadooriginalObj = tblElegidos.getValueAt(i, 11);
            String abonadooriginal = (abonadooriginalObj == null)
                    ? "0"
                    : abonadooriginalObj.toString();

            Object[] datosApuesta = {
                idApuestas, idcaballo, nombrecaballo, apostado, abonado,
                idapostador, nombreapostador, saldo, origenpago, saldousado,
                saldooriginal, abonadooriginal
            };
            apuestas.add(datosApuesta);
        }

        if (validateFields()) {
            if (txtIdcarreras.getText().length() == 0) {
                System.out.println("Entró metodo save");
                Integer idGanador = (txtIdganador.getText().trim().isEmpty()) ? null : Integer.valueOf(txtIdganador.getText().trim());
                save(Integer.parseInt(txtNumero.getText()), txtNombre.getText(), "Sin Lugar", fechaFinal, fechaLimitefinal, idGanador, atxtObservacion.getText(), apuestas, fechaactualfinal);
            } else {
                System.out.println("Entro metodo update");
                Integer idGanador = (txtIdganador.getText().trim().isEmpty()) ? null : Integer.valueOf(txtIdganador.getText().trim());
                update(Integer.parseInt(txtIdcarreras.getText()), txtNombre.getText(), "Sin Lugar", fechaFinal, idGanador, fechaLimitefinal, atxtObservacion.getText(), apuestas, fechaactualfinal);
            }
        }
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
        int rowCount = tblElegidos.getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero cargue las apuestas",
                    "Tabla vacía",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean tieneDatos = false;
        TableModel model = tblElegidos.getModel();
        for (int row = 0; row < rowCount; row++) {
            Object val1 = model.getValueAt(row, 1);
            Object val2 = model.getValueAt(row, 2);

            if (val1 != null && val2 != null
                    && !val1.toString().trim().isEmpty()
                    && !val2.toString().trim().isEmpty()) {

                tieneDatos = true;
                break;
            }
        }

        if (!tieneDatos) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay datos de caballos para elegir un ganador",
                    "Faltan datos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        HashMap<String, String> caballosMap = new HashMap<>();

        for (int row = 0; row < model.getRowCount(); row++) {
            Object idVal = model.getValueAt(row, 1);  //columna 1: idCaballo
            Object nameVal = model.getValueAt(row, 2);  //columna 2: nombreCaballo

            if (idVal != null && nameVal != null) {
                String idCaballo = idVal.toString();
                String nombreCaballo = nameVal.toString();
                caballosMap.putIfAbsent(idCaballo, nombreCaballo);
            }
        }

        CargarGanador dialog = new CargarGanador(f, true, Integer.parseInt(txtNumero.getText()), txtNombre.getText(), caballosMap);
        dialog.setVisible(true);

        this.setLocationRelativeTo(null);
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
            if (txtGanador.getText().length() != 0) {
                resultadoGanador = calcularMontoGanadorYNombreApostadorDesdeUI(apuestas, txtIdG);
            } else {
                resultadoGanador = "Sin ganador";
            }

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

    private void btnActualizarsaldoocultoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarsaldoocultoActionPerformed
        showApostadores("", "activo");
    }//GEN-LAST:event_btnActualizarsaldoocultoActionPerformed

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
        if (tblElegidos.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "La tabla principal no puede estar vacía.", "No hay nada", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validar que la fecha límite no sea menor que la fecha principal
        Date fecha = dchFecha.getDate();
        Date fechaLimite = dchFechalimite.getDate();
        if (fechaLimite.before(fecha)) {
            JOptionPane.showMessageDialog(null, "La fecha límite no puede ser menor a la fecha principal.", "Error de Fechas", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Obtener el modelo de la tabla
        DefaultTableModel model = (DefaultTableModel) tblElegidos.getModel();

        // Recorrer cada fila para validar los datos de las apuestas
        for (int i = 0; i < model.getRowCount(); i++) {
            // Validar que existan ID de caballo y de apostador (columna 1 y 5)
            Object idCaballoObj = model.getValueAt(i, 1);
            Object idApostadorObj = model.getValueAt(i, 5);
            if (idCaballoObj == null || idCaballoObj.toString().trim().isEmpty()
                    || idApostadorObj == null || idApostadorObj.toString().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe existir un caballo y un apostador en la fila " + (i + 1) + ".", "Error de Datos", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Validar que los montos apostado y abonado no estén vacíos y no sean 0 (columnas 3 y 4)
            Object apostadoObj = model.getValueAt(i, 3);
            Object abonadoObj = model.getValueAt(i, 4);

            if (apostadoObj == null || apostadoObj.toString().trim().isEmpty()
                    || abonadoObj == null || abonadoObj.toString().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Los montos 'apostado' y 'abonado' no pueden estar vacíos en la fila " + (i + 1) + ".", "Error de Datos", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (apostadoObj.toString().equals("0") || apostadoObj.toString().equals("")) {
                JOptionPane.showMessageDialog(null, "El monto apostado no puede ser 0 en la fila " + (i + 1) + ".", "Error de Datos", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (abonadoObj.toString().equals("0") || abonadoObj.toString().equals("0")) {
                JOptionPane.showMessageDialog(null, "El monto abonado no puede ser 0 en la fila " + (i + 1) + ".", "Error de Datos", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            try {
                // Se eliminan posibles puntos de formateo antes de parsear
                int apostado = Integer.parseInt(apostadoObj.toString().replace(".", "").trim());
                int abonado = Integer.parseInt(abonadoObj.toString().replace(".", "").trim());

                if (apostado == 0 || abonado == 0) {
                    JOptionPane.showMessageDialog(null, "Los montos 'apostado' y 'abonado' no pueden ser 0 en la fila " + (i + 1) + ".", "Error de Datos", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Formato numérico inválido en la fila " + (i + 1) + ".", "Error de Datos", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea atxtObservacion;
    public static javax.swing.JButton btnActualizarsaldooculto;
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
    private javax.swing.JTable tblApostadores;
    private javax.swing.JTable tblCaballos;
    private javax.swing.JTable tblElegidos;
    public static javax.swing.JTextField txtGanador;
    private javax.swing.JTextField txtIdcarreras;
    public static javax.swing.JTextField txtIdganador;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNumero;
    // End of variables declaration//GEN-END:variables

    private void save(int idcarrera, String nombrecarrera, String lugar, String fecha, String fechalimite, Integer idganador, String observacion, List<Object[]> apuestas, String fechaactual) {
        finalState = "activo";
        idestado = State_Controller.getEstadoId(finalState, Run.model);

        carrerascontroller.createCarreras(nombrecarrera, lugar, fecha, idganador, observacion, idestado);
        System.out.println("Pasó carrerascontroller.create  en save");

        System.out.println("idcarrera antes: " + idcarrera);
        idcarrera = carrerascontroller.getMaxCodigo() - 1;
        System.out.println("idcarrera despues: " + idcarrera);

        int i = 0;
        try {
            for (Object[] datos : apuestas) {
                System.out.println("int i: " + i);
                i++;
                int idCaballo = Integer.parseInt(datos[1].toString());
                String nombrecaballo = datos[2].toString();
                int apostado = Integer.parseInt(datos[3].toString());
                int abonado = Integer.parseInt(datos[4].toString());
                int idApostador = Integer.parseInt(datos[5].toString());
                String nombreapostador = datos[6].toString();
                int saldo = Integer.parseInt(datos[7].toString());
                boolean origenpago = (boolean) datos[8];
                String saldousado = datos[9].toString();
                int saldooriginal = Integer.parseInt(datos[10].toString());

                String origen;
                if (origenpago) {
                    origen = "Si";
                } else {
                    origen = "No";
                }

                System.out.println("Pasó origen pago con : " + origen);
                System.out.println("Pasó carrerascontroller.create en save: " + i);
                System.out.println("idcarrera: " + idcarrera);
                System.out.println("idcaballo: " + idCaballo);
                System.out.println(" ");
                detallecontroller.createDetallecarreras(idcarrera, idCaballo);

                //Estadopago "parcial" es 2
                //tipomovimientos "apuesta_parcial" es 3
                if (apostado > abonado) {
                    System.out.println("Entró en apostado > abonado en: " + i);
                    apuestascontroller.createApuestas("Apuesta", apostado, fecha, fechalimite, "Sin observacion", origen, idcarrera, idCaballo, idApostador, 2, idestado);
                    int idApuesta = apuestascontroller.getMaxid();
                    System.out.println("apostado > abonado en save: " + idApuesta);
                    movimientoscontroller.create(fechaactual, abonado, "Apuesta total realizada", idApostador, idApuesta, 3, idcarrera);
                    apostadorescontroller.addSaldo(-abonado, idApostador);
                }

                //Estadopago "pagada" es 3
                //tipomovimientos "apuesta_completada" es 4
                if (apostado == abonado) {
                    System.out.println("Entró en apostado == abonado en: " + i);
                    apuestascontroller.createApuestas("Apuesta", apostado, fecha, fechalimite, "Sin observacion", origen, idcarrera, idCaballo, idApostador, 2, idestado);
                    int idApuesta = apuestascontroller.getMaxid();
                    System.out.println("apostado == abonado en save: " + idApuesta);
                    movimientoscontroller.create(fechaactual, abonado, "Apuesta total realizada", idApostador, idApuesta, 3, idcarrera);
                    apostadorescontroller.addSaldo(-abonado, idApostador);
                }

                if (apostado < abonado) {
                    JOptionPane.showMessageDialog(null, "El monto abonado no puede ser mayor al monto apostado");
                }
            }
        } catch (HeadlessException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

        if (idganador != null) {
            try {
                List<Movimientos_Model> movimientosAnteriores = movimientoscontroller
                        .getMovimientosPorCarrera(idcarrera);

                if (movimientosAnteriores != null && !movimientosAnteriores.isEmpty()) {
                    movimientoscontroller.deleteMovimientosporcarrera(idcarrera);
                }

                // Agrupar apuestas por apostador
                Map<Integer, List<Object[]>> porApostador = new HashMap<>();
                for (Object[] fila : apuestas) {
                    Integer idApostador = Integer.parseInt(fila[5].toString());
                    porApostador.computeIfAbsent(idApostador, k -> new ArrayList<>()).add(fila);
                }

                // Calcular totales
                int totalApuestas = apuestas.stream().mapToInt(f -> (Integer) f[3]).sum();
                int totalGanador = apuestas.stream()
                        .filter(f -> Integer.parseInt(f[1].toString()) == idganador)
                        .mapToInt(f -> (Integer) f[3])
                        .sum();
                int totalPerdido = totalApuestas - totalGanador;
                int comision = (int) Math.round(totalPerdido * 0.10);
                int gananciaNeta = totalPerdido - comision;

                // Calcular lo que se le debe a cada ganador
                Map<Integer, Integer> resultado = new HashMap<>();

                System.out.println("🟡 ID del ganador seleccionado: " + idganador);
                System.out.println("🟡 Listado de apuestas:");
                for (Object[] fila : apuestas) {
                    System.out.println("  Apostador ID: " + fila[5]
                            + ", Caballo ID: " + fila[1]
                            + ", Monto: " + fila[3]);
                }

                System.out.println("🟡 Total apostado: " + totalApuestas);
                System.out.println("🟢 Total apostado al ganador: " + totalGanador);

                for (Map.Entry<Integer, List<Object[]>> entry : porApostador.entrySet()) {
                    int idApostador = entry.getKey();
                    List<Object[]> apuestasDelApostador = entry.getValue();

                    int apuestaGanadora = 0;
                    int stakePerdedores = 0;

                    for (Object[] f : apuestasDelApostador) {
                        int idCaballo = Integer.parseInt(f[1].toString());
                        int monto = (Integer) f[3];

                        if (idCaballo == idganador) {
                            apuestaGanadora += monto;
                        } else {
                            stakePerdedores += monto;
                        }
                    }

                    if (apuestaGanadora > 0) {
                        int totalARecibir = apuestaGanadora + stakePerdedores + gananciaNeta;
                        resultado.put(idApostador, totalARecibir);
                    }
                }

                // Aplicar resultados y registrar movimientos
                for (Map.Entry<Integer, Integer> entry : resultado.entrySet()) {
                    int idApostador = entry.getKey();
                    int monto = entry.getValue();

                    apostadorescontroller.addSaldo(monto, idApostador);
                    movimientoscontroller.create(fechaactual, monto, "Ganancia total (stake + devolución + neto)", idApostador, null, 5, idcarrera);
                }

                int respuesta = JOptionPane.showOptionDialog(this,
                        "Ha ganado " + txtGanador.getText() + ", imprimir juego?",
                        "Juego ingresado exitosamente!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, new String[]{"Sí", "No"}, "No");

                if (respuesta == JOptionPane.YES_OPTION) {
                    btnImprimir.doClick();
                }

            } catch (SQLException ex) {
                Logger.getLogger(NewApuestas.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Juego ingresado exitosamente!", "Hecho!", JOptionPane.INFORMATION_MESSAGE);
        }

        limpiar();

        showApostadores(
                "", "activo");
        String datefrom = Principal.txtDesde.getText();
        String dateto = Principal.txtHasta.getText();

        if (Principal.rbtnFinalizado.isSelected()
                == true) {
            String state = "";
            switch (cmbEstado.getSelectedIndex()) {
                case 0 ->
                    state = "Todos";
                case 1 ->
                    state = "activo";
                case 2 ->
                    state = "inactivo";
                default -> {
                }
            }
            Principal.instancia.showCarrerasInPrincipal("", state, "Finalizado", datefrom, dateto);
        }

        if (Principal.rbtnPendiente.isSelected()
                == true) {
            String state = "";
            switch (cmbEstado.getSelectedIndex()) {
                case 0 ->
                    state = "Todos";
                case 1 ->
                    state = "activo";
                case 2 ->
                    state = "inactivo";
                default -> {
                }
            }
            Principal.instancia.showCarrerasInPrincipal("", state, "Pendiente", datefrom, dateto);
        }

        if (Principal.rbtnTodos.isSelected()
                == true) {
            String state = "";
            switch (cmbEstado.getSelectedIndex()) {
                case 0 ->
                    state = "Todos";
                case 1 ->
                    state = "activo";
                case 2 ->
                    state = "inactivo";
                default -> {
                }
            }
            Principal.instancia.showCarrerasInPrincipal("", state, "Todos", datefrom, dateto);
        }

        txtNumero.setText(String.valueOf(carrerascontroller.getMaxCodigo()));
        txtNombre.setText("Juego N°: " + String.valueOf(carrerascontroller.getMaxCodigo()));
    }

    private void update(
            int idcarrera,
            String nombrecarrera,
            String lugar,
            String fecha,
            Integer idganador,
            String fechalimite,
            String observacion,
            List<Object[]> apuestas,
            String fechaactual
    ) {
        // 1) Gestión del cambio de estado (activo/inactivo)
        if (initialState.equals("activo") && finalState.equals("inactivo")) {
            if (txtIdcarreras.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un juego para desactivar.",
                        "Advertencia!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int resp = JOptionPane.showOptionDialog(this,
                    "El juego será desactivado",
                    "Desactivar?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Sí", "No"},
                    "Sí");
            if (resp == JOptionPane.YES_OPTION) {
                idestado = State_Controller.getEstadoId(finalState, Run.model);
            } else {
                return;
            }
        } else if (initialState.equals("inactivo") && finalState.equals("activo")) {
            if (txtIdcarreras.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un juego para activar.",
                        "Advertencia!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int resp = JOptionPane.showOptionDialog(this,
                    "El juego será activado",
                    "Activar?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Sí", "No"},
                    "Sí");
            if (resp == JOptionPane.YES_OPTION) {
                idestado = State_Controller.getEstadoId(finalState, Run.model);
            } else {
                return;
            }
        } else {
            idestado = State_Controller.getEstadoId(initialState, Run.model);
        }

        try {
            // 2) Actualizo la carrera y limpio detalles previos
            carrerascontroller.updateCarreras(
                    idcarrera, nombrecarrera, lugar, fecha, idganador, observacion, idestado
            );
            detallecontroller.deleteDetallecarreras(idcarrera);

            // 3) Creo o actualizo cada apuesta
            for (Object[] datos : apuestas) {
                String idApuestaStr = datos[0] == null ? "" : datos[0].toString();
                int idCaballo = Integer.parseInt(datos[1].toString());
                int montoApostado = Integer.parseInt(datos[3].toString());
                int nuevoTotalAbonado = Integer.parseInt(datos[4].toString());
                int idApostador = Integer.parseInt(datos[5].toString());
                int abonadoOriginal = datos[11].toString().isEmpty()
                        ? 0
                        : Integer.parseInt(datos[11].toString());
                boolean origenpago = (boolean) datos[8];
                String origen = origenpago ? "Si" : "No";

                // 3a) Detalle carrera-caballo
                detallecontroller.createDetallecarreras(idcarrera, idCaballo);

                // 3b) Crear o actualizar apuesta y obtener su ID real
                int idApuesta = 0;
                if (idApuestaStr.isEmpty()) {
                    // inserción --> createApuestas devuelve el id generado
                    apuestascontroller.createApuestas(
                            "Apuesta",
                            montoApostado,
                            fecha,
                            fechalimite,
                            "Sin observacion",
                            origen,
                            idcarrera,
                            idCaballo,
                            idApostador,
                            2, // estado parcial
                            idestado
                    );
                } else {
                    idApuesta = Integer.parseInt(idApuestaStr);
                    apuestascontroller.updateApuestas(
                            idApuesta,
                            "Apuesta",
                            montoApostado,
                            fecha,
                            fechalimite,
                            "Sin observacion",
                            origen,
                            idcarrera,
                            idCaballo,
                            idApostador,
                            2, // sigue en parcial hasta que se registre pago total
                            idestado
                    );
                }

                // 3c) Generar movimiento si abonó algo nuevo
                int diff = nuevoTotalAbonado - abonadoOriginal;
                if (diff > 0) {
                    // 3 = parcial, 4 = completo
                    int tipoMov = (nuevoTotalAbonado < montoApostado) ? 3 : 4;
                    movimientoscontroller.create(
                            fechaactual,
                            diff,
                            observacion,
                            idApostador,
                            idApuesta,
                            tipoMov, idcarrera
                    );
                    // si quedó pagada completa, actualizo estado de apuesta
                    if (tipoMov == 4) {
                        apuestascontroller.updateApuestas(
                                idApuesta,
                                "Apuesta",
                                montoApostado,
                                fecha,
                                fechalimite,
                                "Sin observacion",
                                origen,
                                idcarrera,
                                idCaballo,
                                idApostador,
                                3, // pagada
                                idestado
                        );
                    }
                }
            }

            // 4) Si hay ganador, elimino reparto anterior (si existe) y recalculo
            if (idganador != null) {
                System.out.println(">>> Iniciando recálculo de ganancia para carrera " + idcarrera + ", nuevo ganador " + idganador);

                // 1. Obtener movimientos anteriores
                List<Movimientos_Model> movimientosAnteriores = movimientoscontroller.getMovimientosPorCarrera(idcarrera);
                System.out.println("Movimientos anteriores:");
                if (movimientosAnteriores != null && !movimientosAnteriores.isEmpty()) {
                    for (Movimientos_Model mov : movimientosAnteriores) {
                        System.out.println("  Apostador ID: " + mov.getFk_apostadores()
                                + " | Tipo: " + mov.getFk_tipomovimientos()
                                + " | Monto: " + mov.getMonto());
                    }
                    // 2. Revertir saldos aplicados en movimientos anteriores
                    for (Movimientos_Model mov : movimientosAnteriores) {
                        int tipo = mov.getFk_tipomovimientos();
                        int idApost = mov.getFk_apostadores();
                        int monto = mov.getMonto();
                        if (tipo == 5 || tipo == 6) {
                            System.out.println("  → Revirtiendo " + monto + " a apostador " + idApost);
                            apostadorescontroller.addSaldo(-monto, idApost);
                        }
                    }
                    // 3. Eliminar movimientos anteriores
                    movimientoscontroller.deleteMovimientosporcarrera(idcarrera);
                    System.out.println("  Movimientos anteriores eliminados.");
                } else {
                    System.out.println("  No había movimientos de reparto previos.");
                }

                // Verificar idganador y apuestas
                System.out.println("  Apuestas recibidas para recálculo:");
                for (Object[] fila : apuestas) {
                    System.out.println("    Apostador=" + fila[5] + ", Caballo=" + fila[1] + ", Monto=" + fila[3]);
                }
                System.out.println("  idganador en update: " + idganador);

                // 4. Agrupar apuestas por apostador
                Map<Integer, List<Object[]>> porApostador = new HashMap<>();
                for (Object[] fila : apuestas) {
                    int idApost = Integer.parseInt(fila[5].toString());
                    porApostador.computeIfAbsent(idApost, k -> new ArrayList<>()).add(fila);
                }

                // 5. Calcular totales generales
                int totalApostado = apuestas.stream().mapToInt(f -> Integer.parseInt(f[3].toString())).sum();
                int totalGanador = apuestas.stream()
                        .filter(f -> Integer.parseInt(f[1].toString()) == idganador)
                        .mapToInt(f -> Integer.parseInt(f[3].toString()))
                        .sum();
                int totalPerdido = totalApostado - totalGanador;
                int comision = (int) Math.round(totalPerdido * 0.10);
                int gananciaNeta = totalPerdido - comision;
                System.out.println("  Totales: totalApostado=" + totalApostado
                        + ", totalGanador=" + totalGanador
                        + ", totalPerdido=" + totalPerdido
                        + ", gananciaNeta=" + gananciaNeta);

                // 6. Determinar ganadores y aplicar saldo
                for (Map.Entry<Integer, List<Object[]>> entry : porApostador.entrySet()) {
                    int idApost = entry.getKey();
                    List<Object[]> apuestasApostador = entry.getValue();

                    int sumaGanadora = 0;
                    int stakePerdedores = 0;
                    for (Object[] f : apuestasApostador) {
                        int idCab = Integer.parseInt(f[1].toString());
                        int mon = Integer.parseInt(f[3].toString());
                        if (idCab == idganador) {
                            sumaGanadora += mon;
                        } else {
                            stakePerdedores += mon;
                        }
                    }
                    System.out.println("    Apostador " + idApost + ": sumaGanadora=" + sumaGanadora
                            + ", stakePerdedores=" + stakePerdedores);

                    if (sumaGanadora > 0) {
                        int totalRecibir = sumaGanadora + stakePerdedores + gananciaNeta;
                        System.out.println("    → Aplicando totalRecibir=" + totalRecibir + " a apostador " + idApost);
                        apostadorescontroller.addSaldo(totalRecibir, idApost);
                        System.out.println("    → Insertando movimiento reparto para apostador " + idApost);
                        movimientoscontroller.create(
                                fechaactual,
                                totalRecibir,
                                "Ganancia total (stake + devolución + neto)",
                                idApost,
                                null,
                                5,
                                idcarrera
                        );
                    }
                }

                // 7. Confirmar impresión
                int resp = JOptionPane.showOptionDialog(
                        this,
                        "Ha ganado " + txtGanador.getText() + ", ¿imprimir juego?",
                        "Selecciona",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Sí", "No"},
                        "No"
                );
                if (resp == JOptionPane.YES_OPTION) {
                    btnImprimir.doClick();
                }
            }

            // 5) Refresco interfaz y listo…
            limpiar();
            showApostadores("", "activo");
            String datefrom = Principal.txtDesde.getText();
            String dateto = Principal.txtHasta.getText();
            String filtroEstado = switch (cmbEstado.getSelectedIndex()) {
                case 1 ->
                    "activo";
                case 2 ->
                    "inactivo";
                default ->
                    "Todos";
            };
            String status = Principal.rbtnFinalizado.isSelected() ? "Finalizado"
                    : Principal.rbtnPendiente.isSelected() ? "Pendiente"
                    : /*else*/ "Todos";
            Principal.instancia.showCarrerasInPrincipal("", filtroEstado, status, datefrom, dateto);

            int maxCod = carrerascontroller.getMaxCodigo();
            txtNumero.setText(String.valueOf(maxCod));
            txtNombre.setText("Juego N°: " + maxCod);

        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al actualizar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
}
