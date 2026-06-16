package com.stadela.ui.panel;

import com.stadela.dao.KategoriDAO;
import com.stadela.model.Kategori;
import com.stadela.ui.MainFrame;

import java.awt.*;
import java.util.List;

public class KategoriPanel extends javax.swing.JPanel implements MainFrame.Refreshable {

    private List<Kategori> data;

    public KategoriPanel() {
        initComponents();
        setupTable();
        refresh();
    }

    // ── Business Logic ───────────────────────────────────────────

    private void setupTable() {
        tblKategori.getColumn("Aksi").setCellRenderer(new ActionRenderer());
        tblKategori.getColumn("Aksi").setCellEditor(new ActionEditor());
        tblKategori.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblKategori.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblKategori.getColumnModel().getColumn(3).setPreferredWidth(140);
    }

    @Override
    public void refresh() {
        javax.swing.SwingWorker<List<Kategori>, Void> w = new javax.swing.SwingWorker<>() {
            @Override protected List<Kategori> doInBackground() throws Exception {
                return new KategoriDAO().findAll();
            }
            @Override protected void done() {
                try {
                    data = get();
                    tableModel.setRowCount(0);
                    int i = 1;
                    for (Kategori k : data) {
                        tableModel.addRow(new Object[]{
                            i++,
                            k.getNamaKategori(),
                            k.getTglInput() != null ? k.getTglInput().toString().substring(0, 10) : "-",
                            k.getIdKategori()
                        });
                    }
                } catch (Exception e) {
                    javax.swing.JOptionPane.showMessageDialog(KategoriPanel.this, e.getMessage(), "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    private void showForm(Kategori existing) {
        javax.swing.JTextField tfNama = new javax.swing.JTextField(
            existing != null ? existing.getNamaKategori() : "", 24);
        String title = existing == null ? "Tambah Kategori" : "Ubah Kategori";
        int opt = javax.swing.JOptionPane.showConfirmDialog(this,
            new Object[]{"Nama Kategori:", tfNama}, title, javax.swing.JOptionPane.OK_CANCEL_OPTION);
        if (opt != javax.swing.JOptionPane.OK_OPTION) return;
        String nama = tfNama.getText().trim();
        if (nama.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Nama kategori tidak boleh kosong.", "Validasi",
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            KategoriDAO dao = new KategoriDAO();
            if (existing == null) dao.insert(nama);
            else                  dao.update(existing.getIdKategori(), nama);
            refresh();
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete(int id) {
        int c = javax.swing.JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus data ini?", "Hapus", javax.swing.JOptionPane.YES_NO_OPTION);
        if (c != javax.swing.JOptionPane.YES_OPTION) return;
        try { new KategoriDAO().delete(id); refresh(); }
        catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage(), "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {
        showForm(null);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {
        tableModel  = new javax.swing.table.DefaultTableModel(
            new Object[]{"#", "Nama Kategori", "Tgl Input", "Aksi"}, 0);
        lblTitle    = new javax.swing.JLabel();
        lblSub      = new javax.swing.JLabel();
        btnTambah   = new javax.swing.JButton();
        scpTable    = new javax.swing.JScrollPane();
        tblKategori = new javax.swing.JTable();
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTitle.setForeground(new java.awt.Color(44, 62, 80));
        lblTitle.setText("Kategori");

        lblSub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSub.setForeground(new java.awt.Color(128, 128, 128));
        lblSub.setText("Manajemen kategori menu");

        btnTambah.setBackground(new java.awt.Color(39, 174, 96));
        btnTambah.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnTambah.setForeground(java.awt.Color.WHITE);
        btnTambah.setText("+ Tambah");
        btnTambah.setFocusPainted(false);
        btnTambah.addActionListener(evt -> btnTambahActionPerformed(evt));

        tblKategori.setModel(tableModel);
        tblKategori.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        tblKategori.setRowHeight(36);
        tblKategori.setGridColor(new java.awt.Color(230, 230, 230));
        tblKategori.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        scpTable.setViewportView(tblKategori);

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
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblSub)
                    .addComponent(scpTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitle)
                    .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4)
                .addComponent(lblSub)
                .addGap(16)
                .addComponent(scpTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20))
        );
            }//GEN-END:initComponents
    // </editor-fold>


    // ── Inner classes ────────────────────────────────────────────
    private class ActionRenderer extends javax.swing.JPanel implements javax.swing.table.TableCellRenderer {
        ActionRenderer() { setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 4, 4)); setOpaque(true); }
        @Override public java.awt.Component getTableCellRendererComponent(javax.swing.JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            removeAll();
            javax.swing.JButton bEdit = makeBtn("Edit", new java.awt.Color(243, 156, 18));
            javax.swing.JButton bDel  = makeBtn("Hapus", new java.awt.Color(231, 76, 60));
            add(bEdit); add(bDel);
            setBackground(sel ? t.getSelectionBackground() : t.getBackground());
            return this;
        }
    }

    private class ActionEditor extends javax.swing.DefaultCellEditor {
        private javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 4, 4));
        private int currentId;
        ActionEditor() { super(new javax.swing.JCheckBox()); }
        @Override public java.awt.Component getTableCellEditorComponent(javax.swing.JTable t, Object v,
                boolean sel, int row, int col) {
            currentId = (int) tableModel.getValueAt(row, 3);
            panel.removeAll();
            int dataIdx = row;
            javax.swing.JButton bEdit = makeBtn("Edit", new java.awt.Color(243, 156, 18));
            javax.swing.JButton bDel  = makeBtn("Hapus", new java.awt.Color(231, 76, 60));
            bEdit.addActionListener(e -> { fireEditingStopped(); showForm(data.get(dataIdx)); });
            bDel.addActionListener(e  -> { fireEditingStopped(); doDelete(currentId); });
            panel.add(bEdit); panel.add(bDel);
            return panel;
        }
        @Override public Object getCellEditorValue() { return currentId; }
    }

    private javax.swing.JButton makeBtn(String text, java.awt.Color bg) {
        javax.swing.JButton b = new javax.swing.JButton(text);
        b.setBackground(bg);
        b.setForeground(java.awt.Color.WHITE);
        b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new java.awt.Dimension(60, 26));
        return b;
    }

    // ── Component fields ──────────────────────────────────────────────
    private javax.swing.table.DefaultTableModel  tableModel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel  lblTitle;
    private javax.swing.JLabel  lblSub;
    private javax.swing.JButton  btnTambah;
    private javax.swing.JScrollPane  scpTable;
    private javax.swing.JTable  tblKategori;
    // End of variables declaration//GEN-END:variables
}
