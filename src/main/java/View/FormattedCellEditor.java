package View;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

/**
 * Un editor de celdas para JTable que formatea y valida la entrada de números
 * enteros. Limita el número de dígitos y aplica un formato de separador de
 * miles.
 */
public class FormattedCellEditor extends DefaultCellEditor {

    private final JTextField textField;
    private static final int MAX_DIGITOS = 9; // Límite de dígitos (ej. 999.999.999)
    private final DecimalFormat formateador;

    public FormattedCellEditor() {
        super(new JTextField());
        this.textField = (JTextField) getComponent();

        // Se configura el formateador para usar punto como separador de miles.
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        this.formateador = new DecimalFormat("#,###", symbols);

        // Se agrega un listener para finalizar la edición cuando el campo pierde el foco.
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                stopCellEditing();
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                // Se reformatea el texto mientras el usuario escribe.
                // Se ejecuta en keyReleased para tener el valor más actualizado.
                String textoLimpio = textField.getText().replace(".", "");
                if (!textoLimpio.isEmpty()) {
                    try {
                        long valor = Long.parseLong(textoLimpio);
                        // Se guarda la posición del cursor para restaurarla después.
                        int cursorPos = textField.getCaretPosition();
                        textField.setText(formateador.format(valor));
                        // Se intenta restaurar la posición del cursor de forma inteligente.
                        textField.setCaretPosition(Math.min(cursorPos, textField.getText().length()));
                    } catch (NumberFormatException ex) {
                        // No se hace nada si el texto no es un número válido.
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                String textoLimpio = textField.getText().replace(".", "");

                // Se permite la entrada solo de dígitos y teclas de control (borrar, etc.).
                if (!Character.isDigit(c) && !Character.isISOControl(c)) {
                    Toolkit.getDefaultToolkit().beep(); // Sonido de alerta, no intrusivo.
                    evt.consume(); // Se ignora la tecla presionada.
                    return;
                }

                // Se verifica que no se exceda el número máximo de dígitos.
                if (textoLimpio.length() >= MAX_DIGITOS && !Character.isISOControl(c)) {
                    Toolkit.getDefaultToolkit().beep();
                    evt.consume();
                }
            }
        });
    }

    @Override
    public boolean stopCellEditing() {
        // Al detener la edición, se asegura de que el formato sea correcto.
        String textoLimpio = textField.getText().replace(".", "");
        if (textoLimpio.isEmpty()) {
            textField.setText("0");
        } else {
            try {
                long valor = Long.parseLong(textoLimpio);
                textField.setText(formateador.format(valor));
            } catch (NumberFormatException e) {
                // Si el formato es inválido, se podría revertir o dejar como está.
                // Por ahora se mantiene el texto para no perder la entrada del usuario.
            }
        }
        return super.stopCellEditing();
    }

    @Override
    public Object getCellEditorValue() {
        // Devuelve el valor numérico limpio (un Integer) al TableModel.
        String textoLimpio = textField.getText().replace(".", "");
        if (textoLimpio.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(textoLimpio);
        } catch (NumberFormatException e) {
            // Si el valor final no es un número, se devuelve 0 por seguridad.
            return 0;
        }
    }
}
