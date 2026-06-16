package com.stadela.ui.panel;

import com.stadela.dao.UserDAO;
import com.stadela.model.User;
import com.stadela.ui.MainFrame;
import com.stadela.util.SessionManager;

import java.util.List;

public class UserPanel extends javax.swing.JPanel implements MainFrame.Refreshable {

    private List<User> data;

    public UserPanel() {
        initComponents();
        setupTable();
        refresh();
    }

    // ── Business Logic ───────────────────────────────────────────

    private void setupTable() {
        tblUser.getColumn("Aksi").setCellRenderer(new ActionRenderer());
        tblUser.getColumn("Aksi").setCellEditor(new ActionEditor());
        tblUser.getColumnModel().getColumn(0).setPreferredWidth(35);
        tblUser.getColumnModel().getColumn(3).setPreferredWidth(70);
        tblUser.getColumnModel().getColumn(5).setPreferredWidth(140);
    }

    @Override
    public void refresh() {
        javax.swing.SwingWorker<List<User>, Void> w = new javax.swing.SwingWorker<>() {
            @Override protected List<User> doInBackground() throws Exception { return new UserDAO().findAll(); }
            @Override protected void done() {
                try {
                    data = get();
                    tableModel.setRowCount(0);
                    int i = 1;
                    for (User u : data) {
                        tableModel.addRow(new Object[]{i++, u.getUsername(), u.getNama(),
                            u.getRole(), u.getEmail(), u.getIdUser()});
                    }
                } catch (Exception e) { showMsg(e.getMessage()); }
            }
        };
        w.execute();
    }

