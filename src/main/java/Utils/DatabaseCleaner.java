package Utils;

import Config.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 * Utilidad para limpiar los datos de las tablas principales de la base de
 * datos. Esta operación es destructiva y debe usarse con precaución.
 */
public final class DatabaseCleaner {

    /**
     * Constructor privado para evitar la instanciación.
     */
    private DatabaseCleaner() {
    }

    /**
     * Elimina todos los registros de las tablas de negocio. La operación se
     * ejecuta dentro de una transacción para garantizar la integridad. Si
     * alguna eliminación falla, se revierten todos los cambios.
     */
    public static void cleanBusinessData() {
        // Lista de tablas de las que se eliminarán los datos.
        // El orden puede ser importante si no se desactivan las claves foráneas.
        String[] tables = {
            "movimientos", "detallecarreras", "apuestas",
            "carreras", "caballos", "apostadores"
        };

        Connection conn = null;
        try {
            // Se obtiene la conexión desde la fuente centralizada.
            conn = DataSource.getConnection();

            // Se inicia la transacción.
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                System.out.println("Iniciando limpieza de datos...");
                for (String table : tables) {
                    String sql = "DELETE FROM " + table;
                    int rowsAffected = stmt.executeUpdate(sql);
                    System.out.println("Se eliminaron " + rowsAffected + " filas de la tabla: " + table);

                    // En SQLite, también se puede reiniciar la secuencia autoincremental.
                    // Esto es opcional y puede ser útil para empezar desde cero.
                    stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='" + table + "';");
                }
            }

            // Si todas las operaciones fueron exitosas, se confirman los cambios.
            conn.commit();
            System.out.println("Limpieza de datos completada exitosamente. Todos los cambios han sido confirmados.");
            JOptionPane.showMessageDialog(null, "Todos los datos han sido eliminados correctamente.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            System.err.println("Error durante la limpieza de datos. Revirtiendo cambios. Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al eliminar los datos. Se revirtieron los cambios.", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            // Si hubo un error, se revierten todos los cambios hechos en esta transacción.
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error crítico al intentar revertir la transacción: " + ex.getMessage());
            }
        } finally {
            // Se asegura de que la conexión vuelva a su estado normal y se cierre.
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }

    /**
     * Permite ejecutar la limpieza como un script independiente.
     */
    public static void main(String[] args) {
        int response = JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro de que desea eliminar TODOS los datos de las tablas?\nEsta acción es irreversible.",
                "Confirmación Requerida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            cleanBusinessData();
        } else {
            System.out.println("Operación de limpieza cancelada por el usuario.");
        }
    }
}
