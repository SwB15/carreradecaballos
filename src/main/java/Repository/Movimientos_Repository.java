package Repository;

import Config.DataSource;
import Model.Movimientos_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SwichBlade15
 */
public class Movimientos_Repository {

    DecimalFormat formateador14 = new DecimalFormat("#,###.###");
    String sql = "";
    PreparedStatement pst = null;

//********************************Begin of Insert, Update, Disable********************************
    public boolean insert(Movimientos_Model model) {
        String sql = "INSERT INTO movimientos(fecha, monto, descripcion, fk_apostadores, fk_apuestas, fk_tipomovimientos, fk_carreras) VALUES(?,?,?,?,?,?,?)";
        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {

            int i = 1;
            pst.setString(i++, model.getFecha());
            pst.setInt(i++, model.getMonto()); // Si monto puede tener decimales, usar setDouble o setBigDecimal
            pst.setString(i++, model.getObservacion());

            if (model.getFk_apostadores() != null) {
                pst.setInt(i++, model.getFk_apostadores());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }

            if (model.getFk_apuestas() != null) {
                pst.setInt(i++, model.getFk_apuestas());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }

            if (model.getFk_tipomovimientos() != null) {
                pst.setInt(i++, model.getFk_tipomovimientos());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }
            
            if (model.getFk_carreras() != null) {
                pst.setInt(i++, model.getFk_carreras());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Movimientos_Repository.class.getName()).log(Level.SEVERE, "Error al insertar movimiento", ex);
            return false;
        }
    }

