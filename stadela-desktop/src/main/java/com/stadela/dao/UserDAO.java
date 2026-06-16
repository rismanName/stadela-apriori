package com.stadela.dao;

import com.stadela.db.DBConnection;
import com.stadela.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setIdUser(rs.getInt("id_user"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setNama(rs.getString("nama"));
        u.setAlamat(rs.getString("alamat"));
        u.setTelepon(rs.getString("telepon"));
        u.setEmail(rs.getString("email"));
        u.setFoto(rs.getString("foto"));
        u.setNik(rs.getString("NIK"));
        return u;
    }

    /** Login: cek username + password (bcrypt) */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? LIMIT 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = map(rs);
                // Cek bcrypt
                // PHP password_hash() pakai prefix $2y$, jBCrypt hanya kenal $2a$
                String hash = u.getPassword().replace("$2y$", "$2a$");
                if (BCrypt.checkpw(password, hash)) {
                    return u;
                }
            }
        }
        return null;
    }

    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY nama ASC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id_user = ? LIMIT 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public boolean usernameExists(String username, int excludeId) throws SQLException {
        String sql = "SELECT id_user FROM users WHERE username = ? AND id_user != ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, excludeId);
            return ps.executeQuery().next();
        }
    }

    public void insert(User u) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, nama, alamat, telepon, email, foto, NIK) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, BCrypt.hashpw(u.getPassword(), BCrypt.gensalt()));
            ps.setString(3, u.getRole());
            ps.setString(4, u.getNama());
            ps.setString(5, u.getAlamat());
            ps.setString(6, u.getTelepon());
            ps.setString(7, u.getEmail());
            ps.setString(8, u.getFoto());
            ps.setString(9, u.getNik());
            ps.executeUpdate();
        }
    }

    public void update(User u, boolean changePassword) throws SQLException {
        String sql;
        if (changePassword) {
            sql = "UPDATE users SET username=?, password=?, role=?, nama=?, alamat=?, telepon=?, email=?, foto=?, NIK=? WHERE id_user=?";
        } else {
            sql = "UPDATE users SET username=?, role=?, nama=?, alamat=?, telepon=?, email=?, foto=?, NIK=? WHERE id_user=?";
        }
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, u.getUsername());
            if (changePassword) ps.setString(i++, BCrypt.hashpw(u.getPassword(), BCrypt.gensalt()));
            ps.setString(i++, u.getRole());
            ps.setString(i++, u.getNama());
            ps.setString(i++, u.getAlamat());
            ps.setString(i++, u.getTelepon());
            ps.setString(i++, u.getEmail());
            ps.setString(i++, u.getFoto());
            ps.setString(i++, u.getNik());
            ps.setInt(i, u.getIdUser());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id_user = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
