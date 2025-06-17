package Config;

import java.io.File;

/**
 *
 * @author SwichBlade15
 */
public class AppPaths {

    public static final String BASE_DIR
            = System.getProperty("user.home")
            + File.separator + "AppData"
            + File.separator + "Local"
            + File.separator + "CarreraDeCaballos";

    // Ruta a Reports
    public static final String REPORTS_DIR
            = BASE_DIR + File.separator + "Reports";

    private AppPaths() {
        /* evita instanciación */ }
}
