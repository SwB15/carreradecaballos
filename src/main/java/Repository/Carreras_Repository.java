package Repository;

import Config.DataSource;
import Model.Carreras_Model;
import Model.Caballos_Model;
import Model.CarreraParaVista_DTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Gestiona el acceso a la base de datos para la entidad Carreras. Se enfoca
 * únicamente en operaciones CRUD y consultas, devolviendo objetos de Modelo o
 * DTO.
 */
public class Carreras_Repository {

    // --- Métodos CRUD ---
    public void insert(Carreras_Model model) throws SQLException {
        String sql = "INSERT INTO carreras(nombre, lugar, fecha, fechalimite, idganador, observacion, fk_estados, comision) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            pst.setString(i++, model.getNombre());
            pst.setString(i++, model.getLugar());
            pst.setObject(i++, model.getFecha()); // Se usa setObject para LocalDate
            pst.setObject(i++, model.getFechaLimite()); // Se usa setObject para LocalDate

            // Se maneja el ganador nulo de forma segura
            if (model.getFk_ganador() != null) {
                pst.setInt(i++, model.getFk_ganador());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }

            pst.setString(i++, model.getObservacion());
            pst.setInt(i++, model.getFk_estados());
            pst.setInt(i++, model.getComision());

            pst.executeUpdate();
        }
    }

    public void update(Carreras_Model model) throws SQLException {
        String sql = "UPDATE carreras SET nombre = ?, lugar = ?, fecha = ?, fechalimite = ?, idganador = ?, observacion = ?, fk_estados = ?, comision = ? WHERE idcarreras = ?";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            pst.setString(i++, model.getNombre());
            pst.setString(i++, model.getLugar());
            pst.setObject(i++, model.getFecha());
            pst.setObject(i++, model.getFechaLimite());

            if (model.getFk_ganador() != null) {
                pst.setInt(i++, model.getFk_ganador());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }

            pst.setString(i++, model.getObservacion());
            pst.setInt(i++, model.getFk_estados());
            pst.setInt(i++, model.getComision());
            pst.setInt(i++, model.getIdCarrera());

            pst.executeUpdate();
        }
    }

    // --- Métodos de Consulta ---
    // En Repository/Carreras_Repository.java
    public List<CarreraParaVista_DTO> findCarrerasParaVista(String search, String fase, String statusFilter) throws SQLException {
        List<CarreraParaVista_DTO> carreras = new ArrayList<>();
        String sql = """
        SELECT
            c.idcarreras, c.nombre, c.lugar, c.fecha, c.fechalimite, c.idganador AS fk_ganador,
            c.observacion, c.fk_estados, c.comision, e.estados,
            ganador.caballos AS nombre_ganador,
            GROUP_CONCAT(p.idcaballos) AS ids_participantes,
            GROUP_CONCAT(p.caballos) AS nombres_participantes
        FROM carreras c
        JOIN estados e ON c.fk_estados = e.idestados
        LEFT JOIN caballos ganador ON c.idganador = ganador.idcaballos
        LEFT JOIN detallecarreras dc ON c.idcarreras = dc.fk_carreras
        LEFT JOIN caballos p ON dc.fk_caballos = p.idcaballos
        WHERE c.nombre LIKE ?
    """;

        // Construcción dinámica de la consulta
        if ("finalizados".equalsIgnoreCase(fase)) {
            sql += " AND c.idganador IS NOT NULL";
        } else if ("pendientes".equalsIgnoreCase(fase)) {
            sql += " AND c.idganador IS NULL";
        }
        if ("activo".equalsIgnoreCase(statusFilter)) {
            sql += " AND e.estados = 'activo'";
        } else if ("inactivo".equalsIgnoreCase(statusFilter)) {
            sql += " AND e.estados = 'inactivo'";
        }
        sql += " GROUP BY c.idcarreras ORDER BY c.idcarreras DESC";

//    sql += " GROUP BY c.idcarreras ORDER BY c.idcarreras DESC";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, "%" + search + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {

                    // --- INICIO DE LA CORRECCIÓN ---
                    // Se obtiene la fecha límite y se comprueba si es nula antes de usarla.
                    LocalDate fechaLimite = rs.getObject("fechalimite") != null ? rs.getObject("fechalimite", LocalDate.class) : null;
                    // --- FIN DE LA CORRECCIÓN ---

                    Carreras_Model carrera = new Carreras_Model(
                            rs.getInt("idcarreras"), rs.getString("nombre"), rs.getString("lugar"),
                            rs.getObject("fecha", LocalDate.class),
                            fechaLimite, // Se usa la variable segura
                            (Integer) rs.getObject("fk_ganador"), rs.getString("observacion"),
                            rs.getInt("fk_estados"), rs.getInt("comision")
                    );

                    List<Caballos_Model> participantes = new ArrayList<>();
                    String ids = rs.getString("ids_participantes");
                    String nombres = rs.getString("nombres_participantes");
                    if (ids != null && nombres != null) {
                        String[] idArray = ids.split(",");
                        String[] nameArray = nombres.split(",");
                        for (int i = 0; i < idArray.length; i++) {
                            participantes.add(new Caballos_Model(Integer.parseInt(idArray[i]), nameArray[i], null, null, 1));
                        }
                    }

                    CarreraParaVista_DTO dto = new CarreraParaVista_DTO(
                            carrera, rs.getString("estados"),
                            rs.getString("nombre_ganador"), participantes, carrera.getObservacion()
                    );
                    carreras.add(dto);
                }
            }
        }
        return carreras;
    }

    public HashMap<Integer, String> findCaballosPorCarrera(int idCarrera) throws SQLException {
        HashMap<Integer, String> caballosMap = new HashMap<>();
        String sql = "SELECT c.idcaballos, c.caballos FROM detallecarreras dc "
                + "JOIN caballos c ON dc.fk_caballos = c.idcaballos "
                + "WHERE dc.fk_carreras = ?";

        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idCarrera);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    caballosMap.put(rs.getInt("idcaballos"), rs.getString("caballos"));
                }
            }
        }
        return caballosMap;
    }

    /**
     * Busca una carrera por su ID dentro de una transacción existente.
     *
     * @param conn La conexión de la transacción actual.
     * @param idCarrera El ID de la carrera a buscar.
     * @return El objeto Carreras_Model.
     * @throws SQLException Si la carrera no se encuentra o hay un error.
     */
    public Carreras_Model findById(Connection conn, int idCarrera) throws SQLException {
        String sql = "SELECT * FROM carreras WHERE idcarreras = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idCarrera);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Carreras_Model(
                            rs.getInt("idcarreras"), rs.getString("nombre"), rs.getString("lugar"),
                            rs.getObject("fecha", LocalDate.class), rs.getObject("fechalimite", LocalDate.class),
                            (Integer) rs.getObject("idganador"), rs.getString("observacion"),
                            rs.getInt("fk_estados"), rs.getInt("comision")
                    );
                } else {
                    throw new SQLException("No se encontró la carrera con ID: " + idCarrera);
                }
            }
        }
    }

    // Añade estos métodos a tu clase Carreras_Repository
    /**
     * Inserta una nueva carrera y devuelve su ID autogenerado. Necesario para
     * poder insertar sus detalles en la misma transacción.
     *
     * @param conn La conexión de la transacción actual.
     * @param model El objeto de la carrera a insertar.
     * @return El ID de la carrera recién creada.
     * @throws SQLException Si la inserción falla.
     */
    public int insertAndGetId(Connection conn, Carreras_Model model) throws SQLException {
        String sql = "INSERT INTO carreras(nombre, lugar, fecha, fechalimite, idganador, observacion, fk_estados, comision) VALUES(?,?,?,?,?,?,?,?)";
        // Se añade el flag para retornar las claves generadas.
        try (PreparedStatement pst = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            pst.setString(i++, model.getNombre());
            pst.setString(i++, model.getLugar());
            pst.setObject(i++, model.getFecha());
            pst.setObject(i++, model.getFechaLimite());

            if (model.getFk_ganador() != null) {
                pst.setInt(i++, model.getFk_ganador());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }

            pst.setString(i++, model.getObservacion());
            pst.setInt(i++, model.getFk_estados());
            pst.setInt(i++, model.getComision());

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La inserción de la carrera falló, no se crearon filas.");
            }

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Devuelve el ID generado
                } else {
                    throw new SQLException("La inserción de la carrera falló, no se obtuvo el ID.");
                }
            }
        }
    }

    /**
     * Actualiza una carrera usando una conexión existente (para transacciones).
     *
     * @param conn La conexión de la transacción actual.
     * @param model El objeto con los datos a actualizar.
     * @throws SQLException Si ocurre un error.
     */
    public void update(Connection conn, Carreras_Model model) throws SQLException {
        String sql = "UPDATE carreras SET nombre = ?, lugar = ?, fecha = ?, fechalimite = ?, idganador = ?, observacion = ?, fk_estados = ?, comision = ? WHERE idcarreras = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            pst.setString(i++, model.getNombre());
            pst.setString(i++, model.getLugar());
            pst.setObject(i++, model.getFecha());
            pst.setObject(i++, model.getFechaLimite());

            if (model.getFk_ganador() != null) {
                pst.setInt(i++, model.getFk_ganador());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }

            pst.setString(i++, model.getObservacion());
            pst.setInt(i++, model.getFk_estados());
            pst.setInt(i++, model.getComision());
            pst.setInt(i++, model.getIdCarrera());

            pst.executeUpdate();
        }
    }

    public int findMaxId() throws SQLException {
        String sql = "SELECT MAX(idcarreras) FROM carreras";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0; // Devuelve 0 si la tabla está vacía
    }

    /**
     * Calcula el siguiente ID disponible para una nueva carrera. Lo hace
     * buscando el ID máximo actual en la tabla y sumándole 1.
     *
     * @return El próximo ID para una nueva carrera. Si la tabla está vacía,
     * devuelve 1.
     * @throws SQLException Si ocurre un error al consultar la base de datos.
     */
    public int findNextId() throws SQLException {
        // La consulta busca el valor máximo en la columna 'idcarreras'.
        String sql = "SELECT MAX(idcarreras) FROM carreras";
        int nextId = 1; // Valor por defecto si la tabla está vacía.

        // Usamos tu método para obtener la conexión y try-with-resources.
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            // Si el ResultSet tiene un resultado...
            if (rs.next()) {
                // Obtenemos el ID máximo. Si la tabla está vacía, MAX() devuelve NULL,
                // y rs.getInt(1) convenientemente devuelve 0 en ese caso.
                int maxId = rs.getInt(1);

                // El siguiente ID es el máximo encontrado + 1.
                nextId = maxId + 1;
            }
        }
        // La SQLException se propaga automáticamente gracias al "throws".

        return nextId;
    }
}
