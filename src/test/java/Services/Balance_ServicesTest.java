package Services;

import Model.ResultadosCarrera_DTO;
import Repository.Balance_Repository;
import Services.Exceptions.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Balance_ServicesTest {

    @Mock
    private Balance_Repository repository;

    @InjectMocks
    private Balance_Services services;

    @Test
    void consultarResultados_CuandoExistenResultados_DevuelveListaCorrecta() throws SQLException, ServiceException {
        // --- 1. Arrange (Preparar el escenario) ---
        int idCarrera = 1;

        // Se crea una lista de DTOs de resultado que simula la respuesta del repositorio.
        List<ResultadosCarrera_DTO> listaDeResultados = List.of(
                new ResultadosCarrera_DTO(1, "Apostador A", "123", 50000, 70000, 7000, 113000),
                new ResultadosCarrera_DTO(2, "Apostador B", "456", 30000, 70000, 0, -30000)
        );

        // Se le dice al mock que, cuando se le pida buscar los resultados de la carrera 1, devuelva nuestra lista.
        when(repository.findResultadosPorCarrera(eq(idCarrera))).thenReturn(listaDeResultados);

        // --- 2. Act (Ejecutar el método) ---
        List<ResultadosCarrera_DTO> resultado = services.consultarResultados(idCarrera);

        // --- 3. Assert (Verificar el resultado) ---
        assertNotNull(resultado);
        assertEquals(2, resultado.size(), "La lista devuelta debe contener 2 resultados.");
        assertEquals("Apostador A", resultado.get(0).getNombreApostador());
        assertEquals(-30000, resultado.get(1).getResultadoFinal());
    }

    @Test
    void consultarResultados_CuandoRepositorioFalla_LanzaServiceException() throws SQLException {
        // --- Arrange ---
        int idCarrera = 1;
        when(repository.findResultadosPorCarrera(eq(idCarrera))).thenThrow(new SQLException("Error de BD simulado"));

        // --- Act & Assert ---
        assertThrows(ServiceException.class, () -> {
            services.consultarResultados(idCarrera);
        });
    }
}
