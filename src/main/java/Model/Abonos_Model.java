
package Model;

/**
 *
 * @author SwichBlade15
 */
public class Abonos_Model {
    private int idabonos;
    private String fecha;
    private Integer monto;
    private String origen;
    private Integer fk_apuestas;
    private Integer fk_apostadores;

    public Abonos_Model() {
    }

    public Abonos_Model(int idabonos, String fecha, Integer monto, String origen, Integer fk_apuestas, Integer fk_apostadores) {
        this.idabonos = idabonos;
        this.fecha = fecha;
        this.monto = monto;
        this.origen = origen;
        this.fk_apuestas = fk_apuestas;
        this.fk_apostadores = fk_apostadores;
    }

    public int getIdabonos() {
        return idabonos;
    }

    public void setIdabonos(int idabonos) {
        this.idabonos = idabonos;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Integer getMonto() {
        return monto;
    }

    public void setMonto(Integer monto) {
        this.monto = monto;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public Integer getFk_apuestas() {
        return fk_apuestas;
    }

    public void setFk_apuestas(Integer fk_apuestas) {
        this.fk_apuestas = fk_apuestas;
    }

    public Integer getFk_apostadores() {
        return fk_apostadores;
    }

    public void setFk_apostadores(Integer fk_apostadores) {
        this.fk_apostadores = fk_apostadores;
    }
}
