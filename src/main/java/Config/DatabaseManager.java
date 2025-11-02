package Config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestiona la inicialización y migración del esquema de la base de datos. Debe
 * ser llamado una sola vez al inicio de la aplicación.
 */
public class DatabaseManager {

    /**
     * Método principal que inicializa la base de datos. Crea las tablas si no
     * existen y aplica las migraciones necesarias.
     */
    // En Config/DatabaseManager.java
    public static void initializeDatabase() {
        String sqlScript = getSchemaCreationScript();

        // Se divide el script completo en sentencias individuales usando el punto y coma.
        String[] statements = sqlScript.split(";");

        try (Connection conn = DataSource.getConnection(); Statement stmt = conn.createStatement()) {

            // Se ejecuta cada sentencia una por una.
            for (String sql : statements) {
                // Se asegura de no ejecutar sentencias vacías.
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql);
                }
            }
            System.out.println("Esquema de base de datos verificado/creado correctamente.");

            // Se ejecutan las migraciones de la misma forma.
            applyMigrations(stmt);

            System.out.println("Base de datos inicializada en: " + AppPaths.BASE_DIR);

        } catch (SQLException e) {
            System.err.println("Error crítico al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Contiene el script SQL para crear el esquema inicial de la base de datos.
     *
     * @return un String con todas las sentencias CREATE TABLE e INSERT
     * iniciales.
     */
    // En Config/DatabaseManager.java
    private static String getSchemaCreationScript() {
        // Este es el script final y unificado para la creación de la base de datos.
        return """
        -- Crear catálogos
        CREATE TABLE IF NOT EXISTS estados (idestados INTEGER PRIMARY KEY, estados TEXT);
        CREATE TABLE IF NOT EXISTS estadopago (idestadopago INTEGER PRIMARY KEY, descripcion TEXT UNIQUE NOT NULL);
        CREATE TABLE IF NOT EXISTS tipomovimientos (idtipomovimientos INTEGER PRIMARY KEY, descripcion TEXT NOT NULL UNIQUE);

        -- Tablas principales
        CREATE TABLE IF NOT EXISTS apostadores (
            idapostadores INTEGER PRIMARY KEY,
            cedula TEXT,
            nombre TEXT,
            saldo INTEGER DEFAULT 0,
            observacion TEXT,
            fk_estados INTEGER REFERENCES estados(idestados)
        );

        CREATE TABLE IF NOT EXISTS caballos (
            idcaballos INTEGER PRIMARY KEY,
            caballos TEXT,
            jinete TEXT,
            observacion TEXT,
            fk_estados INTEGER REFERENCES estados(idestados)
        );

        CREATE TABLE IF NOT EXISTS carreras (
            idcarreras INTEGER PRIMARY KEY,
            nombre TEXT,
            lugar TEXT,
            fecha DATE, -- Tipo de dato correcto
            idganador INTEGER,
            observacion TEXT,
            fk_estados INTEGER REFERENCES estados(idestados)
            -- fechalimite DATE, -- Columna añadida
            -- comision INTEGER DEFAULT 10 -- Columna añadida
        );

        CREATE TABLE IF NOT EXISTS apuestas (
            idapuestas INTEGER PRIMARY KEY,
            apuesta TEXT,
            monto INTEGER,
            fecha DATE,
            observacion TEXT,
            saldousado BOOLEAN, -- Tipo de dato correcto
            fk_carreras INTEGER REFERENCES carreras(idcarreras),
            fk_caballos INTEGER REFERENCES caballos(idcaballos),
            fk_apostadores INTEGER REFERENCES apostadores(idapostadores),
            fk_estados INTEGER REFERENCES estados(idestados),
            fechalimite DATE,
            fk_estadopago INTEGER DEFAULT 1 REFERENCES estadopago(idestadopago)
            -- abonado INTEGER DEFAULT 0 -- Columna añadida
        );

        CREATE TABLE IF NOT EXISTS detallecarreras (
            iddetallecarreras INTEGER PRIMARY KEY,
            fk_carreras INTEGER REFERENCES carreras(idcarreras),
            fk_caballos INTEGER REFERENCES caballos(idcaballos)
        );

        CREATE TABLE IF NOT EXISTS movimientos (
            idmovimientos INTEGER PRIMARY KEY AUTOINCREMENT,
            fecha DATE, -- Tipo DATE para compatibilidad
            monto INTEGER,
            descripcion TEXT,
            fk_apostadores INTEGER REFERENCES apostadores(idapostadores),
            fk_apuestas INTEGER REFERENCES apuestas(idapuestas),
            fk_tipomovimientos INTEGER NOT NULL DEFAULT 1 REFERENCES tipomovimientos(idtipomovimientos),
            fk_carreras INTEGER REFERENCES carreras(idcarreras)
        );

        -- Insertar valores iniciales en catálogos (seeding)
        INSERT OR IGNORE INTO estados (idestados, estados) VALUES (1, 'activo'), (2, 'inactivo');
        INSERT OR IGNORE INTO estadopago (idestadopago, descripcion) VALUES (1, 'pendiente'), (2, 'parcial'), (3, 'pagada'), (4, 'cancelada');
        INSERT OR IGNORE INTO tipomovimientos (idtipomovimientos, descripcion) VALUES (1, 'Depósito'), (2, 'Retiro'), (3, 'Abono Parcial'), (4, 'Abono Total'), (5, 'Ganancia Neta'), (6, 'Devolución'), (7, 'Apuesta Perdida');
        
        -- Insertar datos de ejemplo
        INSERT OR IGNORE INTO apostadores (idapostadores, nombre, fk_estados) VALUES (1, 'Rey Galeano', 1), (2, 'Emanuel Escobar', 1), (3, 'Juan Paiva', 1), (4, 'Cliente MB', 1), (5, 'Joel Gómez', 1), (6, 'Diego Rojas', 1), (7, 'Carlile Baez', 1), (8, 'Juan Maidana', 1), (9, 'Vicente Gonzalez', 1), (10, 'Alfredo Arrua', 1), (11, 'Alcides Centurion', 1), (12, 'Ruben Davalos', 1), (13, 'Carlos Reynaga', 1), (14, 'Cever Vigo', 1), (15, 'Cesar Riveros', 1), (16, 'Carlos Ojeda', 1);
        INSERT OR IGNORE INTO caballos (idcaballos, caballos, fk_estados) VALUES (1, 'Tord. Valtra', 1), (2, 'Tord. Britez', 1), (3, 'Guarani', 1), (4, 'Tembetary', 1), (5, 'Tord. Aduanero', 1), (6, 'Potrillazo', 1), (7, 'Black Good Dash', 1), (8, 'Brazilian Seis', 1), (9, 'Zidane Perfect', 1), (10, 'Galaxy Verde', 1), (11, 'Atacama Fast', 1), (12, 'Golden Verde', 1), (13, 'Evina Cartel', 1), (14, 'Tropa', 1);
        """;
    }

    /**
     * Aplica las migraciones necesarias a la base de datos (ej. añadir nuevas
     * columnas).
     *
     * @param stmt El Statement para ejecutar las consultas.
     * @throws SQLException si ocurre un error.
     */
    private static void applyMigrations(Statement stmt) throws SQLException {
        // Migración 1: Añadir columna 'comision' a 'carreras'
        if (!columnExists(stmt, "carreras", "comision")) {
            stmt.execute("ALTER TABLE carreras ADD COLUMN comision INTEGER DEFAULT 10;");
            System.out.println("Migración aplicada: Se agregó la columna 'comision' a la tabla 'carreras'.");
        }

        // Migración 2: Añadir columna 'fechalimite' a 'carreras'
        if (!columnExists(stmt, "carreras", "fechalimite")) {
            stmt.execute("ALTER TABLE carreras ADD COLUMN fechalimite DATE;");
            System.out.println("Migración aplicada: Se agregó la columna 'fechalimite' a la tabla 'carreras'.");
        }

        // Migración 3: Añadir columna 'abonado' a 'apuestas'
        if (!columnExists(stmt, "apuestas", "abonado")) {
            stmt.execute("ALTER TABLE apuestas ADD COLUMN abonado INTEGER DEFAULT 0;");
            System.out.println("Migración aplicada: Se agregó la columna 'abonado' a la tabla 'apuestas'.");
        }

        // Futuras migraciones se pueden añadir aquí de la misma forma.
    }

    /**
     * Verifica si una columna ya existe en una tabla para evitar errores de
     * migración.
     *
     * @param stmt El Statement para ejecutar la consulta.
     * @param tableName El nombre de la tabla.
     * @param columnName El nombre de la columna.
     * @return true si la columna existe, false en caso contrario.
     * @throws SQLException si ocurre un error.
     */
    private static boolean columnExists(Statement stmt, String tableName, String columnName) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tableName + ");")) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }
}
