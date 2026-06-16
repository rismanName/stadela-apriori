package com.stadela.dao;

import com.stadela.db.DBConnection;
import com.stadela.model.Toko;

import java.sql.*;

public class TokoDAO {

    public Toko find() throws SQLException {
        String sql = "SELECT * FROM toko WHERE id_toko = 1 LIMIT 1";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                Toko t = new Toko();
                t.setIdToko(rs.getInt("id_toko"));
                t.setNamaToko(rs.getString("nama_toko"));
                t.setAlamatToko(rs.getString("alamat_toko"));
                t.setTlp(rs.getString("tlp"));
                t.setNamaPemilik(rs.getString("nama_pemilik"));
                return t;
            }
        }
        return null;
    }

    public void update(Toko t) throws SQLException {
        String sql = "UPDATE toko SET nama_toko=?, alamat_toko=?, tlp=?, nama_pemilik=? WHERE id_toko=1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getNamaToko());
            ps.setString(2, t.getAlamatToko());
            ps.setString(3, t.getTlp());
            ps.setString(4, t.getNamaPemilik());
            ps.executeUpdate();
        }
    }
}
