package View;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class PerfilApostador_TableCellRenderer extends DefaultTableCellRenderer {

    private final DecimalFormat formateador = new DecimalFormat("#,###");
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        int modelColumn = table.convertColumnIndexToModel(column);

        // Formato de texto y alineación
        if (value instanceof Number) {
            setText(formateador.format(value));
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else if (value instanceof LocalDate) {
            setText(((LocalDate) value).format(dateFormat));
            setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            setText(value == null ? "" : value.toString());
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        // Lógica de colores para la columna "Resultado"
        if (modelColumn == 5) { // Se asume que la columna "Resultado" es la 5
            String resultado = value.toString();
            if (!isSelected) {
                if ("Ganador".equalsIgnoreCase(resultado)) {
                    setBackground(new Color(204, 255, 204)); // Verde
                } else if ("Perdedor".equalsIgnoreCase(resultado)) {
                    setBackground(new Color(255, 204, 204)); // Rojo
                } else {
                    setBackground(table.getBackground());
                }
            }
        } else {
            if (!isSelected) {
                setBackground(table.getBackground());
            }
        }

        return this;
    }
}
