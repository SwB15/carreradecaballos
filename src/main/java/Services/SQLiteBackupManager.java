package Services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 *
 * @author SwichBlade15
 */
public class SQLiteBackupManager {

    public static final String BASE_DIR = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "CarreraDeCaballos";
    public static final String DB_PATH = BASE_DIR + File.separator + "carreradecaballos.db"; // Ruta de la BD
    public static final String BACKUP_DIR = BASE_DIR + File.separator + "backups"; // Carpeta de backups
    private static final int MAX_BACKUPS = 7;

    public static void main(String[] args) {
        crearBackup();
        limpiarBackups();
    }

    public static void crearBackup() {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        // Formato de fecha para el backup (ejemplo: backup_2025-03-16.db)
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File backupFile = new File(BACKUP_DIR + File.separator + "backup_" + fecha + ".db");

        if (backupFile.exists()) {
            System.out.println("El backup de hoy ya existe: " + backupFile.getAbsolutePath());
            return;
        }

        try {
            Files.copy(new File(DB_PATH).toPath(), backupFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            System.out.println("Backup creado en: " + backupFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error al crear el backup: " + e.getMessage());
        }
    }

    public static void limpiarBackups() {
        File backupDir = new File(BACKUP_DIR);
        File[] backups = backupDir.listFiles((dir, name) -> name.startsWith("backup_") && name.endsWith(".db"));

        if (backups == null || backups.length <= MAX_BACKUPS) {
            return;
        }

        // Ordenar backups por fecha (más antiguos primero)
        Arrays.sort(backups, (a, b) -> Long.compare(a.lastModified(), b.lastModified()));

        // Borrar los más antiguos si hay más de MAX_BACKUPS
        while (backups.length > MAX_BACKUPS) {
            File oldestBackup = backups[0];
            if (oldestBackup.delete()) {
                System.out.println("Backup eliminado: " + oldestBackup.getAbsolutePath());
            }
            backups = Arrays.copyOfRange(backups, 1, backups.length);
        }
    }
}
