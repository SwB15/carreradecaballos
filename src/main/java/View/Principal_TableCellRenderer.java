package View;

import java.awt.Component;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class Principal_TableCellRenderer extends DefaultTableCellRenderer {

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat formateador = new DecimalFormat("#,###'%'");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Formato para fechas
        if (value instanceof LocalDate) {
            setText(((LocalDate) value).format(dateFormat));
        } // Formato para la lista de caballos
        else if (value instanceof List) {
            // Se asume que es una lista de Caballos_Model o similar
            // Esta lógica puede ser más compleja si es necesario
            setText(String.format("%d participante(s)", ((List<?>) value).size()));
        } // Formato para la comisión
        else if (table.getColumnName(column).equalsIgnoreCase("Comisión") && value instanceof Number) {
            setText(formateador.format(value));
        }

        return this;
    }
}
