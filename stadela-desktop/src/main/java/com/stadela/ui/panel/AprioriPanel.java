package com.stadela.ui.panel;

import com.stadela.dao.AprioriDAO;
import com.stadela.dao.TokoDAO;
import com.stadela.model.AprioriProses;
import com.stadela.model.HasilApriori;
import com.stadela.ui.MainFrame;
import com.stadela.util.AprioriEngine;
import com.stadela.util.ReportUtil;
import com.stadela.util.SessionManager;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AprioriPanel extends javax.swing.JPanel implements MainFrame.Refreshable {

    private List<AprioriProses> riwayat;

    public AprioriPanel() {
        initComponents();
        setupTables();
        refresh();
    }

    // ── Business Logic ───────────────────────────────────────────

    private void setupTables() {
        tblHasil.getColumnModel().getColumn(1).setPreferredWidth(25);
        tblRiwayat.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblRiwayat.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadHasil();
        });
    }

    @Override
    public void refresh() {
        javax.swing.SwingWorker<List<AprioriProses>, Void> w = new javax.swing.SwingWorker<>() {
            @Override protected List<AprioriProses> doInBackground() throws Exception {
                return new AprioriDAO().findAll();
            }
            @Override protected void done() {
                try {
                    riwayat = get();
                    riwayatModel.setRowCount(0);
                    int i = 1;
                    for (AprioriProses p : riwayat) {
                        String periode = (p.getTanggalDari() != null ? p.getTanggalDari() : "-")
                            + " s/d " + (p.getTanggalSampai() != null ? p.getTanggalSampai() : "-");
                        riwayatModel.addRow(new Object[]{
                            i++,
                            String.format("%.0f%%", p.getMinSupport() * 100),
                            String.format("%.0f%%", p.getMinConfidence() * 100),
                            periode, p.getTotalTransaksi(), p.getStatus()
                        });
                    }
                } catch (Exception e) { showMsg(e.getMessage()); }
            }
        };
        w.execute();
    }

    private void loadHasil() {
        int sel = tblRiwayat.getSelectedRow();
        if (sel < 0 || riwayat == null || sel >= riwayat.size()) return;
        int idProses = riwayat.get(sel).getIdProses();
        javax.swing.SwingWorker<List<HasilApriori>, Void> w = new javax.swing.SwingWorker<>() {
            @Override protected List<HasilApriori> doInBackground() throws Exception {
                return new AprioriDAO().findHasilByProses(idProses);
            }
            @Override protected void done() {
                try {
                    List<HasilApriori> hasil = get();
                    hasilModel.setRowCount(0);
                    for (HasilApriori h : hasil) {
                        hasilModel.addRow(new Object[]{
                            h.getAntecedent(), "→", h.getConsequent(),
                            String.format("%.1f%%", h.getSupportPct()),
                            String.format("%.1f%%", h.getConfidencePct()),
                            String.format("%.2f", h.getLift())
                        });
                    }
                    if (hasil.isEmpty())
                        hasilModel.addRow(new Object[]{"Tidak ada rule yang memenuhi threshold","","","","",""});
                } catch (Exception e) { showMsg(e.getMessage()); }
            }
        };
        w.execute();
    }

    private void runApriori() {
        float minSup  = ((Number) spSupport.getValue()).floatValue();
        float minConf = ((Number) spConfidence.getValue()).floatValue();
        String dariStr   = tfDari.getText().trim();
        String sampaiStr = tfSampai.getText().trim();
        Date dari = null, sampai = null;
        try {
            if (!dariStr.isEmpty())   dari   = Date.valueOf(dariStr);
            if (!sampaiStr.isEmpty()) sampai = Date.valueOf(sampaiStr);
        } catch (IllegalArgumentException ex) {
            showMsg("Format tanggal harus yyyy-MM-dd"); return;
        }

        btnProses.setEnabled(false);
        btnProses.setText("Memproses...");
        final Date fDari = dari, fSampai = sampai;

        javax.swing.SwingWorker<Integer, Void> w = new javax.swing.SwingWorker<>() {
            @Override protected Integer doInBackground() throws Exception {
                return AprioriEngine.run(
                    SessionManager.getCurrentUser().getIdUser(),
                    minSup, minConf, fDari, fSampai, new AprioriDAO()
                );
            }
            @Override protected void done() {
                btnProses.setEnabled(true);
                btnProses.setText("▶ Jalankan Apriori");
                try {
                    get();
                    javax.swing.JOptionPane.showMessageDialog(AprioriPanel.this,
                        "Apriori selesai! Lihat hasil di tabel.", "Sukses",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    showMsg("Apriori gagal: " + cause.getMessage());
                    try { refresh(); } catch (Exception ignored) {}
                }
            }
        };
        w.execute();
    }

    private void showMsg(String msg) {
        javax.swing.JOptionPane.showMessageDialog(this, msg);
    }

    private void doExportPdf() {
        int sel = tblRiwayat.getSelectedRow();
        if (sel < 0 || riwayat == null || sel >= riwayat.size()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Pilih salah satu proses apriori dari tabel riwayat terlebih dahulu.", "Info",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        AprioriProses proses = riwayat.get(sel);

        btnExportPdf.setEnabled(false);
        final AprioriProses finalProses = proses;
        new javax.swing.SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                java.util.List<HasilApriori> data = new AprioriDAO().findHasilByProses(finalProses.getIdProses());
                Map<String, Object> params = new HashMap<>();
                try { params.put("TOKO_NAMA", new TokoDAO().find().getNamaToko()); } catch (Exception ignored) {}
                params.put("MIN_SUPPORT",    String.format("%.0f%%", finalProses.getMinSupport() * 100));
                params.put("MIN_CONFIDENCE", String.format("%.0f%%", finalProses.getMinConfidence() * 100));
                String dari   = finalProses.getTanggalDari()   != null ? finalProses.getTanggalDari().toString()   : "-";
                String sampai = finalProses.getTanggalSampai() != null ? finalProses.getTanggalSampai().toString() : "-";
                params.put("PERIODE", dari + " s/d " + sampai);
                params.put("TOTAL_TRANSAKSI", finalProses.getTotalTransaksi());
                ReportUtil.preview("hasil_apriori.jrxml", params, data, "Preview — Hasil Apriori");
                return null;
            }
            @Override protected void done() {
                btnExportPdf.setEnabled(true);
                try { get(); } catch (Exception e) {
                    javax.swing.JOptionPane.showMessageDialog(AprioriPanel.this,
                        "Gagal membuka preview: " + e.getMessage(), "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void btnProsesActionPerformed(java.awt.event.ActionEvent evt) { runApriori(); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {
        riwayatModel = new javax.swing.table.DefaultTableModel(
            new Object[]{"#","Min Sup","Min Conf","Periode","Transaksi","Status"}, 0);
        hasilModel   = new javax.swing.table.DefaultTableModel(
            new Object[]{"Antecedent","→","Consequent","Support (%)","Confidence (%)","Lift"}, 0);

        lblTitle     = new javax.swing.JLabel();
        lblSub       = new javax.swing.JLabel();
        pnlForm      = new javax.swing.JPanel();
        pnlHasil     = new javax.swing.JPanel();

        // Form components
        spSupport    = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(0.3d, 0.01d, 1.0d, 0.01d));
        spConfidence = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(0.5d, 0.01d, 1.0d, 0.01d));
        ((javax.swing.JSpinner.NumberEditor)spSupport.getEditor()).getFormat().setMinimumFractionDigits(2);
        ((javax.swing.JSpinner.NumberEditor)spConfidence.getEditor()).getFormat().setMinimumFractionDigits(2);
        tfDari       = new javax.swing.JTextField("2026-01-01", 14);
        tfSampai     = new javax.swing.JTextField("2026-12-31", 14);
        btnProses    = new javax.swing.JButton();

        btnExportPdf = new javax.swing.JButton();
        // Riwayat
        scpRiwayat   = new javax.swing.JScrollPane();
        tblRiwayat   = new javax.swing.JTable();
        scpHasil     = new javax.swing.JScrollPane();
        tblHasil     = new javax.swing.JTable();
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTitle.setForeground(new java.awt.Color(44, 62, 80));
        lblTitle.setText("Algoritma Apriori");

        lblSub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSub.setForeground(new java.awt.Color(128, 128, 128));
        lblSub.setText("Analisis asosiasi produk dari data transaksi");

        // ── Form panel ───────────────────────────────────────────
        pnlForm.setBackground(java.awt.Color.WHITE);
        pnlForm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)));

        // declare labels as local vars so same instance used in H+V groups
        final javax.swing.JLabel lblSupport    = mkLabel("Min Support (0-1):");
        final javax.swing.JLabel lblConfidence = mkLabel("Min Confidence (0-1):");
        final javax.swing.JLabel lblDari       = mkLabel("Tanggal Dari (yyyy-MM-dd):");
        final javax.swing.JLabel lblSampai2    = mkLabel("Tanggal Sampai (yyyy-MM-dd):");
        final javax.swing.JLabel lblRiwayat    = mkBoldLabel("Riwayat Proses:");

        spSupport.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        spConfidence.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        tfDari.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        tfSampai.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));

        btnProses.setBackground(new java.awt.Color(231, 76, 60));
        btnProses.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnProses.setForeground(java.awt.Color.WHITE);
        btnProses.setText("▶ Jalankan Apriori");
        btnProses.setFocusPainted(false);
        btnProses.addActionListener(evt -> btnProsesActionPerformed(evt));

        btnExportPdf.setBackground(new java.awt.Color(192, 57, 43));
        btnExportPdf.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnExportPdf.setForeground(java.awt.Color.WHITE);
        btnExportPdf.setText("Export PDF");
        btnExportPdf.setFocusPainted(false);
        btnExportPdf.addActionListener(evt -> doExportPdf());

        tblRiwayat.setModel(riwayatModel);
        tblRiwayat.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        tblRiwayat.setRowHeight(30);
        tblRiwayat.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        scpRiwayat.setViewportView(tblRiwayat);

        javax.swing.GroupLayout formLayout = new javax.swing.GroupLayout(pnlForm);
        pnlForm.setLayout(formLayout);
        formLayout.setAutoCreateContainerGaps(true);
        formLayout.setHorizontalGroup(
            formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblSupport)
            .addComponent(spSupport, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
            .addComponent(lblConfidence)
            .addComponent(spConfidence, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
            .addComponent(lblDari)
            .addComponent(tfDari, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
            .addComponent(lblSampai2)
            .addComponent(tfSampai, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
            .addComponent(btnProses, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
            .addComponent(btnExportPdf, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
            .addComponent(lblRiwayat)
            .addComponent(scpRiwayat, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );
        formLayout.setVerticalGroup(
            formLayout.createSequentialGroup()
                .addComponent(lblSupport)
                .addGap(4)
                .addComponent(spSupport, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8)
                .addComponent(lblConfidence)
                .addGap(4)
                .addComponent(spConfidence, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8)
                .addComponent(lblDari)
                .addGap(4)
                .addComponent(tfDari, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8)
                .addComponent(lblSampai2)
                .addGap(4)
                .addComponent(tfSampai, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12)
                .addComponent(btnProses, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6)
                .addComponent(btnExportPdf, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12)
                .addComponent(lblRiwayat)
                .addGap(6)
                .addComponent(scpRiwayat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        // ── Hasil panel ──────────────────────────────────────────
        pnlHasil.setBackground(new java.awt.Color(248, 249, 250));
        pnlHasil.setBorder(javax.swing.BorderFactory.createTitledBorder("Association Rules"));

        tblHasil.setModel(hasilModel);
        tblHasil.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        tblHasil.setRowHeight(30);
        tblHasil.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        scpHasil.setViewportView(tblHasil);

        javax.swing.GroupLayout hasilLayout = new javax.swing.GroupLayout(pnlHasil);
        pnlHasil.setLayout(hasilLayout);
        hasilLayout.setHorizontalGroup(hasilLayout.createSequentialGroup()
            .addGap(8)
            .addComponent(scpHasil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGap(8));
        hasilLayout.setVerticalGroup(hasilLayout.createSequentialGroup()
            .addGap(8)
            .addComponent(scpHasil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGap(8));

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
                        .addComponent(pnlForm, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16)
                        .addComponent(pnlHasil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(20))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addComponent(lblTitle)
                .addGap(4)
                .addComponent(lblSub)
                .addGap(16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlHasil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20)
        );
            }//GEN-END:initComponents
    // </editor-fold>


    private javax.swing.JLabel mkLabel(String text) {
        javax.swing.JLabel l = new javax.swing.JLabel(text);
        l.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        return l;
    }
    private javax.swing.JLabel mkBoldLabel(String text) {
        javax.swing.JLabel l = new javax.swing.JLabel(text);
        l.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        return l;
    }

    private javax.swing.JButton btnExportPdf;
    // ── Component fields ──────────────────────────────────────────────
    private javax.swing.table.DefaultTableModel  riwayatModel;
    private javax.swing.table.DefaultTableModel  hasilModel;
    private javax.swing.JSpinner  spSupport;
    private javax.swing.JSpinner  spConfidence;
    private javax.swing.JTextField  tfDari;
    private javax.swing.JTextField  tfSampai;
    private javax.swing.JButton  btnProses;
    private javax.swing.JScrollPane  scpRiwayat;
    private javax.swing.JTable  tblRiwayat;
    private javax.swing.JScrollPane  scpHasil;
    private javax.swing.JTable  tblHasil;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel  lblTitle;
    private javax.swing.JLabel  lblSub;
    private javax.swing.JPanel  pnlForm;
    private javax.swing.JPanel  pnlHasil;
    // End of variables declaration//GEN-END:variables
}
