package Controller;

import Model.ReportesHistorialApostador_Model;
import Repository.Apostadores_Repository;
import Services.Apostadores_Services;
import Services.Exceptions.ServiceException;
import Utils.ReportManager; // Se usa la utilidad centralizada
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;

public class Reportes_Controller {

    private final Apostadores_Services apostadoresService;

    public Reportes_Controller() {
        this.apostadoresService = new Apostadores_Services(new Apostadores_Repository());
    }

    /**
     * Orquesta la generación del reporte de historial de un apostador.
     *
     * @param owner
     * @param idApostador
     * @param cedula
     * @param nombre
     * @throws Services.Exceptions.ServiceException
     */
    public void generarReporteHistorial(JFrame owner, int idApostador, String cedula, String nombre) throws ServiceException {
        try {
            // 1. El controlador pide los datos al servicio.
            List<ReportesHistorialApostador_Model> historial = apostadoresService.consultarHistorial(idApostador, null, null);

            // 2. Se preparan los parámetros específicos para este reporte.
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("Cedula", cedula);
            parametros.put("Nombre", nombre);

            // 3. Se llama a la utilidad genérica para que haga todo el trabajo.
            ReportManager.generarReporte(owner, "Historial de " + nombre, "Perfil.jrxml", parametros, historial);

        } catch (ServiceException e) {
            // Se relanza la excepción para que la vista la maneje.
            throw new ServiceException("Error al preparar los datos del reporte: " + e.getMessage(), e);
        }
    }

    // Aquí podrías añadir más métodos para otros reportes en el futuro.
    // public void generarReporteDeCarreras(JFrame owner) throws ServiceException { ... }
}
