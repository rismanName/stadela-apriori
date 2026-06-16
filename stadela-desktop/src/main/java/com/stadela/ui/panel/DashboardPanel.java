package com.stadela.ui.panel;

import com.stadela.dao.KategoriDAO;
import com.stadela.dao.MenuDAO;
import com.stadela.dao.TokoDAO;
import com.stadela.dao.TransaksiDAO;
import com.stadela.model.Toko;
import com.stadela.ui.MainFrame;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class DashboardPanel extends javax.swing.JPanel implements MainFrame.Refreshable {

    public DashboardPanel() {
        initComponents();
        refresh();
    }

    // ── Business Logic ───────────────────────────────────────────

    @Override
    public void refresh() {
        javax.swing.SwingWorker<Void, Void> w = new javax.swing.SwingWorker<>() {
            int jmlMenu, jmlKategori, totalStok;
            BigDecimal totalPendapatan;
            Toko toko;

            @Override
            protected Void doInBackground() throws Exception {
                MenuDAO    menuDAO  = new MenuDAO();
                KategoriDAO katDAO  = new KategoriDAO();
                TransaksiDAO tDAO   = new TransaksiDAO();
                TokoDAO   tokoDAO   = new TokoDAO();
                jmlMenu           = menuDAO.count();
                jmlKategori       = katDAO.count();
                totalStok         = menuDAO.totalStok();
                totalPendapatan   = tDAO.totalPendapatan();
                toko              = tokoDAO.find();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (toko != null) {
                        lblNamaToko.setText(toko.getNamaToko());
                        lblAlamat.setText(toko.getAlamatToko());
                    }

                    NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
                    NumberFormat nfCur = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    String pendapatan = "Rp " + nfCur.format(totalPendapatan).replace("Rp", "").trim();

                    lblMenuVal.setText(String.valueOf(jmlMenu));
                    lblKatVal.setText(String.valueOf(jmlKategori));
                    lblStokVal.setText(nf.format(totalStok));
                    lblPendapatanVal.setText(pendapatan);
                } catch (Exception e) {
                    javax.swing.JOptionPane.showMessageDialog(DashboardPanel.this,
                        "Gagal memuat dashboard: " + e.getMessage(), "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {
        lblTitle        = new javax.swing.JLabel();
        lblSub          = new javax.swing.JLabel();
        lblNamaToko     = new javax.swing.JLabel();
        lblAlamat       = new javax.swing.JLabel();
        pnlStats        = new javax.swing.JPanel();
        pnlCard1        = new javax.swing.JPanel();
        lblMenuLbl      = new javax.swing.JLabel();
        lblMenuVal      = new javax.swing.JLabel();
        pnlCard2        = new javax.swing.JPanel();
        lblKatLbl       = new javax.swing.JLabel();
        lblKatVal       = new javax.swing.JLabel();
        pnlCard3        = new javax.swing.JPanel();
        lblStokLbl      = new javax.swing.JLabel();
        lblStokVal      = new javax.swing.JLabel();
        pnlCard4        = new javax.swing.JPanel();
        lblPendapatanLbl= new javax.swing.JLabel();
        lblPendapatanVal= new javax.swing.JLabel();
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTitle.setForeground(new java.awt.Color(44, 62, 80));
        lblTitle.setText("Dashboard");

        lblSub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSub.setForeground(new java.awt.Color(128, 128, 128));
        lblSub.setText("Ringkasan data Warkop Stadela");

        lblNamaToko.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        lblNamaToko.setText("...");

        lblAlamat.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        lblAlamat.setForeground(new java.awt.Color(128, 128, 128));
        lblAlamat.setText("...");

        // Card 1 - Menu
        pnlCard1.setBackground(java.awt.Color.WHITE);
        pnlCard1.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)),
            javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        lblMenuVal.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        lblMenuVal.setForeground(new java.awt.Color(44, 62, 80));
        lblMenuVal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMenuVal.setText("0");
        lblMenuLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblMenuLbl.setForeground(new java.awt.Color(128, 128, 128));
        lblMenuLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMenuLbl.setText("Total Menu");
        javax.swing.GroupLayout c1Layout = new javax.swing.GroupLayout(pnlCard1);
        pnlCard1.setLayout(c1Layout);
        c1Layout.setHorizontalGroup(c1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(lblMenuVal, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
            .addComponent(lblMenuLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE));
        c1Layout.setVerticalGroup(c1Layout.createSequentialGroup()
            .addGap(16).addComponent(lblMenuVal).addGap(8).addComponent(lblMenuLbl).addGap(16));

        // Card 2 - Kategori
        pnlCard2.setBackground(java.awt.Color.WHITE);
        pnlCard2.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)),
            javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        lblKatVal.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        lblKatVal.setForeground(new java.awt.Color(52, 152, 219));
        lblKatVal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblKatVal.setText("0");
        lblKatLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblKatLbl.setForeground(new java.awt.Color(128, 128, 128));
        lblKatLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblKatLbl.setText("Total Kategori");
        javax.swing.GroupLayout c2Layout = new javax.swing.GroupLayout(pnlCard2);
        pnlCard2.setLayout(c2Layout);
        c2Layout.setHorizontalGroup(c2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(lblKatVal, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
            .addComponent(lblKatLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE));
        c2Layout.setVerticalGroup(c2Layout.createSequentialGroup()
            .addGap(16).addComponent(lblKatVal).addGap(8).addComponent(lblKatLbl).addGap(16));

        // Card 3 - Stok
        pnlCard3.setBackground(java.awt.Color.WHITE);
        pnlCard3.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)),
            javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        lblStokVal.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        lblStokVal.setForeground(new java.awt.Color(39, 174, 96));
        lblStokVal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStokVal.setText("0");
        lblStokLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblStokLbl.setForeground(new java.awt.Color(128, 128, 128));
        lblStokLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStokLbl.setText("Total Stok");
        javax.swing.GroupLayout c3Layout = new javax.swing.GroupLayout(pnlCard3);
        pnlCard3.setLayout(c3Layout);
        c3Layout.setHorizontalGroup(c3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(lblStokVal, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
            .addComponent(lblStokLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE));
        c3Layout.setVerticalGroup(c3Layout.createSequentialGroup()
            .addGap(16).addComponent(lblStokVal).addGap(8).addComponent(lblStokLbl).addGap(16));

        // Card 4 - Pendapatan
        pnlCard4.setBackground(java.awt.Color.WHITE);
        pnlCard4.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)),
            javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        lblPendapatanVal.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        lblPendapatanVal.setForeground(new java.awt.Color(231, 76, 60));
        lblPendapatanVal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPendapatanVal.setText("Rp 0");
        lblPendapatanLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblPendapatanLbl.setForeground(new java.awt.Color(128, 128, 128));
        lblPendapatanLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPendapatanLbl.setText("Total Pendapatan");
        javax.swing.GroupLayout c4Layout = new javax.swing.GroupLayout(pnlCard4);
        pnlCard4.setLayout(c4Layout);
        c4Layout.setHorizontalGroup(c4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(lblPendapatanVal, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
            .addComponent(lblPendapatanLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE));
        c4Layout.setVerticalGroup(c4Layout.createSequentialGroup()
            .addGap(16).addComponent(lblPendapatanVal).addGap(8).addComponent(lblPendapatanLbl).addGap(16));

        // Stats row
        pnlStats.setOpaque(false);
        pnlStats.setLayout(new java.awt.GridLayout(1, 4, 16, 0));
        pnlStats.add(pnlCard1);
        pnlStats.add(pnlCard2);
        pnlStats.add(pnlCard3);
        pnlStats.add(pnlCard4);

        // Main layout
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitle)
                            .addComponent(lblSub))
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNamaToko)
                            .addComponent(lblAlamat)))
                    .addComponent(pnlStats, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addGap(4)
                        .addComponent(lblSub))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblNamaToko)
                        .addGap(2)
                        .addComponent(lblAlamat)))
                .addGap(20)
                .addComponent(pnlStats, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
            }//GEN-END:initComponents
    // </editor-fold>


    // ── Component fields ──────────────────────────────────────────────
    private javax.swing.JLabel  lblMenuLbl;
    private javax.swing.JLabel  lblMenuVal;
    private javax.swing.JLabel  lblKatLbl;
    private javax.swing.JLabel  lblKatVal;
    private javax.swing.JLabel  lblStokLbl;
    private javax.swing.JLabel  lblStokVal;
    private javax.swing.JLabel  lblPendapatanLbl;
    private javax.swing.JLabel  lblPendapatanVal;
    private javax.swing.JPanel  pnlCard1;
    private javax.swing.JPanel  pnlCard2;
    private javax.swing.JPanel  pnlCard3;
    private javax.swing.JPanel  pnlCard4;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel  lblTitle;
    private javax.swing.JLabel  lblSub;
    private javax.swing.JLabel  lblNamaToko;
    private javax.swing.JLabel  lblAlamat;
    private javax.swing.JPanel  pnlStats;
    // End of variables declaration//GEN-END:variables
}
