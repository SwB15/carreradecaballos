package Config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author SwichBlade15
 */
public class DataSource {

    private static final String BD = "carreradecaballos.db";
    private static final String URL = "jdbc:sqlite:" + new File(BD).getAbsolutePath();

    static {
        try {
            Class.forName("org.sqlite.JDBC"); // Cargar el driver de SQLite
        } catch (ClassNotFoundException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    // Método para crear la base de datos y las tablas si no existen
    public static void createDatabase() {
        String sql = """
    CREATE TABLE IF NOT EXISTS estados (
        idestados INTEGER PRIMARY KEY,
        estados TEXT
    );

    CREATE TABLE IF NOT EXISTS apostadores (
        idapostadores INTEGER PRIMARY KEY,
        cedula TEXT,
        nombre TEXT,
        observacion TEXT,
        fk_estados INTEGER,
        FOREIGN KEY (fk_estados) REFERENCES estados(idestados)
    );

    CREATE TABLE IF NOT EXISTS caballos (
        idcaballos INTEGER PRIMARY KEY,
        caballos TEXT,
        jinete TEXT,
        observacion TEXT,
        fk_estados INTEGER,
        FOREIGN KEY (fk_estados) REFERENCES estados(idestados)
    );

    CREATE TABLE IF NOT EXISTS carreras (
        idcarreras INTEGER PRIMARY KEY,
        nombre TEXT,
        lugar TEXT,
        fecha TEXT,
        idganador INTEGER,
        observacion TEXT,
        fk_estados INTEGER,
        FOREIGN KEY (fk_estados) REFERENCES estados(idestados)
    );

    CREATE TABLE IF NOT EXISTS apuestas (
        idapuestas INTEGER PRIMARY KEY,
        apuesta TEXT,
        monto INTEGER,
        abonado INTEGER,
        fecha DATE,
        observacion TEXT,
        fk_carreras INTEGER,
        fk_caballos INTEGER,
        fk_apostadores INTEGER,
        fk_estados INTEGER,
        fechalimite DATE,
        FOREIGN KEY (fk_carreras) REFERENCES carreras(idcarreras),
        FOREIGN KEY (fk_caballos) REFERENCES caballos(idcaballos),
        FOREIGN KEY (fk_apostadores) REFERENCES apostadores(idapostadores),
        FOREIGN KEY (fk_estados) REFERENCES estados(idestados)
    );

    CREATE TABLE IF NOT EXISTS detallecarreras (
        iddetallecarreras INTEGER PRIMARY KEY,
        fk_carreras INTEGER,
        fk_caballos INTEGER,
        FOREIGN KEY (fk_carreras) REFERENCES carreras(idcarreras),
        FOREIGN KEY (fk_caballos) REFERENCES caballos(idcaballos)
    );
    """;

        String insertEstados = """
    INSERT OR IGNORE INTO estados (idestados, estados) VALUES 
    (1, 'activo'), 
    (2, 'inactivo');
    """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            stmt.executeUpdate(insertEstados);
            System.out.println("Base de datos y tablas creadas exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al crear la base de datos: " + e.getMessage());
        }
    }
}
