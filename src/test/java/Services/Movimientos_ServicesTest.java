package Services;

import Model.Carreras_Model;
import Model.Movimientos_Model;
import Repository.Apostadores_Repository;
import Repository.Apuestas_Repository;
import Repository.Carreras_Repository;
import Repository.Movimientos_Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Movimientos_ServicesTest {

    // Se simulan TODOS los repositorios de los que depende el servicio.
    @Mock
    private Movimientos_Repository movimientosRepository;
    @Mock
    private Apostadores_Repository apostadoresRepository;
    @Mock
    private Apuestas_Repository apuestasRepository;
    @Mock
    private Carreras_Repository carrerasRepository;

    @InjectMocks // Se crea una instancia real de Movimientos_Services con los mocks de arriba.
    private Movimientos_Services movimientosServices;

    @Test
    void procesarResultados_ConUnGanadorYVariosPerdedores_CalculaTodoCorrectamente() throws Exception {
        // --- 1. Arrange (Preparar el escenario de prueba) ---

        // Datos de la carrera
        int idCarrera = 1;
        int idCaballoGanador = 101;
        Carreras_Model carrera = new Carreras_Model(idCarrera, "Clásico", "Hipódromo", LocalDate.now(), LocalDate.now(), idCaballoGanador, "", 1, 10);

        // Datos de las apuestas (quién apostó a qué)
        List<Apuestas_Repository.ResultadoPorApostadorDTO> calculosPrevios = List.of(
                new Apuestas_Repository.ResultadoPorApostadorDTO(1, 50000, 20000), // Apostador 1: 50k al ganador, 20k a un perdedor
                new Apuestas_Repository.ResultadoPorApostadorDTO(2, 0, 30000), // Apostador 2: 30k a un perdedor
                new Apuestas_Repository.ResultadoPorApostadorDTO(3, 0, 40000) // Apostador 3: 40k a un perdedor
        );

        // Se le dice a los mocks cómo deben responder
        when(carrerasRepository.findById(any(Connection.class), eq(idCarrera))).thenReturn(carrera);
        when(apuestasRepository.findResultadosPorApostador(any(Connection.class), eq(idCarrera))).thenReturn(calculosPrevios);

        // El pozo de perdedores es solo la suma de las apuestas de los que no ganaron (30k + 40k = 70k)
        when(apuestasRepository.getPoolPerdedores(any(Connection.class), eq(idCarrera))).thenReturn(70000L);
        // El total apostado al caballo ganador fue 50k
        when(apuestasRepository.getTotalApostadoAlGanador(any(Connection.class), eq(idCarrera))).thenReturn(50000L);

        // --- 2. Act (Ejecutar el método a probar) ---
        assertDoesNotThrow(() -> movimientosServices.procesarYGuardarResultadosDeCarrera(idCarrera));

        // --- 3. Assert/Verify (Verificar que el servicio hizo lo correcto) ---
        // Se verifica que se intentaron borrar los movimientos antiguos
        verify(movimientosRepository).deleteMovimientosDeResultadosPorCarrera(any(Connection.class), eq(idCarrera));

        // Se captura la lista de movimientos que el servicio intentó guardar
        ArgumentCaptor<List<Movimientos_Model>> captorMovimientos = ArgumentCaptor.forClass(List.class);
        verify(movimientosRepository).insertBatch(any(Connection.class), captorMovimientos.capture());

        List<Movimientos_Model> movimientosGenerados = captorMovimientos.getValue();
        assertEquals(5, movimientosGenerados.size(), "Deberían generarse 5 movimientos en total.");

        // Se verifica que se actualizó el saldo de cada apostador con el monto correcto
        // Apostador 1 (Ganador): 50k (devolución) + 20k (devolución obviada) + 63k (premio) = 133k total. Saldo cambia en +113k.
        // Pozo: 70k. Comisión: 7k. Premio: 63k.
        verify(apostadoresRepository).updateSaldo(any(Connection.class), eq(1), eq(113000)); // 50k devueltos + 63k de premio
        // Apostador 2 (Perdedor): -30k
        verify(apostadoresRepository).updateSaldo(any(Connection.class), eq(2), eq(-30000));
        // Apostador 3 (Perdedor): -40k
        verify(apostadoresRepository).updateSaldo(any(Connection.class), eq(3), eq(-40000));
    }
}
