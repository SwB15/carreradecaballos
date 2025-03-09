package Model;

/**
 *
 * @author SwichBlade15
 */
public class ReportesHistorialApostador_Model {

    private int idApuesta;
    private String apuesta;
    private int monto;
    private String fecha;
    private String caballo;
    private String resultado;

    public ReportesHistorialApostador_Model() {
    }

    public ReportesHistorialApostador_Model(int idApuesta, String apuesta, int monto, String fecha, String caballo, String resultado) {
        this.idApuesta = idApuesta;
        this.apuesta = apuesta;
        this.monto = monto;
        this.fecha = fecha;
        this.caballo = caballo;
        this.resultado = resultado;
    }

    public int getIdApuesta() {
        return idApuesta;
    }

    public void setIdApuesta(int idApuesta) {
        this.idApuesta = idApuesta;
    }

    public String getApuesta() {
        return apuesta;
    }

    public void setApuesta(String apuesta) {
        this.apuesta = apuesta;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCaballo() {
        return caballo;
    }

    public void setCaballo(String caballo) {
        this.caballo = caballo;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
