package Controller;

import Config.DataSource;
import Model.Vencidos_Model;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SwichBlade15
 */
public class Fecha_Controller {

    public static List<Vencidos_Model> checkPending() {
        List<Vencidos_Model> lista = new ArrayList<>();

        String sql = """
        WITH pagos AS (
          SELECT
            a.idapuestas,
            a.monto               AS montoApuesta,
            COALESCE(
              SUM(CASE WHEN m.fk_tipomovimientos = 3 THEN m.monto ELSE 0 END),
              0
            ) AS montoPagado,
            a.fecha       AS fechaApuesta,
            a.fechalimite AS fechaLimite,
            ap.nombre     AS nombreApostador,
            c.nombre      AS nombreCarrera
          FROM apuestas a
          LEFT JOIN movimientos m
            ON m.fk_apuestas = a.idapuestas
           AND m.fk_tipomovimientos = 3    -- sólo sumamos los pagos (apuesta_parcial)
          INNER JOIN apostadores ap 
            ON a.fk_apostadores = ap.idapostadores
          INNER JOIN estados e 
            ON a.fk_estados = e.idestados
          INNER JOIN carreras c 
            ON a.fk_carreras = c.idcarreras
          WHERE 
            e.estados = 'activo'
          GROUP BY a.idapuestas
        )
        SELECT *
        FROM pagos
        WHERE
          montoPagado < montoApuesta
          AND DATE(fechaLimite) < DATE('now')
        ORDER BY idapuestas DESC
        """;

        try (Connection cn = DataSource.getConnection(); Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Vencidos_Model v = new Vencidos_Model();
                v.id = rs.getString("idapuestas");
                v.montoApuesta = rs.getInt("montoApuesta");
                v.montoAbonado = rs.getInt("montoPagado");
                v.fechaApuesta = rs.getString("fechaApuesta");
                v.fechaLimite = rs.getString("fechaLimite");
                v.nombreApostador = rs.getString("nombreApostador");
                v.nombreCarrera = rs.getString("nombreCarrera");
                lista.add(v);
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar la BD: " + e.getMessage());
        }

        return lista;
    }
}
