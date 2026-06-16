package com.stadela.ui.panel;

import com.stadela.dao.TokoDAO;
import com.stadela.dao.TransaksiDAO;
import com.stadela.model.Transaksi;
import com.stadela.ui.MainFrame;
import com.stadela.util.ReportUtil;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LaporanPanel extends javax.swing.JPanel implements MainFrame.Refreshable {

    public LaporanPanel() {
        initComponents();
        refresh();
    }

    // ── Business Logic ───────────────────────────────────────────

    @Override
    public void refresh() {
        javax.swing.SwingWorker<List<Transaksi>, Void> w = new javax.swing.SwingWorker<>() {
            @Override protected List<Transaksi> doInBackground() throws Exception {
                return new TransaksiDAO().findAll();
            }
            @Override protected void done() {
                try {
                    List<Transaksi> data = get();
                    tableModel.setRowCount(0);
                    NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    BigDecimal total = BigDecimal.ZERO;
                    int i = 1;
                    for (Transaksi t : data) {
                        tableModel.addRow(new Object[]{
                            i++, t.getKodeTransaksi(), t.getNamaUser(),
                            t.getTanggalTransaksi(), nf.format(t.getTotal())
                        });
                        total = total.add(t.getTotal());
                    }
                    lblTotal.setText("Total Pendapatan: " + nf.format(total));
                } catch (Exception e) {
                    javax.swing.JOptionPane.showMessageDialog(LaporanPanel.this, e.getMessage(), "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    private void doExportPdf() {
        btnExportPdf.setEnabled(false);
        new javax.swing.SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                java.util.List<Transaksi> data = new TransaksiDAO().findAll();
                Map<String, Object> params = new HashMap<>();
                try { params.put("TOKO_NAMA", new TokoDAO().find().getNamaToko()); } catch (Exception ignored) {}
                ReportUtil.preview("laporan_transaksi.jrxml", params, data, "Preview — Laporan Penjualan");
                return null;
            }
            @Override protected void done() {
                btnExportPdf.setEnabled(true);
                try { get(); } catch (Exception e) {
                    javax.swing.JOptionPane.showMessageDialog(LaporanPanel.this,
                        "Gagal membuka preview: " + e.getMessage(), "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) { refresh(); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {
        tableModel  = new javax.swing.table.DefaultTableModel(
            new Object[]{"#","Kode Transaksi","Kasir","Tanggal","Total"}, 0);
        lblTitle    = new javax.swing.JLabel();
        lblSub      = new javax.swing.JLabel();
        btnRefresh  = new javax.swing.JButton();
        btnExportPdf= new javax.swing.JButton();
        scpTable    = new javax.swing.JScrollPane();
        tblLaporan  = new javax.swing.JTable();
        lblTotal    = new javax.swing.JLabel();
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTitle.setForeground(new java.awt.Color(44, 62, 80));
        lblTitle.setText("Laporan Penjualan");

        lblSub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSub.setForeground(new java.awt.Color(128, 128, 128));
        lblSub.setText("Rekapitulasi seluruh transaksi");

        btnRefresh.setBackground(new java.awt.Color(52, 152, 219));
        btnRefresh.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnRefresh.setForeground(java.awt.Color.WHITE);
        btnRefresh.setText("Refresh");
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(evt -> btnRefreshActionPerformed(evt));

        btnExportPdf.setBackground(new java.awt.Color(192, 57, 43));
        btnExportPdf.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnExportPdf.setForeground(java.awt.Color.WHITE);
        btnExportPdf.setText("Export PDF");
        btnExportPdf.setFocusPainted(false);
        btnExportPdf.addActionListener(evt -> doExportPdf());

        tblLaporan.setModel(tableModel);
        tblLaporan.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        tblLaporan.setRowHeight(36);
        tblLaporan.setGridColor(new java.awt.Color(230, 230, 230));
        tblLaporan.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        scpTable.setViewportView(tblLaporan);

        lblTotal.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));
        lblTotal.setForeground(new java.awt.Color(39, 174, 96));
        lblTotal.setText("Total Pendapatan: Rp 0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnExportPdf, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblSub)
                    .addComponent(scpTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTotal))
                .addGap(20))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitle)
                    .addComponent(btnExportPdf, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4)
                .addComponent(lblSub)
                .addGap(16)
                .addComponent(scpTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(8)
                .addComponent(lblTotal)
                .addGap(20)
        );
            }//GEN-END:initComponents
    // </editor-fold>


    // ── Component fields ──────────────────────────────────────────────
    private javax.swing.table.DefaultTableModel  tableModel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel  lblTitle;
    private javax.swing.JLabel  lblSub;
    private javax.swing.JButton  btnRefresh;
    private javax.swing.JScrollPane  scpTable;
    private javax.swing.JTable  tblLaporan;
    private javax.swing.JLabel  lblTotal;
    private javax.swing.JButton  btnExportPdf;
    // End of variables declaration//GEN-END:variables
}
