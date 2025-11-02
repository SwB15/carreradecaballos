package Controller;

import Model.MovimientoParaVista_DTO;
import Repository.*;
import Services.Movimientos_Services;
import Services.Exceptions.ServiceException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para las interacciones de la Vista relacionadas con los
 * Movimientos y el procesamiento de resultados de carreras.
 */
public class Movimientos_Controller {

    private final Movimientos_Services services;

    public Movimientos_Controller() {
        // El controlador construye la cadena de dependencias completa para el servicio.
        Movimientos_Repository movsRepo = new Movimientos_Repository();
        Apostadores_Repository apostadoresRepo = new Apostadores_Repository();
        Apuestas_Repository apuestasRepo = new Apuestas_Repository();
        Carreras_Repository carrerasRepo = new Carreras_Repository();
        this.services = new Movimientos_Services(movsRepo, apostadoresRepo, apuestasRepo, carrerasRepo);
    }

    /**
     * Procesa los resultados de una carrera, generando los movimientos de
     * ganancias/pérdidas y actualizando los saldos de los apostadores.
     *
     * @param idCarrera El ID de la carrera a procesar.
     * @throws ServiceException Si la operación falla.
     */
    public void procesarResultadosCarrera(int idCarrera) throws ServiceException {
        try {
            services.procesarYGuardarResultadosDeCarrera(idCarrera);
        } catch (Exception e) {
            // Se convierte la excepción genérica en una ServiceException para la Vista.
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * Pide al servicio la lista de movimientos para mostrar en la vista.
     *
     * @param apostadorSearch Filtro por nombre de apostador.
     * @param tipoMovimientoSearch Filtro por tipo de movimiento.
     * @param dateFrom Fecha de inicio del filtro.
     * @param dateTo Fecha de fin del filtro.
     * @return Una lista de DTOs con la información de los movimientos.
     * @throws ServiceException Si la consulta falla.
     */
    public List<MovimientoParaVista_DTO> listarMovimientos(String apostadorSearch, String tipoMovimientoSearch,
            LocalDate dateFrom, LocalDate dateTo) throws ServiceException {

        System.out.println("\n--- CONTROLLER (Movimientos): Petición para listar movimientos ---");
        System.out.println("1. Filtros recibidos de la Vista:");
        System.out.println("   - Búsqueda Apostador: '" + apostadorSearch + "'");
        System.out.println("   - Tipo Movimiento: '" + tipoMovimientoSearch + "'");
        System.out.println("   - Fecha Desde: " + dateFrom);
        System.out.println("   - Fecha Hasta: " + dateTo);

        System.out.println("2. Llamando al servicio...");
        List<MovimientoParaVista_DTO> resultado = services.consultarMovimientosParaVista(apostadorSearch, tipoMovimientoSearch, dateFrom, dateTo);

        System.out.println("3. Controlador recibió " + resultado.size() + " movimientos del servicio.");
        System.out.println("--- CONTROLLER (Movimientos): Finaliza listado de movimientos ---");

        return resultado;
    }

    /**
     * Procesa la solicitud de la Vista para crear un movimiento de saldo
     * (depósito/retiro).
     *
     * @param idApostador El ID del apostador.
     * @param monto El monto (positivo para depósito, negativo para retiro).
     * @param descripcion La descripción del movimiento.
     * @throws ServiceException Si la lógica de negocio falla.
     */
    public void crearMovimientoDeSaldo(int idApostador, int monto, String descripcion) throws ServiceException {
        // El controlador simplemente valida y pasa la llamada al servicio.
        if (monto == 0) {
            throw new ServiceException("El monto no puede ser cero.");
        }
        services.crearMovimientoDeSaldo(idApostador, monto, descripcion);
    }
}
