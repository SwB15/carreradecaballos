package Services;

import Config.DataSource;
import Model.Abonos_Model;
import Model.Detalle_Pendientes;
import Repository.Abonos_Repository;
import Repository.Apostadores_Repository;
import Repository.Apuestas_Repository;
import Services.Exceptions.ServiceException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Contiene la lógica de negocio relacionada con los abonos y pagos de apuestas.
 */
public class Abonos_Services {

    private final Abonos_Repository abonosRepository;
    private final Apuestas_Repository apuestasRepository;
    private final Apostadores_Repository apostadoresRepository;

    /**
     * Constructor que recibe sus dependencias (Inyección de Dependencias).
     *
     * @param abonosRepository El repositorio para manejar los abonos.
     * @param apuestasRepository El repositorio para manejar las apuestas.
     */
    public Abonos_Services(Abonos_Repository abonosRepository, Apuestas_Repository apuestasRepository, Apostadores_Repository apostadoresRepository) {
        this.abonosRepository = abonosRepository;
        this.apuestasRepository = apuestasRepository;
        this.apostadoresRepository = apostadoresRepository;
    }

    /**
     * Registra un nuevo abono y actualiza el estado y monto abonado de la
     * apuesta correspondiente. Toda la operación se ejecuta dentro de una
     * transacción segura.
     *
     * @param nuevoAbono El modelo del abono a registrar.
     * @throws ServiceException Si la operación falla, para que la capa superior
     * pueda notificar al usuario.
     */
    public void registrarAbono(Abonos_Model nuevoAbono) throws ServiceException {
        Connection conn = null;
        try {
            conn = DataSource.getConnection();
            conn.setAutoCommit(false); // Se inicia la transacción

            // 1. Insertar el nuevo abono usando la conexión de la transacción.
            abonosRepository.insert(conn, nuevoAbono);

            int idApuesta = nuevoAbono.getFk_apuestas();

            // 2. Obtener los datos necesarios para la lógica.
            int montoTotalApuesta = apuestasRepository.getMonto(conn, idApuesta);
            int totalAbonadoActual = apuestasRepository.getTotalAbonado(conn, idApuesta);

            // 3. Aplicar la lógica de negocio para determinar el nuevo estado de pago.
            int nuevoEstadoPagoId;
            if (totalAbonadoActual >= montoTotalApuesta) {
                nuevoEstadoPagoId = 3; // 3 = pagada
            } else if (totalAbonadoActual > 0) {
                nuevoEstadoPagoId = 2; // 2 = parcial
            } else {
                nuevoEstadoPagoId = 1; // 1 = pendiente
            }

            // 4. Actualizar la tabla de apuestas con los nuevos totales y el nuevo estado.
            apuestasRepository.updateEstadoPago(conn, idApuesta, totalAbonadoActual, nuevoEstadoPagoId);

            conn.commit(); // Se confirman todos los cambios en la base de datos.

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                /* Log rollback error */ }
            // Se lanza una excepción personalizada para notificar a la capa superior.
            throw new ServiceException("No se pudo registrar el abono. Error de base de datos.", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                /* Log close error */ }
        }
    }

    /**
     * Registra un abono actualizando la apuesta y el saldo del apostador en una
     * sola transacción.
     *
     * @param idApuesta El ID de la apuesta que se está pagando.
     * @param idApostador El ID del apostador que paga.
     * @param montoAAbonar El monto del pago.
     * @throws ServiceException Si la operación falla.
     */
    public void registrarAbonoDirecto(int idApuesta, int idApostador, int montoAAbonar) throws ServiceException {
        Connection conn = null;
        try {
            conn = DataSource.getConnection();
            conn.setAutoCommit(false); // Inicia la transacción

            // 1. Sumar el monto al campo 'abonado' de la apuesta.
            apuestasRepository.addAbono(conn, idApuesta, montoAAbonar);

            // 2. Sumar el monto al saldo del apostador.
            apostadoresRepository.updateSaldo(conn, idApostador, montoAAbonar);

            // 3. (Opcional pero recomendado) Actualizar el estado de pago de la apuesta.
            int montoTotalApuesta = apuestasRepository.getMonto(conn, idApuesta);
            int totalAbonadoActual = apuestasRepository.getTotalAbonado(conn, idApuesta); // Obtiene el nuevo total

            int nuevoEstadoPagoId;
            if (totalAbonadoActual >= montoTotalApuesta) {
                nuevoEstadoPagoId = 3; // pagada
            } else {
                nuevoEstadoPagoId = 2; // parcial
            }
            apuestasRepository.updateEstadoPago(conn, idApuesta, totalAbonadoActual, nuevoEstadoPagoId);

            conn.commit(); // Se confirman los cambios.

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
            }
            throw new ServiceException("Error al registrar el abono: " + e.getMessage(), e);
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

    // En Services/Abonos_Services.java
    /**
     * Paga la totalidad de las deudas pendientes de un apostador en una sola
     * transacción.
     *
     * @param idApostador El ID del apostador.
     * @param deudas La lista de deudas a pagar.
     * @throws ServiceException Si la operación falla.
     */
    public void saldarTodasLasDeudas(int idApostador, List<Detalle_Pendientes> deudas) throws ServiceException {
        Connection conn = null;
        try {
            conn = DataSource.getConnection();
            conn.setAutoCommit(false);

            int montoTotalSaldado = 0;
            for (Detalle_Pendientes detalle : deudas) {
                int montoPendiente = detalle.getMontoPendiente();
                // 1. Se actualiza cada apuesta individualmente.
                apuestasRepository.addAbono(conn, detalle.getIdApuesta(), montoPendiente);
                montoTotalSaldado += montoPendiente;
            }

            // 2. Se actualiza el saldo del apostador UNA SOLA VEZ con el total.
            apostadoresRepository.updateSaldo(conn, idApostador, montoTotalSaldado);

            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
            }
            throw new ServiceException("Error al saldar todas las deudas: " + e.getMessage(), e);
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

}
