package Repository;

import Config.DataSource;
import Model.Caballos_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el acceso a la base de datos para la entidad Caballos. Se enfoca
 * únicamente en operaciones CRUD y consultas, devolviendo objetos de Modelo.
 */
public class Caballos_Repository {

    // --- Métodos CRUD ---
    public void insert(Caballos_Model model) throws SQLException {
        String sql = "INSERT INTO caballos(caballos, jinete, observacion, fk_estados) VALUES(?,?,?,?)";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            int i = 1;
            pst.setString(i++, model.getCaballos());
            pst.setString(i++, model.getJinete());
            pst.setString(i++, model.getObservacion());
            pst.setInt(i++, model.getFk_estados());

            pst.executeUpdate();
        }
    }

    public void update(Caballos_Model model) throws SQLException {
        String sql = "UPDATE caballos SET caballos = ?, jinete = ?, observacion = ?, fk_estados = ? WHERE idcaballos = ?";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            int i = 1;
            pst.setString(i++, model.getCaballos());
            pst.setString(i++, model.getJinete());
            pst.setString(i++, model.getObservacion());
            pst.setInt(i++, model.getFk_estados());
            pst.setInt(i++, model.getIdcaballos());

            pst.executeUpdate();
        }
    }

    // --- Métodos de Consulta ---
    /**
     * Busca y devuelve una lista de caballos filtrada por nombre y estado.
     *
     * @param search El término de búsqueda para el nombre del caballo.
     * @param stateFilter El estado a filtrar ("activo", "inactivo", o todos si
     * es diferente).
     * @return Una lista de objetos Caballos_Model.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Caballos_Model> findCaballos(String search, String stateFilter) throws SQLException {
        List<Caballos_Model> caballos = new ArrayList<>();

        // Se construye la consulta SQL dinámicamente pero de forma segura.
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT c.idcaballos, c.caballos, c.jinete, c.observacion, e.idestados AS fk_estados "
                + "FROM caballos c JOIN estados e ON c.fk_estados = e.idestados "
                + "WHERE c.caballos LIKE ?"
        );

        if ("activo".equalsIgnoreCase(stateFilter)) {
            sqlBuilder.append(" AND e.estados = 'activo'");
        } else if ("inactivo".equalsIgnoreCase(stateFilter)) {
            sqlBuilder.append(" AND e.estados = 'inactivo'");
        }
        sqlBuilder.append(" ORDER BY c.idcaballos DESC");

        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sqlBuilder.toString())) {

            pst.setString(1, "%" + search + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    // Se mapea cada fila del resultado a un objeto del Modelo.
                    Caballos_Model caballo = new Caballos_Model(
                            rs.getInt("idcaballos"),
                            rs.getString("caballos"),
                            rs.getString("jinete"),
                            rs.getString("observacion"),
                            rs.getInt("fk_estados")
                    );
                    caballos.add(caballo);
                }
            }
        }
        return caballos;
    }

    public int findMaxId() throws SQLException {
        String sql = "SELECT MAX(idcaballos) AS max_id FROM caballos";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("max_id");
            }
            return 0;
        }
    }
}
