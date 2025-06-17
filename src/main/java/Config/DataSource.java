package Config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author SwichBlade15
 */
public class DataSource {

//    private static final String BD = "carreradecaballos";
//    private static final String LOGIN = "root";
//    private static final String PASSWORD = "";
//    private static final String URL = "jdbc:mysql://localhost:3306/" + BD;
//    static {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException ex) {
//            throw new ExceptionInInitializerError(ex);
//        }
//    }
//
//    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(URL, LOGIN, PASSWORD);
//    }
//
//    public static void closeConnection(Connection connection) {
//        if (connection != null) {
//            try {
//                connection.close();
//            } catch (SQLException ex) {
//                System.out.println(ex.getMessage());
//            }
//        }
//    }
    private static final String BD = "carreradecaballos.db";
    private static final String DIR = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "CarreraDeCaballos";
    private static final String URL = "jdbc:sqlite:" + DIR + File.separator + BD;

    static {
        try {
            // Crear la carpeta si no existe
            File dir = new File(DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

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

    public static void createDatabase() {
        // Prepara un bloque de sentencias SQL separado por ';'
        String sqlScript = """
        PRAGMA foreign_keys = ON;

        -- Crear catálogos
        CREATE TABLE IF NOT EXISTS estados (
            idestados INTEGER PRIMARY KEY,
            estados   TEXT
        );

        CREATE TABLE IF NOT EXISTS estadopago (
            idestadopago INTEGER PRIMARY KEY,
            descripcion  TEXT UNIQUE NOT NULL
        );

        CREATE TABLE IF NOT EXISTS tipomovimientos (
            idtipomovimientos INTEGER PRIMARY KEY,
            descripcion       TEXT NOT NULL UNIQUE
        );

        -- Tablas principales
        CREATE TABLE IF NOT EXISTS apostadores (
            idapostadores INTEGER PRIMARY KEY,
            cedula        TEXT,
            nombre        TEXT,
            saldo         INTEGER DEFAULT 0,
            observacion   TEXT,
            fk_estados    INTEGER
                REFERENCES estados(idestados)
        );

        CREATE TABLE IF NOT EXISTS caballos (
            idcaballos   INTEGER PRIMARY KEY,
            caballos     TEXT,
            jinete       TEXT,
            observacion  TEXT,
            fk_estados   INTEGER
                REFERENCES estados(idestados)
        );

        CREATE TABLE IF NOT EXISTS carreras (
            idcarreras  INTEGER PRIMARY KEY,
            nombre      TEXT,
            lugar       TEXT,
            fecha       TEXT,
            idganador   INTEGER,
            observacion TEXT,
            fk_estados  INTEGER
                REFERENCES estados(idestados)
        );

        CREATE TABLE IF NOT EXISTS apuestas (
            idapuestas     INTEGER PRIMARY KEY,
            apuesta        TEXT,
            monto          INTEGER,
            fecha          DATE,
            observacion    TEXT,
            saldousado     TEXT,
            fk_carreras    INTEGER
                REFERENCES carreras(idcarreras),
            fk_caballos    INTEGER
                REFERENCES caballos(idcaballos),
            fk_apostadores INTEGER
                REFERENCES apostadores(idapostadores),
            fk_estados     INTEGER
                REFERENCES estados(idestados),
            fechalimite    DATE,
            fk_estadopago  INTEGER DEFAULT 1
                REFERENCES estadopago(idestadopago)
        );

        CREATE TABLE IF NOT EXISTS detallecarreras (
            iddetallecarreras INTEGER PRIMARY KEY,
            fk_carreras       INTEGER
                REFERENCES carreras(idcarreras),
            fk_caballos       INTEGER
                REFERENCES caballos(idcaballos)
        );

        CREATE TABLE IF NOT EXISTS movimientos (
            idmovimientos     INTEGER PRIMARY KEY AUTOINCREMENT,
            fecha             DATE,
            monto             INTEGER,
            descripcion       TEXT,
            fk_apostadores    INTEGER
                REFERENCES apostadores(idapostadores),
            fk_apuestas       INTEGER
                REFERENCES apuestas(idapuestas),
            fk_tipomovimientos INTEGER NOT NULL DEFAULT 1
                REFERENCES tipomovimientos(idtipomovimientos),
            fk_carreras       INTEGER
                REFERENCES carreras(idcarreras)
        );

        -- Insertar valores iniciales en catálogos
        INSERT OR IGNORE INTO estados (idestados, estados) VALUES
            (1, 'activo'),
            (2, 'inactivo');

        INSERT OR IGNORE INTO estadopago (idestadopago, descripcion) VALUES
            (1, 'pendiente'),
            (2, 'parcial'),
            (3, 'pagada'),
            (4, 'cancelada');

        INSERT OR IGNORE INTO tipomovimientos (idtipomovimientos, descripcion) VALUES
            (1, 'Depósito'),
            (2, 'Retiro'),
            (3, 'Apuesta parcial'),
            (4, 'Apuesta completa'),
            (5, 'Apuesta ganada'),
            (6, 'Devolución');

        -- Insertar apostadores de ejemplo (si lo deseas)
        /*INSERT OR IGNORE INTO apostadores (idapostadores, nombre, fk_estados) VALUES
            (1, 'Rey Galeano', 1),
            (2, 'Emanuel Escobar', 1),
            (3, 'Juan Paiva', 1),
            (4, 'Cliente MB', 1),
            (5, 'Joel Gómez', 1),
            (6, 'Diego Rojas', 1),
            (7, 'Carlile Baez', 1),
            (8, 'Juan Maidana', 1),
            (9, 'Vicente Gonzalez', 1),
            (10, 'Alfredo Arrua', 1),
            (11, 'Alcides Centurion', 1),
            (12, 'Ruben Davalos', 1),
            (13, 'Carlos Reynaga', 1),
            (14, 'Cever Vigo', 1),
            (15, 'Cesar Riveros', 1),
            (16, 'Carlos Ojeda', 1);

        INSERT OR IGNORE INTO caballos (idcaballos, caballos, fk_estados) VALUES
            (1, 'Tord. Valtra', 1),
            (2, 'Tord. Britez', 1),
            (3, 'Guarani', 1),
            (4, 'Tembetary', 1),
            (5, 'Tord. Aduanero', 1),
            (6, 'Potrillazo', 1),
            (7, 'Black Good Dash', 1),
            (8, 'Brazilian Seis', 1),
            (9, 'Zidane Perfect', 1),
            (10, 'Galaxy Verde', 1),
            (11, 'Atacama Fast', 1),
            (12, 'Golden Verde', 1),
            (13, 'Evina Cartel', 1),
            (14, 'Tropa', 1);*/
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // SQLite JDBC permite ejecutar múltiples sentencias separadas por ';' en un solo executeUpdate
            stmt.executeUpdate(sqlScript);
            System.out.println("Base de datos SQLite creada/existente correctamente en: " + URL);
        } catch (SQLException e) {
            System.err.println("Error al crear la base de datos SQLite: " + e.getMessage());
        }
    }

}
