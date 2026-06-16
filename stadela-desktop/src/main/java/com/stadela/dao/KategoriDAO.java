package com.stadela.dao;

import com.stadela.db.DBConnection;
import com.stadela.model.Kategori;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategoriDAO {

    private Kategori map(ResultSet rs) throws SQLException {
        Kategori k = new Kategori();
        k.setIdKategori(rs.getInt("id_kategori"));
        k.setNamaKategori(rs.getString("nama_kategori"));
        k.setTglInput(rs.getTimestamp("tgl_input"));
        k.setTglUpdate(rs.getTimestamp("tgl_update"));
        return k;
    }

    public List<Kategori> findAll() throws SQLException {
        List<Kategori> list = new ArrayList<>();
        String sql = "SELECT * FROM kategori ORDER BY nama_kategori ASC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Kategori findById(int id) throws SQLException {
        String sql = "SELECT * FROM kategori WHERE id_kategori = ? LIMIT 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public int count() throws SQLException {
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM kategori")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public void insert(String namaKategori) throws SQLException {
        String sql = "INSERT INTO kategori (nama_kategori) VALUES (?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, namaKategori);
            ps.executeUpdate();
        }
    }

    public void update(int id, String namaKategori) throws SQLException {
        String sql = "UPDATE kategori SET nama_kategori=?, tgl_update=NOW() WHERE id_kategori=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, namaKategori);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM kategori WHERE id_kategori=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
