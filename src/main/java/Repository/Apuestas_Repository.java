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
        sql = "INSERT INTO apuestas(apuesta, monto, abonado, fecha, fechalimite, observacion, fk_carreras, fk_caballos, fk_apostadores, fk_estados) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?)";

        try (Connection cn = DataSource.getConnection()) {
            // Asegurar que las claves foráneas estén activadas
            try (Statement stmt = cn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            try (PreparedStatement pst = cn.prepareStatement(sql)) {
                pst.setString(1, model.getApuesta());
                pst.setInt(2, model.getMonto());
                pst.setInt(3, model.getAbonado());
                pst.setString(4, model.getFecha()); // Asegurar formato YYYY-MM-DD HH:MM:SS
                pst.setString(5, model.getFechalimite());
                pst.setString(6, model.getObservacion());
                pst.setInt(7, model.getFk_carreras());
                pst.setInt(8, model.getFk_caballos());
                pst.setInt(9, model.getFk_apostadores());
                pst.setInt(10, model.getFk_estados());

                return pst.execute(); // `execute()` es más eficiente en SQLite
            }
        } catch (SQLException ex) {
            Logger.getLogger(Apuestas_Repository.class.getName()).log(Level.SEVERE, "Error al insertar apuesta", ex);
            return false;
        }
    }

    public boolean update(Apuestas_Model model) {
        sql = "UPDATE apuestas SET apuesta = ?, monto = ?, abonado = ?, fecha = ?, fechalimite = ?, observacion = ?, "
                + "fk_carreras = ?, fk_caballos = ?, fk_apostadores = ?, fk_estados = ? WHERE idapuestas = ?";

        try (Connection cn = DataSource.getConnection()) {
            try (Statement stmt = cn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            try (PreparedStatement pst = cn.prepareStatement(sql)) {
                pst.setString(1, model.getApuesta());
                pst.setInt(2, model.getMonto());
                pst.setInt(3, model.getAbonado());
                pst.setString(4, model.getFecha());
                pst.setString(5, model.getFechalimite());
                pst.setString(6, model.getObservacion());
                pst.setInt(7, model.getFk_carreras());
                pst.setInt(8, model.getFk_caballos());
                pst.setInt(9, model.getFk_apostadores());
                pst.setInt(10, model.getFk_estados());
                pst.setInt(11, model.getIdapuestas());

                return pst.execute(); // Más eficiente para SQLite
            }
        } catch (SQLException ex) {
            Logger.getLogger(Apuestas_Repository.class.getName()).log(Level.SEVERE, "Error al actualizar apuesta", ex);
            return false;
        }
    }
//******************************** End of Insert, Update, Disable ********************************  

//********************************Begin of Display Methods********************************
    public DefaultTableModel showApuestas(String search, String stateFilter, Date startDate, Date endDate) {
        SimpleDateFormat formatoBD = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoMostrar = new SimpleDateFormat("dd/MM/yyyy");
        DefaultTableModel model;
        String[] titles = {"Id", "Apuesta", "Monto", "Abonado", "Fecha", "F. Limite", "Observacion", "Apostador", "Carreras", "Caballos", "Estado"};
        String[] records = new String[titles.length];
        int totalRecords = 0;
        model = new DefaultTableModel(null, titles);

        String sSQL = "SELECT a.idapuestas, "
                + "       a.apuesta, "
                + "       a.monto, "
                + "       a.abonado, "
                + "       a.fecha, "
                + "       a.fechalimite, "
                + "       a.observacion, "
                + "       ap.nombre AS apostador, "
                + "       c.nombre  AS carrera, "
                + "       cc.caballos  AS caballo, "
                + "       e.estados "
                + "FROM   apuestas a "
                + "JOIN   apostadores ap ON a.fk_apostadores = ap.idapostadores "
                + "JOIN   carreras c     ON a.fk_carreras    = c.idcarreras "
                + "JOIN   caballos cc     ON a.fk_caballos    = cc.idcaballos "
                + "JOIN   estados e      ON a.fk_estados     = e.idestados "
                + "WHERE  ap.nombre LIKE ?";

        if (stateFilter.equalsIgnoreCase("activo")) {
            sSQL += " AND e.estados = 'activo'";
        } else if (stateFilter.equalsIgnoreCase("inactivo")) {
            sSQL += " AND e.estados = 'inactivo'";
        }

        // Filtro de fechas si no son nulas
        if (startDate != null && endDate != null) {
            sSQL += " AND a.fecha BETWEEN ? AND ?";
        }

        sSQL += " ORDER BY a.idapuestas DESC";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            int index = 1;
            pst.setString(index++, "%" + search + "%");

            if (startDate != null && endDate != null) {
                pst.setDate(index++, new java.sql.Date(startDate.getTime()));
                pst.setDate(index++, new java.sql.Date(endDate.getTime()));
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                records[0] = rs.getString("idapuestas");
                records[1] = rs.getString("apuesta");
                records[2] = rs.getString("monto");
                records[3] = rs.getString("abonado");

                //Format fecha from yyyy-MM-dd to dd/MM/yyyy
                String fecha = rs.getString("fecha");
                if (fecha != null && !fecha.equals("")) {
                    try {
                        Date fechaBD = formatoBD.parse(fecha);
                        records[4] = formatoMostrar.format(fechaBD);
                    } catch (ParseException e) {
                        records[4] = fecha;
                    }
                } else {
                    records[4] = "";
                }

                //Format fechalimite from yyyy-MM-dd to dd/MM/yyyy
                String fechalimite = rs.getString("fechalimite");
                if (fechalimite != null && !fechalimite.equals("")) {
                    try {
                        Date fechaBD = formatoBD.parse(fechalimite);
                        records[5] = formatoMostrar.format(fechaBD);
                    } catch (ParseException e) {
                        records[5] = fechalimite;
                    }
                } else {
                    records[5] = "";
                }

                records[6] = rs.getString("observacion");
                records[7] = rs.getString("apostador");
                records[8] = rs.getString("carrera");
                records[9] = rs.getString("caballo");
                records[10] = rs.getString("estados");

                totalRecords++;
                model.addRow(records);
            }
            return model;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en la consulta SQL: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
