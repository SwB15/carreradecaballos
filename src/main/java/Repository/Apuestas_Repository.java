package Repository;

import Config.DataSource;
import Model.Apuestas_Model;
import Model.ApuestasParaVista_DTO;
import Model.Vencidos_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Gestiona el acceso a la base de datos para la entidad Apuestas. Se enfoca
 * únicamente en operaciones CRUD y consultas, devolviendo objetos de Modelo o
 * DTO.
 */
public class Apuestas_Repository {

    // --- Métodos CRUD (Crear, Leer, Actualizar, Borrar) ---
    public void insert(Apuestas_Model model) throws SQLException {
        String sql = """
            INSERT INTO apuestas(
                apuesta, monto, fecha, fechalimite, observacion, saldousado,
                fk_carreras, fk_caballos, fk_apostadores, fk_estadopago, fk_estados, abonado
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            pst.setString(i++, model.getApuesta());
            pst.setInt(i++, model.getMonto());
            pst.setObject(i++, model.getFecha()); // Se usa setObject para LocalDate
            pst.setObject(i++, model.getFechaLimite()); // Se usa setObject para LocalDate
            pst.setString(i++, model.getObservacion());
            pst.setBoolean(i++, model.isSaldoUsado()); // Se usa setBoolean para el tipo de dato correcto
            pst.setInt(i++, model.getFk_carreras());
            pst.setInt(i++, model.getFk_caballos());
            pst.setInt(i++, model.getFk_apostadores());
            pst.setInt(i++, model.getFk_estadopago());
            pst.setInt(i++, model.getFk_estados());
            pst.setInt(i++, model.getAbonado());
            pst.executeUpdate();
        }
    }

    public void update(Apuestas_Model model) throws SQLException {
        String sql = """
            UPDATE apuestas SET apuesta = ?, monto = ?, fecha = ?, fechalimite = ?,
            observacion = ?, saldousado = ?, fk_carreras = ?, fk_caballos = ?,
            fk_apostadores = ?, fk_estadopago = ?, fk_estados = ?, abonado = ?
            WHERE idapuestas = ?
        """;
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            // ... Se establecen todos los parámetros de la misma forma que en insert ...
            pst.setString(i++, model.getApuesta());
            pst.setInt(i++, model.getMonto());
            // ... etc. ...
            pst.setInt(i++, model.getIdApuesta());
            pst.executeUpdate();
        }
    }

    public void delete(int idApuesta) throws SQLException {
        String sql = "DELETE FROM apuestas WHERE idapuestas = ?";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idApuesta);
            pst.executeUpdate();
        }
    }

    // --- Métodos Específicos de Consulta y Actualización ---
    public int findMaxId() throws SQLException {
        String sql = "SELECT MAX(idapuestas) AS max_id FROM apuestas";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("max_id");
            }
            return 0;
        }
    }

    // En Repository/Apuestas_Repository.java
    /**
     * Elimina todas las apuestas asociadas a una carrera específica. Se usa
     * dentro de una transacción al actualizar una carrera.
     *
     * @param conn La conexión de la transacción actual.
     * @param idCarrera El ID de la carrera cuyas apuestas se eliminarán.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void deleteByCarreraId(Connection conn, int idCarrera) throws SQLException {
        String sql = "DELETE FROM apuestas WHERE fk_carreras = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idCarrera);
            pst.executeUpdate();
        }
    }

    // En Repository/Apuestas_Repository.java
    /**
     * Inserta una nueva apuesta usando una conexión existente (para
     * transacciones).
     *
     * @param conn La conexión de la transacción actual.
     * @param model El objeto Apuestas_Model a insertar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void insert(Connection conn, Apuestas_Model model) throws SQLException {
        String sql = """
        INSERT INTO apuestas(
            apuesta, monto, fecha, fechalimite, observacion, saldousado,
            fk_carreras, fk_caballos, fk_apostadores, fk_estadopago, fk_estados, abonado
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        System.out.println("--- REPOSITORY (Apuestas): Inicia 'insert' ---");
        // Se usa un bloque try-with-resources para asegurar que el PreparedStatement se cierre.
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;

            // Se imprimen los valores que se van a insertar.
            System.out.println(String.format("   -> Parámetros: [Monto: %d, Fecha: %s, fk_carreras: %d, fk_caballos: %d, fk_apostadores: %d, Abonado: %d]",
                    model.getMonto(), model.getFecha(), model.getFk_carreras(), model.getFk_caballos(), model.getFk_apostadores(), model.getAbonado()));

            pst.setString(i++, model.getApuesta());
            pst.setInt(i++, model.getMonto());
            pst.setObject(i++, model.getFecha());
            pst.setObject(i++, model.getFechaLimite());
            pst.setString(i++, model.getObservacion());
            pst.setBoolean(i++, model.isSaldoUsado());
            pst.setInt(i++, model.getFk_carreras());
            pst.setInt(i++, model.getFk_caballos());
            pst.setInt(i++, model.getFk_apostadores());
            pst.setInt(i++, model.getFk_estadopago());
            pst.setInt(i++, model.getFk_estados());
            pst.setInt(i++, model.getAbonado());

            pst.executeUpdate();
            System.out.println("   -> Inserción en 'apuestas' completada.");
        }
    }

