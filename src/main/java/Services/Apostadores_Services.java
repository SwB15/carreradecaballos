package Services;

import Model.ApostadorParaVista_DTO;
import Model.Apostadores_Model;
import Model.HistorialApuesta_DTO;
import Model.ReportesHistorialApostador_Model;
import Model.Resultado_Pendientes;
import Repository.Apostadores_Repository;
import Services.Exceptions.ServiceException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Contiene la lógica de negocio relacionada con los Apostadores. Orquesta las
 * operaciones del repositorio y aplica las reglas de negocio.
 */
public class Apostadores_Services {

    private final Apostadores_Repository repository;

    // Se usa Inyección de Dependencias para facilitar las pruebas en el futuro.
    public Apostadores_Services(Apostadores_Repository repository) {
        this.repository = repository;
    }

    /**
     * Crea un nuevo apostador.
     *
     * @param model El modelo con los datos del nuevo apostador.
     * @throws ServiceException si ocurre un error al guardar.
     */
    public void crearApostador(Apostadores_Model model) throws ServiceException {
        try {
            repository.insert(model);
        } catch (SQLException e) {
            throw new ServiceException("Error al crear el apostador: " + e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un apostador existente.
     *
     * @param model El modelo con los datos a actualizar.
     * @throws ServiceException si ocurre un error al actualizar.
     */
    public void actualizarApostador(Apostadores_Model model) throws ServiceException {
        try {
            repository.update(model);
        } catch (SQLException e) {
            throw new ServiceException("Error al actualizar el apostador: " + e.getMessage());
        }
    }

    /**
     * Consulta la lista de apostadores para la vista principal.
     *
     * @param search Término de búsqueda.
     * @param stateFilter Filtro de estado ('activo', 'inactivo').
     * @return Una lista de DTOs listos para la vista.
     * @throws ServiceException si ocurre un error en la consulta.
     */
    public List<ApostadorParaVista_DTO> consultarApostadoresParaVista(String search, String stateFilter) throws ServiceException {
        try {
            return repository.findApostadoresParaVista(search, stateFilter);
        } catch (SQLException e) {
            throw new ServiceException("Error al consultar los apostadores: " + e.getMessage());
        }
    }

    /**
     * Consulta el historial de apuestas de un apostador y calcula los
     * resultados.
     *
     * @param idApostador El ID del apostador.
     * @param desde
     * @param hasta
     * @return Una lista de DTOs con el historial procesado.
     * @throws ServiceException si ocurre un error.
     */
    public List<ReportesHistorialApostador_Model> consultarHistorial(int idApostador, LocalDate desde, LocalDate hasta) throws ServiceException {
        try {
            // 1. El servicio pide los DATOS CRUDOS al repositorio, AHORA PASANDO LAS FECHAS.
            List<HistorialApuesta_DTO> historialCrudo = repository.findHistorialData(idApostador, desde, hasta);
            List<ReportesHistorialApostador_Model> historialProcesado = new ArrayList<>();

            // 2. El servicio aplica la LÓGICA DE NEGOCIO (esta parte no cambia).
            for (HistorialApuesta_DTO datoCrudo : historialCrudo) {
                String resultado;
                long montoFinal;

                if (datoCrudo.getIdCaballoGanador() == null) {
                    resultado = "Pendiente";
                    montoFinal = datoCrudo.getMontoApostado();
                } else if (datoCrudo.getIdCaballoGanador() == datoCrudo.getIdCaballoApostado()) {
                    resultado = "Ganador";
                    // Lógica de cálculo de ganancia
                    if (datoCrudo.getTotalApostadoAlGanador() > 0) {
                        double premioBruto = datoCrudo.getPoolPerdedores() * (1 - (datoCrudo.getComisionCarrera() / 100.0));
                        montoFinal = Math.round(premioBruto * ((double) datoCrudo.getMontoApostado() / datoCrudo.getTotalApostadoAlGanador()));
                    } else {
                        montoFinal = 0; // Evita división por cero
                    }
                } else {
                    resultado = "Perdedor";
                    montoFinal = -datoCrudo.getMontoApostado();
                }

                // 3. El servicio crea el DTO final para la vista.
                historialProcesado.add(new ReportesHistorialApostador_Model(
                        datoCrudo.getIdApuesta(),
                        datoCrudo.getNombreCarrera(),
                        (int) montoFinal, // Se convierte a int
                        datoCrudo.getFecha(),
                        datoCrudo.getNombreCaballo(),
                        resultado
                ));
            }
            return historialProcesado;
        } catch (SQLException e) {
            throw new ServiceException("Error al consultar el historial del apostador: " + e.getMessage());
        }
    }

    public Resultado_Pendientes consultarDeudas(int apostadorId) throws ServiceException {
        System.out.println("--- SERVICIO (Apostadores): Petición recibida para consultar deudas del apostador ID: " + apostadorId + " ---");
        try {
            // Llama al repositorio para que haga el trabajo de base de datos.
            Resultado_Pendientes resultado = repository.findPendientesPorApostador(apostadorId);

            System.out.println("   -> Servicio recibió " + resultado.getDetalles().size() + " deudas desde el repositorio.");
            System.out.println("--- SERVICIO (Apostadores): Finaliza consulta de deudas ---");
            return resultado;

        } catch (SQLException e) {
            System.err.println("!!! ERROR en el servicio al consultar deudas: " + e.getMessage());
            throw new ServiceException("Error al consultar las deudas del apostador: " + e.getMessage(), e);
        }
    }

    /**
     * Contiene la lógica de negocio para actualizar el saldo de un apostador.
     * Llama al repositorio para ejecutar la actualización en la base de datos.
     *
     * @param idApostador El ID del apostador.
     * @param monto El monto a añadir (positivo o negativo).
     * @throws ServiceException Si la operación en el repositorio falla.
     */
    public void actualizarSaldo(int idApostador, int monto) throws ServiceException {
        try {
            // Llama al método 'updateSaldo' que ya existe en tu repositorio.
            repository.updateSaldo(idApostador, monto);
        } catch (SQLException e) {
            throw new ServiceException("Error al actualizar el saldo del apostador: " + e.getMessage(), e);
        }
    }
}
