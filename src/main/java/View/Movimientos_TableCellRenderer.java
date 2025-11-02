package View;

import java.awt.Component;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class Movimientos_TableCellRenderer extends DefaultTableCellRenderer {

    // --- CORRECCIÓN EN EL FORMATO ---
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat formateador = new DecimalFormat("#,###");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // --- CORRECCIÓN EN EL TIPO DE DATO ---
        // Se formatea si el valor es de tipo LocalDate
        if (value instanceof LocalDate) {
            setText(((LocalDate) value).format(formatter));
            setHorizontalAlignment(SwingConstants.LEFT);
        } else if (value instanceof Number) {
            setText(formateador.format(value));
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            setText(value == null ? "" : value.toString());
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        return this;
    }
}
