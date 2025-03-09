package Config;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author SwichBlade15
 */

public class ApuestasRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Obtener valores de la tabla
        Object montoApostadoObj = table.getValueAt(row, 2); // Columna "Monto"
        Object montoAbonadoObj = table.getValueAt(row, 3);  // Columna "Abonado"

        // Convertir a String para validar si están vacíos
        String montoApostadoStr = (montoApostadoObj != null) ? montoApostadoObj.toString().trim() : "";
        String montoAbonadoStr = (montoAbonadoObj != null) ? montoAbonadoObj.toString().trim() : "";

        try {
            // Si alguno de los valores es null o está vacío, marcar en amarillo
            if (montoApostadoStr.isEmpty() || montoAbonadoStr.isEmpty()) {
                c.setBackground(new Color(255, 235, 59, 180)); //amarillo
            } else {
                int montoApostado = Integer.parseInt(montoApostadoStr);
                int montoAbonado = Integer.parseInt(montoAbonadoStr);

                // Si el monto abonado es menor al apostado, marcar en amarillo
                if (montoAbonado < montoApostado) {
                    c.setBackground(new Color(255, 235, 59, 180));  //verde
                } else {
                    c.setBackground(new Color(76, 175, 80, 180)); //verde
                }
            }
        } catch (NumberFormatException e) {
            // Si ocurre un error en la conversión, dejar en amarillo por seguridad
            c.setBackground(Color.YELLOW);
        }

        return c;
    }
}
