package Controller;

import Model.Backup_DTO;
import Services.Exceptions.ServiceException;
import Utils.SQLiteBackupManager; // Se usa el paquete correcto 'Utils'

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Controlador para gestionar las operaciones de backup y restauración.
 */
public class Backup_Controller {

    public List<Backup_DTO> listarBackups() {
        List<Backup_DTO> backupsDTO = new ArrayList<>();
        // CORRECCIÓN: Se usa directamente la constante Path.
        File backupDir = SQLiteBackupManager.BACKUP_DIR.toFile();
        if (!backupDir.exists()) {
            return backupsDTO;
        }

        File[] backupFiles = backupDir.listFiles((dir, name) -> name.startsWith("backup_") && name.endsWith(".db"));
        if (backupFiles == null) {
            return backupsDTO;
        }

        Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified).reversed());

        for (File file : backupFiles) {
            LocalDateTime fecha = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
            double tamanoMb = file.length() / (1024.0 * 1024.0);
            backupsDTO.add(new Backup_DTO(file.getName(), fecha, tamanoMb));
        }
        return backupsDTO;
    }

    public void restaurarBackup(String nombreBackup) throws ServiceException {
        // CORRECCIÓN: Para unir un Path y un String, se usa .resolve()
        Path backupPath = SQLiteBackupManager.BACKUP_DIR.resolve(nombreBackup);
        // CORRECCIÓN: DB_PATH ya es un objeto Path, se usa directamente.
        Path dbPath = SQLiteBackupManager.DB_PATH;

        if (!Files.exists(backupPath)) {
            throw new ServiceException("El archivo de backup seleccionado no existe.");
        }

        try {
            Files.copy(backupPath, dbPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ServiceException("Error de E/S al restaurar el backup: " + e.getMessage(), e);
        }
    }

    public void eliminarBackup(String nombreBackup) throws ServiceException {
        // CORRECCIÓN: Se usa .resolve() para unir un Path y un String.
        Path backupPath = SQLiteBackupManager.BACKUP_DIR.resolve(nombreBackup);
        try {
            if (!Files.deleteIfExists(backupPath)) {
                throw new ServiceException("No se pudo eliminar el backup porque no se encontró.");
            }
        } catch (IOException e) {
            throw new ServiceException("Error de E/S al eliminar el backup: " + e.getMessage(), e);
        }
    }
}
