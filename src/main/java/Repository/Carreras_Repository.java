package Repository;

import Config.DataSource;
import Model.Carreras_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Carreras_Repository {

    String sql = "";
    PreparedStatement pst = null;

//********************************Begin of Insert, Update, Disable********************************
    public boolean insert(Carreras_Model model) {
        sql = "INSERT INTO carreras(nombre, lugar, fecha, idganador, observacion, fk_estados) VALUES(?,?,?,?,?,?)";
        try (Connection cn = DataSource.getConnection()) {
            int i = 1;
            pst = cn.prepareStatement(sql);
            pst.setString(i++, model.getNombre());
            pst.setString(i++, model.getLugar());
            pst.setString(i++, model.getFecha());
            if (model.getIdganador() != null) {
                pst.setInt(i++, model.getIdganador());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }
            pst.setString(i++, model.getObservacion());
            pst.setInt(i++, model.getFk_estados());

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
            int i = 1;
            pst.setString(i++, model.getNombre());
            pst.setString(i++, model.getLugar());
            pst.setString(i++, model.getFecha());

            if (model.getIdganador() != null) {
                pst.setInt(i++, model.getIdganador());
            } else {
                pst.setNull(i++, Types.INTEGER);
            }

            pst.setString(i++, model.getObservacion());
            pst.setInt(i++, model.getFk_estados());
            pst.setInt(i++, model.getIdcarreras());

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

    public DefaultTableModel showCarrerasInPrincipal(
            String search,
            String estadoFilter, // "Activo", "Inactivo" o "Todos"
            String completionFilter, // "Finalizado", "Pendiente" o cualquier otro
            String dateFrom, // en "yyyy-MM-dd" o vacío
            String dateTo // en "yyyy-MM-dd" o vacío
    ) {
        LocalDate desde = null, hasta = null;
        try {
            if (dateFrom != null && !dateFrom.isEmpty()) {
                desde = LocalDate.parse(dateFrom);
            }
            if (dateTo != null && !dateTo.isEmpty()) {
                hasta = LocalDate.parse(dateTo);
            }
            if (desde != null && hasta != null && desde.isAfter(hasta)) {
                // rango inválido → devolver vacío
                return new DefaultTableModel();
            }
        } catch (DateTimeParseException ex) {
            // formato inválido → vacío (o lanza excepción)
            return new DefaultTableModel();
        }

        String[] titles = {
            "Id", "Nombre", "Lugar", "Fecha", "Id Ganador",
            "Observacion", "Estado", "IDs Caballos", "Caballos", "Ganador"
        };
        DefaultTableModel model = new DefaultTableModel(null, titles);

        // Construcción dinámica del SQL
        String sSQL = """
            SELECT c.idcarreras, c.nombre, c.lugar, c.fecha,
                   c.idganador, c.observacion, e.estados,
                   g.caballos AS nombre_ganador,
                   p.idcaballos, p.caballos
              FROM carreras c
              JOIN estados e    ON c.fk_estados   = e.idestados
              LEFT JOIN caballos g ON c.idganador  = g.idcaballos
              LEFT JOIN detallecarreras dc ON c.idcarreras = dc.fk_carreras
              LEFT JOIN caballos p ON dc.fk_caballos = p.idcaballos
             WHERE 1=1
        """;

        // 1) Estado
        if (estadoFilter != null && !"Todos".equalsIgnoreCase(estadoFilter)) {
            sSQL += " AND e.estados = ? ";
        }
        // 2) Búsqueda por ID exacto
        if (search != null && search.matches("\\d+")) {
            sSQL += " AND c.idcarreras = ? ";
        }
        // 3) Finalizado / Pendiente
        if ("Finalizado".equalsIgnoreCase(completionFilter)) {
            sSQL += " AND c.idganador IS NOT NULL ";
        } else if ("Pendiente".equalsIgnoreCase(completionFilter)) {
            sSQL += " AND c.idganador IS NULL ";
        }
        // 4) Rango de fechas (texto ISO)
        if (desde != null && hasta != null) {
            sSQL += " AND c.fecha BETWEEN ? AND ? ";
        }

        sSQL += " ORDER BY c.idcarreras DESC";

        try (
                Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            int param = 1;
            // 1) Asignar estado
            if (estadoFilter != null && !"Todos".equalsIgnoreCase(estadoFilter)) {
                pst.setString(param++, estadoFilter);
            }
            // 2) Asignar búsqueda por ID
            if (search != null && search.matches("\\d+")) {
                pst.setInt(param++, Integer.parseInt(search));
            }
            // 4) Asignar rango de fechas como String ISO
            if (desde != null && hasta != null) {
                pst.setString(param++, dateFrom);
                pst.setString(param++, dateTo);
            }

            ResultSet rs = pst.executeQuery();
            int lastId = -1;
            Map<Integer, String> horses = new LinkedHashMap<>();
            Map<Integer, String> horsesId = new LinkedHashMap<>();
            String lastNombre = "", lastLugar = "", lastFecha = "",
                    lastIdGan = "", lastObs = "", lastEstado = "", lastWin = "";

            SimpleDateFormat formatoBD = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatoMostrar = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                int curId = rs.getInt("idcarreras");
                if (curId != lastId && lastId != -1) {
                    model.addRow(new String[]{
                        String.valueOf(lastId), lastNombre, lastLugar, lastFecha,
                        lastIdGan, lastObs, lastEstado,
                        String.join("\n", horsesId.values()),
                        String.join("\n", horses.values()),
                        lastWin
                    });
                    horses.clear();
                    horsesId.clear();
                }
                lastId = curId;
                lastNombre = rs.getString("nombre");
                lastLugar = rs.getString("lugar");

                // Formatear fecha TEXT ISO → dd/MM/yyyy
                String f = rs.getString("fecha");
                if (f != null && !f.isEmpty()) {
                    try {
                        Date d = formatoBD.parse(f);
                        lastFecha = formatoMostrar.format(d);
                    } catch (ParseException ex) {
                        lastFecha = f;
                    }
                } else {
                    lastFecha = "";
                }

                lastIdGan = rs.getString("idganador");
                lastObs = rs.getString("observacion");
                lastEstado = rs.getString("estados");
                lastWin = rs.getString("nombre_ganador") != null
                        ? rs.getString("nombre_ganador")
                        : "Sin ganador";

                int hId = rs.getInt("idcaballos");
                String hName = rs.getString("caballos");
                if (hId != 0 && hName != null) {
                    horses.put(hId, hName);
                    horsesId.put(hId, String.valueOf(hId));
                }
            }
            // Agregar última fila
            if (lastId != -1) {
                model.addRow(new String[]{
                    String.valueOf(lastId), lastNombre, lastLugar, lastFecha,
                    lastIdGan, lastObs, lastEstado,
                    String.join("\n", horsesId.values()),
                    String.join("\n", horses.values()),
                    lastWin
                });
            }
            return model;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar carreras: " + e.getMessage());
            return null;
        }
    }

    public DefaultTableModel showCarrerasForEdit(String search, String statusFilter) {
        // 1) Ajusta aquí los títulos para que casen con los datos que vas a meter:
        String[] titles = {
            "Id", "Nombre", "Lugar", "Fecha", "Id Ganador",
            "Observacion", "Estado",
            "IDs Caballos", "Caballos",
            "ID Apostador", "Nombre Apostador",
            "Ganador"
        };
        DefaultTableModel model = new DefaultTableModel(null, titles);

        // 2) Consulta: sumamos joins para apuestas y apostadores
        String sSQL
                = "SELECT "
                + "c.idcarreras, c.nombre, c.lugar, c.fecha, c.idganador, c.observacion, "
                + "e.estados, "
                + "g.caballos AS nombre_ganador, "
                + "p.idcaballos, p.caballos AS nombre_caballo, "
                + "ap.idapostadores, ap.nombre AS nombre_apostador "
                + "FROM carreras c "
                + "JOIN estados e ON c.fk_estados = e.idestados "
                + "LEFT JOIN caballos g ON c.idganador = g.idcaballos "
                + "LEFT JOIN detallecarreras dc ON c.idcarreras = dc.fk_carreras "
                + "LEFT JOIN caballos p ON dc.fk_caballos = p.idcaballos "
                + "LEFT JOIN apuestas a ON c.idcarreras = a.fk_carreras AND p.idcaballos = a.fk_caballos "
                + "LEFT JOIN apostadores ap ON a.fk_apostadores = ap.idapostadores "
                + "WHERE e.estados = 'activo' ";

        // Filtros opcionales
        if (search != null && search.matches("\\d+")) {
            sSQL += "AND c.idcarreras = ? ";
        }
        if ("Finalizado".equals(statusFilter)) {
            sSQL += "AND c.idganador IS NOT NULL ";
        } else if ("Pendiente".equals(statusFilter)) {
            sSQL += "AND c.idganador IS NULL ";
        }
        sSQL += "ORDER BY c.idcarreras DESC";

        try (
                Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) {
            // 3) Parsea parámetro de búsqueda
            if (search != null && search.matches("\\d+")) {
                pst.setInt(1, Integer.parseInt(search));
            }

            ResultSet rs = pst.executeQuery();

            // 4) Variables de agrupación por carrera
            int lastCarreraId = -1;
            String lastNombre = "", lastLugar = "", lastFecha = "";
            String lastIdGanador = "", lastObservacion = "", lastEstado = "", lastGanador = "";

            // Mapas para acumular listas únicas
            Map<Integer, String> horsesIdList = new LinkedHashMap<>();
            Map<Integer, String> horsesNameList = new LinkedHashMap<>();
            Map<Integer, String> apostIdList = new LinkedHashMap<>();
            Map<Integer, String> apostNameList = new LinkedHashMap<>();

            while (rs.next()) {
                int currId = rs.getInt("idcarreras");
                // Cuando cambiamos de carrera, volcamos la fila anterior
                if (lastCarreraId != -1 && currId != lastCarreraId) {
                    model.addRow(new String[]{
                        String.valueOf(lastCarreraId),
                        lastNombre,
                        lastLugar,
                        lastFecha,
                        lastIdGanador,
                        lastObservacion,
                        lastEstado,
                        String.join("\n", horsesIdList.values()),
                        String.join("\n", horsesNameList.values()),
                        String.join("\n", apostIdList.values()),
                        String.join("\n", apostNameList.values()),
                        lastGanador
                    });
                    // limpiamos para la siguiente carrera
                    horsesIdList.clear();
                    horsesNameList.clear();
                    apostIdList.clear();
                    apostNameList.clear();
                }

                // Actualizamos valores “cabecera” de la carrera
                lastCarreraId = currId;
                lastNombre = rs.getString("nombre");
                lastLugar = rs.getString("lugar");
                lastFecha = rs.getString("fecha");
                lastIdGanador = rs.getString("idganador");
                lastObservacion = rs.getString("observacion");
                lastEstado = rs.getString("estados");
                lastGanador = rs.getString("nombre_ganador") != null
                        ? rs.getString("nombre_ganador")
                        : "Sin ganador";

                // Acumulamos caballo (si existe)
                Integer hId = (Integer) rs.getObject("idcaballos");
                String hName = rs.getString("nombre_caballo");
                if (hId != null && hName != null) {
                    horsesIdList.put(hId, String.valueOf(hId));
                    horsesNameList.put(hId, hName);
                }

                // Acumulamos apostador (si existe)
                Integer aId = (Integer) rs.getObject("idapostadores");
                String aName = rs.getString("nombre_apostador");
                if (aId != null && aName != null) {
                    apostIdList.put(aId, String.valueOf(aId));
                    apostNameList.put(aId, aName);
                }
            }

            // 5) Volcar la última carrera
            if (lastCarreraId != -1) {
                model.addRow(new String[]{
                    String.valueOf(lastCarreraId),
                    lastNombre,
                    lastLugar,
                    lastFecha,
                    lastIdGanador,
                    lastObservacion,
                    lastEstado,
                    String.join("\n", horsesIdList.values()),
                    String.join("\n", horsesNameList.values()),
                    String.join("\n", apostIdList.values()),
                    String.join("\n", apostNameList.values()),
                    lastGanador
                });
            }

            return model;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar carreras: " + e.getMessage());
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

    public int getIdganador(Integer idcarreras) {
        Integer idganador = 0;
        String sSQL = "SELECT idganador FROM carreras WHERE idcarreras =?";

        try (Connection cn = DataSource.getConnection(); PreparedStatement pst = cn.prepareStatement(sSQL)) { //Don't touch!
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                idganador = rs.getInt("idganador");
            }
            return idganador;
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