    private void showForm(User existing) {
        javax.swing.JTextField tfUsername = new javax.swing.JTextField(
            existing != null ? existing.getUsername() : "", 18);
        javax.swing.JPasswordField pfPass = new javax.swing.JPasswordField(18);
        javax.swing.JComboBox<String> cbRole = new javax.swing.JComboBox<>(new String[]{"admin","kasir"});
        javax.swing.JTextField tfNama  = new javax.swing.JTextField(existing != null ? existing.getNama() : "", 18);
        javax.swing.JTextField tfEmail = new javax.swing.JTextField(existing != null ? existing.getEmail() : "", 18);
        javax.swing.JTextField tfTlp   = new javax.swing.JTextField(existing != null ? existing.getTelepon() : "", 18);
        javax.swing.JTextField tfNIK   = new javax.swing.JTextField(existing != null ? existing.getNik() : "", 18);
        javax.swing.JTextArea  taAlamat= new javax.swing.JTextArea(2, 18);
        if (existing != null) { taAlamat.setText(existing.getAlamat()); cbRole.setSelectedItem(existing.getRole()); }

        javax.swing.JPanel form = new javax.swing.JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gc = new java.awt.GridBagConstraints();
        gc.insets = new java.awt.Insets(4,4,4,4); gc.anchor = java.awt.GridBagConstraints.WEST;
        addFormRow(form, gc, 0, "Username:", tfUsername);
        addFormRow(form, gc, 1, "Password" + (existing!=null?" (kosongkan jika tidak diubah)":"") + ":", pfPass);
        addFormRow(form, gc, 2, "Role:", cbRole);
        addFormRow(form, gc, 3, "Nama Lengkap:", tfNama);
        addFormRow(form, gc, 4, "Email:", tfEmail);
        addFormRow(form, gc, 5, "Telepon:", tfTlp);
        addFormRow(form, gc, 6, "NIK:", tfNIK);
        addFormRow(form, gc, 7, "Alamat:", new javax.swing.JScrollPane(taAlamat));

        int opt = javax.swing.JOptionPane.showConfirmDialog(this, form,
            existing == null ? "Tambah User" : "Ubah User", javax.swing.JOptionPane.OK_CANCEL_OPTION);
        if (opt != javax.swing.JOptionPane.OK_OPTION) return;

        String username = tfUsername.getText().trim();
        String pass     = new String(pfPass.getPassword()).trim();
        String nama     = tfNama.getText().trim();
        if (username.isEmpty() || nama.isEmpty()) { showMsg("Username dan nama wajib diisi."); return; }
        if (existing == null && pass.isEmpty()) { showMsg("Password wajib diisi untuk user baru."); return; }

        try {
            UserDAO dao = new UserDAO();
            if (dao.usernameExists(username, existing != null ? existing.getIdUser() : 0)) {
                showMsg("Username sudah digunakan."); return;
            }
            User u = existing != null ? existing : new User();
            u.setUsername(username);
            u.setRole((String) cbRole.getSelectedItem());
            u.setNama(nama);
            u.setEmail(tfEmail.getText().trim());
            u.setTelepon(tfTlp.getText().trim());
            u.setNik(tfNIK.getText().trim());
            u.setAlamat(taAlamat.getText().trim());
            boolean changePass = !pass.isEmpty();
            if (changePass) u.setPassword(pass);
            if (existing == null) dao.insert(u);
            else dao.update(u, changePass);
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
        if (id == SessionManager.getCurrentUser().getIdUser()) {
            showMsg("Tidak bisa hapus akun sendiri."); return;
        }
        int c = javax.swing.JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus user ini?", "Hapus", javax.swing.JOptionPane.YES_NO_OPTION);
        if (c != javax.swing.JOptionPane.YES_OPTION) return;
        try { new UserDAO().delete(id); refresh(); }
        catch (Exception e) { showMsg("Gagal hapus: " + e.getMessage()); }
    }

    private void showMsg(String msg) {
        javax.swing.JOptionPane.showMessageDialog(this, msg);
    }

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) { showForm(null); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {
        tableModel  = new javax.swing.table.DefaultTableModel(
            new Object[]{"#","Username","Nama","Role","Email","Aksi"}, 0);
        lblTitle    = new javax.swing.JLabel();
        lblSub      = new javax.swing.JLabel();
        btnTambah   = new javax.swing.JButton();
        scpTable    = new javax.swing.JScrollPane();
        tblUser     = new javax.swing.JTable();
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTitle.setForeground(new java.awt.Color(44, 62, 80));
        lblTitle.setText("Manajemen User");

        lblSub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSub.setForeground(new java.awt.Color(128, 128, 128));
        lblSub.setText("Kelola akun pengguna sistem");

        btnTambah.setBackground(new java.awt.Color(39, 174, 96));
        btnTambah.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnTambah.setForeground(java.awt.Color.WHITE);
        btnTambah.setText("+ Tambah User");
        btnTambah.setFocusPainted(false);
        btnTambah.addActionListener(evt -> btnTambahActionPerformed(evt));

        tblUser.setModel(tableModel);
        tblUser.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        tblUser.setRowHeight(36);
        tblUser.setGridColor(new java.awt.Color(230, 230, 230));
        tblUser.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        scpTable.setViewportView(tblUser);

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
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblSub)
                    .addComponent(scpTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitle)
                    .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4)
                .addComponent(lblSub)
                .addGap(16)
                .addComponent(scpTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20)
        );
            }//GEN-END:initComponents
    // </editor-fold>


    // ── Inner classes ────────────────────────────────────────────
    private class ActionRenderer extends javax.swing.JPanel implements javax.swing.table.TableCellRenderer {
        ActionRenderer() { setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER,4,4)); setOpaque(true); }
        @Override public java.awt.Component getTableCellRendererComponent(javax.swing.JTable t, Object v,
                boolean s, boolean f, int r, int c) {
            removeAll();
            add(makeBtn("Edit", new java.awt.Color(243, 156, 18)));
            add(makeBtn("Hapus", new java.awt.Color(231, 76, 60)));
            setBackground(s ? t.getSelectionBackground() : t.getBackground()); return this;
        }
    }
    private class ActionEditor extends javax.swing.DefaultCellEditor {
        private javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER,4,4));
        private int id;
        ActionEditor() { super(new javax.swing.JCheckBox()); }
        @Override public java.awt.Component getTableCellEditorComponent(javax.swing.JTable t, Object v,
                boolean s, int row, int col) {
            id = (int) tableModel.getValueAt(row, 5);
            int idx = row;
            panel.removeAll();
            javax.swing.JButton bEdit = makeBtn("Edit", new java.awt.Color(243, 156, 18));
            javax.swing.JButton bDel  = makeBtn("Hapus", new java.awt.Color(231, 76, 60));
            bEdit.addActionListener(e -> { fireEditingStopped(); showForm(data.get(idx)); });
            bDel.addActionListener(e  -> { fireEditingStopped(); doDelete(id); });
            panel.add(bEdit); panel.add(bDel); return panel;
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
    private javax.swing.JButton  btnTambah;
    private javax.swing.JScrollPane  scpTable;
    private javax.swing.JTable  tblUser;
    // End of variables declaration//GEN-END:variables
}