    public int getMonto(int idApuesta) throws SQLException {
        String sql = "SELECT monto FROM apuestas WHERE idapuestas = ?";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idApuesta);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("monto");
                }
            }
        }
        throw new SQLException("No se encontró la apuesta con id: " + idApuesta);
    }

    public void updateEstadoPago(Connection conn, int idApuesta, int totalAbonado, int fkEstadoPago) throws SQLException {
        // Recibe la conexión para ser parte de una transacción.
        String sql = "UPDATE apuestas SET abonado = ?, fk_estadopago = ? WHERE idapuestas = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            int i = 1;
            pst.setInt(i++, totalAbonado);
            pst.setInt(i++, fkEstadoPago);
            pst.setInt(i++, idApuesta);
            pst.executeUpdate();
        }
    }

    public List<ApuestasParaVista_DTO> findApuestasParaVista(int idCarrera) throws SQLException {
        List<ApuestasParaVista_DTO> resultados = new ArrayList<>();
        String sql = """
            SELECT a.*, ap.nombre AS apostador, ap.saldo, c.nombre AS carrera,
                   cc.caballos AS caballo, e.estados AS estado, ep.descripcion AS estadopago
            FROM apuestas a
            JOIN apostadores ap ON a.fk_apostadores = ap.idapostadores
            JOIN carreras c ON a.fk_carreras = c.idcarreras
            JOIN caballos cc ON a.fk_caballos = cc.idcaballos
            JOIN estados e ON a.fk_estados = e.idestados
            JOIN estadopago ep ON a.fk_estadopago = ep.idestadopago
            WHERE a.fk_carreras = ? AND a.fk_estados = 1
            ORDER BY a.idapuestas DESC
        """;
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idCarrera);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Apuestas_Model apuesta = new Apuestas_Model(
                            rs.getInt("idapuestas"), rs.getString("apuesta"), rs.getInt("monto"),
                            rs.getObject("fecha", LocalDate.class), rs.getObject("fechalimite", LocalDate.class),
                            rs.getString("observacion"), rs.getBoolean("saldousado"), rs.getInt("abonado"),
                            rs.getInt("fk_carreras"), rs.getInt("fk_caballos"), rs.getInt("fk_apostadores"),
                            rs.getInt("fk_estadopago"), rs.getInt("fk_estados")
                    );
                    ApuestasParaVista_DTO dto = new ApuestasParaVista_DTO(
                            apuesta, rs.getString("apostador"), rs.getInt("saldo"),
                            rs.getString("carrera"), rs.getString("caballo"),
                            rs.getString("estado"), rs.getString("estadopago")
                    );
                    resultados.add(dto);
                }
            }
        }
        return resultados;
    }

    public HashMap<String, Integer> fillCombos(String tableName, String idColumn, String nameColumn) throws SQLException {
        HashMap<String, Integer> map = new HashMap<>();
        // Método genérico para llenar combos.
        String sql = String.format("SELECT %s, %s FROM %s WHERE fk_estados = 1", idColumn, nameColumn, tableName);

        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString(nameColumn), rs.getInt(idColumn));
            }
        }
        return map;
    }

    // Añade esta clase DTO pública y estática DENTRO de tu Apuestas_Repository.java
    public static class ResultadoPorApostadorDTO {

        private final int idApostador;
        private final int stakeGanado;
        private final int stakePerdido;

        public ResultadoPorApostadorDTO(int idApostador, int stakeGanado, int stakePerdido) {
            this.idApostador = idApostador;
            this.stakeGanado = stakeGanado;
            this.stakePerdido = stakePerdido;
        }
        // Getters para todos los campos...

        public int getIdApostador() {
            return idApostador;
        }

        public int getStakeGanado() {
            return stakeGanado;
        }

        public int getStakePerdido() {
            return stakePerdido;
        }
    }

