package com.stadela.ui.panel;

import com.stadela.dao.TransaksiDAO;
import com.stadela.model.DetailTransaksi;
import com.stadela.model.Transaksi;
import com.stadela.ui.MainFrame;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RiwayatPanel extends javax.swing.JPanel implements MainFrame.Refreshable {

    private List<Transaksi> data;

    public RiwayatPanel() {
        initComponents();
        setupTable();
        refresh();
    }

    // ── Business Logic ───────────────────────────────────────────

    private void setupTable() {
        tblRiwayat.getColumn("Aksi").setCellRenderer(new DetailRenderer());
        tblRiwayat.getColumn("Aksi").setCellEditor(new DetailEditor());
        tblRiwayat.getColumnModel().getColumn(0).setPreferredWidth(35);
        tblRiwayat.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblRiwayat.getColumnModel().getColumn(5).setPreferredWidth(90);
    }

    @Override
    public void refresh() {
        javax.swing.SwingWorker<List<Transaksi>, Void> w = new javax.swing.SwingWorker<>() {
            @Override protected List<Transaksi> doInBackground() throws Exception {
                return new TransaksiDAO().findAll();
            }
            @Override protected void done() {
                try {
                    data = get();
                    tableModel.setRowCount(0);
                    NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    int i = 1;
                    for (Transaksi t : data) {
                        tableModel.addRow(new Object[]{
                            i++, t.getKodeTransaksi(), t.getNamaUser(),
                            t.getTanggalTransaksi(), nf.format(t.getTotal()), t.getIdTransaksi()
                        });
                    }
                } catch (Exception e) {
                    javax.swing.JOptionPane.showMessageDialog(RiwayatPanel.this, e.getMessage(), "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    private void showDetail(int idTransaksi) {
        try {
            List<DetailTransaksi> details = new TransaksiDAO().findDetailByTransaksi(idTransaksi);
            String[] cols = {"Nama Menu","Jumlah","Harga Satuan","Subtotal"};
            javax.swing.table.DefaultTableModel m = new javax.swing.table.DefaultTableModel(cols, 0);
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            for (DetailTransaksi d : details) {
                m.addRow(new Object[]{d.getNamaMenu(), d.getJumlah(),
                    nf.format(d.getHargaSatuan()), nf.format(d.getSubtotal())});
            }
            javax.swing.JTable tbl = new javax.swing.JTable(m);
            tbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
            tbl.setRowHeight(30);
            javax.swing.JScrollPane sp = new javax.swing.JScrollPane(tbl);
            sp.setPreferredSize(new java.awt.Dimension(520, 220));
            javax.swing.JOptionPane.showMessageDialog(this, sp,
                "Detail Transaksi #" + idTransaksi, javax.swing.JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) { refresh(); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {
        tableModel  = new javax.swing.table.DefaultTableModel(
            new Object[]{"#","Kode Transaksi","Kasir","Tanggal","Total","Aksi"}, 0);
        lblTitle    = new javax.swing.JLabel();
        lblSub      = new javax.swing.JLabel();
        btnRefresh  = new javax.swing.JButton();
        scpTable    = new javax.swing.JScrollPane();
        tblRiwayat  = new javax.swing.JTable();
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTitle.setForeground(new java.awt.Color(44, 62, 80));
        lblTitle.setText("Riwayat Transaksi");

        lblSub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSub.setForeground(new java.awt.Color(128, 128, 128));
        lblSub.setText("Semua transaksi penjualan");

        btnRefresh.setBackground(new java.awt.Color(52, 152, 219));
        btnRefresh.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnRefresh.setForeground(java.awt.Color.WHITE);
        btnRefresh.setText("Refresh");
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(evt -> btnRefreshActionPerformed(evt));

        tblRiwayat.setModel(tableModel);
        tblRiwayat.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        tblRiwayat.setRowHeight(36);
        tblRiwayat.setGridColor(new java.awt.Color(230, 230, 230));
        tblRiwayat.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        scpTable.setViewportView(tblRiwayat);

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
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblSub)
                    .addComponent(scpTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitle)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4)
                .addComponent(lblSub)
                .addGap(16)
                .addComponent(scpTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20)
        );
            }//GEN-END:initComponents
    // </editor-fold>


    // ── Inner classes ────────────────────────────────────────────
    private class DetailRenderer extends javax.swing.JPanel implements javax.swing.table.TableCellRenderer {
        DetailRenderer() { setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER,4,4)); setOpaque(true); }
        @Override public java.awt.Component getTableCellRendererComponent(javax.swing.JTable t, Object v,
                boolean s, boolean f, int r, int c) {
            removeAll();
            add(makeBtn("Detail"));
            setBackground(s ? t.getSelectionBackground() : t.getBackground()); return this;
        }
    }
    private class DetailEditor extends javax.swing.DefaultCellEditor {
        private javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER,4,4));
        private int id;
        DetailEditor() { super(new javax.swing.JCheckBox()); }
        @Override public java.awt.Component getTableCellEditorComponent(javax.swing.JTable t, Object v,
                boolean s, int r, int c) {
            id = (int) tableModel.getValueAt(r, 5);
            panel.removeAll();
            javax.swing.JButton b = makeBtn("Detail");
            b.addActionListener(e -> { fireEditingStopped(); showDetail(id); });
            panel.add(b); return panel;
        }
        @Override public Object getCellEditorValue() { return id; }
    }
    private javax.swing.JButton makeBtn(String text) {
        javax.swing.JButton b = new javax.swing.JButton(text);
        b.setBackground(new java.awt.Color(52, 152, 219));
        b.setForeground(java.awt.Color.WHITE);
        b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setPreferredSize(new java.awt.Dimension(70, 26));
        return b;
    }

    // ── Component fields ──────────────────────────────────────────────
    private javax.swing.table.DefaultTableModel  tableModel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel  lblTitle;
    private javax.swing.JLabel  lblSub;
    private javax.swing.JButton  btnRefresh;
    private javax.swing.JScrollPane  scpTable;
    private javax.swing.JTable  tblRiwayat;
    // End of variables declaration//GEN-END:variables
}
