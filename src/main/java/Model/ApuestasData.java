package Model;

/**
 *
 * @author SwichBlade15
 */
public class ApuestasData {

    private Integer idApuesta;     // puede ser null si aún no existe
    private Integer idCaballo;
    private String nombreCaballo;
    private int montoApostado;     // apostado
    private int montoAbonado;      // abonado
    private Integer idApostador;
    private String nombreApostador;
    private int saldo;             // saldo actual en la tabla
    private boolean origenPago;    // true/false según checkbox
    private String saldoUsado;     // texto, p.ej. "No" o valor
    private int saldoOriginal;     // saldooriginal
    private int abonadoOriginal;   // abonadooriginal

    // Constructor completo
    public ApuestasData(Integer idApuesta, Integer idCaballo, String nombreCaballo,
            int montoApostado, int montoAbonado,
            Integer idApostador, String nombreApostador,
            int saldo, boolean origenPago, String saldoUsado,
            int saldoOriginal, int abonadoOriginal) {
        this.idApuesta = idApuesta;
        this.idCaballo = idCaballo;
        this.nombreCaballo = nombreCaballo;
        this.montoApostado = montoApostado;
        this.montoAbonado = montoAbonado;
        this.idApostador = idApostador;
        this.nombreApostador = nombreApostador;
        this.saldo = saldo;
        this.origenPago = origenPago;
        this.saldoUsado = saldoUsado;
        this.saldoOriginal = saldoOriginal;
        this.abonadoOriginal = abonadoOriginal;
    }

    // Getters y setters (solo si necesitas mutarlos; para lectura bastan getters)
    public Integer getIdApuesta() {
        return idApuesta;
    }

    public Integer getIdCaballo() {
        return idCaballo;
    }

    public String getNombreCaballo() {
        return nombreCaballo;
    }

    public int getMontoApostado() {
        return montoApostado;
    }

    public int getMontoAbonado() {
        return montoAbonado;
    }

    public Integer getIdApostador() {
        return idApostador;
    }

    public String getNombreApostador() {
        return nombreApostador;
    }

    public int getSaldo() {
        return saldo;
    }

    public boolean isOrigenPago() {
        return origenPago;
    }

    public String getSaldoUsado() {
        return saldoUsado;
    }

    public int getSaldoOriginal() {
        return saldoOriginal;
    }

    public int getAbonadoOriginal() {
        return abonadoOriginal;
    }
}