// Añade estos NUEVOS MÉTODOS a tu clase Apuestas_Repository
    public List<ResultadoPorApostadorDTO> findResultadosPorApostador(Connection conn, int idCarrera) throws SQLException {
        List<ResultadoPorApostadorDTO> resultados = new ArrayList<>();
        String sql = """
        SELECT
            a.fk_apostadores,
            SUM(CASE WHEN a.fk_caballos = c.idganador THEN a.monto ELSE 0 END) AS stake_ganado,
            SUM(CASE WHEN a.fk_caballos <> c.idganador THEN (a.monto - a.abonado) ELSE 0 END) AS stake_perdido_real
        FROM apuestas a
        JOIN carreras c ON a.fk_carreras = c.idcarreras
        WHERE a.fk_carreras = ?
        GROUP BY a.fk_apostadores
    """;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idCarrera);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    resultados.add(new ResultadoPorApostadorDTO(
                            rs.getInt("fk_apostadores"),
                            rs.getInt("stake_ganado"),
                            rs.getInt("stake_perdido_real")
                    ));
                }
            }
        }
        return resultados;
    }

    public long getPoolPerdedores(Connection conn, int idCarrera) throws SQLException {
        String sql = """
        SELECT COALESCE(SUM(a.monto), 0)
        FROM apuestas a
        WHERE a.fk_carreras = ?
          AND a.fk_apostadores NOT IN (
            SELECT fk_apostadores FROM apuestas
            WHERE fk_carreras = ? AND fk_caballos = (SELECT idganador FROM carreras WHERE idcarreras = ?)
          )
    """;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idCarrera);
            pst.setInt(2, idCarrera);
            pst.setInt(3, idCarrera);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }

    public long getTotalApostadoAlGanador(Connection conn, int idCarrera) throws SQLException {
        String sql = "SELECT COALESCE(SUM(monto), 0) FROM apuestas WHERE fk_carreras = ? AND fk_caballos = (SELECT idganador FROM carreras WHERE idcarreras = ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idCarrera);
            pst.setInt(2, idCarrera);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }

    // Añade estos NUEVOS MÉTODOS a tu clase Apuestas_Repository
    public int getMonto(Connection conn, int idApuesta) throws SQLException {
        String sql = "SELECT monto FROM apuestas WHERE idapuestas = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idApuesta);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("monto");
                }
            }
        }
        throw new SQLException("No se encontró la apuesta con id: " + idApuesta);
    }

    public int getTotalAbonado(Connection conn, int idApuesta) throws SQLException {
        String sql = "SELECT COALESCE(abonado, 0) FROM apuestas WHERE idapuestas = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idApuesta);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Busca todas las apuestas que están vencidas y no han sido perdonadas. Una
     * apuesta está vencida si su fecha límite ya pasó y no está completamente
     * pagada. Se excluyen las apuestas de carreras donde el apostador tuvo un
     * ganador.
     *
     * @return Una lista de DTOs Vencidos_Model.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Vencidos_Model> findVencidos() throws SQLException {
        List<Vencidos_Model> lista = new ArrayList<>();
        // Esta es tu consulta original, que es muy buena para esta tarea.
        String sql = """
        WITH pagos AS (
            SELECT a.idapuestas, a.fk_apostadores AS idApostador, a.fk_carreras AS idCarrera,
                   a.monto AS montoApuesta, a.abonado AS montoPagado,
                   a.fecha AS fechaApuesta, a.fechalimite AS fechaLimite,
                   ap.nombre AS nombreApostador, c.nombre AS nombreCarrera
            FROM apuestas a
            JOIN apostadores ap ON a.fk_apostadores = ap.idapostadores
            JOIN carreras c ON a.fk_carreras = c.idcarreras
            WHERE a.fk_estados = 1
        )
        SELECT p.*
        FROM pagos p
        WHERE DATE(p.fechaLimite) < DATE('now', 'localtime')
          AND p.montoPagado < p.montoApuesta
          AND NOT EXISTS (
              SELECT 1 FROM apuestas a2
              JOIN carreras c2 ON a2.fk_carreras = c2.idcarreras
              WHERE a2.fk_apostadores = p.idApostador
                AND a2.fk_carreras = p.idCarrera
                AND a2.fk_caballos = c2.idganador
          )
        ORDER BY p.idapuestas DESC
    """;

        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                // Se mapea el resultado al DTO Vencidos_Model refactorizado
                Vencidos_Model vencido = new Vencidos_Model(
                        rs.getInt("idapuestas"),
                        rs.getInt("montoApuesta"),
                        rs.getInt("montoPagado"),
                        rs.getObject("fechaApuesta", LocalDate.class),
                        rs.getObject("fechaLimite", LocalDate.class),
                        rs.getString("nombreApostador"),
                        rs.getString("nombreCarrera")
                );
                lista.add(vencido);
            }
        }
        return lista;
    }

    public void addAbono(Connection conn, int idApuesta, int montoAAbonar) throws SQLException {
        String sql = "UPDATE apuestas SET abonado = abonado + ? WHERE idapuestas = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, montoAAbonar);
            pst.setInt(2, idApuesta);
            pst.executeUpdate();
        }
    }

    /**
     * Busca todas las deudas pendientes de un apostador (apuestas perdidas no
     * pagadas).
     *
     * @param conn La conexión de la transacción.
     * @param idApostador El ID del apostador.
     * @return Una lista de los objetos Apuestas_Model que representan sus
     * deudas.
     * @throws SQLException Si hay un error.
     */
    public List<Apuestas_Model> findDeudasPendientesByApostador(Connection conn, int idApostador) throws SQLException {
        List<Apuestas_Model> deudas = new ArrayList<>();
        String sql = """
        SELECT ap.*
        FROM apuestas ap
        JOIN carreras c ON ap.fk_carreras = c.idcarreras
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
        ORDER BY ap.fechalimite ASC
    """;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idApostador);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    deudas.add(new Apuestas_Model(
                            rs.getInt("idapuestas"), rs.getString("apuesta"), rs.getInt("monto"),
                            rs.getObject("fecha", LocalDate.class), rs.getObject("fechalimite", LocalDate.class),
                            rs.getString("observacion"), rs.getBoolean("saldousado"), rs.getInt("abonado"),
                            rs.getInt("fk_carreras"), rs.getInt("fk_caballos"), rs.getInt("fk_apostadores"),
                            rs.getInt("fk_estadopago"), rs.getInt("fk_estados")
                    ));
                }
            }
        }
        return deudas;
    }
}
