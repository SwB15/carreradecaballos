package Services;

import Model.Caballos_Model;
import Repository.Caballos_Repository;
import Services.Exceptions.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Caballos_ServicesTest {

    @Mock
    private Caballos_Repository repository;

    @InjectMocks
    private Caballos_Services services;

    @Test
    void crearCaballo_ConDatosValidos_LlamaAlRepositorioCorrectamente() {
        // --- 1. Arrange (Preparar) ---
        Caballos_Model nuevoCaballo = new Caballos_Model(0, "Relámpago", "Juan Pérez", "Veloz", 1);

        // No necesitamos 'when().thenReturn()' porque el método 'insert' del repo es void.
        // --- 2. Act (Ejecutar) ---
        assertDoesNotThrow(() -> services.crearCaballo(nuevoCaballo));

        // --- 3. Verify (Verificar) ---
        try {
            // Se verifica que el servicio llamó al método 'insert' del repositorio
            // exactamente una vez, pasándole el objeto 'nuevoCaballo'.
            verify(repository).insert(eq(nuevoCaballo));
        } catch (SQLException e) {
            // La firma del método del mock puede lanzar SQLException, por eso el try-catch.
        }
    }

    @Test
    void crearCaballo_CuandoRepositorioFalla_LanzaServiceException() throws SQLException {
        // --- Arrange ---
        Caballos_Model nuevoCaballo = new Caballos_Model(0, "Relámpago", "Juan Pérez", "Veloz", 1);

        // Se configura el mock para que lance una SQLException cuando se intente insertar.
        doThrow(new SQLException("Error de conexión simulado")).when(repository).insert(eq(nuevoCaballo));

        // --- Act & Assert ---
        // Se verifica que el servicio captura la SQLException y lanza nuestra ServiceException.
        assertThrows(ServiceException.class, () -> {
            services.crearCaballo(nuevoCaballo);
        });
    }
}
