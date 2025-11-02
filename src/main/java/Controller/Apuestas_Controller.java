package Controller;

import Model.Apuestas_Model;
import Model.ApuestasParaVista_DTO;
import Repository.Apuestas_Repository;
import Services.Apuestas_Services;
import Services.Exceptions.ServiceException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

/**
 * Controlador que gestiona las interacciones entre la Vista y los Servicios
 * para las Apuestas.
 */
public class Apuestas_Controller {

    private final Apuestas_Services services;

    public Apuestas_Controller() {
        // El controlador construye la cadena de dependencias.
        Apuestas_Repository repository = new Apuestas_Repository();
        this.services = new Apuestas_Services(repository);
    }

    /**
     * Procesa la solicitud de la Vista para crear una nueva apuesta.
     *
     * @param apuesta Nombre o descripción de la apuesta.
     * @param monto Monto apostado.
     * @param fecha Fecha de la apuesta.
     * @param fechaLimite Fecha límite para pagar.
     * @param observacion Notas adicionales.
     * @param saldoUsado Si se utilizó saldo para el pago.
     * @param abonado Monto abonado inicialmente.
     * @param fk_carreras ID de la carrera.
     * @param fk_caballos ID del caballo.
     * @param fk_apostadores ID del apostador.
     * @throws ServiceException Si ocurre un error durante la creación.
     */
    public void crearApuesta(String apuesta, int monto, LocalDate fecha, LocalDate fechaLimite,
            String observacion, boolean saldoUsado, int abonado,
            int fk_carreras, int fk_caballos, int fk_apostadores) throws ServiceException {

        // Se crea el objeto Model con los datos de la vista.
        Apuestas_Model nuevaApuesta = new Apuestas_Model(
                0, apuesta, monto, fecha, fechaLimite, observacion, saldoUsado,
                abonado, fk_carreras, fk_caballos, fk_apostadores, 1, 1 // fk_estadopago=1 (pendiente), fk_estados=1 (activo)
        );

        services.crearApuesta(nuevaApuesta);
    }

    /**
     * Pide al servicio la lista de apuestas para mostrar en la vista.
     *
     * @param idCarrera El ID de la carrera a consultar.
     * @return Una lista de DTOs con la información de las apuestas.
     * @throws ServiceException Si la consulta falla.
     */
    public List<ApuestasParaVista_DTO> listarApuestasPorCarrera(int idCarrera) throws ServiceException {
        return services.consultarApuestasParaVista(idCarrera);
    }

    /**
     * Pide al servicio los datos para rellenar los ComboBoxes de la interfaz.
     *
     * @param tipo El tipo de combo a rellenar ("apostadores", "carreras",
     * etc.).
     * @return Un HashMap listo para ser usado por la vista.
     * @throws ServiceException Si la consulta falla.
     */
    public HashMap<String, Integer> obtenerDatosParaCombo(String tipo) throws ServiceException {
        return switch (tipo.toLowerCase()) {
            case "apostadores" ->
                services.getDatosParaCombo("apostadores", "idapostadores", "nombre");
            case "carreras" ->
                services.getDatosParaCombo("carreras", "idcarreras", "nombre");
            // Se pueden añadir más casos si son necesarios.
            default ->
                new HashMap<>();
        };
    }
}
