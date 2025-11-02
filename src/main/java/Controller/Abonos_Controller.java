package Controller;

import Model.Detalle_Pendientes;
import Repository.Abonos_Repository; // Necesario para la inyección de dependencias
import Repository.Apostadores_Repository;
import Repository.Apuestas_Repository; // Necesario para la inyección de dependencias
import Services.Abonos_Services;
import Services.Exceptions.ServiceException;
import java.util.List;

/**
 * Controlador que gestiona las interacciones entre la Vista y los Servicios
 * para los Abonos. Es el intermediario que traduce las acciones del usuario en
 * llamadas a la lógica de negocio.
 */
public class Abonos_Controller {

    private final Abonos_Services services;

    /**
     * Constructor que inicializa el controlador con sus dependencias de
     * servicio.
     */
    public Abonos_Controller() {
        // El controlador construye la cadena de dependencias.
        Abonos_Repository abonosRepo = new Abonos_Repository();
        Apuestas_Repository apuestasRepo = new Apuestas_Repository();
        Apostadores_Repository apostadoresRepo = new Apostadores_Repository();

        // Se inyectan todas las dependencias que el servicio necesita.
        this.services = new Abonos_Services(abonosRepo, apuestasRepo, apostadoresRepo);
    }
    
    /**
     * Procesa la solicitud para registrar un abono directo a una apuesta.
     *
     * @param idApuesta El ID de la apuesta que se está pagando.
     * @param idApostador El ID del apostador que paga.
     * @param montoAAbonar El monto del pago.
     * @throws ServiceException Si laF operación falla.
     */
    public void registrarAbonoDirecto(int idApuesta, int idApostador, int montoAAbonar) throws ServiceException {
        // Se valida y se pasa la llamada al servicio.
        if (montoAAbonar <= 0) {
            throw new ServiceException("El monto a abonar debe ser positivo.");
        }
        services.registrarAbonoDirecto(idApuesta, idApostador, montoAAbonar);
    }

    public void saldarTodasLasDeudas(int idApostador, List<Detalle_Pendientes> deudas) throws ServiceException {
        if (deudas == null || deudas.isEmpty()) {
            throw new ServiceException("No hay deudas para saldar.");
        }
        services.saldarTodasLasDeudas(idApostador, deudas);
    }
}
