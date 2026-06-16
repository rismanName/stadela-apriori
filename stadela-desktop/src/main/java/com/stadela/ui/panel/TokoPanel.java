package com.stadela.ui.panel;

import com.stadela.dao.TokoDAO;
import com.stadela.model.Toko;
import com.stadela.ui.MainFrame;

public class TokoPanel extends javax.swing.JPanel implements MainFrame.Refreshable {

    private Toko current;

    public TokoPanel() {
        initComponents();
        refresh();
    }

    // ── Business Logic ───────────────────────────────────────────

    @Override
    public void refresh() {
        javax.swing.SwingWorker<Toko, Void> w = new javax.swing.SwingWorker<>() {
            @Override protected Toko doInBackground() throws Exception { return new TokoDAO().find(); }
            @Override protected void done() {
                try {
                    current = get();
                    if (current != null) {
                        tfNama.setText(current.getNamaToko());
                        taAlamat.setText(current.getAlamatToko());
                        tfTlp.setText(current.getTlp());
                        tfPemilik.setText(current.getNamaPemilik());
                    }
                } catch (Exception e) { showMsg(e.getMessage()); }
            }
        };
        w.execute();
    }

    private void doSave() {
        if (current == null) return;
        current.setNamaToko(tfNama.getText().trim());
        current.setAlamatToko(taAlamat.getText().trim());
        current.setTlp(tfTlp.getText().trim());
        current.setNamaPemilik(tfPemilik.getText().trim());
        try {
            new TokoDAO().update(current);
            javax.swing.JOptionPane.showMessageDialog(this, "Data toko berhasil disimpan.", "Sukses",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) { showMsg(e.getMessage()); }
    }

    private void showMsg(String msg) {
        javax.swing.JOptionPane.showMessageDialog(this, msg, "Error",
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) { doSave(); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {
        lblTitle   = new javax.swing.JLabel();
        lblSub     = new javax.swing.JLabel();
        pnlForm    = new javax.swing.JPanel();
        lblNamaLbl = new javax.swing.JLabel();
        tfNama     = new javax.swing.JTextField();
        lblAlamatLbl = new javax.swing.JLabel();
        taAlamat   = new javax.swing.JTextArea(3, 24);
        scpAlamat  = new javax.swing.JScrollPane(taAlamat);
        lblTlpLbl  = new javax.swing.JLabel();
        tfTlp      = new javax.swing.JTextField();
        lblPemilikLbl = new javax.swing.JLabel();
        tfPemilik  = new javax.swing.JTextField();
        btnSimpan  = new javax.swing.JButton();
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTitle.setForeground(new java.awt.Color(44, 62, 80));
        lblTitle.setText("Data Toko");

        lblSub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSub.setForeground(new java.awt.Color(128, 128, 128));
        lblSub.setText("Informasi Warkop Stadela");

        // Form
        pnlForm.setBackground(java.awt.Color.WHITE);
        pnlForm.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)),
            javax.swing.BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        lblNamaLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblNamaLbl.setText("Nama Toko:");
        tfNama.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));

        lblAlamatLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblAlamatLbl.setText("Alamat:");
        taAlamat.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        taAlamat.setLineWrap(true);

        lblTlpLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblTlpLbl.setText("Telepon:");
        tfTlp.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));

        lblPemilikLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblPemilikLbl.setText("Nama Pemilik:");
        tfPemilik.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));

        btnSimpan.setBackground(new java.awt.Color(39, 174, 96));
        btnSimpan.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnSimpan.setForeground(java.awt.Color.WHITE);
        btnSimpan.setText("Simpan Perubahan");
        btnSimpan.setFocusPainted(false);
        btnSimpan.addActionListener(evt -> btnSimpanActionPerformed(evt));

        javax.swing.GroupLayout formLayout = new javax.swing.GroupLayout(pnlForm);
        pnlForm.setLayout(formLayout);
        formLayout.setAutoCreateGaps(true);
        formLayout.setAutoCreateContainerGaps(true);
        formLayout.setHorizontalGroup(
            formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblNamaLbl)
            .addComponent(tfNama, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblAlamatLbl)
            .addComponent(scpAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblTlpLbl)
            .addComponent(tfTlp, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblPemilikLbl)
            .addComponent(tfPemilik, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        formLayout.setVerticalGroup(
            formLayout.createSequentialGroup()
                .addComponent(lblNamaLbl)
                .addComponent(tfNama, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblAlamatLbl)
                .addComponent(scpAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblTlpLbl)
                .addComponent(tfTlp, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblPemilikLbl)
                .addComponent(tfPemilik, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8)
                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(lblSub)
                    .addComponent(pnlForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addComponent(lblTitle)
                .addGap(4)
                .addComponent(lblSub)
                .addGap(20)
                .addComponent(pnlForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
            }//GEN-END:initComponents
    // </editor-fold>


    // ── Component fields ──────────────────────────────────────────────
    private javax.swing.JLabel  lblNamaLbl;
    private javax.swing.JTextField  tfNama;
    private javax.swing.JLabel  lblAlamatLbl;
    private javax.swing.JTextArea  taAlamat;
    private javax.swing.JScrollPane  scpAlamat;
    private javax.swing.JLabel  lblTlpLbl;
    private javax.swing.JTextField  tfTlp;
    private javax.swing.JLabel  lblPemilikLbl;
    private javax.swing.JTextField  tfPemilik;
    private javax.swing.JButton  btnSimpan;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel  lblTitle;
    private javax.swing.JLabel  lblSub;
    private javax.swing.JPanel  pnlForm;
    // End of variables declaration//GEN-END:variables
}
