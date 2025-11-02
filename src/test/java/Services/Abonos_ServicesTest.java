package Services;

import Model.Abonos_Model;
import Repository.Abonos_Repository;
import Repository.Apuestas_Repository;
import Services.Exceptions.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Abonos_ServicesTest {

    @Mock // Mock (simulación) del repositorio de abonos
    private Abonos_Repository abonosRepository;

    @Mock // Mock del repositorio de apuestas
    private Apuestas_Repository apuestasRepository;

    @InjectMocks // Instancia real del servicio que vamos a probar
    private Abonos_Services abonosServices;

    @Test
    void registrarAbono_CuandoEsPagoParcial_ActualizaEstadoAParcial() throws Exception {
        // --- 1. Arrange (Preparar el escenario) ---

        // Se crea el abono que simula la entrada del usuario.
        Abonos_Model nuevoAbono = new Abonos_Model(0, LocalDate.now(), 20000, "Efectivo", 15, 1);

        // Se le dice a los mocks cómo deben comportarse.
        // Cuando el servicio pregunte por el monto de la apuesta 15, el mock responderá 100000.
        when(apuestasRepository.getMonto(any(Connection.class), eq(15))).thenReturn(100000);
        // Cuando el servicio pregunte por el total ya abonado, el mock responderá 30000 (10k que había + 20k nuevos).
        when(apuestasRepository.getTotalAbonado(any(Connection.class), eq(15))).thenReturn(30000);

        // --- 2. Act (Ejecutar el método a probar) ---
        // Se ejecuta el método y se confirma que NO lanza una excepción.
        assertDoesNotThrow(() -> abonosServices.registrarAbono(nuevoAbono));

        // --- 3. Assert/Verify (Verificar el resultado) ---
        // Se verifica que el servicio intentó insertar el abono en la BD.
        verify(abonosRepository).insert(any(Connection.class), eq(nuevoAbono));

        // Se verifica que el servicio actualizó el estado de la apuesta.
        // Queremos asegurarnos de que el nuevo estado sea 2 (parcial).
        verify(apuestasRepository).updateEstadoPago(any(Connection.class), eq(15), eq(30000), eq(2));
    }

    @Test
    void registrarAbono_CuandoRepositorioFalla_LanzaServiceException() throws SQLException {
        // --- 1. Arrange ---
        Abonos_Model nuevoAbono = new Abonos_Model(0, LocalDate.now(), 20000, "Efectivo", 15, 1);

        // Se configura el mock para que FALLE.
        // Cuando se intente insertar el abono, el mock lanzará una SQLException.
        doThrow(new SQLException("Error de BD simulado")).when(abonosRepository).insert(any(Connection.class), any(Abonos_Model.class));

        // --- 2. Act & 3. Assert ---
        // Se ejecuta el método y se verifica que SÍ lanza una ServiceException.
        // Esto confirma que nuestro manejo de errores funciona.
        assertThrows(ServiceException.class, () -> {
            abonosServices.registrarAbono(nuevoAbono);
        });
    }
}
