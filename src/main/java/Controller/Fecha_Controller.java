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
        List<Vencidos_Model> model = new ArrayList<>();
        String sql = "SELECT a.idapuestas, a.monto, a.abonado, a.fecha, a.fechalimite, "
                + "ap.nombre AS nombre_apostador, c.nombre AS nombre_carrera "
                + "FROM apuestas a "
                + "INNER JOIN apostadores ap ON a.fk_apostadores = ap.idapostadores "
                + "INNER JOIN estados e ON a.fk_estados = e.idestados "
                + "INNER JOIN carreras c ON a.fk_carreras = c.idcarreras "
                + "WHERE DATE(a.fechalimite) < DATE('now') "
                + "AND a.monto > a.abonado "
                + "AND e.estados = 'activo' "
                + "ORDER BY a.idapuestas DESC";

        try (Connection cn = DataSource.getConnection(); Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Vencidos_Model apuesta = new Vencidos_Model();
                apuesta.id = rs.getString("idapuestas");
                apuesta.montoApuesta = rs.getInt("monto");
                apuesta.montoAbonado = rs.getInt("abonado");
                apuesta.fechaApuesta = rs.getString("fecha");
                apuesta.fechaLimite = rs.getString("fechalimite");
                apuesta.nombreApostador = rs.getString("nombre_apostador");
                apuesta.nombreCarrera = rs.getString("nombre_carrera");

                model.add(apuesta);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar la BD: " + e.getMessage());
        }
        return model;
    }
}