package Services;

import Model.Apuestas_Model;
import Model.EstadoApuesta;
import Repository.Apuestas_Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Apuestas_ServicesTest {

    @Mock
    private Apuestas_Repository repository; // Se necesita el mock para el constructor

    private Apuestas_Services services;

    @BeforeEach
    void setUp() {
        // Se crea una nueva instancia del servicio antes de cada prueba
        services = new Apuestas_Services(repository);
    }

    @Test
    void determinarEstado_CuandoFechaLimitePasoYNoEstaPagado_RetornaVencido() {
        // --- 1. Arrange (Preparar el escenario) ---
        Apuestas_Model apuesta = new Apuestas_Model();
        apuesta.setMonto(100000);
        apuesta.setAbonado(50000);
        // Se establece una fecha límite de hace 5 días.
        apuesta.setFechaLimite(LocalDate.now().minusDays(5));

        // --- 2. Act (Ejecutar el método) ---
        EstadoApuesta resultado = services.determinarEstado(apuesta);

        // --- 3. Assert (Verificar el resultado) ---
        assertEquals(EstadoApuesta.VENCIDO, resultado, "La apuesta debería estar VENCIDA.");
    }

    @Test
    void determinarEstado_CuandoEstaTotalmentePagado_RetornaPagado() {
        // --- Arrange ---
        Apuestas_Model apuesta = new Apuestas_Model();
        apuesta.setMonto(100000);
        apuesta.setAbonado(100000);
        // La fecha límite aún no ha pasado.
        apuesta.setFechaLimite(LocalDate.now().plusDays(5));

        // --- Act ---
        EstadoApuesta resultado = services.determinarEstado(apuesta);

        // --- Assert ---
        assertEquals(EstadoApuesta.PAGADO, resultado, "La apuesta debería estar PAGADA.");
    }

    @Test
    void determinarEstado_CuandoEsParcialYFechaNoHaVencido_RetornaPendiente() {
        // --- Arrange ---
        Apuestas_Model apuesta = new Apuestas_Model();
        apuesta.setMonto(100000);
        apuesta.setAbonado(50000);
        apuesta.setFechaLimite(LocalDate.now().plusDays(5));

        // --- Act ---
        EstadoApuesta resultado = services.determinarEstado(apuesta);

        // --- Assert ---
        assertEquals(EstadoApuesta.PENDIENTE, resultado, "La apuesta debería estar PENDIENTE.");
    }

    @Test
    void determinarEstado_CuandoFechaLimitePasoHaceMucho_RetornaArchivado() {
        // --- Arrange ---
        Apuestas_Model apuesta = new Apuestas_Model();
        apuesta.setMonto(100000);
        apuesta.setAbonado(50000);
        // Se establece una fecha límite de hace 10 días.
        apuesta.setFechaLimite(LocalDate.now().minusDays(10));

        // --- Act ---
        EstadoApuesta resultado = services.determinarEstado(apuesta);

        // --- Assert ---
        assertEquals(EstadoApuesta.ARCHIVADO, resultado, "La apuesta debería estar ARCHIVADA.");
    }
}
