package View;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class Pendientes_TableCellRenderer extends DefaultTableCellRenderer {

    private final DecimalFormat formateador;

    public Pendientes_TableCellRenderer() {
        // Se fuerza el uso del punto como separador de miles.
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        this.formateador = new DecimalFormat("#,###", symbols);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        int modelRow = table.convertRowIndexToModel(row);

        // 1. Lógica de color de fondo
        if (!isSelected) {
            // Se lee el color de la columna oculta (índice 6)
            Object colorValue = table.getModel().getValueAt(modelRow, 6);
            String estadoColor = (colorValue == null) ? "" : colorValue.toString();

            switch (estadoColor.toUpperCase()) {
                case "ROJO" ->
                    c.setBackground(new Color(255, 204, 204));
                case "VERDE" ->
                    c.setBackground(new Color(204, 255, 204));
                case "AZUL" ->
                    c.setBackground(new Color(204, 229, 255));
                case "AMARILLO" ->
                    c.setBackground(new Color(255, 255, 153));
                default ->
                    c.setBackground(table.getBackground());
            }
        }

        // 2. Lógica de formato de texto
        if (value instanceof Number) {
            setHorizontalAlignment(SwingConstants.RIGHT);
            setText(formateador.format(value));
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
            setText(value == null ? "" : value.toString());
        }

        return c;
    }
}
