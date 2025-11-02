package Repository;

import Config.DataSource;
import Model.ResultadosCarrera_DTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Gestiona el acceso a datos para los cálculos de balance y resultados de
 * carreras.
 */
public class Balance_Repository {

    /**
     * Obtiene los resultados finales de todos los apostadores para una carrera
     * específica. La lógica de cálculo se mantiene en la consulta SQL, pero el
     * método devuelve una lista de DTOs en lugar de un componente de UI.
     *
     * @param idCarrera El ID de la carrera a consultar.
     * @return Una lista de DTOs con los resultados.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<ResultadosCarrera_DTO> findResultadosPorCarrera(int idCarrera) throws SQLException {
        List<ResultadosCarrera_DTO> resultados = new ArrayList<>();
        // Esta es tu potente consulta SQL original, sin cambios.
        String sql = """
            WITH
              race AS (SELECT idganador, comision FROM carreras WHERE idcarreras = ?),
              pool AS (
                SELECT COALESCE(SUM(a.monto), 0) AS pool_monto
                FROM apuestas a
                WHERE a.fk_carreras = ? AND a.fk_caballos <> (SELECT idganador FROM race)
                  AND a.fk_apostadores NOT IN (
                    SELECT fk_apostadores FROM apuestas WHERE fk_carreras = ? AND fk_caballos = (SELECT idganador FROM race)
                  )
              ),
              total_win AS (
                SELECT COALESCE(SUM(monto), 0) AS win_monto
                FROM apuestas WHERE fk_carreras = ? AND fk_caballos = (SELECT idganador FROM race)
              )
            SELECT
              a.fk_apostadores AS idapostadores, ap.nombre, ap.cedula,
              a.monto AS total_apostado, p.pool_monto AS pool_perdedor,
              CASE
                WHEN a.fk_caballos = (SELECT idganador FROM race) THEN ROUND(p.pool_monto * r.comision / 100)
                ELSE 0
              END AS comision,
              CASE
                WHEN a.fk_caballos = (SELECT idganador FROM race) THEN ROUND(p.pool_monto * (100 - r.comision) / 100 * a.monto / tw.win_monto)
                ELSE -a.monto
              END AS resultado_final
            FROM apuestas a
            JOIN apostadores ap ON ap.idapostadores = a.fk_apostadores
            CROSS JOIN pool p CROSS JOIN total_win tw CROSS JOIN race r
            WHERE a.fk_carreras = ?
        """;

        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {

            // Se establecen todos los parámetros de la consulta
            for (int i = 1; i <= 5; i++) {
                pst.setInt(i, idCarrera);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    // Se mapea cada fila del resultado a nuestro nuevo DTO
                    ResultadosCarrera_DTO dto = new ResultadosCarrera_DTO(
                            rs.getInt("idapostadores"),
                            rs.getString("nombre"),
                            rs.getString("cedula"),
                            rs.getInt("total_apostado"),
                            rs.getInt("pool_perdedor"),
                            rs.getInt("comision"),
                            rs.getInt("resultado_final")
                    );
                    resultados.add(dto);
                }
            }
        }
        return resultados;
    }

    /**
     * Obtiene un mapa de carreras que ya tienen un ganador asignado. Ideal para
     * poblar ComboBoxes en la UI.
     *
     * @return Un HashMap donde la clave es el nombre de la carrera y el valor
     * es su ID.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public HashMap<String, Integer> findCarrerasFinalizadas() throws SQLException {
        // Se usa LinkedHashMap para mantener el orden de la consulta.
        HashMap<String, Integer> carrerasMap = new LinkedHashMap<>();

        // Se cambia el ORDER BY para que sea por ID descendente.
        String sql = "SELECT idcarreras, nombre FROM carreras WHERE idganador IS NOT NULL ORDER BY idcarreras DESC";

        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                carrerasMap.put(rs.getString("nombre"), rs.getInt("idcarreras"));
            }
        }
        return carrerasMap;
    }

    /**
     * Obtiene la comisión de una carrera específica.
     *
     * @param idCarrera El ID de la carrera.
     * @return El porcentaje de comisión.
     * @throws SQLException Si la carrera no se encuentra o hay un error de BD.
     */
    public int findComisionPorCarrera(int idCarrera) throws SQLException {
        String sql = "SELECT comision FROM carreras WHERE idcarreras = ?";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idCarrera);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("comision");
                } else {
                    // Es mejor lanzar una excepción si la carrera no existe.
                    throw new SQLException("No se encontró una carrera con ID: " + idCarrera);
                }
            }
        }
    }
}
