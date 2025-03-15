package Repository;

import Config.DataSource;
import Model.Detallecarreras_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Detallecarreras_Repository{
    
    String sql = "";
    PreparedStatement pst = null;

//********************************Begin of Insert, Update, Disable********************************
    public boolean insert(Detallecarreras_Model model) {
        sql = "INSERT INTO detallecarreras(fk_carreras, fk_caballos) VALUES(?,?)";
        try (Connection cn = DataSource.getConnection()) {
            pst = cn.prepareStatement(sql);
            pst.setInt(1, model.getFk_carreras());
            pst.setInt(2, model.getFk_caballos());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Detallecarreras_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean update(Detallecarreras_Model model) {
        sql = "UPDATE Detallecarreras SET fk_carreras = ?, fk_caballos = ? WHERE iddetallecarreras = ?";
        try (Connection cn = DataSource.getConnection()) {
            pst = cn.prepareStatement(sql);
            pst.setInt(1, model.getFk_carreras());
            pst.setInt(2, model.getFk_caballos());
            pst.setInt(3, model.getIddetallecarreras());

            int N = pst.executeUpdate();
            return N != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Detallecarreras_Repository.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
//******************************** End of Insert, Update, Disable ********************************  
}