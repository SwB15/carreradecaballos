package Repository;

import Config.DataSource;
import Model.Detallecarreras_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Gestiona el acceso a la base de datos para la entidad de unión
 * DetalleCarreras. Se encarga de asociar y desasociar caballos con carreras.
 */
public class Detallecarreras_Repository {

    /**
     * Inserta una nueva asociación entre una carrera y un caballo.
     *
     * @param model El objeto con los IDs de la carrera y el caballo.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void insert(Detallecarreras_Model model) throws SQLException {
        String sql = "INSERT INTO detallecarreras(fk_carreras, fk_caballos) VALUES(?,?)";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            int i = 1;
            pst.setInt(i++, model.getFk_carreras());
            pst.setInt(i++, model.getFk_caballos());

            pst.executeUpdate();
        }
    }

    /**
     * Elimina todas las asociaciones de caballos para una carrera específica.
     * Útil para cuando se edita la lista de participantes de una carrera.
     *
     * @param idCarrera El ID de la carrera cuyos detalles se van a eliminar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void deleteByCarreraId(int idCarrera) throws SQLException {
        String sql = "DELETE FROM detallecarreras WHERE fk_carreras = ?";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, idCarrera);
            pst.executeUpdate();
        }
    }

    /**
     * Inserta una lista de caballos participantes para una carrera en un solo
     * lote (batch).
     *
     * @param conn La conexión de la transacción actual.
     * @param idCarrera El ID de la carrera a la que pertenecen los caballos.
     * @param idsCaballos La lista de IDs de los caballos a insertar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void insertBatch(Connection conn, int idCarrera, List<Integer> idsCaballos) throws SQLException {
        String sql = "INSERT INTO detallecarreras(fk_carreras, fk_caballos) VALUES(?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            for (Integer idCaballo : idsCaballos) {
                pst.setInt(1, idCarrera);
                pst.setInt(2, idCaballo);
                pst.addBatch();
            }
            pst.executeBatch();
        }
    }

    /**
     * Elimina todas las asociaciones de caballos para una carrera específica
     * usando una conexión existente (para transacciones).
     *
     * @param conn La conexión de la transacción actual.
     * @param idCarrera El ID de la carrera cuyos detalles se van a eliminar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void deleteByCarreraId(Connection conn, int idCarrera) throws SQLException {
        String sql = "DELETE FROM detallecarreras WHERE fk_carreras = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idCarrera);
            pst.executeUpdate();
        }
    }

    /**
     * Inserta una nueva asociación entre carrera y caballo usando una conexión
     * existente.
     *
     * @param conn La conexión de la transacción actual.
     * @param model El objeto con los IDs.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void insert(Connection conn, Detallecarreras_Model model) throws SQLException {
        String sql = "INSERT INTO detallecarreras(fk_carreras, fk_caballos) VALUES(?,?)";

        System.out.println("--- REPOSITORY (Detallecarreras): Inicia 'insert' ---");
        System.out.println(String.format("   -> Intentando insertar: [fk_carreras: %d, fk_caballos: %d]",
                model.getFk_carreras(), model.getFk_caballos()));

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            pst.setInt(i++, model.getFk_carreras());
            pst.setInt(i++, model.getFk_caballos());

            pst.executeUpdate();
            System.out.println("   -> Inserción en 'detallecarreras' completada.");
        }
    }
}
