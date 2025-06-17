package Repository;

import Config.DataSource;
import Model.Abonos_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SwichBlade15
 */
public class Abonos_Repository {

    String sql = "";
    PreparedStatement pst = null;
    DecimalFormat formateador14 = new DecimalFormat("#,###.###");

//********************************Begin of Insert, Update, Disable********************************
    public boolean insert(Abonos_Model model) {
        String sqlInsertAbono
                = "INSERT INTO abonos(fecha, monto, origen, fk_apuestas, fk_apostadores) VALUES(?,?,?,?,?)";
        String sqlSumAbonos = """
        SELECT 
          SUM(monto) AS totalAbonos,
          MIN(origen) AS minOrigen,
          MAX(origen) AS maxOrigen
        FROM abonos
        WHERE fk_apuestas = ?
    """;
        String sqlGetMontoApuesta
                = "SELECT monto FROM apuestas WHERE idapuestas = ?";
        String sqlUpdateApuesta
                = "UPDATE apuestas SET abonado = ?, estadopago = ?, origenpago = ? WHERE idapuestas = ?";

        try (Connection cn = DataSource.getConnection()) {
            cn.setAutoCommit(false);

            // 1) Insertar abono
            try (PreparedStatement pst = cn.prepareStatement(sqlInsertAbono)) {
                pst.setString(1, model.getFecha());
                pst.setInt(2, model.getMonto());
                pst.setString(3, model.getOrigen());
                pst.setInt(4, model.getFk_apuestas());
                pst.setInt(5, model.getFk_apostadores());
                pst.executeUpdate();
            }

            int idApuesta = model.getFk_apuestas();

            // 2) Calcular totalAbonos, minOrigen, maxOrigen
            int totalAbonos;
            String minOrigen, maxOrigen;
            try (PreparedStatement ps = cn.prepareStatement(sqlSumAbonos)) {
                ps.setInt(1, idApuesta);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    totalAbonos = rs.getInt("totalAbonos");
                    minOrigen = rs.getString("minOrigen");
                    maxOrigen = rs.getString("maxOrigen");
                }
            }

            // 3) Obtener el monto total de la apuesta
            int montoApuesta;
            try (PreparedStatement ps2 = cn.prepareStatement(sqlGetMontoApuesta)) {
                ps2.setInt(1, idApuesta);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (!rs2.next()) {
                        throw new SQLException("Apuesta no encontrada: " + idApuesta);
                    }
                    montoApuesta = rs2.getInt("monto");
                }
            }

            // 4) Determinar estado_pago
            String estadoPago;
            if (totalAbonos == 0) {
                estadoPago = "pendiente";
            } else if (totalAbonos < montoApuesta) {
                estadoPago = "parcial";
            } else {
                estadoPago = "completa";
            }

            // 5) Determinar origenpago
            String origenPago;
            if (minOrigen != null && minOrigen.equals(maxOrigen)) {
                // todos los abonos comparten mismo origen
                origenPago = minOrigen;
            } else {
                origenPago = "mixto";
            }

            // 6) Actualizar la apuesta
            try (PreparedStatement up = cn.prepareStatement(sqlUpdateApuesta)) {
                up.setInt(1, totalAbonos);
                up.setString(2, estadoPago);
                up.setString(3, origenPago);
                up.setInt(4, idApuesta);
                up.executeUpdate();
            }

