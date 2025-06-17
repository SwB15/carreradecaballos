package Config;

import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author SwichBlade15
 */
public class FormattedCellEditor extends DefaultCellEditor {

    private final JTextField textField;
    private final int maxCaracteres = 10;
    private final DecimalFormat formateador14;

    public FormattedCellEditor() {
        super(new JTextField());
        textField = (JTextField) getComponent();

        // Configurar el formateador para usar punto como separador de miles
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formateador14 = new DecimalFormat("#,###", symbols);

        // Finaliza la edición al perder el foco
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                stopCellEditing();
            }
        });

        // Formateo dinámico mientras se escribe
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                String texto = textField.getText().replace(".", "").replace(",", "");
                if (!texto.isEmpty()) {
                    try {
                        int valor = Integer.parseInt(texto);
                        String formatted = formateador14.format(valor);
                        textField.setText(formatted);
                        textField.setCaretPosition(formatted.length());
                    } catch (NumberFormatException ex) {
                        // No hace nada si no es numérico
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                // Permitir dígitos y caracteres de control (backspace, delete, etc.)
                if (!Character.isDigit(c) && !Character.isISOControl(c)) {
                    Toolkit.getDefaultToolkit().beep();
                    evt.consume();
                    JOptionPane.showMessageDialog(null, "Ingrese solo números", "Advertencia!", JOptionPane.WARNING_MESSAGE);
                }

                // Verificar que no se exceda el número máximo de caracteres
                if (textField.getText().replace(".", "").length() >= maxCaracteres && !Character.isISOControl(c)) {
                    evt.consume();
                    JOptionPane.showMessageDialog(null, "No ingrese tantos números", "Advertencia!", JOptionPane.WARNING_MESSAGE);
                }
            }

        });
    }

    @Override
    public boolean stopCellEditing() {
        String texto = textField.getText().replace(".", "").replace(",", "");
        try {
            int valor = Integer.parseInt(texto);
            textField.setText(formateador14.format(valor));
        } catch (NumberFormatException e) {
            // Si hay error, dejamos el texto tal cual
        }
        return super.stopCellEditing();
    }

    @Override
    public Object getCellEditorValue() {
        String texto = textField.getText().replace(".", "").replace(",", "");
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
