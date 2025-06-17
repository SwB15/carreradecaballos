package Repository;

import Config.DataSource;
import Model.Apuestas_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Apuestas_Repository {

    String sql = "";
    PreparedStatement pst = null;

//********************************Begin of Insert, Update, Disable********************************
    public boolean insert(Apuestas_Model model) {
        String sql = """
            INSERT INTO apuestas(
                apuesta, monto, fecha, fechalimite,
                observacion, saldousado,
                fk_carreras, fk_caballos,
                fk_apostadores, fk_estadopago, fk_estados
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection cn = DataSource.getConnection()) {
            // activar claves foráneas
//            try (Statement stmt = cn.createStatement()) {
//                stmt.execute("PRAGMA foreign_keys = ON;");
//            }
            // preparamos el statement pidiendo las claves generadas
            try (PreparedStatement pst = cn.prepareStatement(sql)) {

                int i = 1;
                pst.setString(i++, model.getApuesta());
                pst.setInt(i++, model.getMonto());
                pst.setString(i++, model.getFecha());
                pst.setString(i++, model.getFechalimite());
                pst.setString(i++, model.getObservacion());
                pst.setString(i++, model.getSaldousado());
                pst.setInt(i++, model.getFk_carreras());
                pst.setInt(i++, model.getFk_caballos());
                pst.setInt(i++, model.getFk_apostadores());
                pst.setInt(i++, model.getFk_estadopago());
                pst.setInt(i++, model.getFk_estados());

                return pst.execute();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Apuestas_Repository.class.getName()).log(Level.SEVERE, "Error al insertar apuesta", ex);
            return false;
        }
    }

    public boolean update(Apuestas_Model model) {
        sql = "UPDATE apuestas SET apuesta = ?, monto = ?, fecha = ?, fechalimite = ?, observacion = ?, saldousado = ?,"
                + "fk_carreras = ?, fk_caballos = ?, fk_apostadores = ?, fk_estadopago = ?, fk_estados = ? WHERE idapuestas = ?";

        try (Connection cn = DataSource.getConnection()) {
//            try (Statement stmt = cn.createStatement()) {
//                stmt.execute("PRAGMA foreign_keys = ON;");
//            }

            try (PreparedStatement pst = cn.prepareStatement(sql)) {
                int i = 1;
                pst.setString(i++, model.getApuesta());
                pst.setInt(i++, model.getMonto());
                pst.setString(i++, model.getFecha());
                pst.setString(i++, model.getFechalimite());
                pst.setString(i++, model.getObservacion());
                pst.setString(i++, model.getSaldousado());
                pst.setInt(i++, model.getFk_carreras());
                pst.setInt(i++, model.getFk_caballos());
                pst.setInt(i++, model.getFk_apostadores());
                pst.setInt(i++, model.getFk_estadopago());
                pst.setInt(i++, model.getFk_estados());
                pst.setInt(i++, model.getIdapuestas());

                return pst.execute();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Apuestas_Repository.class.getName()).log(Level.SEVERE, "Error al actualizar apuesta", ex);
            return false;
        }
    }

    public boolean disable(Apuestas_Model model) {
        sql = "UPDATE apuestas SET fk_estadopago = ?, fk_estados = ? WHERE idapuestas = ?";

        try (Connection cn = DataSource.getConnection()) {
            try (Statement stmt = cn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            try (PreparedStatement pst = cn.prepareStatement(sql)) {
                int i = 1;
                pst.setInt(i++, model.getFk_estadopago());
                pst.setInt(i++, model.getFk_estados());
                pst.setInt(i++, model.getIdapuestas());

                return pst.execute(); // Más eficiente para SQLite
            }
        } catch (SQLException ex) {
            Logger.getLogger(Apuestas_Repository.class.getName()).log(Level.SEVERE, "Error al actualizar apuesta", ex);
            return false;
        }
    }

    public boolean delete(Apuestas_Model model) {
        sql = "DELETE FROM apuestas WHERE idapuestas = ?";

        try (Connection cn = DataSource.getConnection()) {
            try (Statement stmt = cn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            try (PreparedStatement pst = cn.prepareStatement(sql)) {
                int i = 1;
                pst.setInt(i++, model.getIdapuestas());

                int filas = pst.executeUpdate();
                return filas > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Apuestas_Repository.class.getName()).log(Level.SEVERE, "Error al actualizar apuesta", ex);
            return false;
        }
    }
//******************************** End of Insert, Update, Disable ********************************  
//********************************Begin of Display Methods********************************

    public DefaultTableModel showApuestas(String searchIdCarrera,
            String stateFilter,
            Date startDate,
            Date endDate) {
        SimpleDateFormat formatoBD = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoMostrar = new SimpleDateFormat("dd/MM/yyyy");

        // 1) Añadimos dos columnas: "Total Abonado" y "Estado Pago"
        String[] titles = {
            "Id", "Apuesta", "Monto",
            "Fecha", "F. Límite", "Observación", "Saldousado",
            "Id Apostador", "Apostador", "Saldo",
            "Carrera", "Id Caballo", "Caballo", "Estado",
            "Total Abonado", "Estado Pago"
        };
        DefaultTableModel model = new DefaultTableModel(null, titles);

        // 2) Consulta: sumamos abonos y traemos descripción de estado de pago
        String sSQL
                = "SELECT a.idapuestas, "
                + "       a.apuesta, "
                + "       a.monto, "
                + "       a.fecha, "
                + "       a.fechalimite, "
                + "       a.observacion, "
                + "       a.saldousado, "
                + "       ap.idapostadores AS idapostador, "
                + "       ap.nombre         AS apostador, "
                + "       ap.saldo, "
                + "      /* COALESCE(v.saldo,0) AS saldo,*/ "
                + "       c.nombre          AS carrera, "
                + "       cc.idcaballos     AS idcaballo, "
                + "       cc.caballos       AS caballo, "
                + "       e.estados         AS estado, "
                + // subconsulta que agrupa abonos por apuesta
                "       COALESCE(mp.totalabonado,0) AS totalabonado, "
                + // descripción del estado de pago
                "       ep.descripcion    AS estadopago "
                + "FROM   apuestas a "
                + "JOIN   apostadores ap ON a.fk_apostadores = ap.idapostadores "
                + "/* LEFT JOIN v_saldosapostadores v ON ap.idapostadores = v.idapostadores*/ "
                + "JOIN   carreras c     ON a.fk_carreras    = c.idcarreras "
                + "JOIN   caballos cc    ON a.fk_caballos    = cc.idcaballos "
                + "JOIN   estados e      ON a.fk_estados     = e.idestados "
                + "LEFT JOIN ( "
                + "    SELECT fk_apuestas, SUM(monto) AS totalabonado "
                + "      FROM movimientos "
                + "      WHERE fk_tipomovimientos = 3 "
                + "     GROUP BY fk_apuestas "
                + ") mp ON mp.fk_apuestas = a.idapuestas "
                + "JOIN   estadopago ep  ON a.fk_estadopago  = ep.idestadopago "
                + "WHERE  a.fk_carreras  = ? "
                + "  AND e.estados       = 'activo' "
                + "ORDER  BY a.idapuestas DESC";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {

            // Parámetro de carrera
            try {
                pst.setInt(1, Integer.parseInt(searchIdCarrera));
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(
                        null,
                        "ID de carrera inválido: " + searchIdCarrera,
                        "Error de conversión",
                        JOptionPane.ERROR_MESSAGE
                );
                return model;
            }

            // Ejecución y volcado
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                // 3) Cada fila: nuevo array + índice reiniciado
                String[] records = new String[titles.length];
                int i = 0;

                records[i++] = rs.getString("idapuestas");
                records[i++] = rs.getString("apuesta");
                records[i++] = rs.getString("monto");

                // Fecha
                String f = rs.getString("fecha");
                if (f != null && !f.isEmpty()) {
                    try {
                        Date d = formatoBD.parse(f);
                        records[i++] = formatoMostrar.format(d);
                    } catch (ParseException ex) {
                        records[i++] = f;
                    }
                } else {
                    records[i++] = "";
                }

                // Fecha límite
                String fl = rs.getString("fechalimite");
                if (fl != null && !fl.isEmpty()) {
                    try {
                        Date d = formatoBD.parse(fl);
                        records[i++] = formatoMostrar.format(d);
                    } catch (ParseException ex) {
                        records[i++] = fl;
                    }
                } else {
                    records[i++] = "";
                }

                records[i++] = rs.getString("observacion");
                records[i++] = rs.getString("saldousado");
                records[i++] = rs.getString("idapostador");
                records[i++] = rs.getString("apostador");
                records[i++] = rs.getString("saldo");
                records[i++] = rs.getString("carrera");
                records[i++] = rs.getString("idcaballo");
                records[i++] = rs.getString("caballo");
                records[i++] = rs.getString("estado");

                // Nuestras dos nuevas columnas:
                records[i++] = rs.getString("totalabonado");
                records[i++] = rs.getString("estadopago");

                model.addRow(records);
            }
            return model;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error en la consulta SQL: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }

    //Get max Id from Apuestas
    public int getMaxCodigo() {
        int maxcodigo = 0;
        String codigo;
        String sSQL = "SELECT MAX(idapuestas) AS max_id FROM apuestas";

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

    //Get max Id from Apuestas
    public int getMaxid() {
        int maxcodigo = 0;
        String codigo = "";
        String sSQL = "SELECT MAX(idapuestas) AS max_id FROM apuestas";

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
            return maxcodigo;
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, e);
            return 0;
        }
    }

    public HashMap<String, String> fillApostadoresCombos() {
        HashMap<String, String> apostadoresMap = new HashMap<>();
        String sSQL = "SELECT a.idapostadores, a.nombre "
                + "FROM apostadores a "
                + "JOIN estados e ON a.fk_estados = e.idestados "
                + "WHERE e.estados = 'activo'";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("idapostadores");
                String name = rs.getString("nombre");

                apostadoresMap.put(id, name);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener los apostadores: " + e.getMessage());
            Logger.getLogger(Apuestas_Repository.class.getName()).log(Level.SEVERE, "Error en fillApostadoresCombos", e);
        }
        return apostadoresMap;
    }

    public HashMap<String, String> fillCarrerasCombos() {
        HashMap<String, String> carrerasMap = new HashMap<>();
        String sSQL = "SELECT c.idcarreras, c.nombre "
                + "FROM carreras c "
                + "JOIN estados e ON c.fk_estados = e.idestados "
                + "WHERE e.estados = 'activo'";

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

    public HashMap<String, String> fillCaballosCombos(int idcarreras) {
        HashMap<String, String> carrerasMap = new HashMap<>();
        String sSQL = "SELECT c.idcaballos, c.caballos "
                + "FROM caballos c "
                + "JOIN detallecarreras dc ON c.idcaballos = dc.fk_caballos "
                + "JOIN estados e ON c.fk_estados = e.idestados "
                + "WHERE dc.fk_carreras = ? AND e.estados = 'activo'";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {

            pst.setInt(1, idcarreras); // Pasar el ID de la carrera

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("idcaballos");
                    String name = rs.getString("caballos");
                    carrerasMap.put(id, name);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener los caballos: " + e.getMessage());
            Logger.getLogger(Apuestas_Repository.class.getName()).log(Level.SEVERE, "Error en fillCaballosCombos", e);
        }
        return carrerasMap;
    }
//********************************End of Display Methods********************************
}
