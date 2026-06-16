package com.stadela.ui.panel;

import com.stadela.dao.UserDAO;
import com.stadela.model.User;
import com.stadela.ui.MainFrame;
import com.stadela.util.SessionManager;

public class ProfilPanel extends javax.swing.JPanel implements MainFrame.Refreshable {

    public ProfilPanel() {
        initComponents();
        refresh();
    }

    // ── Business Logic ───────────────────────────────────────────

    @Override
    public void refresh() {
        User u = SessionManager.getCurrentUser();
        tfNama.setText(u.getNama());
        tfEmail.setText(u.getEmail() != null ? u.getEmail() : "");
        tfTlp.setText(u.getTelepon() != null ? u.getTelepon() : "");
        tfNIK.setText(u.getNik() != null ? u.getNik() : "");
        taAlamat.setText(u.getAlamat() != null ? u.getAlamat() : "");
    }

    private void saveInfo() {
        User u = SessionManager.getCurrentUser();
        u.setNama(tfNama.getText().trim());
        u.setEmail(tfEmail.getText().trim());
        u.setTelepon(tfTlp.getText().trim());
        u.setNik(tfNIK.getText().trim());
        u.setAlamat(taAlamat.getText().trim());
        try {
            new UserDAO().update(u, false);
            javax.swing.JOptionPane.showMessageDialog(this, "Profil berhasil diperbarui.", "Sukses",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) { showMsg(e.getMessage()); }
    }

    private void changePassword() {
        String oldPw  = new String(pfOld.getPassword()).trim();
        String newPw  = new String(pfNew.getPassword()).trim();
        String confPw = new String(pfConf.getPassword()).trim();
        if (oldPw.isEmpty() || newPw.isEmpty()) { showMsg("Semua kolom password wajib diisi."); return; }
        if (!newPw.equals(confPw)) { showMsg("Konfirmasi password tidak cocok."); return; }
        try {
            User check = new UserDAO().login(SessionManager.getCurrentUser().getUsername(), oldPw);
            if (check == null) { showMsg("Password lama salah."); return; }
            User u = SessionManager.getCurrentUser();
            u.setPassword(newPw);
            new UserDAO().update(u, true);
            pfOld.setText(""); pfNew.setText(""); pfConf.setText("");
            javax.swing.JOptionPane.showMessageDialog(this, "Password berhasil diubah.", "Sukses",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) { showMsg(e.getMessage()); }
    }

    private void showMsg(String msg) {
        javax.swing.JOptionPane.showMessageDialog(this, msg, "Error",
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    private void btnSimpanInfoActionPerformed(java.awt.event.ActionEvent evt) { saveInfo(); }
    private void btnUbahPassActionPerformed(java.awt.event.ActionEvent evt) { changePassword(); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {
        lblTitle      = new javax.swing.JLabel();
        lblSub        = new javax.swing.JLabel();
        pnlInfo       = new javax.swing.JPanel();
        pnlPass       = new javax.swing.JPanel();
        tfNama        = new javax.swing.JTextField();
        tfEmail       = new javax.swing.JTextField();
        tfTlp         = new javax.swing.JTextField();
        tfNIK         = new javax.swing.JTextField();
        taAlamat      = new javax.swing.JTextArea(3, 20);
        pfOld         = new javax.swing.JPasswordField();
        pfNew         = new javax.swing.JPasswordField();
        pfConf        = new javax.swing.JPasswordField();
        btnSimpanInfo = new javax.swing.JButton();
        btnUbahPass   = new javax.swing.JButton();
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTitle.setForeground(new java.awt.Color(44, 62, 80));
        lblTitle.setText("Profil Saya");

        lblSub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSub.setForeground(new java.awt.Color(128, 128, 128));
        lblSub.setText("Edit informasi akun Anda");

        java.awt.Font font13 = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13);
        java.awt.Font font12 = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12);

        // ── Info pribadi ─────────────────────────────────────────
        pnlInfo.setBackground(java.awt.Color.WHITE);
        pnlInfo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)),
            javax.swing.BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        tfNama.setFont(font13);
        tfEmail.setFont(font13);
        tfTlp.setFont(font13);
        tfNIK.setFont(font13);
        taAlamat.setFont(font13);
        taAlamat.setLineWrap(true);

        btnSimpanInfo.setBackground(new java.awt.Color(39, 174, 96));
        btnSimpanInfo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnSimpanInfo.setForeground(java.awt.Color.WHITE);
        btnSimpanInfo.setText("Simpan Info");
        btnSimpanInfo.setFocusPainted(false);
        btnSimpanInfo.addActionListener(evt -> btnSimpanInfoActionPerformed(evt));

        final javax.swing.JLabel iNama   = mkLbl("Nama:", font12);
        final javax.swing.JLabel iEmail  = mkLbl("Email:", font12);
        final javax.swing.JLabel iTlp    = mkLbl("Telepon:", font12);
        final javax.swing.JLabel iNIK    = mkLbl("NIK:", font12);
        final javax.swing.JLabel iAlamat = mkLbl("Alamat:", font12);
        final javax.swing.JScrollPane scpAlamatInfo = new javax.swing.JScrollPane(taAlamat);

        javax.swing.GroupLayout infoLayout = new javax.swing.GroupLayout(pnlInfo);
        pnlInfo.setLayout(infoLayout);
        infoLayout.setAutoCreateContainerGaps(true);
        infoLayout.setHorizontalGroup(
            infoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iNama)
            .addComponent(tfNama, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(iEmail)
            .addComponent(tfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(iTlp)
            .addComponent(tfTlp, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(iNIK)
            .addComponent(tfNIK, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(iAlamat)
            .addComponent(scpAlamatInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnSimpanInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        infoLayout.setVerticalGroup(
            infoLayout.createSequentialGroup()
                .addComponent(iNama).addGap(4)
                .addComponent(tfNama, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                .addComponent(iEmail).addGap(4)
                .addComponent(tfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                .addComponent(iTlp).addGap(4)
                .addComponent(tfTlp, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                .addComponent(iNIK).addGap(4)
                .addComponent(tfNIK, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                .addComponent(iAlamat).addGap(4)
                .addComponent(scpAlamatInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(16)
                .addComponent(btnSimpanInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        // ── Ubah password ────────────────────────────────────────
        pnlPass.setBackground(java.awt.Color.WHITE);
        pnlPass.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)),
            javax.swing.BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        pfOld.setFont(font13);
        pfNew.setFont(font13);
        pfConf.setFont(font13);

        btnUbahPass.setBackground(new java.awt.Color(243, 156, 18));
        btnUbahPass.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnUbahPass.setForeground(java.awt.Color.WHITE);
        btnUbahPass.setText("Ubah Password");
        btnUbahPass.setFocusPainted(false);
        btnUbahPass.addActionListener(evt -> btnUbahPassActionPerformed(evt));

        final javax.swing.JLabel pOld  = mkLbl("Password Lama:", font12);
        final javax.swing.JLabel pNew  = mkLbl("Password Baru:", font12);
        final javax.swing.JLabel pConf = mkLbl("Konfirmasi Baru:", font12);

        javax.swing.GroupLayout passLayout = new javax.swing.GroupLayout(pnlPass);
        pnlPass.setLayout(passLayout);
        passLayout.setAutoCreateContainerGaps(true);
        passLayout.setHorizontalGroup(
            passLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pOld)
            .addComponent(pfOld, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(pNew)
            .addComponent(pfNew, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(pConf)
            .addComponent(pfConf, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnUbahPass, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        passLayout.setVerticalGroup(
            passLayout.createSequentialGroup()
                .addComponent(pOld).addGap(4)
                .addComponent(pfOld, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                .addComponent(pNew).addGap(4)
                .addComponent(pfNew, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                .addComponent(pConf).addGap(4)
                .addComponent(pfConf, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(16)
                .addComponent(btnUbahPass, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        // Main layout
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(lblSub)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20)
                        .addComponent(pnlPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addComponent(lblTitle)
                .addGap(4)
                .addComponent(lblSub)
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
            }//GEN-END:initComponents
    // </editor-fold>


    private javax.swing.JLabel mkLbl(String text, java.awt.Font font) {
        javax.swing.JLabel l = new javax.swing.JLabel(text);
        l.setFont(font);
        return l;
    }

    // ── Component fields ──────────────────────────────────────────────
    private javax.swing.JTextField  tfNama;
    private javax.swing.JTextField  tfEmail;
    private javax.swing.JTextField  tfTlp;
    private javax.swing.JTextField  tfNIK;
    private javax.swing.JTextArea  taAlamat;
    private javax.swing.JPasswordField  pfOld;
    private javax.swing.JPasswordField  pfNew;
    private javax.swing.JPasswordField  pfConf;
    private javax.swing.JButton  btnSimpanInfo;
    private javax.swing.JButton  btnUbahPass;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel  lblTitle;
    private javax.swing.JLabel  lblSub;
    private javax.swing.JPanel  pnlInfo;
    private javax.swing.JPanel  pnlPass;
    // End of variables declaration//GEN-END:variables
}
