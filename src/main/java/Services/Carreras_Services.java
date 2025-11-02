package Services;

import Config.DataSource;
import Model.*;
import Repository.*;
import Services.Exceptions.ServiceException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Carreras_Services {

    private final Carreras_Repository carrerasRepository;
    private final Detallecarreras_Repository detalleRepository; // Nombre corregido
    private final Apuestas_Repository apuestasRepository;
    private final Movimientos_Services movimientosServices;

    public Carreras_Services(Carreras_Repository carrerasRepo, Detallecarreras_Repository detalleRepo,
            Apuestas_Repository apuestasRepo, Movimientos_Services movServices) {
        this.carrerasRepository = carrerasRepo;
        this.detalleRepository = detalleRepo;
        this.apuestasRepository = apuestasRepo;
        this.movimientosServices = movServices;
    }

    /**
     * Obtiene el siguiente ID de carrera disponible desde el repositorio.
     *
     * @return El próximo ID para una nueva carrera.
     * @throws ServiceException Si ocurre un error de base de datos durante la
     * consulta.
     */
    public int obtenerSiguienteIdCarrera() throws ServiceException {
        try {
            // Suponiendo que tu variable se llama 'carrerasRepository'
            // y no 'carrerasDAO'.
            return carrerasRepository.findNextId();
        } catch (SQLException e) {
            // Envolvemos la excepción real (SQLException) en una de servicio.
            throw new ServiceException("No se pudo obtener el siguiente ID de carrera.", e);
        }
    }

    public void crearCarreraCompleta(Carreras_Model carrera, List<Apuestas_DTO> apuestasDTO) throws ServiceException {
        Connection conn = null;
        System.out.println("\n--- [SERVICIO] Inicia 'crearCarreraCompleta' ---");
        try {
            conn = DataSource.getConnection();
            conn.setAutoCommit(false);
            System.out.println("1. Conexión obtenida y transacción iniciada.");

            int idNuevaCarrera = carrerasRepository.insertAndGetId(conn, carrera);
            carrera.setIdCarrera(idNuevaCarrera);
            System.out.println("2. Carrera guardada en la BD. Nuevo ID de Carrera: " + idNuevaCarrera);

            System.out.println("3. Procesando " + apuestasDTO.size() + " apuestas...");
            for (Apuestas_DTO dto : apuestasDTO) {
                System.out.println(String.format("   -> Procesando Apuesta: [Apostador ID: %d, Caballo ID: %d, Monto: %d, Abonado: %d]",
                        dto.getIdApostador(), dto.getIdCaballo(), dto.getMontoApostado(), dto.getMontoAbonado()));

                detalleRepository.insert(conn, new Detallecarreras_Model(0, idNuevaCarrera, dto.getIdCaballo()));

                Apuestas_Model apuesta = new Apuestas_Model(0, "Apuesta", dto.getMontoApostado(), carrera.getFecha(),
                        carrera.getFechaLimite(), "", true, dto.getMontoAbonado(), idNuevaCarrera,
                        dto.getIdCaballo(), dto.getIdApostador(), 1, 1);
                apuestasRepository.insert(conn, apuesta);
            }
            System.out.println("4. Todas las apuestas y detalles han sido insertados.");

            if (carrera.getFk_ganador() != null) {
                System.out.println("5. Ganador detectado (ID: " + carrera.getFk_ganador() + "). Se procederá a calcular y guardar los resultados...");
                movimientosServices.ejecutarProcesamientoDeResultados(conn, carrera);
                System.out.println("5.1. Procesamiento de resultados finalizado.");
            } else {
                System.out.println("5. No se detectó un ganador. No se procesan los resultados.");
            }

            conn.commit();
            System.out.println("6. Transacción confirmada (Commit) exitosamente.");

        } catch (Exception e) {
            System.err.println("!!! ERROR DURANTE LA TRANSACCIÓN: " + e.getMessage());
            try {
                if (conn != null) {
                    System.err.println("... revirtiendo cambios (Rollback)...");
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("!!! ERROR CRÍTICO al intentar hacer rollback: " + ex.getMessage());
            }
            throw new ServiceException("Error al crear la carrera completa: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                    System.out.println("7. Conexión a la base de datos cerrada.");
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
            System.out.println("--- [SERVICIO] Finaliza 'crearCarreraCompleta' ---");
        }
    }

    public void actualizarCarreraCompleta(Carreras_Model carrera, List<Apuestas_DTO> apuestasDTO) throws ServiceException {
        Connection conn = null;
        try {
            conn = DataSource.getConnection();
            conn.setAutoCommit(false);

            carrerasRepository.update(conn, carrera);
            apuestasRepository.deleteByCarreraId(conn, carrera.getIdCarrera());
            detalleRepository.deleteByCarreraId(conn, carrera.getIdCarrera());

            for (Apuestas_DTO dto : apuestasDTO) {
                detalleRepository.insert(conn, new Detallecarreras_Model(0, carrera.getIdCarrera(), dto.getIdCaballo()));

                Apuestas_Model apuesta = new Apuestas_Model(
                        dto.getIdApuesta() != null ? dto.getIdApuesta() : 0,
                        "Apuesta", dto.getMontoApostado(), carrera.getFecha(),
                        carrera.getFechaLimite(), "", true, dto.getMontoAbonado(), carrera.getIdCarrera(),
                        dto.getIdCaballo(), dto.getIdApostador(), 1, 1);
                apuestasRepository.insert(conn, apuesta);
            }

            if (carrera.getFk_ganador() != null) {
                movimientosServices.ejecutarProcesamientoDeResultados(conn, carrera);
            }

            conn.commit();
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
            }
            throw new ServiceException("Error al actualizar la carrera: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public List<CarreraParaVista_DTO> consultarCarrerasParaVista(String search, String fase, String statusFilter) throws ServiceException {
        try {
            return carrerasRepository.findCarrerasParaVista(search, fase, statusFilter);
        } catch (SQLException e) {
            throw new ServiceException("Error al consultar las carreras: " + e.getMessage(), e);
        }
    }

    public int obtenerSiguienteNumeroDeCarrera() throws ServiceException {
        try {
            // Se le suma 1 al ID máximo encontrado para obtener el siguiente.
            return carrerasRepository.findMaxId() + 1;
        } catch (SQLException e) {
            throw new ServiceException("Error al obtener el número de la siguiente carrera.", e);
        }
    }
}
