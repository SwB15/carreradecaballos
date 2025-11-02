package Services;

import Model.Estados_Model;
import Repository.Estados_Repository;
import Services.Exceptions.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Estados_ServicesTest {

    @Mock
    private Estados_Repository repository;

    @InjectMocks
    private Estados_Services services;

    @Test
    void consultarTodos_CuandoHayDatos_DevuelveListaCorrecta() throws SQLException, ServiceException {
        // --- 1. Arrange (Preparar) ---
        // Se crea una lista de estados que simula la respuesta de la base de datos.
        List<Estados_Model> listaDeEstados = List.of(
                new Estados_Model(1, "activo"),
                new Estados_Model(2, "inactivo")
        );

        // Se le dice al mock que devuelva nuestra lista cuando se llame a findAll().
        when(repository.findAll()).thenReturn(listaDeEstados);

        // --- 2. Act (Ejecutar) ---
        List<Estados_Model> resultado = services.consultarTodos();

        // --- 3. Assert (Verificar) ---
        assertNotNull(resultado);
        assertEquals(2, resultado.size(), "La lista devuelta debe contener 2 estados.");
        assertEquals("activo", resultado.get(0).getNombre());
    }

    @Test
    void consultarTodos_CuandoRepositorioFalla_LanzaServiceException() throws SQLException {
        // --- Arrange ---
        // Se configura el mock para que falle.
        when(repository.findAll()).thenThrow(new SQLException("Error de BD simulado"));

        // --- Act & Assert ---
        // Se verifica que el servicio maneja el error y lanza una ServiceException.
        assertThrows(ServiceException.class, () -> {
            services.consultarTodos();
        });
    }
}