            cn.commit();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            // Si la conexión está abierta, deshacemos cualquier cambio
            try {
                if (!DataSource.getConnection().isClosed()) {
                    DataSource.getConnection().rollback();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            return false;
        }
    }

//    public boolean update(Abonos_Model model) {
//        sql = "UPDATE abonos SET fecha = ?, monto = ?, origen = ?, fk_apuestas = ?, fk_apostadores = ? WHERE idabonos = ?";
//        try (Connection cn = DataSource.getConnection()) {
//            pst = cn.prepareStatement(sql);
//            pst.setString(1, model.getFecha());
//            pst.setInt(2, model.getMonto());
//            pst.setString(3, model.getOrigen());
//            pst.setInt(4, model.getFk_apuestas());
//            pst.setInt(5, model.getFk_apostadores());
//            pst.setInt(6, model.getIdabonos());
//
//            int N = pst.executeUpdate();
//            return N != 0;
//        } catch (SQLException ex) {
//            Logger.getLogger(Apostadores_Repository.class.getName()).log(Level.SEVERE, null, ex);
//            return false;
//        }
//    }
//    public boolean updateSaldo(Apostadores_Model model) {
//        sql = "UPDATE apostadores SET saldo = saldo + ? WHERE idapostadores = ?";
//        try (Connection cn = DataSource.getConnection()) {
//            pst = cn.prepareStatement(sql);
//            pst.setInt(1, model.getSaldo());
//            pst.setInt(2, model.getIdapostadores());
//
//            int N = pst.executeUpdate();
//            return N != 0;
//        } catch (SQLException ex) {
//            Logger.getLogger(Apostadores_Repository.class.getName()).log(Level.SEVERE, null, ex);
//            return false;
//        }
//    }
//******************************** End of Insert, Update, Disable ********************************  
//********************************Begin of Display Methods********************************
//    public DefaultTableModel showApostadores(String search, String stateFilter) {
//        String[] titles = {"Id", "Cedula", "Apostadores", "Saldo", "Observacion", "Estado"};
//        DefaultTableModel model = new DefaultTableModel(null, titles);
//
//        String sSQL = "SELECT a.idapostadores, a.cedula, a.nombre, a.saldo, a.observacion, e.estados "
//                + "FROM apostadores a "
//                + "INNER JOIN estados e ON a.fk_estados = e.idestados "
//                + "WHERE a.nombre LIKE ? COLLATE NOCASE ";
//
//        if ("activo".equals(stateFilter)) {
//            sSQL += "AND e.estados = 'activo' ";
//        } else if ("inactivo".equals(stateFilter)) {
//            sSQL += "AND e.estados = 'inactivo' ";
//        }
//
//        sSQL += "ORDER BY a.idapostadores DESC";
//
//        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
//
//            pst.setString(1, "%" + search + "%");
//
//            try (ResultSet rs = pst.executeQuery()) {
//                while (rs.next()) {
//                    int saldo = rs.getInt("saldo");
//                    String saldoformateado = formateador14.format(saldo);
//
//                    model.addRow(new Object[]{
//                        rs.getString("idapostadores"),
//                        rs.getString("cedula"),
//                        rs.getString("nombre"),
//                        saldoformateado,
//                        rs.getString("observacion"),
//                        rs.getString("estados")
//                    });
//                }
//            }
//
//        } catch (SQLException e) {
//            Logger.getLogger(Apostadores_Repository.class.getName()).log(Level.SEVERE, "Error al consultar apostadores", e);
//        }
//
//        return model;
//    }
//
//    //Mostrar apuestas
//    public DefaultTableModel showHistorial(int apostadorId) {
//        String[] titles = {"ID Apuesta", "Id Apostador", "Apostador", "Cédula", "Apuesta", "Monto", "Fecha", "Caballo", "Resultado", "Saldo"};
//        DefaultTableModel model = new DefaultTableModel(null, titles);
//
//        String sSQL = "SELECT a.idapuestas, ap.idapostadores, ap.nombre, ap.cedula, a.apuesta, a.monto, a.fecha, "
//                + "c.caballos, c.idcaballos, ca.idganador, ap.saldo "
//                + "FROM apuestas a "
//                + "INNER JOIN apostadores ap ON a.fk_apostadores = ap.idapostadores "
//                + "INNER JOIN caballos c ON a.fk_caballos = c.idcaballos "
//                + "INNER JOIN carreras ca ON a.fk_carreras = ca.idcarreras "
//                + "WHERE a.fk_apostadores = ?";
//
//        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
//
//            pst.setInt(1, apostadorId);
//
//            try (ResultSet rs = pst.executeQuery()) {
//                while (rs.next()) {
//                    Object[] row = new Object[10];
//
//                    row[0] = rs.getInt("idapuestas");
//                    row[1] = rs.getInt("idapostadores");
//                    row[2] = rs.getString("nombre");
//                    row[3] = rs.getString("cedula");
//                    row[4] = rs.getString("apuesta");
//                    row[5] = rs.getInt("monto");
//                    row[6] = rs.getString("fecha");
//                    row[7] = rs.getString("caballos");
//
//                    int idCaballoApostado = rs.getInt("idcaballos");
//                    Integer idGanador = rs.getObject("idganador") != null ? rs.getInt("idganador") : null;
//
//                    if (idGanador == null) {
//                        row[8] = "Pendiente";
//                    } else if (idGanador == idCaballoApostado) {
//                        row[8] = "Ganador";
//                    } else {
//                        row[8] = "Perdedor";
//                    }
//
//                    row[9] = rs.getString("saldo");
//                    model.addRow(row);
//                }
//            }
//        } catch (SQLException e) {
//            Logger.getLogger(Apostadores_Repository.class.getName()).log(Level.SEVERE, "Error al obtener el historial de apuestas", e);
//        }
//        return model;
//    }
//
//    public int getMaxCodigo() {
//        int maxcodigo = 0;
//        String codigo;
//        String sSQL = "SELECT MAX(idapostadores) AS max_id FROM apostadores";
//
//        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) { //Don't touch!
//
//            ResultSet rs = pst.executeQuery();
//
//            while (rs.next()) {
//                codigo = rs.getString("max_id");
//                if (codigo != null) {
//                    maxcodigo = Integer.parseInt(rs.getString("max_id"));
//                } else {
//                    maxcodigo = 0;
//                }
//
//            }
//            return maxcodigo + 1;
//        } catch (SQLException e) {
//            JOptionPane.showConfirmDialog(null, e);
//            return 0;
//        }
//    }
//********************************End of Display Methods********************************
}
