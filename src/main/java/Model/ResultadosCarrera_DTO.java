package Model;

/**
 * DTO que representa el resultado final de un apostador en una carrera específica.
 * Contiene todos los datos calculados necesarios para la vista de balance.
 */
public class ResultadosCarrera_DTO {

    private final int idApostador;
    private final String nombreApostador;
    private final String cedulaApostador;
    private final int totalApostado;
    private final int poolPerdedor;
    private final int comisionGestor;
    private final int resultadoFinal; // Ganancia neta (positivo) o pérdida (negativo)

    public ResultadosCarrera_DTO(int idApostador, String nombreApostador, String cedulaApostador, int totalApostado, int poolPerdedor, int comisionGestor, int resultadoFinal) {
        this.idApostador = idApostador;
        this.nombreApostador = nombreApostador;
        this.cedulaApostador = cedulaApostador;
        this.totalApostado = totalApostado;
        this.poolPerdedor = poolPerdedor;
        this.comisionGestor = comisionGestor;
        this.resultadoFinal = resultadoFinal;
    }

    public int getIdApostador() {
        return idApostador;
    }

    public String getNombreApostador() {
        return nombreApostador;
    }

    public String getCedulaApostador() {
        return cedulaApostador;
    }

    public int getTotalApostado() {
        return totalApostado;
    }

    public int getPoolPerdedor() {
        return poolPerdedor;
    }

    public int getComisionGestor() {
        return comisionGestor;
    }

    public int getResultadoFinal() {
        return resultadoFinal;
    }

    
}