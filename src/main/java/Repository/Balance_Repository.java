package Repository;

import Config.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Balance_Repository {

    DecimalFormat formateador14 = new DecimalFormat("#,###.###");

    public DefaultTableModel showResultados(int raceId) {
        String[] titles = {
            "ID Apostador", "Apostador", "Cedula",
            "Total Apuesto", "Pool Perdedor",
            "Comision (10%)", "Monto Neto"
        };
        DefaultTableModel model = new DefaultTableModel(null, titles);

        String sSQL = """
        WITH
          -- 1) sacar el ganador de la carrera
          race AS (
            SELECT idganador
            FROM carreras
            WHERE idcarreras = ?
          ),
          -- 2) calcular el pool de los perdedores
          pool AS (
            SELECT COALESCE(SUM(a.monto),0) AS pool_monto
            FROM apuestas a
            WHERE a.fk_carreras = ?
              AND a.fk_caballos <> (SELECT idganador FROM race)
              AND a.fk_apostadores NOT IN (
                SELECT fk_apostadores
                FROM apuestas
                WHERE fk_carreras = ?
                  AND fk_caballos = (SELECT idganador FROM race)
              )
          ),
          -- 3) suma total apostado sobre el caballo ganador
          total_win AS (
            SELECT COALESCE(SUM(monto),0) AS win_monto
            FROM apuestas
            WHERE fk_carreras = ?
              AND fk_caballos = (SELECT idganador FROM race)
          )
        SELECT
          a.fk_apostadores       AS idapostadores,
          ap.nombre              AS nombre,
          ap.cedula              AS cedula,
          a.monto                AS total_apostado,
          p.pool_monto           AS pool_perdedor,
          -- sólo a las apuestas ganadoras se les pone la comisión; al resto, 0
          CASE
            WHEN a.fk_caballos = (SELECT idganador FROM race)
            THEN ROUND(p.pool_monto * 0.10)
            ELSE 0
          END                     AS comision,
          -- si ganó, reparte el 90% del pool prorrateado por monto; si perdió, -apuesta
          CASE
            WHEN a.fk_caballos = (SELECT idganador FROM race)
            THEN ROUND(p.pool_monto * 0.90 * a.monto / tw.win_monto)
            ELSE -a.monto
          END                     AS monto_neto
        FROM apuestas a
        JOIN apostadores ap ON ap.idapostadores = a.fk_apostadores
        CROSS JOIN pool p
        CROSS JOIN total_win tw
        WHERE a.fk_carreras = ?;
        """;

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            // seteamos raceId en los 5 placeholders
            for (int i = 1; i <= 5; i++) {
                pst.setInt(i, raceId);
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("idapostadores"),
                        rs.getString("nombre"),
                        rs.getString("cedula"),
                        formateador14.format(rs.getInt("total_apostado")),
                        formateador14.format(rs.getInt("pool_perdedor")),
                        formateador14.format(rs.getInt("comision")),
                        formateador14.format(rs.getInt("monto_neto"))
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al obtener resultados: " + e.getMessage());
        }
        return model;
    }

    public HashMap<String, String> fillCarrerasCombos() {
        HashMap<String, String> carrerasMap = new HashMap<>();
        String sSQL = "SELECT idcarreras, nombre FROM carreras WHERE idganador IS NOT NULL ";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("idcarreras");
                String name = rs.getString("nombre");

                carrerasMap.put(id, name);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener las carreras: " + e.getMessage());
            Logger.getLogger(Apuestas_Repository.class.getName()).log(Level.SEVERE, "Error en fillCarrerasCombos", e);
        }
        return carrerasMap;
    }

    public int calcularGanancia(int raceId) {
        int totalGanancia = 0;

        String sSQL = "SELECT a.monto FROM apuestas a "
                + "JOIN carreras ca ON a.fk_carreras = ca.idcarreras "
                + "JOIN detallecarreras dc ON ca.idcarreras = dc.fk_carreras "
                + "JOIN caballos c ON dc.fk_caballos = c.idcaballos "
                + "WHERE ca.idcarreras = ? AND c.idcaballos IN (SELECT fk_caballos FROM detallecarreras WHERE fk_carreras = ?)";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            pst.setInt(1, raceId);
            pst.setInt(2, raceId);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int monto = rs.getInt("monto");
                totalGanancia += monto * 0.1; // Aplica el descuento del 10%
            }
            return totalGanancia;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
            return 0;
        }
    }
}
