package Services;

import Model.HistorialApuesta_DTO;
import Model.ReportesHistorialApostador_Model;
import Repository.Apostadores_Repository;
import Services.Exceptions.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Apostadores_ServicesTest {

    @Mock
    private Apostadores_Repository repository;

    @InjectMocks
    private Apostadores_Services services;

    // El @InjectMocks de arriba es una forma avanzada. Si te da problemas,
    // puedes borrarlo y usar este método @BeforeEach que es más explícito.
    /*
    @BeforeEach
    void setUp() {
        // Se crea la instancia del servicio manualmente antes de cada prueba,
        // inyectándole el repositorio falso (mock).
        services = new Apostadores_Services(repository);
    }
     */
    @Test
    void consultarHistorial_ProcesaResultadosCorrectamente() throws SQLException, ServiceException {
        // --- 1. Arrange (Preparar el escenario) ---
        int idApostador = 1;
        List<HistorialApuesta_DTO> historialCrudo = List.of(
                // Caso 1: Apuesta Ganadora
                new HistorialApuesta_DTO(10, "Carrera 1", LocalDate.now(), "Caballo A", 101, 101, 50000, 10, 200000, 80000),
                // Caso 2: Apuesta Perdedora
                new HistorialApuesta_DTO(11, "Carrera 2", LocalDate.now(), "Caballo B", 102, 103, 30000, 10, 0, 0),
                // Caso 3: Apuesta Pendiente (sin ganador en la carrera)
                new HistorialApuesta_DTO(12, "Carrera 3", LocalDate.now(), "Caballo C", 104, null, 25000, 10, 0, 0)
        );

        // Se le dice al mock que espere los parámetros de fecha (pueden ser nulos o cualquier fecha).
        when(repository.findHistorialData(eq(idApostador), any(), any())).thenReturn(historialCrudo);

        // --- Act ---
        // Se llama al método del servicio con fechas nulas para la prueba (sin filtro de fecha).
        List<ReportesHistorialApostador_Model> resultado = services.consultarHistorial(idApostador, null, null);

        // --- 3. Assert (Verificar el resultado) ---
        assertEquals(3, resultado.size());

        // Verificar Ganador
        assertEquals("Ganador", resultado.get(0).getResultado());
        assertEquals(112500, resultado.get(0).getMonto());

        // Verificar Perdedor
        assertEquals("Perdedor", resultado.get(1).getResultado());
        assertEquals(-30000, resultado.get(1).getMonto());

        // Verificar Pendiente
        assertEquals("Pendiente", resultado.get(2).getResultado());
        assertEquals(25000, resultado.get(2).getMonto());
    }

    @Test
    void consultarHistorial_CuandoRepositorioFalla_LanzaServiceException() throws SQLException {
        // --- Arrange ---
        int idApostador = 1;
        when(repository.findHistorialData(eq(idApostador), any(), any())).thenThrow(new SQLException("Error de conexión simulado"));

        assertThrows(ServiceException.class, () -> {
            // Se llama al método con fechas nulas
            services.consultarHistorial(idApostador, null, null);
        });
    }
}
