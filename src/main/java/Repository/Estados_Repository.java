package Repository;

import Config.DataSource;
import Model.Estados_Model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Estados_Repository {

    public List<Estados_Model> findAll() throws SQLException {
        List<Estados_Model> estados = new ArrayList<>();
        String sql = "SELECT idestados, estados FROM estados ORDER BY idestados DESC";
        try (Connection conn = DataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                estados.add(new Estados_Model(
                        rs.getInt("idestados"),
                        rs.getString("estados")
                ));
            }
        }
        return estados;
    }
}
