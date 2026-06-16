package com.stadela.ui.panel;

import com.stadela.dao.KategoriDAO;
import com.stadela.dao.MenuDAO;
import com.stadela.dao.TokoDAO;
import com.stadela.model.Kategori;
import com.stadela.model.Menu;
import com.stadela.ui.MainFrame;
import com.stadela.util.ReportUtil;

import java.util.HashMap;
import java.util.Map;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MenuPanel extends javax.swing.JPanel implements MainFrame.Refreshable {

    private List<Menu> data;

    public MenuPanel() {
        initComponents();
        setupTable();
        refresh();
    }

    // ── Business Logic ───────────────────────────────────────────

    private void setupTable() {
        tblMenu.getColumn("Aksi").setCellRenderer(new ActionRenderer());
        tblMenu.getColumn("Aksi").setCellEditor(new ActionEditor());
        tblMenu.getColumnModel().getColumn(0).setPreferredWidth(35);
        tblMenu.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblMenu.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblMenu.getColumnModel().getColumn(5).setPreferredWidth(55);
        tblMenu.getColumnModel().getColumn(6).setPreferredWidth(65);
        tblMenu.getColumnModel().getColumn(7).setPreferredWidth(140);
    }

    private void doSearch() {
        String kw = tfSearch.getText().trim();
        javax.swing.SwingWorker<List<Menu>, Void> w = new javax.swing.SwingWorker<>() {
            @Override protected List<Menu> doInBackground() throws Exception {
                return kw.isEmpty() ? new MenuDAO().findAll() : new MenuDAO().findByKeyword(kw);
            }
            @Override protected void done() {
                try { data = get(); populateTable(); }
                catch (Exception e) { showMsg(e.getMessage()); }
            }
        };
        w.execute();
    }

    @Override
    public void refresh() { tfSearch.setText(""); doSearch(); }

    private void populateTable() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        tableModel.setRowCount(0);
        int i = 1;
        for (Menu m : data) {
            tableModel.addRow(new Object[]{
                i++, m.getKodeMenu(), m.getNamaMenu(), m.getNamaKategori(),
                nf.format(m.getHarga()), m.getStok(), m.getSatuan(), m.getIdMenu()
            });
        }
    }

    private void showForm(Menu existing) {
        try {
            List<Kategori> kats = new KategoriDAO().findAll();
            if (kats.isEmpty()) { showMsg("Tambah kategori terlebih dahulu."); return; }

            javax.swing.JComboBox<Kategori> cbKat = new javax.swing.JComboBox<>(kats.toArray(new Kategori[0]));
            javax.swing.JTextField tfNama    = new javax.swing.JTextField(24);
            javax.swing.JTextField tfHarga   = new javax.swing.JTextField(12);
            javax.swing.JTextField tfStok    = new javax.swing.JTextField(8);
            javax.swing.JTextField tfSatuan  = new javax.swing.JTextField(10);
            javax.swing.JTextArea  taDeskripsi = new javax.swing.JTextArea(3, 24);

            if (existing != null) {
                for (int i = 0; i < kats.size(); i++) {
                    if (kats.get(i).getIdKategori() == existing.getIdKategori()) { cbKat.setSelectedIndex(i); break; }
                }
                tfNama.setText(existing.getNamaMenu());
                tfHarga.setText(existing.getHarga().toPlainString());
                tfStok.setText(String.valueOf(existing.getStok()));
                tfSatuan.setText(existing.getSatuan());
                taDeskripsi.setText(existing.getDeskripsi());
            }

            javax.swing.JPanel form = new javax.swing.JPanel(new java.awt.GridBagLayout());
            java.awt.GridBagConstraints gc = new java.awt.GridBagConstraints();
            gc.anchor = java.awt.GridBagConstraints.WEST;
            gc.insets = new java.awt.Insets(4, 4, 4, 4);
            addFormRow(form, gc, 0, "Kategori:", cbKat);
            addFormRow(form, gc, 1, "Nama Menu:", tfNama);
            addFormRow(form, gc, 2, "Harga (Rp):", tfHarga);
            addFormRow(form, gc, 3, "Stok:", tfStok);
            addFormRow(form, gc, 4, "Satuan:", tfSatuan);
            addFormRow(form, gc, 5, "Deskripsi:", new javax.swing.JScrollPane(taDeskripsi));

            String title = existing == null ? "Tambah Menu" : "Ubah Menu";
            int opt = javax.swing.JOptionPane.showConfirmDialog(this, form, title,
                javax.swing.JOptionPane.OK_CANCEL_OPTION);
            if (opt != javax.swing.JOptionPane.OK_OPTION) return;

            String nama = tfNama.getText().trim();
            if (nama.isEmpty()) { showMsg("Nama menu wajib diisi."); return; }
            BigDecimal harga;
            try { harga = new BigDecimal(tfHarga.getText().trim()); }
            catch (NumberFormatException ex) { showMsg("Harga tidak valid."); return; }
            int stok;
            try { stok = Integer.parseInt(tfStok.getText().trim()); }
            catch (NumberFormatException ex) { showMsg("Stok tidak valid."); return; }

            MenuDAO dao = new MenuDAO();
            Menu m = existing != null ? existing : new Menu();
            m.setIdKategori(((Kategori) cbKat.getSelectedItem()).getIdKategori());
            m.setNamaMenu(nama);
            m.setHarga(harga);
            m.setStok(stok);
            m.setSatuan(tfSatuan.getText().trim());
            m.setDeskripsi(taDeskripsi.getText().trim());
            if (existing == null) { m.setKodeMenu(dao.nextKode()); dao.insert(m); }
            else dao.update(m);
            refresh();
        } catch (Exception e) { showMsg(e.getMessage()); }
    }

    private void addFormRow(javax.swing.JPanel p, java.awt.GridBagConstraints gc, int row,
            String label, java.awt.Component field) {
        gc.gridx = 0; gc.gridy = row; gc.fill = java.awt.GridBagConstraints.NONE;
        p.add(new javax.swing.JLabel(label), gc);
        gc.gridx = 1; gc.fill = java.awt.GridBagConstraints.HORIZONTAL; gc.weightx = 1;
        p.add(field, gc); gc.weightx = 0;
    }

    private void doDelete(int id) {
        int c = javax.swing.JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus?", "Hapus",
            javax.swing.JOptionPane.YES_NO_OPTION);
        if (c != javax.swing.JOptionPane.YES_OPTION) return;
        try { new MenuDAO().delete(id); refresh(); }
        catch (Exception e) { showMsg("Gagal hapus: " + e.getMessage()); }
    }

    private void showMsg(String msg) {
        javax.swing.JOptionPane.showMessageDialog(this, msg, "Info",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private void doExportPdf() {
        btnExportPdf.setEnabled(false);
        new javax.swing.SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                java.util.List<Menu> data = new MenuDAO().findAll();
                Map<String, Object> params = new HashMap<>();
                try { params.put("TOKO_NAMA", new TokoDAO().find().getNamaToko()); } catch (Exception ignored) {}
                ReportUtil.preview("data_menu.jrxml", params, data, "Preview — Data Menu");
                return null;
            }
            @Override protected void done() {
                btnExportPdf.setEnabled(true);
                try { get(); } catch (Exception e) {
                    javax.swing.JOptionPane.showMessageDialog(MenuPanel.this,
                        "Gagal membuka preview: " + e.getMessage(), "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) { doSearch(); }
    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) { showForm(null); }
    private void tfSearchActionPerformed(java.awt.event.ActionEvent evt) { doSearch(); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {
        tableModel  = new javax.swing.table.DefaultTableModel(
            new Object[]{"#","Kode","Nama Menu","Kategori","Harga","Stok","Satuan","Aksi"}, 0);
        lblTitle    = new javax.swing.JLabel();
        lblSub      = new javax.swing.JLabel();
        lblCari     = new javax.swing.JLabel();
        tfSearch    = new javax.swing.JTextField();
        btnCari     = new javax.swing.JButton();
        btnTambah   = new javax.swing.JButton();
        btnExportPdf= new javax.swing.JButton();
        scpTable    = new javax.swing.JScrollPane();
        tblMenu     = new javax.swing.JTable();
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTitle.setForeground(new java.awt.Color(44, 62, 80));
        lblTitle.setText("Menu");

        lblSub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSub.setForeground(new java.awt.Color(128, 128, 128));
        lblSub.setText("Manajemen data menu/produk");

        lblCari.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblCari.setText("Cari:");

        tfSearch.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        tfSearch.addActionListener(evt -> tfSearchActionPerformed(evt));

        btnCari.setBackground(new java.awt.Color(52, 152, 219));
        btnCari.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnCari.setForeground(java.awt.Color.WHITE);
        btnCari.setText("Cari");
        btnCari.setFocusPainted(false);
        btnCari.addActionListener(evt -> btnCariActionPerformed(evt));

        btnTambah.setBackground(new java.awt.Color(39, 174, 96));
        btnTambah.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnTambah.setForeground(java.awt.Color.WHITE);
        btnTambah.setText("+ Tambah");
        btnTambah.setFocusPainted(false);
        btnTambah.addActionListener(evt -> btnTambahActionPerformed(evt));

        btnExportPdf.setBackground(new java.awt.Color(192, 57, 43));
        btnExportPdf.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnExportPdf.setForeground(java.awt.Color.WHITE);
        btnExportPdf.setText("Export PDF");
        btnExportPdf.setFocusPainted(false);
        btnExportPdf.addActionListener(evt -> doExportPdf());

        tblMenu.setModel(tableModel);
        tblMenu.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        tblMenu.setRowHeight(36);
        tblMenu.setGridColor(new java.awt.Color(230, 230, 230));
        tblMenu.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        scpTable.setViewportView(tblMenu);

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
                        .addComponent(lblCari)
                        .addGap(6)
                        .addComponent(tfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6)
                        .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6)
                        .addComponent(btnExportPdf, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(lblCari)
                    .addComponent(tfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExportPdf, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            add(makeBtn("Edit", new java.awt.Color(243, 156, 18)));
            add(makeBtn("Hapus", new java.awt.Color(231, 76, 60)));
            setBackground(sel ? t.getSelectionBackground() : t.getBackground());
            return this;
        }
    }
    private class ActionEditor extends javax.swing.DefaultCellEditor {
        private javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 4, 4));
        private int id;
        ActionEditor() { super(new javax.swing.JCheckBox()); }
        @Override public java.awt.Component getTableCellEditorComponent(javax.swing.JTable t, Object v,
                boolean sel, int row, int col) {
            id = (int) tableModel.getValueAt(row, 7);
            int dataIdx = row;
            panel.removeAll();
            javax.swing.JButton bEdit = makeBtn("Edit", new java.awt.Color(243, 156, 18));
            javax.swing.JButton bDel  = makeBtn("Hapus", new java.awt.Color(231, 76, 60));
            bEdit.addActionListener(e -> { fireEditingStopped(); showForm(data.get(dataIdx)); });
            bDel.addActionListener(e  -> { fireEditingStopped(); doDelete(id); });
            panel.add(bEdit); panel.add(bDel);
            return panel;
        }
        @Override public Object getCellEditorValue() { return id; }
    }
    private javax.swing.JButton makeBtn(String text, java.awt.Color bg) {
        javax.swing.JButton b = new javax.swing.JButton(text);
        b.setBackground(bg); b.setForeground(java.awt.Color.WHITE);
        b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setPreferredSize(new java.awt.Dimension(60, 26));
        return b;
    }

    // ── Component fields ──────────────────────────────────────────────
    private javax.swing.table.DefaultTableModel  tableModel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel  lblTitle;
    private javax.swing.JLabel  lblSub;
    private javax.swing.JLabel  lblCari;
    private javax.swing.JTextField  tfSearch;
    private javax.swing.JButton  btnCari;
    private javax.swing.JButton  btnTambah;
    private javax.swing.JScrollPane  scpTable;
    private javax.swing.JTable  tblMenu;
    private javax.swing.JButton  btnExportPdf;
    // End of variables declaration//GEN-END:variables
}
