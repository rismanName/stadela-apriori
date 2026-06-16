package com.stadela.dao;

import com.stadela.db.DBConnection;
import com.stadela.model.Menu;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    private Menu map(ResultSet rs) throws SQLException {
        Menu m = new Menu();
        m.setIdMenu(rs.getInt("id_menu"));
        m.setIdKategori(rs.getInt("id_kategori"));
        m.setKodeMenu(rs.getString("kode_menu"));
        m.setNamaMenu(rs.getString("nama_menu"));
        m.setHarga(rs.getBigDecimal("harga"));
        m.setStok(rs.getInt("stok"));
        m.setSatuan(rs.getString("satuan"));
        m.setDeskripsi(rs.getString("deskripsi"));
        m.setFoto(rs.getString("foto"));
        m.setTglInput(rs.getTimestamp("tgl_input"));
        m.setTglUpdate(rs.getTimestamp("tgl_update"));
        try { m.setNamaKategori(rs.getString("nama_kategori")); } catch (SQLException ignored) {}
        return m;
    }

    public List<Menu> findAll() throws SQLException {
        List<Menu> list = new ArrayList<>();
        String sql = "SELECT m.*, k.nama_kategori FROM menu m " +
                     "LEFT JOIN kategori k ON k.id_kategori = m.id_kategori ORDER BY m.nama_menu ASC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Menu> findByKeyword(String keyword) throws SQLException {
        List<Menu> list = new ArrayList<>();
        String param = "%" + keyword + "%";
        String sql = "SELECT m.*, k.nama_kategori FROM menu m " +
                     "LEFT JOIN kategori k ON k.id_kategori = m.id_kategori " +
                     "WHERE m.kode_menu LIKE ? OR m.nama_menu LIKE ? ORDER BY m.nama_menu ASC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, param);
            ps.setString(2, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Menu findById(int id) throws SQLException {
        String sql = "SELECT m.*, k.nama_kategori FROM menu m " +
                     "LEFT JOIN kategori k ON k.id_kategori = m.id_kategori WHERE m.id_menu=? LIMIT 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public int count() throws SQLException {
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM menu")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int totalStok() throws SQLException {
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(stok),0) FROM menu")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Generate kode menu berikutnya */
    public String nextKode() throws SQLException {
        String sql = "SELECT id_menu FROM menu ORDER BY id_menu DESC LIMIT 1";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int lastId = rs.next() ? rs.getInt(1) : 0;
            return String.format("MN%03d", lastId + 1);
        }
    }

    public void insert(Menu m) throws SQLException {
        String sql = "INSERT INTO menu (id_kategori, kode_menu, nama_menu, harga, stok, satuan, deskripsi, foto) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, m.getIdKategori());
            ps.setString(2, m.getKodeMenu());
            ps.setString(3, m.getNamaMenu());
            ps.setBigDecimal(4, m.getHarga());
            ps.setInt(5, m.getStok());
            ps.setString(6, m.getSatuan());
            ps.setString(7, m.getDeskripsi());
            ps.setString(8, m.getFoto());
            ps.executeUpdate();
        }
    }

    public void update(Menu m) throws SQLException {
        String sql = "UPDATE menu SET id_kategori=?, kode_menu=?, nama_menu=?, harga=?, stok=?, satuan=?, deskripsi=?, foto=? WHERE id_menu=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, m.getIdKategori());
            ps.setString(2, m.getKodeMenu());
            ps.setString(3, m.getNamaMenu());
            ps.setBigDecimal(4, m.getHarga());
            ps.setInt(5, m.getStok());
            ps.setString(6, m.getSatuan());
            ps.setString(7, m.getDeskripsi());
            ps.setString(8, m.getFoto());
            ps.setInt(9, m.getIdMenu());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM menu WHERE id_menu=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
