package View;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class Balance_TableCellRenderer extends DefaultTableCellRenderer {

    private final DecimalFormat formateador;

    public Balance_TableCellRenderer() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        this.formateador = new DecimalFormat("#,###", symbols);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        // Se obtiene el componente base y se le asigna el valor.
        // El valor que llega aquí es un NÚMERO (Integer, Long, etc.).
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Se aplica el formato de miles con puntos.
        if (value instanceof Number) {
            setText(formateador.format(value));
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            setText(value == null ? "" : value.toString());
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        // Lógica de colores para la columna "Resultado Final"
        int modelColumn = table.convertColumnIndexToModel(column);
        if (modelColumn == 6 && value instanceof Number) {
            long monto = ((Number) value).longValue();

            if (!isSelected) {
                if (monto > 0) {
                    setBackground(new Color(204, 255, 204)); // Verde
                } else if (monto < 0) {
                    setBackground(new Color(255, 204, 204)); // Rojo
                } else {
                    setBackground(table.getBackground());
                }
            }
            c.setFont(c.getFont().deriveFont(Font.BOLD));
        } else {
            if (!isSelected) {
                setBackground(table.getBackground());
            }
        }

        return c;
    }
}
