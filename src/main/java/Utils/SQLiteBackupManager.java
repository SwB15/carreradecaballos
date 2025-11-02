package Utils;

import Config.AppPaths; // Se utiliza la clase centralizada de rutas
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Gestiona la creación y limpieza de backups de la base de datos SQLite.
 */
public final class SQLiteBackupManager {

    private static final String DB_FILENAME = "carreradecaballos.db";
    public static final Path DB_PATH = Paths.get(AppPaths.BASE_DIR, DB_FILENAME);
    public static final Path BACKUP_DIR = Paths.get(AppPaths.BASE_DIR, "backups");
    private static final int MAX_BACKUPS = 7; // Mantener backups de los últimos 7 días

    /**
     * Constructor privado para evitar la instanciación.
     */
    private SQLiteBackupManager() {
    }

    /**
     * Crea un backup de la base de datos con la fecha actual si no existe uno
     * para hoy.
     */
    public static void crearBackup() {
        try {
            // Se asegura de que el directorio de backups exista.
            Files.createDirectories(BACKUP_DIR);

            // Se usa la API moderna de fecha y hora.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String fecha = LocalDate.now().format(formatter);
            Path backupFile = BACKUP_DIR.resolve("backup_" + fecha + ".db");

            if (Files.exists(backupFile)) {
                System.out.println("El backup de hoy ya existe: " + backupFile);
                return;
            }

            if (Files.exists(DB_PATH)) {
                Files.copy(DB_PATH, backupFile, StandardCopyOption.COPY_ATTRIBUTES);
                System.out.println("Backup creado exitosamente en: " + backupFile);
            } else {
                System.err.println("No se pudo crear el backup. La base de datos no se encontró en: " + DB_PATH);
            }
        } catch (IOException e) {
            System.err.println("Error al crear el backup: " + e.getMessage());
        }
    }

    /**
     * Limpia los backups antiguos, conservando solo los más recientes según
     * MAX_BACKUPS.
     */
    public static void limpiarBackups() {
        try {
            if (!Files.exists(BACKUP_DIR)) {
                return;
            }

            // Se listan los archivos de backup y se ordenan del más antiguo al más nuevo.
            File[] backups = BACKUP_DIR.toFile().listFiles((dir, name) -> name.startsWith("backup_") && name.endsWith(".db"));

            if (backups == null || backups.length <= MAX_BACKUPS) {
                return; // No hay suficientes backups para limpiar.
            }

            Arrays.sort(backups, Comparator.comparingLong(File::lastModified));

            // Se calcula cuántos backups hay que borrar.
            int backupsAEliminar = backups.length - MAX_BACKUPS;
            for (int i = 0; i < backupsAEliminar; i++) {
                if (backups[i].delete()) {
                    System.out.println("Backup antiguo eliminado: " + backups[i].getName());
                } else {
                    System.err.println("No se pudo eliminar el backup antiguo: " + backups[i].getName());
                }
            }
        } catch (Exception e) {
            System.err.println("Error durante la limpieza de backups: " + e.getMessage());
        }
    }
}
