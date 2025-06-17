package Repository;

import Config.DataSource;
import Model.Apostadores_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Apostadores_Repository {

    String sql = "";
    PreparedStatement pst = null;
    DecimalFormat formateador14 = new DecimalFormat("#,###.###");

//********************************Begin of Insert, Update, Disable********************************
    public boolean insert(Apostadores_Model model) {
        sql = "INSERT INTO apostadores(cedula, nombre, observacion, fk_estados) VALUES(?,?,?,?)";
        try (Connection cn = DataSource.getConnection()) {
            int i = 1;
            pst = cn.prepareStatement(sql);
            pst.setString(i++, model.getCedula());
            pst.setString(i++, model.getNombre());
            pst.setString(i++, model.getObservacion());
            pst.setInt(i++, model.getFk_estados());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Apostadores_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean update(Apostadores_Model model) {
        sql = "UPDATE apostadores SET cedula = ?, nombre = ?, observacion = ?, fk_estados = ? WHERE idapostadores = ?";
        try (Connection cn = DataSource.getConnection()) {
            int i = 1;
            pst = cn.prepareStatement(sql);
            pst.setString(i++, model.getCedula());
            pst.setString(i++, model.getNombre());
            pst.setString(i++, model.getObservacion());
            pst.setInt(i++, model.getFk_estados());
            pst.setInt(i++, model.getIdapostadores());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Apostadores_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean addSaldo(Apostadores_Model model) {
        sql = "UPDATE apostadores SET saldo = saldo + ? WHERE idapostadores = ?";
        try (Connection cn = DataSource.getConnection()) {
            int i = 1;
            pst = cn.prepareStatement(sql);
            pst.setInt(i++, model.getSaldo());
            pst.setInt(i++, model.getIdapostadores());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Apostadores_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
//******************************** End of Insert, Update, Disable ********************************  

//********************************Begin of Display Methods********************************
    public DefaultTableModel showApostadores(String search, String stateFilter) {
        String[] titles = {"Id", "Cedula", "Apostador", "Saldo", "Observacion", "Estado"};
        DefaultTableModel model = new DefaultTableModel(null, titles);

        // Seleccionamos también v.saldo, usando COALESCE para sustituir NULL por 0
        String sSQL = """
        SELECT 
          a.idapostadores,
          a.cedula,
          a.nombre,
          a.saldo,
          a.observacion,
          e.estados
        FROM apostadores a
        INNER JOIN estados e
          ON a.fk_estados = e.idestados
        -- LEFT JOIN v_saldosapostadores v
        --   ON a.idapostadores = v.idapostadores
        WHERE a.nombre LIKE ? -- COLLATE NOCASE
        """;

        // Filtro por estado, si aplica
        if ("activo".equals(stateFilter)) {
            sSQL += " AND e.estados = 'activo' ";
        } else if ("inactivo".equals(stateFilter)) {
            sSQL += " AND e.estados = 'inactivo' ";
        }

        sSQL += " ORDER BY a.idapostadores DESC";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {

            // 1) Param 1: search por nombre
            pst.setString(1, "%" + search + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    // 2) Leemos el saldo (ya nunca será NULL porque usamos COALESCE)
                    int saldo = rs.getInt("saldo");
                    String saldoFormateado = formateador14.format(saldo);

                    model.addRow(new Object[]{
                        rs.getInt("idapostadores"),
                        rs.getString("cedula"),
                        rs.getString("nombre"),
                        saldoFormateado,
                        rs.getString("observacion"),
                        rs.getString("estados")
                    });
                }
            }

        } catch (SQLException e) {
            Logger.getLogger(Apostadores_Repository.class.getName())
                    .log(Level.SEVERE, "Error al consultar apostadores", e);
        }
        return model;
    }

    //Mostrar historial del apostador
    public DefaultTableModel showHistorial(int apostadorId, String dateFrom, String dateTo) {

        boolean filtrarFechas = dateFrom != null && !dateFrom.isBlank()
                && dateTo != null && !dateTo.isBlank();

        String[] titles = {
            "ID Apuesta", "Id Apostador", "Apostador", "Cédula",
            "Apuesta", "Monto", "Fecha", "Caballo", "Resultado", "Saldo"
        };
        DefaultTableModel model = new DefaultTableModel(null, titles);

        String sSQL = """
        SELECT 
          a.idapuestas,
          ap.idapostadores,
          ap.nombre,
          ap.cedula,
          a.apuesta,
          a.monto,
          a.fecha,           -- almacenada como 'yyyy-MM-dd'
          c.caballos,
          c.idcaballos,
          ca.idganador,
          ap.saldo
          -- COALESCE(v.saldo, 0) AS saldo
        FROM apuestas a
        INNER JOIN apostadores ap
          ON a.fk_apostadores = ap.idapostadores
        INNER JOIN caballos c
          ON a.fk_caballos = c.idcaballos
        INNER JOIN carreras ca
          ON a.fk_carreras = ca.idcarreras
        -- LEFT JOIN v_saldosapostadores v
        --  ON ap.idapostadores = v.idapostadores
        WHERE a.fk_apostadores = ?
        """;

        if (filtrarFechas) {
            sSQL += " AND a.fecha BETWEEN ? AND ?";
        }

        sSQL += " ORDER BY a.fecha DESC";

        // Formateadores
        SimpleDateFormat formatoBD = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoMostrar = new SimpleDateFormat("dd/MM/yyyy");

        try (
                Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            int param = 1;

            pst.setInt(param++, apostadorId);

            if (filtrarFechas) {
                pst.setString(param++, dateFrom);
                pst.setString(param++, dateTo);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int i = 0;
                    Object[] row = new Object[10];

                    row[i++] = rs.getInt("idapuestas");
                    row[i++] = rs.getInt("idapostadores");
                    row[i++] = rs.getString("nombre");
                    row[i++] = rs.getString("cedula");
                    row[i++] = rs.getString("apuesta");
                    row[i++] = formateador14.format(rs.getInt("monto"));

                    // Conversión manual de la fecha de la apuesta
                    String fechaBDStr = rs.getString("fecha");
                    String fechaFormateada;
                    if (fechaBDStr != null && !fechaBDStr.isBlank()) {
                        try {
                            java.util.Date parsed = formatoBD.parse(fechaBDStr);
                            fechaFormateada = formatoMostrar.format(parsed);
                        } catch (ParseException pe) {
                            fechaFormateada = fechaBDStr;
                        }
                    } else {
                        fechaFormateada = "";
                    }
                    row[i++] = fechaFormateada;

                    row[i++] = rs.getString("caballos");

                    int idCaballoApostado = rs.getInt("idcaballos");
                    Integer idGanador = rs.getObject("idganador") != null
                            ? rs.getInt("idganador")
                            : null;
                    if (idGanador == null) {
                        row[i++] = "Pendiente";
                    } else if (idGanador.equals(idCaballoApostado)) {
                        row[i++] = "Ganador";
                    } else {
                        row[i++] = "Perdedor";
                    }

                    int saldo = rs.getInt("saldo");
                    row[i++] = formateador14.format(saldo);

                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(Apostadores_Repository.class.getName())
                    .log(Level.SEVERE, "Error al obtener el historial de apuestas", e);
        }
        return model;
    }

    public int getMaxCodigo() {
        int maxcodigo = 0;
        String codigo;
        String sSQL = "SELECT MAX(idapostadores) AS max_id FROM apostadores";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) { //Don't touch!
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                codigo = rs.getString("max_id");
                if (codigo != null) {
                    maxcodigo = Integer.parseInt(rs.getString("max_id"));
                } else {
                    maxcodigo = 0;
                }

            }
            return maxcodigo + 1;
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, e);
            return 0;
        }
    }

    public int getSaldo(int idapostadores) {
        int saldo = 0;
        String codigo;
        String sSQL = "SELECT saldo FROM apostadores WHERE idapostadores = ?";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) { //Don't touch!
            int param = 1;
            pst.setInt(param++, idapostadores);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                codigo = rs.getString("saldo");
                if (codigo != null) {
                    saldo = Integer.parseInt(rs.getString("saldo"));
                } else {
                    saldo = 0;
                }
            }
            return saldo;
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, e);
            return 0;
        }
    }
//********************************End of Display Methods********************************
}
