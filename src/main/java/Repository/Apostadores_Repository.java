package Repository;

import Config.DataSource;
import Model.ApostadorParaVista_DTO;
import Model.Apostadores_Model;
import Model.Detalle_Pendientes;
import Model.HistorialApuesta_DTO;
import Model.Resultado_Pendientes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestiona el acceso a la base de datos para la entidad Apostadores. Se enfoca
 * únicamente en operaciones CRUD y consultas, devolviendo objetos de Modelo o
 * DTO.
 */
public class Apostadores_Repository {

    // --- Métodos CRUD Básicos ---
    public boolean insert(Apostadores_Model model) throws SQLException {
        String sql = "INSERT INTO apostadores(cedula, nombre, observacion, saldo, fk_estados) VALUES(?,?,?,?,?)";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            pst.setString(i++, model.getCedula());
            pst.setString(i++, model.getNombre());
            pst.setString(i++, model.getObservacion());
            pst.setInt(i++, 0); // Saldo inicial es 0
            pst.setInt(i++, model.getFk_estados());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean update(Apostadores_Model model) throws SQLException {
        String sql = "UPDATE apostadores SET cedula = ?, nombre = ?, observacion = ?, fk_estados = ? WHERE idapostadores = ?";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            pst.setString(i++, model.getCedula());
            pst.setString(i++, model.getNombre());
            pst.setString(i++, model.getObservacion());
            pst.setInt(i++, model.getFk_estados());
            pst.setInt(i++, model.getIdapostadores());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean updateSaldo(int idApostador, int monto) throws SQLException {
        // Este método actualiza el saldo de forma atómica.
        String sql = "UPDATE apostadores SET saldo = saldo + ? WHERE idapostadores = ?";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            pst.setInt(i++, monto);
            pst.setInt(i++, idApostador);
            return pst.executeUpdate() > 0;
        }
    }

    // --- Métodos de Consulta Específicos ---
    public int getMaxId() throws SQLException {
        String sql = "SELECT MAX(idapostadores) AS max_id FROM apostadores";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("max_id");
            }
            return 0;
        }
    }

    public int getSaldo(int idApostador) throws SQLException {
        String sql = "SELECT saldo FROM apostadores WHERE idapostadores = ?";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idApostador);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("saldo");
                }
                // Se podría lanzar una excepción si el apostador no se encuentra.
                return 0;
            }
        }
    }

    public List<ApostadorParaVista_DTO> findApostadoresParaVista(String search, String stateFilter) throws SQLException {
        List<ApostadorParaVista_DTO> resultados = new ArrayList<>();
        // La consulta SQL obtiene todos los datos necesarios.
        String sql = """
        SELECT a.idapostadores, a.cedula, a.nombre, a.saldo, a.observacion, a.fk_estados, e.estados,
               ds.color_status AS estado_deuda
        FROM apostadores a
        JOIN estados e ON a.fk_estados = e.idestados
        LEFT JOIN (
            SELECT 
                ap.fk_apostadores, 
                MAX(CASE 
                    WHEN date(ap.fechalimite) < date('now', 'localtime') THEN 4 -- ROJO (Vencido)
                    WHEN date(ap.fechalimite) = date('now', 'localtime') THEN 3 -- VERDE (Vence Hoy)
                    WHEN date(ap.fechalimite) = date('now', 'localtime', '+1 day') THEN 2 -- AZUL (Vence Mañana)
                    ELSE 1 -- AMARILLO (Vence después)
                END) as color_status
            FROM apuestas ap
            JOIN carreras c ON ap.fk_carreras = c.idcarreras
            WHERE c.idganador IS NOT NULL AND ap.abonado < ap.monto AND NOT EXISTS (
                SELECT 1 FROM apuestas win_ap
                JOIN carreras win_c ON win_ap.fk_carreras = win_c.idcarreras
                WHERE win_ap.fk_apostadores = ap.fk_apostadores 
                  AND win_ap.fk_carreras = ap.fk_carreras 
                  AND win_ap.fk_caballos = win_c.idganador
            ) GROUP BY ap.fk_apostadores
        ) AS ds ON a.idapostadores = ds.fk_apostadores
        WHERE a.nombre LIKE ?
    """;

        if ("activo".equalsIgnoreCase(stateFilter)) {
            sql += " AND e.estados = 'activo' ";
        } else if ("inactivo".equalsIgnoreCase(stateFilter)) {
            sql += " AND e.estados = 'inactivo' ";
        }
        sql += " ORDER BY a.idapostadores DESC";

        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "%" + search + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    // Se crea el objeto Apostadores_Model con el estado correcto.
                    Apostadores_Model apostador = new Apostadores_Model(
                            rs.getInt("idapostadores"), rs.getString("cedula"),
                            rs.getString("nombre"), rs.getInt("saldo"),
                            rs.getString("observacion"),
                            rs.getInt("fk_estados") // <-- Se lee el estado real de la BD.
                    );

                    // Se crea el DTO para la vista.
                    resultados.add(new ApostadorParaVista_DTO(apostador, rs.getString("estado_deuda")));
                }
            }
        }
        return resultados;
    }

    public Resultado_Pendientes findPendientesPorApostador(int apostadorId) throws SQLException {
        System.out.println(String.format("\n--- REPOSITORY (Apostadores): Buscando deudas pendientes para Apostador ID: %d ---", apostadorId));
        List<Detalle_Pendientes> detalles = new ArrayList<>();
        String sql = """
        SELECT 
            ap.idapuestas, c.nombre AS nombre_carrera, cb.caballos AS nombre_caballo,
            ap.monto, ap.abonado, (ap.monto - ap.abonado) AS pendiente, ap.fechalimite,
            CASE 
                WHEN date(ap.fechalimite) < date('now', 'localtime') THEN 'ROJO'
                WHEN date(ap.fechalimite) = date('now', 'localtime') THEN 'VERDE'
                WHEN date(ap.fechalimite) = date('now', 'localtime', '+1 day') THEN 'AZUL'
                ELSE 'AMARILLO'
            END AS estado_deuda
        FROM apuestas ap
        JOIN carreras c ON ap.fk_carreras = c.idcarreras
        JOIN caballos cb ON ap.fk_caballos = cb.idcaballos
        WHERE ap.fk_apostadores = ? 
          AND ap.abonado < ap.monto
          AND c.idganador IS NOT NULL
          AND NOT EXISTS (
              SELECT 1 FROM apuestas win_ap
              JOIN carreras win_c ON win_ap.fk_carreras = win_c.idcarreras
              WHERE win_ap.fk_apostadores = ap.fk_apostadores
                AND win_ap.fk_carreras = ap.fk_carreras
                AND win_ap.fk_caballos = win_c.idganador
          )
        ORDER BY c.fecha, c.nombre
    """;

        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, apostadorId);
            System.out.println("1. Ejecutando consulta SQL...");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    System.out.println("   -> Fila encontrada: [ID Apuesta: " + rs.getInt("idapuestas") + ", Carrera: " + rs.getString("nombre_carrera") + ", Pendiente: " + rs.getInt("pendiente") + "]");
                    detalles.add(new Detalle_Pendientes(
                            rs.getInt("idapuestas"), rs.getString("nombre_carrera"),
                            rs.getString("nombre_caballo"), rs.getInt("monto"),
                            rs.getInt("abonado"), rs.getInt("pendiente"),
                            rs.getObject("fechalimite", LocalDate.class), rs.getString("estado_deuda")
                    ));
                }
            }
        }

        int totalPendiente = detalles.stream().mapToInt(Detalle_Pendientes::getMontoPendiente).sum();
        System.out.println("2. Consulta finalizada. Total de deudas encontradas: " + detalles.size());
        System.out.println("3. Suma total del monto pendiente: " + totalPendiente);
        System.out.println("--- REPOSITORY (Apostadores): Fin de la búsqueda ---");
        return new Resultado_Pendientes(detalles, totalPendiente);
    }

    public Map<Integer, Integer> findAllDeudasPendientes() throws SQLException {
        Map<Integer, Integer> mapaDeudas = new HashMap<>();
        String sql = """
        SELECT fk_apostadores, SUM(monto - abonado) as total_deuda
        FROM apuestas
        WHERE abonado < monto
          AND NOT EXISTS (
              SELECT 1 FROM apuestas win_ap
              JOIN carreras win_c ON win_ap.fk_carreras = win_c.idcarreras
              WHERE win_ap.fk_apostadores = apuestas.fk_apostadores
                AND win_ap.fk_carreras = apuestas.fk_carreras
                AND win_ap.fk_caballos = win_c.idganador
          )
        GROUP BY fk_apostadores
    """;
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                mapaDeudas.put(rs.getInt("fk_apostadores"), rs.getInt("total_deuda"));
            }
        }
        return mapaDeudas;
    }

    /**
     * Actualiza el saldo de un apostador usando una conexión existente (para
     * transacciones).
     *
     * @param conn La conexión de la transacción actual.
     * @param idApostador El ID del apostador a actualizar.
     * @param monto El monto a añadir (puede ser negativo).
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void updateSaldo(Connection conn, int idApostador, int monto) throws SQLException {
        String sql = "UPDATE apostadores SET saldo = saldo + ? WHERE idapostadores = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            pst.setInt(i++, monto);
            pst.setInt(i++, idApostador);
            pst.executeUpdate();
        }
    }

    /**
     * Obtiene los datos crudos del historial de apuestas para un apostador.
     * Esta consulta está diseñada para recoger toda la información necesaria
     * para que la capa de servicio pueda calcular los resultados finales.
     *
     * @param idApostador El ID del apostador.
     * @param desde
     * @param hasta
     * @return Una lista de DTOs con los datos crudos para cada apuesta.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<HistorialApuesta_DTO> findHistorialData(int idApostador, LocalDate desde, LocalDate hasta) throws SQLException {
        List<HistorialApuesta_DTO> historial = new ArrayList<>();
        // Esta consulta compleja obtiene todos los números necesarios para los cálculos
        String sql = """
        SELECT
          a.idapuestas, ca.nombre AS nombre_carrera, a.fecha, c.caballos AS nombre_caballo,
          a.fk_caballos AS id_caballo_apostado, ca.idganador AS id_caballo_ganador,
          a.monto AS monto_apostado, ca.comision AS comision_carrera,
          (SELECT COALESCE(SUM(ax.monto), 0) FROM apuestas ax WHERE ax.fk_carreras = ca.idcarreras AND ax.fk_apostadores NOT IN (SELECT fk_apostadores FROM apuestas WHERE fk_carreras = ca.idcarreras AND fk_caballos = ca.idganador)) AS pool_perdedores,
          (SELECT COALESCE(SUM(ax.monto), 0) FROM apuestas ax WHERE ax.fk_carreras = ca.idcarreras AND ax.fk_caballos = ca.idganador) AS total_apostado_ganador
        FROM apuestas a
        JOIN apostadores ap ON a.fk_apostadores = ap.idapostadores
        JOIN caballos c ON a.fk_caballos = c.idcaballos
        JOIN carreras ca ON a.fk_carreras = ca.idcarreras
        WHERE a.fk_apostadores = ?
    """;

        if (desde != null) {
            sql += " AND a.fecha >= ? ";
        }
        if (hasta != null) {
            sql += " AND a.fecha <= ? ";
        }
        sql += " ORDER BY a.fecha DESC";

        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            pst.setInt(paramIndex++, idApostador);
            if (desde != null) {
                pst.setString(paramIndex++, desde.toString()); // SQLite entiende 'YYYY-MM-DD'
            }
            if (hasta != null) {
                pst.setString(paramIndex++, hasta.toString());
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    HistorialApuesta_DTO dto = new HistorialApuesta_DTO(
                            rs.getInt("idapuestas"),
                            rs.getString("nombre_carrera"),
                            rs.getObject("fecha", LocalDate.class),
                            rs.getString("nombre_caballo"),
                            rs.getInt("id_caballo_apostado"),
                            (Integer) rs.getObject("id_caballo_ganador"),
                            rs.getInt("monto_apostado"),
                            rs.getInt("comision_carrera"),
                            rs.getLong("pool_perdedores"),
                            rs.getLong("total_apostado_ganador")
                    );
                    historial.add(dto);
                }
            }
        }
        return historial;
    }

    public List<Apostadores_Model> findAll() throws SQLException {
        List<Apostadores_Model> lista = new ArrayList<>();
        String sql = "SELECT * FROM apostadores";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                lista.add(new Apostadores_Model(
                        rs.getInt("idapostadores"), rs.getString("cedula"),
                        rs.getString("nombre"), rs.getInt("saldo"),
                        rs.getString("observacion"), rs.getInt("fk_estados")
                ));
            }
        }
        return lista;
    }
}
