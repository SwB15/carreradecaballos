package Repository;

import Config.DataSource;
import Model.Abonos_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Gestiona el acceso a la base de datos para la entidad Abonos. Realiza
 * operaciones CRUD puras.
 */
public class Abonos_Repository {

    /**
     * Inserta un nuevo registro de abono en la base de datos.
     *
     * @param abono El objeto Abonos_Model a insertar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void insert(Abonos_Model abono) throws SQLException {
        String sql = "INSERT INTO abonos(fecha, monto, origen, fk_apuestas, fk_apostadores) VALUES(?,?,?,?,?)";

        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            int i = 1; // Se inicializa el contador de parámetros.

            // Se usa setObject para manejar el tipo LocalDate correctamente.
            pst.setObject(i++, abono.getFecha());
            pst.setInt(i++, abono.getMonto());
            pst.setString(i++, abono.getOrigen());
            pst.setInt(i++, abono.getFk_apuestas());
            pst.setInt(i++, abono.getFk_apostadores());

            pst.executeUpdate();
        }
    }

    /**
     * Inserta un nuevo abono usando una conexión existente (para
     * transacciones).
     *
     * @param conn La conexión de la transacción actual.
     * @param model El objeto Abonos_Model a insertar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void insert(Connection conn, Abonos_Model model) throws SQLException {
        String sql = "INSERT INTO abonos(fecha, monto, origen, fk_apuestas, fk_apostadores) VALUES(?,?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            pst.setObject(i++, model.getFecha());
            pst.setInt(i++, model.getMonto());
            pst.setString(i++, model.getOrigen());
            pst.setInt(i++, model.getFk_apuestas());
            pst.setInt(i++, model.getFk_apostadores());
            pst.executeUpdate();
        }
    }
}