    public boolean update(Movimientos_Model model) {
        sql = "UPDATE movimientos SET fecha = ?, monto = ?, descripcion = ?, fk_apostadores = ?, fk_apuestas = ?, fk_tipomovimientos = ?, fk_carreras = ? WHERE idmovimientos = ?";
        try (Connection cn = DataSource.getConnection()) {
            int i = 1;
            pst = cn.prepareStatement(sql);
            pst.setString(i++, model.getFecha());
            pst.setInt(i++, model.getMonto());
            pst.setString(i++, model.getObservacion());

            if (model.getFk_apostadores() != null) {
                pst.setInt(i++, model.getFk_apostadores());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }

            if (model.getFk_apuestas() != null) {
                pst.setInt(i++, model.getFk_apuestas());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }

            if (model.getFk_tipomovimientos() != null) {
                pst.setInt(i++, model.getFk_tipomovimientos());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }
            
            if (model.getFk_carreras() != null) {
                pst.setInt(i++, model.getFk_carreras());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }

            pst.setInt(i++, model.getIdmovimientos());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Caballos_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public int deleteMovimientosporcarrera(int raceId) throws SQLException {
        sql = """
        DELETE FROM movimientos
         WHERE fk_tipomovimientos IN (5,6)
           AND fk_apostadores IN (
               SELECT DISTINCT fk_apostadores
                 FROM apuestas
                WHERE fk_carreras = ?
           )
        """;
        try (Connection conn = DataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setInt(i++, raceId);
            return ps.executeUpdate();
        }
    }

    public List<Movimientos_Model> calcularMovimientosPorCarrera(int raceId, String fecha) throws SQLException {
        sql = """
    WITH
      race AS (
        SELECT idganador
        FROM carreras
        WHERE idcarreras = ?
      ),
      pool AS (
        -- sólo los perdedores de aquellos que no ganaron ninguna apuesta
        SELECT COALESCE(SUM(monto),0) AS pool_monto
        FROM apuestas
        WHERE fk_carreras = ?
          AND fk_caballos <> (SELECT idganador FROM race)
          AND fk_apostadores NOT IN (
            SELECT fk_apostadores
            FROM apuestas
            WHERE fk_carreras = ?
              AND fk_caballos = (SELECT idganador FROM race)
          )
      ),
      total_win AS (
        SELECT COALESCE(SUM(monto),0) AS win_monto
        FROM apuestas
        WHERE fk_carreras = ?
          AND fk_caballos = (SELECT idganador FROM race)
      )
    SELECT
      a.fk_apostadores    AS idapostador,
      -- stake perdido en apuestas sobre otros caballos
      SUM(CASE WHEN a.fk_caballos <> (SELECT idganador FROM race)
               THEN a.monto ELSE 0 END)       AS stake_perdido,
      -- stake apostado al caballo ganador
      SUM(CASE WHEN a.fk_caballos = (SELECT idganador FROM race)
               THEN a.monto ELSE 0 END)       AS stake_ganado,
      p.pool_monto,
      tw.win_monto
    FROM apuestas a
    CROSS JOIN pool p
    CROSS JOIN total_win tw
    WHERE a.fk_carreras = ?
    GROUP BY a.fk_apostadores;
    """;

        List<Movimientos_Model> lista = new ArrayList<>();
        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            // seteamos los 5 “?”
            pst.setInt(1, raceId);
            pst.setInt(2, raceId);
            pst.setInt(3, raceId);
            pst.setInt(4, raceId);
            pst.setInt(5, raceId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int idApo = rs.getInt("idapostador");
                    int stakePerd = rs.getInt("stake_perdido");
                    int stakeGan = rs.getInt("stake_ganado");
                    int pool = rs.getInt("pool_monto");
                    int totalWin = rs.getInt("win_monto");

                    // Si no apostó al ganador, no hay nada que devolver ni premiar
                    if (stakeGan == 0) {
                        continue;
                    }

                    // 1) Devolución del stake de la apuesta ganadora
                    Movimientos_Model mStakeGan = new Movimientos_Model();
                    mStakeGan.setFecha(fecha);
                    mStakeGan.setMonto(stakeGan);
                    mStakeGan.setObservacion("Devolución stake ganador");
                    mStakeGan.setFk_apostadores(idApo);
                    mStakeGan.setFk_apuestas(null);
                    mStakeGan.setFk_tipomovimientos(6);  // devolución
                    lista.add(mStakeGan);

                    // 2) Devolución del stake perdido (solo si hizo más de una apuesta)
                    if (stakePerd > 0) {
                        Movimientos_Model mObviada = new Movimientos_Model();
                        mObviada.setFecha(fecha);
                        mObviada.setMonto(stakePerd);
                        mObviada.setObservacion("Devolución stake obviado");
                        mObviada.setFk_apostadores(idApo);
                        mObviada.setFk_apuestas(null);
                        mObviada.setFk_tipomovimientos(6);  // devolución
                        lista.add(mObviada);
                    }

                    // 3) Premio neto: 90% del pool prorrateado
                    if (totalWin > 0) {
                        int premioNeto = (int) Math.round(pool * 0.90 * (double) stakeGan / totalWin);
                        Movimientos_Model mPremio = new Movimientos_Model();
                        mPremio.setFecha(fecha);
                        mPremio.setMonto(premioNeto);
                        mPremio.setObservacion("Apuesta ganada (neto)");
                        mPremio.setFk_apostadores(idApo);
                        mPremio.setFk_apuestas(null);
                        mPremio.setFk_tipomovimientos(5);  // apuesta_ganada
                        lista.add(mPremio);
                    }
                }
            }
        }
        return lista;
    }
//******************************** End of Insert, Update, Disable ********************************  

//********************************Begin of Display Methods********************************
    public DefaultTableModel showMovimientos(String apostadorSearch, String tipoMovimientoSearch, String dateFrom, String dateTo) {
        boolean filtrarFechas = dateFrom != null && !dateFrom.isBlank()
                && dateTo != null && !dateTo.isBlank();

        // Si vino “Todos”, lo convierto a cadena vacía para que LIKE '%%'  
//        if ("Todos".equalsIgnoreCase(tipoMovimientoSearch)) {
//            tipoMovimientoSearch = "";
//        }
        if (tipoMovimientoSearch.equals("Todos")) {
            tipoMovimientoSearch = "";
        }

        String[] titles = {
            "Id", "Fecha", "Tipo ID", "Tipo Movimiento",
            "Monto", "Id apostadores", "Apostador", "Observacion", "Id Apuesta"
        };
        DefaultTableModel model = new DefaultTableModel(null, titles);
        SimpleDateFormat formatoBD = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoMostrar = new SimpleDateFormat("dd/MM/yyyy");

        // Usamos text block para mayor legibilidad
        String sSQL = """
        SELECT
          m.idmovimientos,
          m.fecha,
          tm.idtipomovimientos,
          tm.descripcion     AS tipo_mov,
          m.monto,
          a.idapostadores    AS idapostadores,
          a.nombre           AS apostador,
          m.descripcion      AS observacion,
          m.fk_apuestas      AS id_apuesta
        FROM movimientos m
        INNER JOIN apostadores a
          ON m.fk_apostadores = a.idapostadores
        INNER JOIN tipomovimientos tm
          ON m.fk_tipomovimientos = tm.idtipomovimientos
        WHERE a.nombre LIKE ? -- COLLATE NOCASE
          AND tm.descripcion LIKE ? -- COLLATE NOCASE
        """;

        if (filtrarFechas) {
            sSQL += " AND m.fecha BETWEEN ? AND ?";
        }

        sSQL += " ORDER BY m.idmovimientos DESC";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            int param = 1;
            pst.setString(param++, "%" + apostadorSearch + "%");
            pst.setString(param++, "%" + tipoMovimientoSearch + "%");

            if (filtrarFechas) {
                pst.setString(param++, dateFrom);
                pst.setString(param++, dateTo);
            }

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                    // Formateo de fecha
                    // 1) Leemos la fecha cruda de la BD (en formato “yyyy-MM-dd”)
                    String fechaStr = rs.getString("fecha");

                    // 2) La parseamos a java.util.Date usando formatoBD
                    String fechaFormateada;
                    if (fechaStr != null && !fechaStr.isEmpty()) {
                        try {
                            Date fechaDate = formatoBD.parse(fechaStr);
                            // 3) La volvemos a formatear con formatoMostrar
                            fechaFormateada = formatoMostrar.format(fechaDate);
                        } catch (ParseException ex) {
                            // Si falla el parseo, usamos el texto tal cual
                            fechaFormateada = fechaStr;
                        }
                    } else {
                        fechaFormateada = "";
                    }

                    // Lectura de columnas
                    int idMov = rs.getInt("idmovimientos");
                    int idTipoMov = rs.getInt("idtipomovimientos");
                    String tipoMov = rs.getString("tipo_mov");
                    int monto = rs.getInt("monto");
                    String montoFmt = formateador14.format(monto);
                    int idapostadores = rs.getInt("idapostadores");
                    String apostador = rs.getString("apostador");
                    String observacion = rs.getString("observacion");
                    int idApuesta = rs.getInt("id_apuesta");

                    model.addRow(new Object[]{
                        idMov,
                        fechaFormateada,
                        idTipoMov,
                        tipoMov,
                        montoFmt,
                        idapostadores,
                        apostador,
                        observacion,
                        idApuesta
                    });

                }
            }
        } catch (SQLException e) {
            Logger.getLogger(Apostadores_Repository.class
                    .getName())
                    .log(Level.SEVERE, "Error al consultar movimientos", e);
        }
        return model;
    }

    public List<Movimientos_Model> getMovimientosPorCarrera(int idCarrera) throws SQLException {
        List<Movimientos_Model> lista = new ArrayList<>();
        sql = "SELECT * FROM movimientos WHERE fk_carreras = ?";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setInt(1, idCarrera);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Movimientos_Model m = new Movimientos_Model();
                m.setIdmovimientos(rs.getInt("idmovimientos"));
                m.setFecha(rs.getString("fecha"));
                m.setMonto(rs.getInt("monto"));
                m.setObservacion(rs.getString("descripcion"));
                m.setFk_apostadores(rs.getInt("fk_apostadores"));
                m.setFk_apuestas(rs.getInt("fk_apuestas"));
                m.setFk_tipomovimientos(rs.getInt("fk_tipomovimientos"));
                m.setFk_carreras(rs.getInt("fk_carreras"));
                lista.add(m);
            }
        }
        return lista;
    }

    public int getsaldoapostador(int idApostador) throws SQLException {
        sql = "SELECT saldo FROM apostadores WHERE idapostadores = ?";
        try (Connection cn = DataSource.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idApostador);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("saldo") : 0;
            }
        }
    }
}
