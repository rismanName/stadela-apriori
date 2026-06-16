package com.stadela.ui.panel;

import com.stadela.dao.MenuDAO;
import com.stadela.dao.TransaksiDAO;
import com.stadela.model.DetailTransaksi;
import com.stadela.model.Menu;
import com.stadela.model.Transaksi;
import com.stadela.ui.MainFrame;
import com.stadela.util.SessionManager;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransaksiPanel extends javax.swing.JPanel implements MainFrame.Refreshable {

    private List<DetailTransaksi> cart = new ArrayList<>();
    private List<Menu> menuList;

    public TransaksiPanel() {
        initComponents();
        setupCart();
        refresh();
    }

    // ── Business Logic ───────────────────────────────────────────

    private void setupCart() {
        tblKeranjang.getColumnModel().getColumn(4).setPreferredWidth(55);
        tblKeranjang.getColumnModel().getColumn(4).setCellRenderer(new DelRenderer());
        tblKeranjang.getColumnModel().getColumn(4).setCellEditor(new DelEditor());
    }

    @Override
    public void refresh() {
        javax.swing.SwingWorker<List<Menu>, Void> w = new javax.swing.SwingWorker<>() {
            @Override protected List<Menu> doInBackground() throws Exception {
                return new MenuDAO().findAll();
            }
            @Override protected void done() {
                try {
                    menuList = get();
                    cbMenu.removeAllItems();
                    for (Menu m : menuList) cbMenu.addItem(m);
                    updateHarga();
                } catch (Exception e) { showMsg(e.getMessage()); }
            }
        };
        w.execute();
    }

    private void updateHarga() {
        Menu sel = (Menu) cbMenu.getSelectedItem();
        if (sel != null) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            lblHarga.setText(nf.format(sel.getHarga()));
        }
    }

    private void addToCart() {
        Menu sel = (Menu) cbMenu.getSelectedItem();
        if (sel == null) return;
        int jml = (int) spJumlah.getValue();
        if (sel.getStok() < jml) { showMsg("Stok tidak mencukupi. Stok tersedia: " + sel.getStok()); return; }
        for (DetailTransaksi d : cart) {
            if (d.getIdMenu() == sel.getIdMenu()) {
                d.setJumlah(d.getJumlah() + jml);
                refreshCart();
                return;
            }
        }
        cart.add(new DetailTransaksi(sel.getIdMenu(), sel.getNamaMenu(), jml, sel.getHarga()));
        refreshCart();
    }

    private void refreshCart() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        cartModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (DetailTransaksi d : cart) {
            cartModel.addRow(new Object[]{
                d.getNamaMenu(), d.getJumlah(),
                nf.format(d.getHargaSatuan()), nf.format(d.getSubtotal()), "✕"
            });
            total = total.add(d.getSubtotal());
        }
        lblTotal.setText("Total: " + nf.format(total));
    }

    private void processTransaction() {
        if (cart.isEmpty()) { showMsg("Keranjang kosong."); return; }
        int c = javax.swing.JOptionPane.showConfirmDialog(this,
            "Proses transaksi sekarang?", "Konfirmasi", javax.swing.JOptionPane.YES_NO_OPTION);
        if (c != javax.swing.JOptionPane.YES_OPTION) return;
        try {
            TransaksiDAO dao = new TransaksiDAO();
            Date today = Date.valueOf(LocalDate.now());
            Transaksi t = new Transaksi();
            t.setIdUser(SessionManager.getCurrentUser().getIdUser());
            t.setTanggalTransaksi(today);
            t.setKodeTransaksi(dao.nextKode(today));
            BigDecimal total = cart.stream()
                .map(DetailTransaksi::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            t.setTotal(total);
            t.setDetails(new ArrayList<>(cart));
            dao.save(t);
            javax.swing.JOptionPane.showMessageDialog(this,
                "Transaksi berhasil!\nKode: " + t.getKodeTransaksi() + "\nTotal: Rp " + total.toPlainString(),
                "Sukses", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            cart.clear();
            refreshCart();
            refresh();
        } catch (Exception e) { showMsg("Gagal menyimpan transaksi: " + e.getMessage()); }
    }

    private void showMsg(String msg) {
        javax.swing.JOptionPane.showMessageDialog(this, msg);
    }

    private void cbMenuActionPerformed(java.awt.event.ActionEvent evt) { updateHarga(); }
    private void btnTambahKeranjangActionPerformed(java.awt.event.ActionEvent evt) { addToCart(); }
    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) { processTransaction(); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {
        lblTitle    = new javax.swing.JLabel();
        lblSub      = new javax.swing.JLabel();
        pnlPilih    = new javax.swing.JPanel();
        pnlKeranjang = new javax.swing.JPanel();

        setBackground(new java.awt.Color(248, 249, 250));

        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTitle.setForeground(new java.awt.Color(44, 62, 80));
        lblTitle.setText("Transaksi");

        lblSub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSub.setForeground(new java.awt.Color(128, 128, 128));
        lblSub.setText("Proses penjualan");

        // ── pnlPilih: form pilih menu ─────────────────────────────
        cbMenu          = new javax.swing.JComboBox<>();
        spJumlah        = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(1, 1, 100, 1));
        lblHargaLbl     = new javax.swing.JLabel("Harga:");
        lblHarga        = new javax.swing.JLabel("Rp 0");
        btnTambahKeranjang = new javax.swing.JButton("+ Keranjang");

        cbMenu.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        cbMenu.addActionListener(evt -> cbMenuActionPerformed(evt));
        spJumlah.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        lblHargaLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblHarga.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        lblHarga.setForeground(new java.awt.Color(39, 174, 96));
        btnTambahKeranjang.setBackground(new java.awt.Color(39, 174, 96));
        btnTambahKeranjang.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnTambahKeranjang.setForeground(java.awt.Color.WHITE);
        btnTambahKeranjang.setFocusPainted(false);
        btnTambahKeranjang.addActionListener(evt -> btnTambahKeranjangActionPerformed(evt));

        pnlPilih.setBackground(new java.awt.Color(255, 255, 255));
        pnlPilih.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)),
            javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        javax.swing.GroupLayout pilihLayout = new javax.swing.GroupLayout(pnlPilih);
        pnlPilih.setLayout(pilihLayout);
        pilihLayout.setHorizontalGroup(pilihLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pilihLayout.createSequentialGroup()
                .addComponent(cbMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(8).addComponent(lblHargaLbl).addGap(4)
                .addComponent(lblHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8).addComponent(spJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8).addComponent(btnTambahKeranjang, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)));
        pilihLayout.setVerticalGroup(pilihLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(cbMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblHargaLbl).addComponent(lblHarga)
            .addComponent(spJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnTambahKeranjang, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE));

        // ── pnlKeranjang: tabel + total + bayar ──────────────────
        cartModel = new javax.swing.table.DefaultTableModel(
            new Object[]{"Menu", "Jml", "Harga", "Subtotal", ""}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 4; }
        };
        tblKeranjang    = new javax.swing.JTable(cartModel);
        scpKeranjang    = new javax.swing.JScrollPane(tblKeranjang);
        lblTotal        = new javax.swing.JLabel("Total: Rp 0");
        btnBayar        = new javax.swing.JButton("Bayar");

        tblKeranjang.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        tblKeranjang.setRowHeight(36);
        tblKeranjang.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        lblTotal.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        lblTotal.setForeground(new java.awt.Color(44, 62, 80));
        btnBayar.setBackground(new java.awt.Color(44, 62, 80));
        btnBayar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        btnBayar.setForeground(java.awt.Color.WHITE);
        btnBayar.setFocusPainted(false);
        btnBayar.addActionListener(evt -> btnBayarActionPerformed(evt));

        pnlKeranjang.setBackground(new java.awt.Color(255, 255, 255));
        pnlKeranjang.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)),
            javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        javax.swing.GroupLayout keranjangLayout = new javax.swing.GroupLayout(pnlKeranjang);
        pnlKeranjang.setLayout(keranjangLayout);
        keranjangLayout.setHorizontalGroup(keranjangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scpKeranjang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, keranjangLayout.createSequentialGroup()
                .addComponent(lblTotal).addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)));
        keranjangLayout.setVerticalGroup(keranjangLayout.createSequentialGroup()
            .addComponent(scpKeranjang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGap(8)
            .addGroup(keranjangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblTotal)
                .addComponent(btnBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(lblSub)
                    .addComponent(pnlPilih, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlKeranjang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20)
                .addComponent(lblTitle)
                .addGap(4)
                .addComponent(lblSub)
                .addGap(16)
                .addComponent(pnlPilih, javax.swing.GroupLayout.PREFERRED_SIZE, 200, Short.MAX_VALUE)
                .addGap(10)
                .addComponent(pnlKeranjang, javax.swing.GroupLayout.PREFERRED_SIZE, 200, Short.MAX_VALUE)
                .addGap(20))
        );
        }//GEN-END:initComponents
    // </editor-fold>
    // </editor-fold>


    // ── Inner classes ────────────────────────────────────────────
    private class DelRenderer extends javax.swing.JPanel implements javax.swing.table.TableCellRenderer {
        DelRenderer() { setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 4)); setOpaque(true); }
        @Override public java.awt.Component getTableCellRendererComponent(javax.swing.JTable t, Object v,
                boolean s, boolean f, int r, int c) {
            removeAll();
            add(makeDelBtn());
            setBackground(s ? t.getSelectionBackground() : t.getBackground());
            return this;
        }
    }
    private class DelEditor extends javax.swing.DefaultCellEditor {
        private javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 4));
        private int row;
        DelEditor() { super(new javax.swing.JCheckBox()); }
        @Override public java.awt.Component getTableCellEditorComponent(javax.swing.JTable t, Object v,
                boolean s, int r, int c) {
            row = r; panel.removeAll();
            javax.swing.JButton b = makeDelBtn();
            b.addActionListener(e -> { fireEditingStopped(); cart.remove(row); refreshCart(); });
            panel.add(b); return panel;
        }
        @Override public Object getCellEditorValue() { return "✕"; }
    }
    private javax.swing.JButton makeDelBtn() {
        javax.swing.JButton b = new javax.swing.JButton("✕");
        b.setBackground(new java.awt.Color(231, 76, 60));
        b.setForeground(java.awt.Color.WHITE);
        b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setPreferredSize(new java.awt.Dimension(40, 26));
        return b;
    }

    // ── Component fields ──────────────────────────────────────────────
    private javax.swing.table.DefaultTableModel  cartModel;
    private javax.swing.JComboBox<Menu>  cbMenu;
    private javax.swing.JSpinner  spJumlah;
    private javax.swing.JLabel  lblHargaLbl;
    private javax.swing.JLabel  lblHarga;
    private javax.swing.JButton  btnTambahKeranjang;
    private javax.swing.JScrollPane  scpKeranjang;
    private javax.swing.JTable  tblKeranjang;
    private javax.swing.JLabel  lblTotal;
    private javax.swing.JButton  btnBayar;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblSub;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlKeranjang;
    private javax.swing.JPanel pnlPilih;
    // End of variables declaration//GEN-END:variables
}
