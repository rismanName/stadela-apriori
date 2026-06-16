package com.stadela.dao;

import com.stadela.db.DBConnection;
import com.stadela.model.DetailTransaksi;
import com.stadela.model.Transaksi;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {

    private Transaksi mapHeader(ResultSet rs) throws SQLException {
        Transaksi t = new Transaksi();
        t.setIdTransaksi(rs.getInt("id_transaksi"));
        t.setKodeTransaksi(rs.getString("kode_transaksi"));
        t.setIdUser(rs.getInt("id_user"));
        t.setTanggalTransaksi(rs.getDate("tanggal_transaksi"));
        t.setTotal(rs.getBigDecimal("total"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        try { t.setNamaUser(rs.getString("nama")); } catch (SQLException ignored) {}
        return t;
    }

    private DetailTransaksi mapDetail(ResultSet rs) throws SQLException {
        DetailTransaksi d = new DetailTransaksi();
        d.setIdDetail(rs.getInt("id_detail"));
        d.setIdTransaksi(rs.getInt("id_transaksi"));
        d.setIdMenu(rs.getInt("id_menu"));
        d.setNamaMenu(rs.getString("nama_menu"));
        d.setHargaSatuan(rs.getBigDecimal("harga_satuan"));  // set before jumlah!
        d.setJumlah(rs.getInt("jumlah"));
        d.setSubtotal(rs.getBigDecimal("subtotal"));
        return d;
    }

    public List<Transaksi> findAll() throws SQLException {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT t.*, u.nama FROM transaksi t " +
                     "LEFT JOIN users u ON u.id_user = t.id_user " +
                     "ORDER BY t.tanggal_transaksi DESC, t.id_transaksi DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapHeader(rs));
        }
        return list;
    }

    public List<DetailTransaksi> findDetailByTransaksi(int idTransaksi) throws SQLException {
        List<DetailTransaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM detail_transaksi WHERE id_transaksi = ? ORDER BY nama_menu ASC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapDetail(rs));
        }
        return list;
    }

    public int totalTerjual() throws SQLException {
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(jumlah),0) FROM detail_transaksi")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public BigDecimal totalPendapatan() throws SQLException {
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(total),0) FROM transaksi")) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    /** Generate kode transaksi: TRX{yyyyMMdd}-{nnn} */
    public String nextKode(java.sql.Date tanggal) throws SQLException {
        String prefix = "TRX" + tanggal.toString().replace("-", "") + "-";
        String sql = "SELECT kode_transaksi FROM transaksi WHERE kode_transaksi LIKE ? ORDER BY kode_transaksi DESC LIMIT 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();
            int seq = 1;
            if (rs.next()) {
                String last = rs.getString(1);
                seq = Integer.parseInt(last.substring(last.lastIndexOf("-") + 1)) + 1;
            }
            return prefix + String.format("%03d", seq);
        }
    }

    /** Simpan transaksi + detail dalam satu transaction */
    public int save(Transaksi t) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            String sqlT = "INSERT INTO transaksi (kode_transaksi, id_user, tanggal_transaksi, total) VALUES (?, ?, ?, ?)";
            int idTransaksi;
            try (PreparedStatement ps = conn.prepareStatement(sqlT, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, t.getKodeTransaksi());
                ps.setInt(2, t.getIdUser());
                ps.setDate(3, t.getTanggalTransaksi());
                ps.setBigDecimal(4, t.getTotal());
                ps.executeUpdate();
                ResultSet gk = ps.getGeneratedKeys();
                gk.next();
                idTransaksi = gk.getInt(1);
            }
            String sqlD = "INSERT INTO detail_transaksi (id_transaksi, id_menu, nama_menu, jumlah, harga_satuan, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlD)) {
                for (DetailTransaksi d : t.getDetails()) {
                    ps.setInt(1, idTransaksi);
                    ps.setInt(2, d.getIdMenu());
                    ps.setString(3, d.getNamaMenu());
                    ps.setInt(4, d.getJumlah());
                    ps.setBigDecimal(5, d.getHargaSatuan());
                    ps.setBigDecimal(6, d.getSubtotal());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            // Kurangi stok
            String sqlStok = "UPDATE menu SET stok = stok - ? WHERE id_menu = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlStok)) {
                for (DetailTransaksi d : t.getDetails()) {
                    ps.setInt(1, d.getJumlah());
                    ps.setInt(2, d.getIdMenu());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
            return idTransaksi;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
