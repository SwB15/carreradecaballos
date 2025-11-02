package Services;

import Config.DataSource;
import Model.Carreras_Model;
import Model.MovimientoParaVista_DTO;
import Model.Movimientos_Model;
import Repository.Apostadores_Repository;
import Repository.Apuestas_Repository;
import Repository.Carreras_Repository;
import Repository.Movimientos_Repository;
import Services.Exceptions.ServiceException;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contiene la lógica de negocio para la generación y gestión de movimientos
 * financieros.
 */
public class Movimientos_Services {

    private final Movimientos_Repository movimientosRepository;
    private final Apostadores_Repository apostadoresRepository;
    private final Apuestas_Repository apuestasRepository;
    private final Carreras_Repository carrerasRepository;

    /**
     * Constructor que recibe sus dependencias (Inyección de Dependencias).
     *
     * @param movsRepo
     * @param apostadoresRepo
     * @param apuestasRepo
     * @param carrerasRepo
     */
    public Movimientos_Services(Movimientos_Repository movsRepo, Apostadores_Repository apostadoresRepo, Apuestas_Repository apuestasRepo, Carreras_Repository carrerasRepo) {
        this.movimientosRepository = movsRepo;
        this.apostadoresRepository = apostadoresRepo;
        this.apuestasRepository = apuestasRepo;
        this.carrerasRepository = carrerasRepo;
    }

