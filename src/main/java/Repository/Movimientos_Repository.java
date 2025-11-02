package Repository;

import Config.DataSource;
import Model.MovimientoParaVista_DTO;
import Model.Movimientos_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el acceso a la base de datos para la entidad Movimientos. Se enfoca
 * en operaciones CRUD y consultas, devolviendo objetos de Modelo/DTO.
 */
public class Movimientos_Repository {

    /**
     * Inserta un nuevo movimiento en la base de datos.
     *
     * @param model El objeto Movimientos_Model a insertar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void insert(Movimientos_Model model) throws SQLException {
        String sql = "INSERT INTO movimientos(fecha, monto, descripcion, fk_apostadores, fk_apuestas, fk_tipomovimientos, fk_carreras) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            pst.setObject(i++, model.getFecha()); // Se usa setObject para LocalDateTime
            pst.setInt(i++, model.getMonto());
            pst.setString(i++, model.getDescripcion());
            setNullableInt(pst, i++, model.getFk_apostadores());
            setNullableInt(pst, i++, model.getFk_apuestas());
            setNullableInt(pst, i++, model.getFk_tipomovimientos());
            setNullableInt(pst, i++, model.getFk_carreras());
            pst.executeUpdate();
        }
    }

    /**
     * Inserta un nuevo movimiento usando una conexión existente (para
     * transacciones).
     *
     * @param conn La conexión de la transacción actual.
     * @param model El objeto Movimientos_Model a insertar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void insert(Connection conn, Movimientos_Model model) throws SQLException {
        String sql = "INSERT INTO movimientos(fecha, monto, descripcion, fk_apostadores, fk_apuestas, fk_tipomovimientos, fk_carreras) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            pst.setObject(i++, model.getFecha());
            pst.setInt(i++, model.getMonto());
            pst.setString(i++, model.getDescripcion());
            setNullableInt(pst, i++, model.getFk_apostadores());
            setNullableInt(pst, i++, model.getFk_apuestas());
            setNullableInt(pst, i++, model.getFk_tipomovimientos());
            setNullableInt(pst, i++, model.getFk_carreras());
            pst.executeUpdate();
        }
    }
    
    public List<Movimientos_Model> findMovimientosDeResultadosPorCarrera(Connection conn, int idCarrera) throws SQLException {
    List<Movimientos_Model> lista = new ArrayList<>();
    // 5=Ganancia Neta, 7=Apuesta Perdida
    String sql = "SELECT * FROM movimientos WHERE fk_carreras = ? AND fk_tipomovimientos IN (5, 7)"; 
    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setInt(1, idCarrera);
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                lista.add(new Movimientos_Model(
                    rs.getInt("idmovimientos"), rs.getObject("fecha", LocalDate.class),
                    rs.getInt("monto"), rs.getString("descripcion"),
                    rs.getInt("fk_apostadores"), (Integer) rs.getObject("fk_apuestas"),
                    rs.getInt("fk_tipomovimientos"), (Integer) rs.getObject("fk_carreras")
                ));
            }
        }
    }
    return lista;
}


    /**
     * Elimina movimientos de ganancias y devoluciones asociados a una carrera.
     *
     * @param idCarrera El ID de la carrera cuyos movimientos se eliminarán.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void deleteMovimientosDeResultadosPorCarrera(int idCarrera) throws SQLException {
        String sql = "DELETE FROM movimientos WHERE fk_carreras = ? AND fk_tipomovimientos IN (5, 6)";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idCarrera);
            pst.executeUpdate();
        }
    }

    public List<MovimientoParaVista_DTO> findMovimientosParaVista(String apostadorSearch, String tipoMovimientoSearch,
            LocalDate dateFrom, LocalDate dateTo) throws SQLException {

        System.out.println("\n--- REPOSITORY (Movimientos): Buscando movimientos para la vista ---");
        System.out.println("1. Filtros recibidos:");
        System.out.println("   - Búsqueda Apostador: '" + apostadorSearch + "'");
        System.out.println("   - Tipo Movimiento: '" + tipoMovimientoSearch + "'");
        System.out.println("   - Fecha Desde: " + dateFrom);
        System.out.println("   - Fecha Hasta: " + dateTo);

        List<MovimientoParaVista_DTO> resultados = new ArrayList<>();
        String sql = """
        SELECT m.*, a.nombre AS apostador, tm.descripcion AS tipo_movimiento,
               c.nombre AS nombre_carrera
        FROM movimientos m
        JOIN apostadores a ON m.fk_apostadores = a.idapostadores
        JOIN tipomovimientos tm ON m.fk_tipomovimientos = tm.idtipomovimientos
        LEFT JOIN carreras c ON m.fk_carreras = c.idcarreras
        WHERE a.nombre LIKE ? AND tm.descripcion LIKE ?
    """;

        if (dateFrom != null && dateTo != null) {
            sql += " AND m.fecha BETWEEN ? AND ?";
        }
        sql += " ORDER BY m.idmovimientos DESC";

        System.out.println("2. Ejecutando consulta SQL...");
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            int i = 1;
            pst.setString(i++, "%" + apostadorSearch + "%");
            pst.setString(i++, "%" + ("Todos".equalsIgnoreCase(tipoMovimientoSearch) ? "" : tipoMovimientoSearch) + "%");
            if (dateFrom != null && dateTo != null) {
                pst.setObject(i++, dateFrom);
                pst.setObject(i++, dateTo);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    LocalDate fecha = rs.getObject("fecha", LocalDate.class);

                    Movimientos_Model movimiento = new Movimientos_Model(
                            rs.getInt("idmovimientos"), fecha,
                            rs.getInt("monto"), rs.getString("descripcion"),
                            (Integer) rs.getObject("fk_apostadores"), (Integer) rs.getObject("fk_apuestas"),
                            (Integer) rs.getObject("fk_tipomovimientos"), (Integer) rs.getObject("fk_carreras")
                    );

                    String nombreApostador = rs.getString("apostador");
                    String tipoMovimiento = rs.getString("tipo_movimiento");
                    String nombreCarrera = rs.getString("nombre_carrera");

                    resultados.add(new MovimientoParaVista_DTO(movimiento, nombreApostador, tipoMovimiento, nombreCarrera));

                    System.out.println(String.format("   -> Fila encontrada: [ID: %d, Fecha: %s, Monto: %d, Apostador: %s, Carrera: %s]",
                            movimiento.getIdMovimiento(), fecha, movimiento.getMonto(), nombreApostador, nombreCarrera));
                }
            }
        }
        System.out.println("3. Consulta finalizada. Total de movimientos encontrados: " + resultados.size());
        System.out.println("--- REPOSITORY (Movimientos): Fin de la búsqueda ---");
        return resultados;
    }

    public List<Movimientos_Model> findAllOrderByFecha() throws SQLException {
        List<Movimientos_Model> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimientos ORDER BY fecha ASC, idmovimientos ASC";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                // --- Se aplica la misma corrección aquí ---
                String fechaStr = rs.getString("fecha");
                LocalDate fecha = null;
                if (fechaStr != null && !fechaStr.isBlank()) {
                    try {
                        fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    } catch (Exception e) {
                        System.err.println("No se pudo parsear la fecha: " + fechaStr);
                    }
                }

                lista.add(new Movimientos_Model(
                        rs.getInt("idmovimientos"), fecha,
                        rs.getInt("monto"), rs.getString("descripcion"),
                        rs.getInt("fk_apostadores"), (Integer) rs.getObject("fk_apuestas"),
                        rs.getInt("fk_tipomovimientos"), (Integer) rs.getObject("fk_carreras")
                ));
            }
        }
        return lista;
    }

    /**
     * Inserta una lista de movimientos en un solo lote (batch). Mucho más
     * eficiente que insertar uno por uno.
     *
     * @param movimientos La lista de movimientos a insertar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void insertBatch(List<Movimientos_Model> movimientos) throws SQLException {
        String sql = "INSERT INTO movimientos(fecha, monto, descripcion, fk_apostadores, fk_apuestas, fk_tipomovimientos, fk_carreras) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            for (Movimientos_Model model : movimientos) {
                int i = 1;
                pst.setObject(i++, model.getFecha());
                pst.setInt(i++, model.getMonto());
                pst.setString(i++, model.getDescripcion());
                setNullableInt(pst, i++, model.getFk_apostadores());
                setNullableInt(pst, i++, model.getFk_apuestas());
                setNullableInt(pst, i++, model.getFk_tipomovimientos());
                setNullableInt(pst, i++, model.getFk_carreras());
                pst.addBatch(); // Se agrega la operación al lote
            }
            pst.executeBatch(); // Se ejecuta el lote de operaciones
        }
    }
    
    /**
     * Helper para asignar un valor Integer que puede ser nulo a un
     * PreparedStatement.
     */
    private void setNullableInt(PreparedStatement pst, int index, Integer value) throws SQLException {
        if (value != null) {
            pst.setInt(index, value);
        } else {
            pst.setNull(index, Types.INTEGER);
        }
    }

