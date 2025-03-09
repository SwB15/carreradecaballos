package Repository;

import Config.DataSource;
import Model.Carreras_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Carreras_Repository {

    String sql = "";
    PreparedStatement pst = null;

//********************************Begin of Insert, Update, Disable********************************
    public boolean insert(Carreras_Model model) {
        sql = "INSERT INTO carreras(nombre, lugar, fecha, observacion, fk_estados) VALUES(?,?,?,?,?)";
        try (Connection cn = DataSource.getConnection()) {
            pst = cn.prepareStatement(sql);
            pst.setString(1, model.getNombre());
            pst.setString(2, model.getLugar());
            pst.setString(3, model.getFecha());
            pst.setString(4, model.getObservacion());
            pst.setInt(5, model.getFk_estados());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Carreras_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean update(Carreras_Model model) {
        sql = "UPDATE carreras SET nombre = ?, lugar = ?, fecha = ?, idganador = ?, observacion = ?, fk_estados = ? WHERE idcarreras = ?";
        try (Connection cn = DataSource.getConnection()) {
            pst = cn.prepareStatement(sql);
            pst.setString(1, model.getNombre());
            pst.setString(2, model.getLugar());
            pst.setString(3, model.getFecha());

            if (model.getIdganador() != null) {
                pst.setInt(4, model.getIdganador());
            } else {
                pst.setNull(4, Types.INTEGER);
            }

            pst.setString(5, model.getObservacion());
            pst.setInt(6, model.getFk_estados());
            pst.setInt(7, model.getIdcarreras());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Carreras_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
//******************************** End of Insert, Update, Disable ********************************  

//********************************Begin of Display Methods********************************
    public DefaultTableModel showCarreras(String search, String fase, String statusFilter) {
        DefaultTableModel model;
        String[] titles = {"Id", "Nombre", "Lugar", "Fecha", "Id Ganador", "Observacion", "Estado", "IDs Caballos", "Caballos", "Ganador"};
        model = new DefaultTableModel(null, titles);

        String sSQL = "SELECT c.idcarreras, c.nombre, c.lugar, c.fecha, c.idganador, c.observacion, e.estados, "
                + "g.caballos AS nombre_ganador, p.idcaballos, p.caballos "
                + "FROM carreras c "
                + "JOIN estados e ON c.fk_estados = e.idestados "
                + "LEFT JOIN caballos g ON c.idganador = g.idcaballos "
                + "LEFT JOIN detallecarreras dc ON c.idcarreras = dc.fk_carreras "
                + "LEFT JOIN caballos p ON dc.fk_caballos = p.idcaballos "
                + "WHERE c.nombre LIKE ? ";

        if (fase.equals("finalizados")) {
            sSQL += "AND c.idganador IS NOT NULL ";
        } else if (fase.equals("pendientes")) {
            sSQL += "AND c.idganador IS NULL ";
        }

        if (statusFilter.equals("activo")) {
            sSQL += " AND e.estados = 'activo'";
        } else if (statusFilter.equals("inactivo")) {
            sSQL += " AND e.estados = 'inactivo'";
        }

        System.out.println("Consulta generada: " + sSQL);

        sSQL += " ORDER BY c.idcarreras DESC";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            pst.setString(1, "%" + search + "%");

            ResultSet rs = pst.executeQuery();
            Integer lastCarreraId = -1;
            String lastIdganador = "";
            String lastGanador = "";
            String lastEstado = "";
            String lastObservacion = "";
            String lastNombre = "";
            String lastLugar = "";
            String lastFecha = "";
            HashMap<Integer, String> horsesList = new HashMap<>();
            HashMap<Integer, String> horsesIdList = new HashMap<>();

            while (rs.next()) {
                int currentCarreraId = rs.getInt("idcarreras");
                if (lastCarreraId != currentCarreraId && lastCarreraId != -1) {
                    model.addRow(new String[]{
                        String.valueOf(lastCarreraId), lastNombre, lastLugar, lastFecha, lastIdganador, lastObservacion,
                        lastEstado,
                        String.join("\n", horsesIdList.values()),
                        String.join("\n", horsesList.values()),
                        lastGanador
                    });

                    horsesList.clear();
                    horsesIdList.clear();
                }

                lastCarreraId = currentCarreraId;
                lastNombre = rs.getString("nombre") != null ? rs.getString("nombre") : "";
                lastLugar = rs.getString("lugar") != null ? rs.getString("lugar") : "";
                lastFecha = rs.getString("fecha") != null ? rs.getString("fecha") : "";
                lastIdganador = rs.getString("idganador");
                lastObservacion = rs.getString("observacion") != null ? rs.getString("observacion") : "";
                lastEstado = rs.getString("estados") != null ? rs.getString("estados") : "";
                lastGanador = rs.getString("nombre_ganador") != null ? rs.getString("nombre_ganador") : "Sin ganador";

                int horseId = rs.getInt("idcaballos");
                String horseName = rs.getString("caballos");
                if (horseId != 0 && horseName != null) {
                    horsesList.put(horseId, horseName);
                    horsesIdList.put(horseId, String.valueOf(horseId));
                }
            }

            if (lastCarreraId != -1) {
                model.addRow(new String[]{
                    String.valueOf(lastCarreraId), lastNombre, lastLugar, lastFecha, lastIdganador, lastObservacion,
                    lastEstado,
                    String.join("\n", horsesIdList.values()),
                    String.join("\n", horsesList.values()),
                    lastGanador
                });
            }

            return model;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return null;
        }
    }

    public DefaultTableModel showCarrerasInPrincipal(String search, String statusFilter) {
        DefaultTableModel model;
        String[] titles = {"Id", "Nombre", "Lugar", "Fecha", "Id Ganador", "Observacion", "Estado", "IDs Caballos", "Caballos", "Ganador"};
        model = new DefaultTableModel(null, titles);

        String sSQL = "SELECT c.idcarreras, c.nombre, c.lugar, c.fecha, c.idganador, c.observacion, e.estados, "
                + "g.caballos AS nombre_ganador, p.idcaballos, p.caballos "
                + "FROM carreras c "
                + "JOIN estados e ON c.fk_estados = e.idestados "
                + "LEFT JOIN caballos g ON c.idganador = g.idcaballos "
                + "LEFT JOIN detallecarreras dc ON c.idcarreras = dc.fk_carreras "
                + "LEFT JOIN caballos p ON dc.fk_caballos = p.idcaballos "
                + "WHERE e.estados = 'activo' "; // Siempre filtrar por activo

        // Filtrado por ID de carrera
        if (search != null && search.matches("\\d+")) {
            sSQL += "AND c.idcarreras = ? ";
        }

        // Filtrado por idGanador según el estado seleccionado en los radio buttons
        if ("Finalizado".equals(statusFilter)) {
            sSQL += "AND c.idganador IS NOT NULL ";
        } else if ("Pendiente".equals(statusFilter)) {
            sSQL += "AND c.idganador IS NULL ";
        }

        sSQL += "ORDER BY c.idcarreras DESC";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            int paramIndex = 1;

            if (search != null && search.matches("\\d+")) {
                pst.setInt(paramIndex++, Integer.parseInt(search));
            }

            ResultSet rs = pst.executeQuery();
            int lastCarreraId = -1;
            String lastIdganador = "";
            String lastGanador = "";
            String lastEstado = "";
            String lastObservacion = "";
            String lastNombre = "";
            String lastLugar = "";
            String lastFecha = "";
            HashMap<Integer, String> horsesList = new HashMap<>();
            HashMap<Integer, String> horsesIdList = new HashMap<>();

            while (rs.next()) {
                int currentCarreraId = rs.getInt("idcarreras");
                if (lastCarreraId != currentCarreraId && lastCarreraId != -1) {
                    model.addRow(new String[]{
                        String.valueOf(lastCarreraId), lastNombre, lastLugar, lastFecha, lastIdganador, lastObservacion,
                        lastEstado,
                        String.join("\n", horsesIdList.values()),
                        String.join("\n", horsesList.values()),
                        lastGanador
                    });

                    horsesList.clear();
                    horsesIdList.clear();
                }

                lastCarreraId = currentCarreraId;
                lastNombre = rs.getString("nombre");
                lastLugar = rs.getString("lugar");
                lastFecha = rs.getString("fecha");
                lastIdganador = rs.getString("idganador");
                lastObservacion = rs.getString("observacion");
                lastEstado = rs.getString("estados");
                lastGanador = rs.getString("nombre_ganador") != null ? rs.getString("nombre_ganador") : "Sin ganador";

                int horseId = rs.getInt("idcaballos");
                String horseName = rs.getString("caballos");
                if (horseId != 0 && horseName != null) {
                    horsesList.put(horseId, horseName);
                    horsesIdList.put(horseId, String.valueOf(horseId));
                }
            }

            if (lastCarreraId != -1) {
                model.addRow(new String[]{
                    String.valueOf(lastCarreraId), lastNombre, lastLugar, lastFecha, lastIdganador, lastObservacion,
                    lastEstado,
                    String.join("\n", horsesIdList.values()),
                    String.join("\n", horsesList.values()),
                    lastGanador
                });
            }

            return model;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return null;
        }
    }

    public DefaultTableModel showCaballosInCarreras(String search) {
        DefaultTableModel model;
        String[] titles = {"Id", "Caballos", "Estado"};
        model = new DefaultTableModel(null, titles);

        String sSQL = "SELECT c.idcaballos, c.caballos, e.estados "
                + "FROM caballos c "
                + "JOIN estados e ON c.fk_estados = e.idestados "
                + "WHERE e.estados = 'activo' ";

        // Si hay un criterio de búsqueda, lo agregamos al filtro
        if (search != null && !search.trim().isEmpty()) {
            sSQL += "AND c.caballos LIKE ? ";
        }

        sSQL += "ORDER BY c.caballos ASC";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            // Si hay un criterio de búsqueda, lo agregamos al PreparedStatement
            if (search != null && !search.trim().isEmpty()) {
                pst.setString(1, "%" + search + "%"); // Búsqueda parcial
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new String[]{
                    rs.getString("idcaballos"),
                    rs.getString("caballos"),
                    rs.getString("estados")
                });
            }
            return model;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return null;
        }
    }

    public int getMaxCodigo() {
        int maxcodigo = 0;
        String codigo;
        String sSQL = "SELECT MAX(idcarreras) AS max_id FROM carreras";

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

    public HashMap<String, String> getCaballosPorCarrera(int idcarrera) {
        HashMap<String, String> caballosMap = new HashMap<>();
        String sSQL = "SELECT c.idcaballos, c.caballos "
                + "FROM detallecarreras dc "
                + "JOIN caballos c ON dc.fk_caballos = c.idcaballos "
                + "WHERE dc.fk_carreras = ?";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {

            pst.setInt(1, idcarrera);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("idcaballos");
                    String nombre = rs.getString("caballos");
                    caballosMap.put(id, nombre);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener los caballos: " + e.getMessage());
            Logger.getLogger(Carreras_Repository.class.getName()).log(Level.SEVERE, "Error en getCaballosPorCarrera", e);
        }
        return caballosMap;
    }

    public HashMap<String, String> getCaballosGanador(int idcarrera) {
        HashMap<String, String> caballosMap = new HashMap<>();
        String sSQL = "SELECT c.idcaballos, c.caballos "
                + "FROM detallecarreras dc "
                + "JOIN caballos c ON dc.fk_caballos = c.idcaballos "
                + "WHERE dc.fk_carreras = ?";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {

            pst.setInt(1, idcarrera);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("idcaballos");
                    String nombre = rs.getString("caballos");
                    caballosMap.put(id, nombre);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener los caballos: " + e.getMessage());
            Logger.getLogger(Carreras_Repository.class.getName()).log(Level.SEVERE, "Error en getCaballosPorCarrera", e);
        }
        return caballosMap;
    }

//********************************End of Display Methods********************************
}