    /**
     * Orquesta la transacción para procesar los resultados de una carrera.
     *
     * @param idCarrera
     * @throws Services.Exceptions.ServiceException
     */
    public void procesarYGuardarResultadosDeCarrera(int idCarrera) throws ServiceException {
        Connection conn = null;
        try {
            conn = DataSource.getConnection();
            conn.setAutoCommit(false);
            // Llama a la versión que trabaja con la conexión
            ejecutarProcesamientoDeResultados(conn, idCarrera);
            conn.commit();
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
            }
            throw new ServiceException("No se pudieron procesar los resultados: " + e.getMessage(), e);
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

    /**
     * MÉTODO PRIVADO (o protegido): Contiene la lógica de negocio pura, asume
     * que ya está dentro de una transacción.
     *
     * @param conn
     * @param carrera
     * @throws java.sql.SQLException
     */
    protected void ejecutarProcesamientoDeResultados(Connection conn, Carreras_Model carrera) throws Exception {
        int idCarrera = carrera.getIdCarrera();
        System.out.println(String.format("\n--- SERVICIO (Movimientos): Iniciando procesamiento para Carrera ID: %d ---", idCarrera));

        // Se buscan y revierten los movimientos de saldo de cálculos anteriores para esta carrera.
        List<Movimientos_Model> movimientosAnteriores = movimientosRepository.findMovimientosDeResultadosPorCarrera(conn, idCarrera);
        for (Movimientos_Model mov : movimientosAnteriores) {
            apostadoresRepository.updateSaldo(conn, mov.getFk_apostadores(), -mov.getMonto());
        }
        // Se eliminan los registros de movimientos antiguos para dar paso a los nuevos.
        movimientosRepository.deleteMovimientosDeResultadosPorCarrera(conn, idCarrera);

        if (carrera.getFk_ganador() == null) {
            throw new IllegalStateException("La carrera no tiene un ganador asignado.");
        }
        System.out.println(String.format("1. Datos de Carrera -> Ganador ID: %d, Comisión: %d%%", carrera.getFk_ganador(), carrera.getComision()));

        // Obtener cálculos previos del repositorio
        List<Apuestas_Repository.ResultadoPorApostadorDTO> calculosPrevios = apuestasRepository.findResultadosPorApostador(conn, idCarrera);
        long poolPerdedores = apuestasRepository.getPoolPerdedores(conn, idCarrera);
        long totalApostadoAlGanador = apuestasRepository.getTotalApostadoAlGanador(conn, idCarrera);

        System.out.println("2. Datos calculados desde el Repositorio:");
        System.out.println("   - Pool de Perdedores (Monto Total): " + poolPerdedores);
        System.out.println("   - Total Apostado al Ganador (Monto Total): " + totalApostadoAlGanador);
        System.out.println("   - Apostadores involucrados: " + calculosPrevios.size());

        // Preparar listas para la transacción
        List<Movimientos_Model> nuevosMovimientos = new ArrayList<>();
        Map<Integer, Integer> cambiosDeSaldo = new HashMap<>();
        LocalDate fechaMovimiento = LocalDate.now();

        System.out.println("3. Aplicando lógica de negocio a cada apostador...");
        for (Apuestas_Repository.ResultadoPorApostadorDTO res : calculosPrevios) {
            int idApostador = res.getIdApostador();
            int stakeGanado = res.getStakeGanado(); // Monto total apostado al ganador
            int deudaRealPerdida = res.getStakePerdido(); // Deuda real (monto - abonado)

            System.out.println(String.format("   -> Procesando Apostador ID %d: [Apostado a Ganador: %d, Deuda Real Perdida: %d]", idApostador, stakeGanado, deudaRealPerdida));

            if (stakeGanado > 0) { // El apostador GANÓ
                int premioNetoIndividual = 0;
                if (totalApostadoAlGanador > 0) {
                    double porcentajeDelGanador = (double) stakeGanado / totalApostadoAlGanador;
                    double premioBruto = poolPerdedores * (1 - (carrera.getComision() / 100.0));
                    premioNetoIndividual = (int) Math.round(premioBruto * porcentajeDelGanador);
                    System.out.println(String.format("      => GANADOR. Cálculo de premio: (%d * (1 - %d/100.0)) * (%d / %d) = %d", poolPerdedores, carrera.getComision(), stakeGanado, totalApostadoAlGanador, premioNetoIndividual));
                }
                nuevosMovimientos.add(new Movimientos_Model(0, fechaMovimiento, premioNetoIndividual, "Ganancia neta apuesta", idApostador, null, 5, idCarrera));
                cambiosDeSaldo.merge(idApostador, premioNetoIndividual, Integer::sum);
            } else { // El apostador PERDIÓ
                if (deudaRealPerdida > 0) {
                    System.out.println(String.format("      => PERDEDOR. Deuda a descontar: %d", -deudaRealPerdida));
                    nuevosMovimientos.add(new Movimientos_Model(0, fechaMovimiento, -deudaRealPerdida, "Cobro de apuesta perdida", idApostador, null, 7, idCarrera));
                    cambiosDeSaldo.merge(idApostador, -deudaRealPerdida, Integer::sum);
                } else {
                    System.out.println(String.format("      => PERDEDOR. Deuda ya pagada (Monto-Abonado = 0). No se descuenta nada."));
                }
            }
        }

        // 4. Persistir Cambios
        System.out.println("4. Persistiendo cambios en la Base de Datos...");
        movimientosRepository.deleteMovimientosDeResultadosPorCarrera(conn, idCarrera);
        System.out.println("   - Movimientos de resultados antiguos eliminados.");

        if (!nuevosMovimientos.isEmpty()) {
            movimientosRepository.insertBatch(conn, nuevosMovimientos);
            System.out.println("   - Se insertarán " + nuevosMovimientos.size() + " nuevos movimientos.");
        }

        for (Map.Entry<Integer, Integer> entry : cambiosDeSaldo.entrySet()) {
            apostadoresRepository.updateSaldo(conn, entry.getKey(), entry.getValue());
            System.out.println(String.format("   - Actualizando saldo para Apostador ID %d. Cambio neto: %+d", entry.getKey(), entry.getValue()));
        }
        System.out.println("--- [SERVICIO] Finaliza 'ejecutarProcesamientoDeResultados' ---");
    }

    /**
     * Consulta la lista de movimientos para la vista.
     *
     * @param apostadorSearch
     * @param tipoMovimientoSearch
     * @param dateFrom
     * @param dateTo
     * @return Una lista de DTOs con la información de los movimientos.
     * @throws ServiceException si ocurre un error.
     */
    public List<MovimientoParaVista_DTO> consultarMovimientosParaVista(String apostadorSearch, String tipoMovimientoSearch,
            LocalDate dateFrom, LocalDate dateTo) throws ServiceException {

        System.out.println("\n--- SERVICIO (Movimientos): Petición recibida para consultar movimientos ---");
        System.out.println("1. Filtros recibidos del Controlador:");
        System.out.println("   - Búsqueda Apostador: '" + apostadorSearch + "'");
        System.out.println("   - Tipo Movimiento: '" + tipoMovimientoSearch + "'");
        System.out.println("   - Fecha Desde: " + dateFrom);
        System.out.println("   - Fecha Hasta: " + dateTo);

        try {
            System.out.println("2. Llamando al repositorio: movimientosRepository.findMovimientosParaVista...");
            List<MovimientoParaVista_DTO> resultado = movimientosRepository.findMovimientosParaVista(apostadorSearch, tipoMovimientoSearch, dateFrom, dateTo);

            System.out.println("3. Servicio recibió " + resultado.size() + " movimientos desde el repositorio.");
            System.out.println("--- SERVICIO (Movimientos): Finaliza consulta de movimientos ---");

            return resultado;

        } catch (SQLException e) {
            System.err.println("!!! ERROR en el servicio al consultar movimientos: " + e.getMessage());
            throw new ServiceException("Error al consultar los movimientos: " + e.getMessage(), e);
        }
    }

    /**
     * Crea un movimiento de saldo (depósito/retiro) y actualiza el saldo del
     * apostador. La operación es transaccional.
     *
     * @param idApostador El ID del apostador.
     * @param monto El monto del movimiento (positivo para depósito, negativo
     * para retiro).
     * @param descripcion La descripción del movimiento.
     * @throws ServiceException Si ocurre un error de base de datos.
     */
    public void crearMovimientoDeSaldo(int idApostador, int monto, String descripcion) throws ServiceException {
        Connection conn = null;
        try {
            conn = DataSource.getConnection();
            conn.setAutoCommit(false); // Se inicia la transacción

            // 1. Crear el objeto del nuevo movimiento.
            // El tipo de movimiento (1=Depósito, 2=Retiro) se determina por el signo del monto.
            Movimientos_Model nuevoMovimiento = new Movimientos_Model(
                    0,
                    LocalDate.now(),
                    monto,
                    descripcion,
                    idApostador,
                    null, // fk_apuestas es nulo porque no está ligado a una apuesta
                    (monto > 0) ? 1 : 2, // fk_tipomovimientos
                    null // fk_carreras es nulo
            );

            // 2. Insertar el nuevo movimiento.
            movimientosRepository.insert(conn, nuevoMovimiento);

            // 3. Actualizar el saldo del apostador.
            apostadoresRepository.updateSaldo(conn, idApostador, monto);

            conn.commit(); // Se confirman los cambios.

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                /* Log rollback error */ }
            throw new ServiceException("Error al registrar el movimiento de saldo: " + e.getMessage(), e);
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
     * Contiene la lógica de negocio pura, asumiendo que ya está dentro de una
     * transacción.
     *
     * @param conn
     * @param idCarrera
     * @throws java.lang.Exception
     */
    protected void ejecutarProcesamientoDeResultados(Connection conn, int idCarrera) throws Exception {
        System.out.println("\n--- [SERVICIO] Inicia 'ejecutarProcesamientoDeResultados' para Carrera ID: " + idCarrera + " ---");

        // 1. Obtener datos
        Carreras_Model carrera = carrerasRepository.findById(conn, idCarrera);
        if (carrera.getFk_ganador() == null) {
            throw new IllegalStateException("La carrera no tiene un ganador asignado.");
        }
        System.out.println("1. Datos de la carrera obtenidos. Ganador ID: " + carrera.getFk_ganador() + ", Comisión: " + carrera.getComision() + "%");

        List<Apuestas_Repository.ResultadoPorApostadorDTO> calculosPrevios = apuestasRepository.findResultadosPorApostador(conn, idCarrera);
        long poolPerdedores = apuestasRepository.getPoolPerdedores(conn, idCarrera);
        long totalApostadoAlGanador = apuestasRepository.getTotalApostadoAlGanador(conn, idCarrera);
        System.out.println("2. Datos de apuestas obtenidos:");
        System.out.println("   - Pool de Perdedores: " + poolPerdedores);
        System.out.println("   - Total Apostado al Ganador: " + totalApostadoAlGanador);

        // 2. Preparar
        List<Movimientos_Model> nuevosMovimientos = new ArrayList<>();
        Map<Integer, Integer> cambiosDeSaldo = new HashMap<>();
        LocalDate fechaMovimiento = LocalDate.now();

        System.out.println("3. Aplicando lógica de negocio para " + calculosPrevios.size() + " apostador(es)...");
        // 3. Aplicar Lógica de Negocio Final
        for (Apuestas_Repository.ResultadoPorApostadorDTO res : calculosPrevios) {
            int idApostador = res.getIdApostador();
            int stakeGanado = res.getStakeGanado();
            int stakePerdido = res.getStakePerdido();

            System.out.println(String.format("   -> Procesando Apostador ID %d: [Apostó al ganador: %d, Apostó a perdedores: %d]", idApostador, stakeGanado, stakePerdido));

            if (stakeGanado > 0) { // El apostador GANÓ
                int premioNetoIndividual = 0;
                if (totalApostadoAlGanador > 0) {
                    double porcentajeDelGanador = (double) stakeGanado / totalApostadoAlGanador;
                    double premioBruto = poolPerdedores * (1 - (carrera.getComision() / 100.0));
                    premioNetoIndividual = (int) Math.round(premioBruto * porcentajeDelGanador);
                }
                System.out.println(String.format("      => GANADOR. Premio Neto Calculado: %d", premioNetoIndividual));
                nuevosMovimientos.add(new Movimientos_Model(0, fechaMovimiento, premioNetoIndividual, "Ganancia neta apuesta", idApostador, null, 5, idCarrera));
                cambiosDeSaldo.merge(idApostador, premioNetoIndividual, Integer::sum);
            } else { // El apostador PERDIÓ
                int totalPerdido = stakePerdido;
                System.out.println(String.format("      => PERDEDOR. Pérdida Neta: %d", -totalPerdido));
                nuevosMovimientos.add(new Movimientos_Model(0, fechaMovimiento, -totalPerdido, "Apuesta perdida", idApostador, null, 7, idCarrera));
                cambiosDeSaldo.merge(idApostador, -totalPerdido, Integer::sum);
            }
        }

        // 4. Persistir Cambios
        System.out.println("4. Persistiendo cambios en la base de datos...");
        movimientosRepository.deleteMovimientosDeResultadosPorCarrera(conn, idCarrera);
        System.out.println("   - Movimientos de resultados antiguos eliminados.");

        if (!nuevosMovimientos.isEmpty()) {
            movimientosRepository.insertBatch(conn, nuevosMovimientos);
            System.out.println("   - " + nuevosMovimientos.size() + " nuevos movimientos insertados en lote.");
        }

        for (Map.Entry<Integer, Integer> entry : cambiosDeSaldo.entrySet()) {
            apostadoresRepository.updateSaldo(conn, entry.getKey(), entry.getValue());
            System.out.println(String.format("   - Actualizando saldo para Apostador ID %d. Cambio: %+d", entry.getKey(), entry.getValue()));
        }
        System.out.println("--- [SERVICIO] Finaliza 'ejecutarProcesamientoDeResultados' ---");
    }
}
