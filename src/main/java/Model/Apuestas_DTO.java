package Model;

/**
 * DTO (Data Transfer Object) para manejar los datos del formulario de
 * creación/edición de apuestas. Agrupa toda la información necesaria para la
 * lógica de la vista en un solo objeto.
 */
public class Apuestas_DTO {

    private Integer idApuesta;
    private Integer idCaballo;
    private String nombreCaballo;
    private int montoApostado;
    private int montoAbonado;
    private Integer idApostador;
    private String nombreApostador;
    private int saldo;
    private int saldoOriginal;
    private int abonadoOriginal;

    /**
     * Constructor completo para crear el DTO simplificado.
     *
     * @param idApuesta
     * @param idCaballo
     * @param nombreCaballo
     * @param montoApostado
     * @param montoAbonado
     * @param idApostador
     * @param nombreApostador
     * @param saldo
     * @param saldoOriginal
     * @param abonadoOriginal
     */
    public Apuestas_DTO(Integer idApuesta, Integer idCaballo, String nombreCaballo,
            int montoApostado, int montoAbonado, Integer idApostador,
            String nombreApostador, int saldo, int saldoOriginal, int abonadoOriginal) {
        this.idApuesta = idApuesta;
        this.idCaballo = idCaballo;
        this.nombreCaballo = nombreCaballo;
        this.montoApostado = montoApostado;
        this.montoAbonado = montoAbonado;
        this.idApostador = idApostador;
        this.nombreApostador = nombreApostador;
        this.saldo = saldo;
        this.saldoOriginal = saldoOriginal;
        this.abonadoOriginal = abonadoOriginal;
    }

    // --- Getters y Setters ---
    public Integer getIdApuesta() {
        return idApuesta;
    }

    public void setIdApuesta(Integer idApuesta) {
        this.idApuesta = idApuesta;
    }

    public Integer getIdCaballo() {
        return idCaballo;
    }

    public void setIdCaballo(Integer idCaballo) {
        this.idCaballo = idCaballo;
    }

    public String getNombreCaballo() {
        return nombreCaballo;
    }

    public void setNombreCaballo(String nombreCaballo) {
        this.nombreCaballo = nombreCaballo;
    }

    public int getMontoApostado() {
        return montoApostado;
    }

    public void setMontoApostado(int montoApostado) {
        this.montoApostado = montoApostado;
    }

    public int getMontoAbonado() {
        return montoAbonado;
    }

    public void setMontoAbonado(int montoAbonado) {
        this.montoAbonado = montoAbonado;
    }

    public Integer getIdApostador() {
        return idApostador;
    }

    public void setIdApostador(Integer idApostador) {
        this.idApostador = idApostador;
    }

    public String getNombreApostador() {
        return nombreApostador;
    }

    public void setNombreApostador(String nombreApostador) {
        this.nombreApostador = nombreApostador;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public int getSaldoOriginal() {
        return saldoOriginal;
    }

    public void setSaldoOriginal(int saldoOriginal) {
        this.saldoOriginal = saldoOriginal;
    }

    public int getAbonadoOriginal() {
        return abonadoOriginal;
    }

    public void setAbonadoOriginal(int abonadoOriginal) {
        this.abonadoOriginal = abonadoOriginal;
    }
}
