package Config;

import java.awt.Color;
import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author SwichBlade15
 */
public class ApuestasRenderer extends DefaultTableCellRenderer {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Obtener valores de la tabla
        Object montoApostadoObj = table.getValueAt(row, 2); // Columna "Monto"
        Object montoAbonadoObj = table.getValueAt(row, 3);  // Columna "Abonado"
        Object fechaLimiteObj = table.getValueAt(row, 5);   // Columna "Fecha Límite"

        // Convertir a String
        String montoApostadoStr = (montoApostadoObj != null) ? montoApostadoObj.toString().trim() : "";
        String montoAbonadoStr = (montoAbonadoObj != null) ? montoAbonadoObj.toString().trim() : "";
        String fechaLimiteStr = (fechaLimiteObj != null) ? fechaLimiteObj.toString().trim() : "";

        try {
            // Obtener la fecha actual
            Date fechaActual = new Date();
            Date fechaLimite = (fechaLimiteStr.isEmpty()) ? null : dateFormat.parse(fechaLimiteStr);

            // Verificar que los montos no estén vacíos y convertirlos
            if (!montoApostadoStr.isEmpty() && !montoAbonadoStr.isEmpty()) {
                int montoApostado = Integer.parseInt(montoApostadoStr);
                int montoAbonado = Integer.parseInt(montoAbonadoStr);

                if (fechaLimite != null) {
                    // Calcular la diferencia en días
                    long diffMillis = fechaActual.getTime() - fechaLimite.getTime();
                    long diasPasados = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);

                    // Si han pasado más de 7 días desde la fecha límite → Blanco
                    if (diasPasados > 7) {
                        c.setBackground(Color.WHITE);
                    } // Si la fecha ya venció Y el monto abonado es menor al apostado → Rojo
                    else if (fechaLimite.before(fechaActual) && montoAbonado < montoApostado) {
                        c.setBackground(new Color(255, 87, 87, 180)); // Rojo
                    } // Si el monto abonado es menor pero la fecha no ha vencido → Amarillo
                    else if (montoAbonado < montoApostado) {
                        c.setBackground(new Color(255, 235, 59, 180)); // Amarillo
                    } // Si el monto abonado es igual o mayor → Verde
                    else {
                        c.setBackground(new Color(76, 175, 80, 180)); // Verde
                    }
                } else {
                    c.setBackground(new Color(255, 235, 59, 180)); // Amarillo si hay datos vacíos
                }
            } else {
                c.setBackground(new Color(255, 235, 59, 180)); // Amarillo si hay datos vacíos
            }
        } catch (NumberFormatException | ParseException e) {
            c.setBackground(Color.YELLOW); // En caso de error, dejar en amarillo
        }
        return c;
    }
}
