<?php
declare(strict_types=1);

class view
{
    protected PDO $db;

    public function __construct(PDO $db)
    {
        $this->db = $db;
    }

    public function user(): array
    {
        $stmt = $this->db->prepare('SELECT * FROM users ORDER BY nama ASC');
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public function user_edit(int $id): array|false
    {
        $stmt = $this->db->prepare('SELECT * FROM users WHERE id_user = ? LIMIT 1');
        $stmt->execute([$id]);
        return $stmt->fetch(PDO::FETCH_ASSOC);
    }

    public function member_edit(int $id): array|false
    {
        return $this->user_edit($id);
    }

    public function toko(): array|false
    {
        $stmt = $this->db->prepare('SELECT * FROM toko WHERE id_toko = 1 LIMIT 1');
        $stmt->execute();
        return $stmt->fetch(PDO::FETCH_ASSOC);
    }

    public function kategori(): array
    {
        $stmt = $this->db->prepare('SELECT * FROM kategori ORDER BY nama_kategori ASC');
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public function kategori_edit(int $id): array|false
    {
        $stmt = $this->db->prepare('SELECT * FROM kategori WHERE id_kategori = ? LIMIT 1');
        $stmt->execute([$id]);
        return $stmt->fetch(PDO::FETCH_ASSOC);
    }

    public function kategori_row(): int
    {
        $stmt = $this->db->prepare('SELECT COUNT(*) FROM kategori');
        $stmt->execute();
        return (int) $stmt->fetchColumn();
    }

    public function menu(): array
    {
        $stmt = $this->db->prepare('
            SELECT m.*, k.nama_kategori
            FROM menu m
            LEFT JOIN kategori k ON k.id_kategori = m.id_kategori
            ORDER BY m.nama_menu ASC
        ');
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public function menu_stok(): array
    {
        $stmt = $this->db->prepare('
            SELECT m.*, k.nama_kategori
            FROM menu m
            LEFT JOIN kategori k ON k.id_kategori = m.id_kategori
            WHERE m.stok <= 3
            ORDER BY m.stok ASC, m.nama_menu ASC
        ');
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public function menu_edit(int $id): array|false
    {
        $stmt = $this->db->prepare('
            SELECT m.*, k.nama_kategori
            FROM menu m
            LEFT JOIN kategori k ON k.id_kategori = m.id_kategori
            WHERE m.id_menu = ?
            LIMIT 1
        ');
        $stmt->execute([$id]);
        return $stmt->fetch(PDO::FETCH_ASSOC);
    }

    public function menu_cari(string $keyword): array
    {
        $param = '%' . trim($keyword) . '%';
        $stmt = $this->db->prepare('
            SELECT m.*, k.nama_kategori
            FROM menu m
            LEFT JOIN kategori k ON k.id_kategori = m.id_kategori
            WHERE m.kode_menu LIKE ? OR m.nama_menu LIKE ?
            ORDER BY m.nama_menu ASC
        ');
        $stmt->execute([$param, $param]);
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public function menu_id(): string
    {
        $stmt = $this->db->prepare('SELECT id_menu FROM menu ORDER BY id_menu DESC LIMIT 1');
        $stmt->execute();
        $lastId = (int) $stmt->fetchColumn();
        return 'MNU' . str_pad((string) ($lastId + 1), 4, '0', STR_PAD_LEFT);
    }

    public function menu_row(): int
    {
        $stmt = $this->db->prepare('SELECT COUNT(*) FROM menu');
        $stmt->execute();
        return (int) $stmt->fetchColumn();
    }

    public function menu_stok_row(): array
    {
        $stmt = $this->db->prepare('SELECT COALESCE(SUM(stok), 0) AS jml FROM menu');
        $stmt->execute();
        return $stmt->fetch(PDO::FETCH_ASSOC) ?: ['jml' => 0];
    }

    public function menu_beli_row(): array
    {
        $stmt = $this->db->prepare('SELECT COALESCE(SUM(harga_beli), 0) AS beli FROM menu');
        $stmt->execute();
        return $stmt->fetch(PDO::FETCH_ASSOC) ?: ['beli' => 0];
    }

    public function jual_row(): array
    {
        $stmt = $this->db->prepare('SELECT COALESCE(SUM(jumlah), 0) AS stok FROM detail_transaksi');
        $stmt->execute();
        return $stmt->fetch(PDO::FETCH_ASSOC) ?: ['stok' => 0];
    }

    public function jual(): array
    {
        return $this->periode_jual(date('m-Y'));
    }

    public function periode_jual(string $periode): array
    {
        $stmt = $this->db->prepare('
            SELECT
                t.id_transaksi,
                t.kode_transaksi,
                t.total,
                t.tanggal,
                t.periode,
                dt.id_detail,
                dt.id_menu,
                dt.nama_menu,
                dt.jumlah,
                dt.harga_satuan,
                dt.subtotal,
                m.kode_menu,
                m.harga_beli,
                u.nama
            FROM transaksi t
            INNER JOIN detail_transaksi dt ON dt.id_transaksi = t.id_transaksi
            LEFT JOIN menu m ON m.id_menu = dt.id_menu
            INNER JOIN users u ON u.id_user = t.id_user
            WHERE t.periode = ?
            ORDER BY t.tanggal ASC, t.id_transaksi ASC, dt.nama_menu ASC
        ');
        $stmt->execute([$periode]);
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public function hari_jual(string $tanggal): array
    {
        $stmt = $this->db->prepare('
            SELECT
                t.id_transaksi,
                t.kode_transaksi,
                t.total,
                t.tanggal,
                t.periode,
                dt.id_detail,
                dt.id_menu,
                dt.nama_menu,
                dt.jumlah,
                dt.harga_satuan,
                dt.subtotal,
                m.kode_menu,
                m.harga_beli,
                u.nama
            FROM transaksi t
            INNER JOIN detail_transaksi dt ON dt.id_transaksi = t.id_transaksi
            LEFT JOIN menu m ON m.id_menu = dt.id_menu
            INNER JOIN users u ON u.id_user = t.id_user
            WHERE t.tanggal = ?
            ORDER BY t.tanggal ASC, t.id_transaksi ASC, dt.nama_menu ASC
        ');
        $stmt->execute([$tanggal]);
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public function penjualan(): array
    {
        $stmt = $this->db->prepare('
            SELECT
                t.id_transaksi,
                t.kode_transaksi,
                t.total,
                t.tanggal,
                t.periode,
                dt.id_detail,
                dt.id_menu,
                dt.nama_menu,
                dt.jumlah,
                dt.harga_satuan,
                dt.subtotal,
                m.kode_menu,
                m.harga_beli,
                u.nama
            FROM transaksi t
            INNER JOIN detail_transaksi dt ON dt.id_transaksi = t.id_transaksi
            LEFT JOIN menu m ON m.id_menu = dt.id_menu
            INNER JOIN users u ON u.id_user = t.id_user
            ORDER BY t.tanggal DESC, t.id_transaksi DESC, dt.nama_menu ASC
        ');
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public function jumlah(): array
    {
        $stmt = $this->db->prepare('SELECT COALESCE(SUM(total), 0) AS bayar FROM transaksi');
        $stmt->execute();
        return $stmt->fetch(PDO::FETCH_ASSOC) ?: ['bayar' => 0];
    }

    public function jumlah_nota(): array
    {
        return $this->jumlah();
    }

    public function jml(): array
    {
        $stmt = $this->db->prepare('SELECT COALESCE(SUM(harga_beli * stok), 0) AS byr FROM menu');
        $stmt->execute();
        return $stmt->fetch(PDO::FETCH_ASSOC) ?: ['byr' => 0];
    }
}
