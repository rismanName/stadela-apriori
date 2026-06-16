package com.stadela.ui;

import com.stadela.dao.UserDAO;
import com.stadela.model.User;
import com.stadela.util.SessionManager;

public class LoginFrame extends javax.swing.JFrame {

    public LoginFrame() {
        initComponents();
        setLocationRelativeTo(null);
        setSize(460, 400);
    }

    // ── Business Logic ───────────────────────────────────────────

    private void doLogin() {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Username dan password tidak boleh kosong.");
            return;
        }

        btnLogin.setEnabled(false);
        lblStatus.setText("Memeriksa...");

        new Thread(() -> {
            try {
                User user = new UserDAO().login(username, password);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    btnLogin.setEnabled(true);
                    if (user != null) {
                        SessionManager.login(user);
                        dispose();
                        new MainFrame().setVisible(true);
                    } else {
                        lblStatus.setText("Username atau password salah.");
                        pfPassword.setText("");
                    }
                });
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    btnLogin.setEnabled(true);
                    lblStatus.setText("Koneksi gagal: " + ex.getMessage());
                });
            }
        }).start();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {
        pnlMain = new javax.swing.JPanel();
        pnlCard = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSub = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        tfUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        pfPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login — Stadela Coffee");
        setResizable(false);

        pnlMain.setBackground(new java.awt.Color(44, 62, 80));

        pnlCard.setBackground(new java.awt.Color(255, 255, 255));
        pnlCard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));

        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        lblTitle.setForeground(new java.awt.Color(44, 62, 80));
        lblTitle.setText("Stadela Coffee");

        lblSub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSub.setForeground(new java.awt.Color(128, 128, 128));
        lblSub.setText("Sistem Informasi Penjualan");

        lblUsername.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblUsername.setText("Username");

        tfUsername.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        tfUsername.addActionListener(evt -> tfUsernameActionPerformed(evt));

        lblPassword.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblPassword.setText("Password");

        pfPassword.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        pfPassword.addActionListener(evt -> pfPasswordActionPerformed(evt));

        btnLogin.setBackground(new java.awt.Color(44, 62, 80));
        btnLogin.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("LOG IN");
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(evt -> btnLoginActionPerformed(evt));

        lblStatus.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        lblStatus.setForeground(new java.awt.Color(204, 0, 0));
        lblStatus.setText(" ");

        javax.swing.GroupLayout pnlCardLayout = new javax.swing.GroupLayout(pnlCard);
        pnlCard.setLayout(pnlCardLayout);
        pnlCardLayout.setHorizontalGroup(
            pnlCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardLayout.createSequentialGroup()
                .addGap(40)
                .addGroup(pnlCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(lblSub)
                    .addComponent(lblUsername)
                    .addComponent(tfUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword)
                    .addComponent(pfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40))
        );
        pnlCardLayout.setVerticalGroup(
            pnlCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardLayout.createSequentialGroup()
                .addGap(28)
                .addComponent(lblTitle)
                .addGap(4)
                .addComponent(lblSub)
                .addGap(20)
                .addComponent(lblUsername)
                .addGap(5)
                .addComponent(tfUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12)
                .addComponent(lblPassword)
                .addGap(5)
                .addComponent(pfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8)
                .addComponent(lblStatus)
                .addGap(28))
        );

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlCard, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);
        pack();
    }//GEN-END:initComponents
    // </editor-fold>

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        doLogin();
    }

    private void pfPasswordActionPerformed(java.awt.event.ActionEvent evt) {
        doLogin();
    }

    private void tfUsernameActionPerformed(java.awt.event.ActionEvent evt) {
        pfPassword.requestFocus();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel  lblPassword;
    private javax.swing.JLabel  lblStatus;
    private javax.swing.JLabel  lblSub;
    private javax.swing.JLabel  lblTitle;
    private javax.swing.JLabel  lblUsername;
    private javax.swing.JPasswordField pfPassword;
    private javax.swing.JPanel  pnlCard;
    private javax.swing.JPanel  pnlMain;
    private javax.swing.JTextField tfUsername;
    // End of variables declaration//GEN-END:variables
}
