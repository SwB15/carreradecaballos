package Services;

import Model.Apuestas_DTO;
import Model.Apuestas_Model;
import Model.Carreras_Model;
import Model.Detallecarreras_Model;
import Repository.Apuestas_Repository;
import Repository.Carreras_Repository;
import Repository.Detallecarreras_Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Carreras_ServicesTest {

    // Se simulan todos los repositorios y servicios de los que depende Carreras_Services
    @Mock
    private Carreras_Repository carrerasRepository;
    @Mock
    private Detallecarreras_Repository detalleRepository;
    @Mock
    private Apuestas_Repository apuestasRepository;
    @Mock
    private Movimientos_Services movimientosServices;

    @InjectMocks // Se crea una instancia real de Carreras_Services con los mocks
    private Carreras_Services carrerasServices;

    @Test
    void crearCarreraCompleta_ConDatosValidos_LlamaATodosLosRepositoriosCorrectamente() throws Exception {
        // --- 1. Arrange (Preparar el escenario) ---

        // Datos de entrada que vendrían de la Vista
        Carreras_Model nuevaCarrera = new Carreras_Model(0, "Gran Premio", "", LocalDate.now(), LocalDate.now().plusDays(5), null, "Observación", 1, 10);
        List<Apuestas_DTO> listaApuestasDTO = List.of(
                new Apuestas_DTO(null, 101, "Caballo A", 50000, 50000, 1, "Apostador 1", 200000, 200000, 50000),
                new Apuestas_DTO(null, 102, "Caballo B", 30000, 0, 2, "Apostador 2", 150000, 150000, 0)
        );

        // Se le dice al mock del repositorio que cuando se inserte la carrera, devuelva el ID "50".
        when(carrerasRepository.insertAndGetId(any(Connection.class), eq(nuevaCarrera))).thenReturn(50);

        // --- 2. Act (Ejecutar el método a probar) ---
        assertDoesNotThrow(() -> carrerasServices.crearCarreraCompleta(nuevaCarrera, listaApuestasDTO));

        // --- 3. Assert/Verify (Verificar que el servicio hizo lo correcto) ---
        // Verificar que se insertó la carrera principal UNA VEZ.
        verify(carrerasRepository, times(1)).insertAndGetId(any(Connection.class), eq(nuevaCarrera));

        // Verificar que se insertaron los detalles de la carrera DOS VECES (uno por cada apuesta).
        verify(detalleRepository, times(2)).insert(any(Connection.class), any(Detallecarreras_Model.class));

        // Usar un ArgumentCaptor para "capturar" los objetos que se pasaron al repositorio de apuestas.
        ArgumentCaptor<Apuestas_Model> apuestaCaptor = ArgumentCaptor.forClass(Apuestas_Model.class);

        // Verificar que se insertaron las apuestas DOS VECES y capturar los objetos.
        verify(apuestasRepository, times(2)).insert(any(Connection.class), apuestaCaptor.capture());

        // Se obtienen los objetos de apuesta capturados.
        List<Apuestas_Model> apuestasGuardadas = apuestaCaptor.getAllValues();

        // Se verifica que los datos de las apuestas guardadas son correctos.
        assertEquals(2, apuestasGuardadas.size());
        assertEquals(50000, apuestasGuardadas.get(0).getMonto());
        assertEquals(102, apuestasGuardadas.get(1).getFk_caballos());
        assertEquals(50, apuestasGuardadas.get(0).getFk_carreras(), "El ID de la carrera debe ser el que devolvió el repositorio.");
    }
}
