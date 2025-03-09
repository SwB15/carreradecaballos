package Controller;

import Config.DataSource;
import View.Principal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Fecha_Controller {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Lista para almacenar apuestas pendientes
    private static final List<ApuestaPendiente> apuestasPendientes = new ArrayList<>();

    public static DefaultTableModel checkPending() {
        String[] titulos = {"Codigo", "Monto", "Abonado", "FechaHora", "FechaLimite"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos);
        String sql = "SELECT idapuestas, monto, abonado, fecha, fechalimite FROM apuestas ORDER BY idapuestas DESC";

        try (Connection cn = DataSource.getConnection(); Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String idApuesta = rs.getString("idapuestas");
                String monto = rs.getString("monto");
                String abonado = rs.getString("abonado");
                String fechaHora = rs.getString("fecha"); // Fecha y hora en formato "yyyy-MM-dd HH:mm"
                String fechaLimite = rs.getString("fechalimite"); // Fecha sin hora

                modelo.addRow(new String[]{idApuesta, monto, abonado, fechaHora, fechaLimite});
            }
            return modelo;
        } catch (SQLException e) {
            System.out.println("❌ Error al consultar la BD: " + e.getMessage());
            return null;
        }
    }

    public static boolean checkPendingBets(DefaultTableModel model) {
        apuestasPendientes.clear();

        if (model.getRowCount() == 0 || model.getColumnCount() < 5) {
            System.out.println("⚠️ La tabla está vacía o no tiene suficientes columnas.");
            return false;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                String fechaApuestaStr = model.getValueAt(i, 3).toString();
                String fechaLimiteStr = model.getValueAt(i, 4).toString();
                double montoApuesta = Double.parseDouble(model.getValueAt(i, 1).toString());
                double montoAbonado = Double.parseDouble(model.getValueAt(i, 2).toString());

                LocalDate fechaApuesta = LocalDateTime.parse(fechaApuestaStr, dateTimeFormatter).toLocalDate();
                LocalDate fechaLimite = LocalDate.parse(fechaLimiteStr);

                // Comparar la fecha límite con la fecha actual del sistema
                LocalDate fechaActual = LocalDate.now();  // Fecha actual del sistema

                // Si la fecha límite es menor que la fecha actual y el monto abonado es menor que el apostado
                if (fechaLimite.isBefore(fechaActual) && montoApuesta > montoAbonado) {
                    System.out.println("⚠️ Apuesta vencida: ID " + model.getValueAt(i, 0));

                    apuestasPendientes.add(new ApuestaPendiente(
                            model.getValueAt(i, 0).toString(), // ID
                            montoApuesta,
                            montoAbonado,
                            fechaApuestaStr,
                            fechaLimiteStr
                    ));
                }
            } catch (Exception e) {
                System.out.println("❌ Error en la fila " + i + ": " + e.getMessage());
            }
        }

        // Actualizar etiqueta de notificaciones en la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            Principal.lblCantidadNotificaciones.setText(String.valueOf(getTotalPendientes()));
        });

        return !apuestasPendientes.isEmpty();
    }

    // Método para obtener el total de apuestas pendientes
    public static int getTotalPendientes() {
        return apuestasPendientes.size();
    }

    // Clase interna para almacenar apuestas vencidas
    static class ApuestaPendiente {

        String id;
        double montoApuesta;
        double montoAbonado;
        String fechaApuesta;
        String fechaLimite;

        public ApuestaPendiente(String id, double montoApuesta, double montoAbonado, String fechaApuesta, String fechaLimite) {
            this.id = id;
            this.montoApuesta = montoApuesta;
            this.montoAbonado = montoAbonado;
            this.fechaApuesta = fechaApuesta;
            this.fechaLimite = fechaLimite;
        }
    }
}
