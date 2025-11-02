package View;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class Apostadores_TableCellRenderer extends DefaultTableCellRenderer {

    private final DecimalFormat formateador;

    public Apostadores_TableCellRenderer() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        this.formateador = new DecimalFormat("#,###", symbols);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        // Se obtiene el componente base, es importante llamar a super() PRIMERO.
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // --- LÓGICA UNIFICADA ---
        int modelRow = table.convertRowIndexToModel(row);

        // 1. Se aplica el color de fondo a TODA la fila.
        if (!isSelected) {
            Object colorValue = table.getModel().getValueAt(modelRow, 6); // Columna "Estado Deuda"
            String estadoColor = (colorValue == null) ? "" : colorValue.toString();

            switch (estadoColor) {
                case "4" -> // ROJO
                    c.setBackground(new Color(255, 204, 204));
                case "3" -> // VERDE
                    c.setBackground(new Color(204, 255, 204));
                case "2" -> // AZUL
                    c.setBackground(new Color(204, 229, 255));
                case "1" -> // AMARILLO
                    c.setBackground(new Color(255, 255, 153));
                default -> c.setBackground(table.getBackground());
            }
        }

        // 2. Se aplica el formato de texto específico a cada celda.
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
