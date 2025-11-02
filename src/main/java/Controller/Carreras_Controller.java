package Controller;

import Model.Apuestas_DTO;
import Model.CarreraParaVista_DTO;
import Model.Carreras_Model;
import Repository.Apostadores_Repository;
import Repository.Apuestas_Repository;
import Repository.Carreras_Repository;
import Repository.Detallecarreras_Repository;
import Repository.Movimientos_Repository;
import Services.Carreras_Services;
import Services.Exceptions.ServiceException;
import Services.Movimientos_Services;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador que gestiona las interacciones entre la Vista y los Servicios
 * para Carreras.
 */
public class Carreras_Controller {

    private final Carreras_Services services;

    public Carreras_Controller() {
        // Se construye toda la cadena de dependencias.
        Carreras_Repository carrerasRepo = new Carreras_Repository();
        Detallecarreras_Repository detalleRepo = new Detallecarreras_Repository();
        Apuestas_Repository apuestasRepo = new Apuestas_Repository();
        Movimientos_Repository movimientosRepo = new Movimientos_Repository();
        Apostadores_Repository apostadoresRepo = new Apostadores_Repository();
        Movimientos_Services movServices = new Movimientos_Services(movimientosRepo, apostadoresRepo, apuestasRepo, carrerasRepo);
        this.services = new Carreras_Services(carrerasRepo, detalleRepo, apuestasRepo, movServices);
    }

    /**
     * Solicita a la capa de servicio el siguiente ID disponible para una nueva
     * carrera.
     *
     * @return El próximo ID autoincremental.
     * @throws ServiceException Si ocurre un error durante la consulta.
     */
    public int obtenerSiguienteIdCarrera() throws ServiceException {
        System.out.println("Controller: Solicitando siguiente ID al servicio...");
        // Suponiendo que tienes una capa de servicio. Si no, llamarías a tu DAO.
        return services.obtenerSiguienteIdCarrera();
    }

    /**
     * Procesa la solicitud para crear una nueva carrera con sus apuestas.
     *
     * @param nombre
     * @param fecha
     * @param fechaLimite
     * @param comision
     * @param obs
     * @param esActivo
     * @param idGanador El ID del caballo ganador (puede ser null).
     * @param apuestas La lista de DTOs de las apuestas.
     * @throws ServiceException Si ocurre un error.
     */
    public void crearCarreraConApuestas(String nombre, LocalDate fecha, LocalDate fechaLimite, int comision,
            String obs, boolean esActivo, Integer idGanador,
            List<Apuestas_DTO> apuestas) throws ServiceException {

        System.out.println("\n--- CONTROLLER: Inicia crearCarreraConApuestas ---");
        System.out.println("1. Datos recibidos de la Vista:");
        System.out.println("   - Nombre Carrera: " + nombre);
        System.out.println("   - Fecha: " + fecha);
        System.out.println("   - Fecha Límite: " + fechaLimite);
        System.out.println("   - Comisión: " + comision);
        System.out.println("   - Observación: " + obs);
        System.out.println("   - Es Activo: " + esActivo);
        System.out.println("   - ID Ganador: " + idGanador);
        System.out.println("   - Número de apuestas a procesar: " + apuestas.size());

        // Imprimir el detalle de cada apuesta recibida
        for (int i = 0; i < apuestas.size(); i++) {
            Apuestas_DTO dto = apuestas.get(i);
            System.out.println(String.format("     > Apuesta [%d]: Apostador ID=%d, Caballo ID=%d, Monto=%d, Abonado=%d",
                    i + 1, dto.getIdApostador(), dto.getIdCaballo(), dto.getMontoApostado(), dto.getMontoAbonado()));
        }

        // Se crea el modelo pasándole el idGanador.
        System.out.println("2. Creando el objeto Carreras_Model para enviar al servicio...");
        Carreras_Model carrera = new Carreras_Model(0, nombre, "", fecha, fechaLimite, idGanador, obs, esActivo ? 1 : 2, comision);

        System.out.println("3. Llamando a services.crearCarreraCompleta...");
        services.crearCarreraCompleta(carrera, apuestas);

        System.out.println("--- CONTROLLER: Finaliza crearCarreraConApuestas ---");
    }

    /**
     * Procesa la solicitud para actualizar una carrera con sus apuestas.
     *
     * @param idCarrera
     * @param nombre
     * @param fecha
     * @param fechaLimite
     * @param comision
     * @param obs
     * @param esActivo
     * @param idGanador
     * @param apuestas
     * @throws Services.Exceptions.ServiceException
     */
    public void actualizarCarreraConApuestas(int idCarrera, String nombre, LocalDate fecha, LocalDate fechaLimite,
            int comision, String obs, boolean esActivo, Integer idGanador,
            List<Apuestas_DTO> apuestas) throws ServiceException {
        Carreras_Model carrera = new Carreras_Model(idCarrera, nombre, "", fecha, fechaLimite, idGanador, obs, esActivo ? 1 : 2, comision);
        services.actualizarCarreraCompleta(carrera, apuestas);
    }

    /**
     * Pide al servicio la lista de carreras para mostrar en una vista.
     *
     * @param search
     * @param fase
     * @param statusFilter
     * @return
     * @throws Services.Exceptions.ServiceException
     */
    public List<CarreraParaVista_DTO> listarCarreras(String search, String fase, String statusFilter) throws ServiceException {
        return services.consultarCarrerasParaVista(search, fase, statusFilter);
    }

    public int getSiguienteNumeroDeCarrera() throws ServiceException {
        return services.obtenerSiguienteNumeroDeCarrera();
    }
}
