package Model;

import java.time.LocalDate;

/**
 * Representa un movimiento o transacción financiera en el sistema. Sirve como
 * un registro de auditoría para todos los cambios de saldo.
 */
public class Movimientos_Model {

    private int idMovimiento;
    private LocalDate fecha;
    private int monto;
    private String descripcion; // "descripcion" es más estándar para un log de transacciones.

    // Se mantiene la nomenclatura para claves foráneas.
    private Integer fk_apostadores;
    private Integer fk_apuestas;
    private Integer fk_tipomovimientos;
    private Integer fk_carreras;

    /**
     * Constructor por defecto.
     */
    public Movimientos_Model() {
    }

    /**
     * Constructor para inicializar un objeto Movimientos_Model con todos sus
     * datos.
     *
     * @param idMovimiento El ID único del movimiento.
     * @param fecha La fecha y hora en que ocurrió el movimiento.
     * @param monto El importe del movimiento (positivo para ingresos, negativo
     * para egresos).
     * @param descripcion Una breve descripción de la transacción.
     * @param fk_apostadores El ID del apostador asociado.
     * @param fk_apuestas El ID de la apuesta asociada (si aplica).
     * @param fk_tipomovimientos El ID del tipo de movimiento.
     * @param fk_carreras El ID de la carrera asociada (si aplica).
     */
    public Movimientos_Model(int idMovimiento, LocalDate fecha, int monto, String descripcion,
            Integer fk_apostadores, Integer fk_apuestas,
            Integer fk_tipomovimientos, Integer fk_carreras) {
        this.idMovimiento = idMovimiento;
        this.fecha = fecha;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fk_apostadores = fk_apostadores;
        this.fk_apuestas = fk_apuestas;
        this.fk_tipomovimientos = fk_tipomovimientos;
        this.fk_carreras = fk_carreras;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getFk_apostadores() {
        return fk_apostadores;
    }

    public void setFk_apostadores(Integer fk_apostadores) {
        this.fk_apostadores = fk_apostadores;
    }

    public Integer getFk_apuestas() {
        return fk_apuestas;
    }

    public void setFk_apuestas(Integer fk_apuestas) {
        this.fk_apuestas = fk_apuestas;
    }

    public Integer getFk_tipomovimientos() {
        return fk_tipomovimientos;
    }

    public void setFk_tipomovimientos(Integer fk_tipomovimientos) {
        this.fk_tipomovimientos = fk_tipomovimientos;
    }

    public Integer getFk_carreras() {
        return fk_carreras;
    }

    public void setFk_carreras(Integer fk_carreras) {
        this.fk_carreras = fk_carreras;
    }
}
