package com.stadela.ui;

import com.stadela.ui.panel.*;
import com.stadela.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private static final Color SIDEBAR_BG     = new Color(30, 44, 58);
    private static final Color SIDEBAR_HEADER = new Color(22, 34, 46);
    private static final Color SIDEBAR_TEXT   = new Color(176, 190, 197);
    private static final Color SIDEBAR_HOVER  = new Color(44, 62, 80);
    private static final Color SIDEBAR_ACTIVE = new Color(231, 76, 60);
    private static final Color SECTION_TEXT   = new Color(100, 120, 135);

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel lblNamaUser;
    private JLabel lblRole;

    /** Track all nav buttons to reset active state */
    private final List<JButton> navButtons = new ArrayList<>();
    private JButton activeButton = null;

    public MainFrame() {
        setTitle("Stadela Coffee — Desktop");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 760);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(960, 620));
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);

        // ── Sidebar ──────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, 0));

        // ── Brand ─────────────────────────────────────────────────
        JPanel brand = new JPanel(new BorderLayout());
        brand.setBackground(SIDEBAR_HEADER);
        brand.setBorder(new EmptyBorder(20, 20, 20, 20));
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        JLabel brandLabel = new JLabel("☕  Stadela Coffee");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        brandLabel.setForeground(Color.WHITE);
        brand.add(brandLabel);
        sidebar.add(brand);

        // ── User info ─────────────────────────────────────────────
        String nama  = SessionManager.getCurrentUser().getNama();
        String role  = SessionManager.getCurrentUser().getRole().toUpperCase();
        // Truncate name nicely if too long
        String namaDisplay = nama.length() > 22 ? nama.substring(0, 20) + "…" : nama;

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBackground(SIDEBAR_HEADER);
        userInfo.setBorder(new EmptyBorder(0, 20, 16, 20));
        userInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        lblNamaUser = new JLabel("👤  " + namaDisplay);
        lblNamaUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNamaUser.setForeground(Color.WHITE);
        lblNamaUser.setToolTipText(nama);   // full name on hover

        lblRole = new JLabel("    " + role);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRole.setForeground(SIDEBAR_TEXT);

        userInfo.add(lblNamaUser);
        userInfo.add(Box.createVerticalStrut(2));
        userInfo.add(lblRole);
        sidebar.add(userInfo);

        // Thin divider
        sidebar.add(thinDivider());

        // ── Navigation ────────────────────────────────────────────
        sidebar.add(sectionLabel("MENU UTAMA"));
        JButton btnDash = addNav(sidebar, "⊞", "Dashboard",  "dashboard");

        sidebar.add(sectionLabel("MASTER DATA"));
        if (SessionManager.isAdmin()) {
            addNav(sidebar, "▤", "Kategori",  "kategori");
            addNav(sidebar, "🍽", "Menu",      "menu");
        }

        sidebar.add(sectionLabel("TRANSAKSI"));
        addNav(sidebar, "💳", "Transaksi",    "transaksi");
        addNav(sidebar, "🗒", "Riwayat",      "riwayat");

        if (SessionManager.isAdmin()) {
            sidebar.add(sectionLabel("DATA MINING"));
            addNav(sidebar, "📊", "Apriori",  "apriori");

            sidebar.add(sectionLabel("LAPORAN"));
            addNav(sidebar, "📄", "Laporan",  "laporan");

            sidebar.add(sectionLabel("PENGATURAN"));
            addNav(sidebar, "🏪", "Data Toko","toko");
            addNav(sidebar, "👥", "User",     "user");
        }

        sidebar.add(sectionLabel("AKUN"));
        addNav(sidebar, "👤", "Profil",        "profil");

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(thinDivider());

        // ── Logout ────────────────────────────────────────────────
        JButton btnLogout = new JButton("  ⏻   Keluar");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setForeground(new Color(231, 76, 60));
        btnLogout.setBackground(SIDEBAR_BG);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnLogout.setBorder(new EmptyBorder(12, 20, 12, 20));
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnLogout.setBackground(new Color(60, 30, 30)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btnLogout.setBackground(SIDEBAR_BG); }
        });
        btnLogout.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                "Yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                SessionManager.logout();
                dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(8));

        // ── Content area ─────────────────────────────────────────
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(248, 249, 250));

        contentPanel.add(new DashboardPanel(), "dashboard");
        if (SessionManager.isAdmin()) {
            contentPanel.add(new KategoriPanel(), "kategori");
            contentPanel.add(new MenuPanel(),     "menu");
        }
        contentPanel.add(new TransaksiPanel(), "transaksi");
        contentPanel.add(new RiwayatPanel(),   "riwayat");
        if (SessionManager.isAdmin()) {
            contentPanel.add(new AprioriPanel(), "apriori");
            contentPanel.add(new LaporanPanel(), "laporan");
            contentPanel.add(new TokoPanel(),    "toko");
            contentPanel.add(new UserPanel(),    "user");
        }
        contentPanel.add(new ProfilPanel(), "profil");

        root.add(sidebar,      BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);

        // Activate Dashboard by default
        setActive(btnDash, "dashboard");
    }

    private JButton addNav(JPanel sidebar, String icon, String label, String card) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setText("  " + icon + "   " + label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(SIDEBAR_TEXT);
        btn.setBackground(SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        navButtons.add(btn);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeButton) btn.setBackground(SIDEBAR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeButton) btn.setBackground(SIDEBAR_BG);
            }
        });
        btn.addActionListener(e -> setActive(btn, card));
        sidebar.add(btn);
        return btn;
    }

    private void setActive(JButton btn, String card) {
        // Reset all buttons
        for (JButton b : navButtons) {
            b.setBackground(SIDEBAR_BG);
            b.setForeground(SIDEBAR_TEXT);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        // Highlight active
        btn.setBackground(SIDEBAR_ACTIVE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        activeButton = btn;

        cardLayout.show(contentPanel, card);
        // Refresh if Refreshable
        for (Component c : contentPanel.getComponents()) {
            if (c.isVisible() && c instanceof Refreshable) {
                ((Refreshable) c).refresh();
                break;
            }
        }
    }

    private JPanel sectionLabel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(SIDEBAR_BG);
        p.setBorder(new EmptyBorder(12, 20, 3, 20));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(SECTION_TEXT);
        p.add(lbl);
        return p;
    }

    private JSeparator thinDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(44, 62, 80));
        sep.setBackground(SIDEBAR_BG);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    public interface Refreshable {
        void refresh();
    }
}
