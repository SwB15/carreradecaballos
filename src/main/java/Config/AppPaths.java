package Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author SwichBlade15
 */
public class AppPaths {

    public static final String BASE_DIR = System.getProperty("user.home")
            + File.separator + "AppData"
            + File.separator + "Local"
            + File.separator + "CarreraDeCaballos";

    public static final String REPORTS_DIR = BASE_DIR + File.separator + "Reports";

    // Bloque estático que se ejecuta una sola vez al cargar la clase.
    static {
        try {
            // Crea el directorio base y todos los directorios padres si no existen.
            Files.createDirectories(Paths.get(BASE_DIR));
            // Crea el directorio de reportes.
            Files.createDirectories(Paths.get(REPORTS_DIR));
        } catch (IOException e) {
            System.err.println("Error al crear directorios de la aplicación: " + e.getMessage());
        }
    }

    /**
     * Constructor privado para evitar la instanciación de la clase.
     */
    private AppPaths() {
        // Esta clase no debe ser instanciada.
    }
}
