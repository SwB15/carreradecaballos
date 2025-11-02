package Services.Exceptions;

/**
 * Una excepción personalizada que se lanza desde la capa de Servicios para
 * notificar a las capas superiores (Controller, View) sobre un error en una
 * operación de negocio.
 */
public class ServiceException extends Exception {

    /**
     * Constructor que acepta un mensaje de error.
     *
     * @param message El mensaje que describe la causa del error.
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Constructor que acepta un mensaje de error y la excepción original
     * (causa). Esto es útil para no perder la traza del error original.
     *
     * @param message El mensaje que describe la causa del error.
     * @param cause La excepción original (ej. una SQLException) que provocó
     * este error.
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