    // Añade estos métodos a tu clase Movimientos_Repository
    /**
     * Inserta una lista de movimientos en un solo lote (batch) usando una
     * conexión existente. Es mucho más eficiente que insertar uno por uno
     * dentro de una transacción.
     *
     * @param conn La conexión de la transacción actual.
     * @param movimientos La lista de movimientos a insertar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void insertBatch(Connection conn, List<Movimientos_Model> movimientos) throws SQLException {
        String sql = "INSERT INTO movimientos(fecha, monto, descripcion, fk_apostadores, fk_apuestas, fk_tipomovimientos, fk_carreras) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            for (Movimientos_Model model : movimientos) {
                int i = 1;
                pst.setObject(i++, model.getFecha());
                pst.setInt(i++, model.getMonto());
                pst.setString(i++, model.getDescripcion());
                setNullableInt(pst, i++, model.getFk_apostadores());
                setNullableInt(pst, i++, model.getFk_apuestas());
                setNullableInt(pst, i++, model.getFk_tipomovimientos());
                setNullableInt(pst, i++, model.getFk_carreras());
                pst.addBatch(); // Se agrega la operación al lote
            }
            pst.executeBatch(); // Se ejecuta el lote de operaciones
        }
    }

    /**
     * Elimina movimientos de ganancias y devoluciones asociados a una carrera
     * usando una conexión existente.
     *
     * @param conn La conexión de la transacción actual.
     * @param idCarrera El ID de la carrera cuyos movimientos se eliminarán.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void deleteMovimientosDeResultadosPorCarrera(Connection conn, int idCarrera) throws SQLException {
        String sql = "DELETE FROM movimientos WHERE fk_carreras = ? AND fk_tipomovimientos IN (5, 6)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idCarrera);
            pst.executeUpdate();
        }
    }

    public List<Movimientos_Model> findByApostadorId(int idApostador) throws SQLException {
        List<Movimientos_Model> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimientos WHERE fk_apostadores = ? ORDER BY fecha ASC";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idApostador);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    // Se usa la conversión manual y segura de fecha
                    String fechaStr = rs.getString("fecha");
                    LocalDate fecha = null;
                    if (fechaStr != null && !fechaStr.isBlank()) {
                        try {
                            fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        } catch (Exception e) {
                            /* Log error */ }
                    }

                    lista.add(new Movimientos_Model(
                            rs.getInt("idmovimientos"), fecha,
                            rs.getInt("monto"), rs.getString("descripcion"),
                            rs.getInt("fk_apostadores"), (Integer) rs.getObject("fk_apuestas"),
                            rs.getInt("fk_tipomovimientos"), (Integer) rs.getObject("fk_carreras")
                    ));
                }
            }
        }
        return lista;
    }
}
