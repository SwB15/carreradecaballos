package View;

import Model.EstadoApuesta;
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

        // Se obtiene el componente original para no perder propiedades.
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Se asume que el TableModel ahora puede devolver el EstadoApuesta para una fila.
        // Esto se logra modificando el TableModel para que tenga una columna (puede ser oculta) con el estado.
        Object estadoValue = table.getValueAt(row, table.getColumnCount() - 1); // Asumiendo que el estado está en la última columna.

        if (estadoValue instanceof EstadoApuesta) {
            EstadoApuesta estado = (EstadoApuesta) estadoValue;
            c.setBackground(estado.getColor());
        } else {
            // Color por defecto si el estado no se puede determinar.
            c.setBackground(table.getBackground());
        }

        return c;
    }
}
