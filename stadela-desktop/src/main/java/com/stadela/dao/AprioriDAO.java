package com.stadela.dao;

import com.stadela.db.DBConnection;
import com.stadela.model.AprioriProses;
import com.stadela.model.HasilApriori;

import java.sql.*;
import java.util.*;
import java.sql.Date;

public class AprioriDAO {

    public List<AprioriProses> findAll() throws SQLException {
        List<AprioriProses> list = new ArrayList<>();
        String sql = "SELECT ap.*, u.nama FROM apriori_proses ap " +
                     "LEFT JOIN users u ON u.id_user = ap.id_user ORDER BY ap.id_proses DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                AprioriProses p = new AprioriProses();
                p.setIdProses(rs.getInt("id_proses"));
                p.setIdUser(rs.getInt("id_user"));
                p.setMinSupport(rs.getFloat("min_support"));
                p.setMinConfidence(rs.getFloat("min_confidence"));
                p.setTanggalDari(rs.getDate("tanggal_dari"));
                p.setTanggalSampai(rs.getDate("tanggal_sampai"));
                p.setTotalTransaksi(rs.getInt("total_transaksi"));
                p.setStatus(rs.getString("status"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                p.setNamaUser(rs.getString("nama"));
                list.add(p);
            }
        }
        return list;
    }

    public List<HasilApriori> findHasilByProses(int idProses) throws SQLException {
        List<HasilApriori> list = new ArrayList<>();
        String sql = "SELECT * FROM hasil_apriori WHERE id_proses = ? ORDER BY confidence DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idProses);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HasilApriori h = new HasilApriori();
                h.setIdHasil(rs.getInt("id_hasil"));
                h.setIdProses(rs.getInt("id_proses"));
                h.setAntecedent(rs.getString("antecedent"));
                h.setConsequent(rs.getString("consequent"));
                h.setSupport(rs.getFloat("support"));
                h.setSupportPct(rs.getFloat("support_pct"));
                h.setConfidence(rs.getFloat("confidence"));
                h.setConfidencePct(rs.getFloat("confidence_pct"));
                h.setLift(rs.getFloat("lift"));
                list.add(h);
            }
        }
        return list;
    }

    /** Ambil data transaksi untuk proses Apriori */
    public Map<Integer, List<String>> getTransaksiItems(Date dari, Date sampai) throws SQLException {
        Map<Integer, List<String>> map = new LinkedHashMap<>();
        String sql = "SELECT dt.id_transaksi, dt.nama_menu " +
                     "FROM detail_transaksi dt JOIN transaksi t ON t.id_transaksi = dt.id_transaksi WHERE 1=1";
        List<Object> params = new ArrayList<>();
        if (dari != null)   { sql += " AND t.tanggal_transaksi >= ?"; params.add(dari); }
        if (sampai != null) { sql += " AND t.tanggal_transaksi <= ?"; params.add(sampai); }
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_transaksi");
                map.computeIfAbsent(id, k -> new ArrayList<>()).add(rs.getString("nama_menu"));
            }
        }
        return map;
    }

    /** Catat proses baru, return id_proses */
    public int insertProses(int idUser, float minSupport, float minConfidence, Date dari, Date sampai) throws SQLException {
        String sql = "INSERT INTO apriori_proses (id_user, min_support, min_confidence, tanggal_dari, tanggal_sampai, status) " +
                     "VALUES (?, ?, ?, ?, ?, 'proses')";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idUser);
            ps.setFloat(2, minSupport);
            ps.setFloat(3, minConfidence);
            ps.setDate(4, dari);
            ps.setDate(5, sampai);
            ps.executeUpdate();
            ResultSet gk = ps.getGeneratedKeys();
            gk.next();
            return gk.getInt(1);
        }
    }

    public void updateStatusProses(int idProses, String status, int totalTransaksi) throws SQLException {
        String sql = "UPDATE apriori_proses SET status=?, total_transaksi=? WHERE id_proses=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, totalTransaksi);
            ps.setInt(3, idProses);
            ps.executeUpdate();
        }
    }

    public void insertItemset(int idProses, String itemset, int ukuran, int jumlah, float support) throws SQLException {
        String sql = "INSERT INTO apriori_itemset (id_proses, itemset, ukuran, jumlah, support, support_pct) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idProses);
            ps.setString(2, itemset);
            ps.setInt(3, ukuran);
            ps.setInt(4, jumlah);
            ps.setFloat(5, support);
            ps.setFloat(6, support * 100);
            ps.executeUpdate();
        }
    }

    public void insertHasil(int idProses, String ant, String con,
                             float support, float confidence, float lift) throws SQLException {
        String sql = "INSERT INTO hasil_apriori (id_proses, antecedent, consequent, support, support_pct, confidence, confidence_pct, lift) " +
                     "VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idProses);
            ps.setString(2, ant);
            ps.setString(3, con);
            ps.setFloat(4, support);
            ps.setFloat(5, support * 100);
            ps.setFloat(6, confidence);
            ps.setFloat(7, confidence * 100);
            ps.setFloat(8, lift);
            ps.executeUpdate();
        }
    }

    public void deleteProses(int idProses) throws SQLException {
        String sql = "DELETE FROM apriori_proses WHERE id_proses=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idProses);
            ps.executeUpdate();
        }
    }
}
