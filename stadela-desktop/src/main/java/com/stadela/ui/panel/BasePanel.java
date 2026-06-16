package com.stadela.ui.panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Panel dasar dengan utilitas bersama.
 */
public abstract class BasePanel extends JPanel {

    protected static final Color PRIMARY   = new Color(44, 62, 80);
    protected static final Color ACCENT    = new Color(231, 76, 60);
    protected static final Color SUCCESS   = new Color(39, 174, 96);
    protected static final Color WARNING   = new Color(243, 156, 18);
    protected static final Color BG        = new Color(248, 249, 250);
    protected static final Font  FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    protected static final Font  FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    protected static final Font  FONT_H2   = new Font("Segoe UI", Font.BOLD, 18);

    public BasePanel() {
        setBackground(BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));
    }

    /** Buat header halaman */
    protected JPanel makePageHeader(String title, String subtitle) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(FONT_H2);
        t.setForeground(PRIMARY);
        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(Color.GRAY);
        p.add(t);
        p.add(s);
        return p;
    }

    /** Buat tombol aksi dengan warna */
    protected JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    /** Buat JTable dengan styling */
    protected JTable makeTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(52, 152, 219, 60));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(PRIMARY);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Center semua kolom
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < model.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        return table;
    }

    /** Buat card statistik */
    protected JPanel makeStatCard(String label, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,220,220), 1, true),
            new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel lValue = new JLabel(value);
        lValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lValue.setForeground(accent);

        JLabel lLabel = new JLabel(label);
        lLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lLabel.setForeground(Color.GRAY);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lValue, BorderLayout.CENTER);

        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.add(top, BorderLayout.CENTER);
        inner.add(lLabel, BorderLayout.SOUTH);

        card.add(inner);
        return card;
    }

    /** Tampilkan pesan error */
    protected void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Tampilkan pesan sukses */
    protected void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Konfirmasi hapus */
    protected boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus data ini?", "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
