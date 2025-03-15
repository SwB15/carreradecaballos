package Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TruncateTables {
    public static void deleteAllData() {
        String url = "jdbc:sqlite:carreradecaballos.db";

        String[] tables = {"apostadores", "caballos", "apuestas", "carreras", "detallecarreras"};

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            for (String table : tables) {
                String sql = "DELETE FROM " + table;
                stmt.executeUpdate(sql);
            }

            System.out.println("Todos los datos han sido eliminados correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        deleteAllData();
    }
}
