package View;

import java.awt.Component;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class ApostadoresSeleccionRenderer extends DefaultTableCellRenderer {

    private final DecimalFormat formateador;

    public ApostadoresSeleccionRenderer() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        this.formateador = new DecimalFormat("#,###", symbols);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Lógica de formato de texto (no necesita coloreado)
        if (value instanceof Number) {
            setHorizontalAlignment(SwingConstants.RIGHT);
            setText(formateador.format(value));
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
            setText(value == null ? "" : value.toString());
        }

        return this;
    }
}
