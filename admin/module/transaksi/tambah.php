<?php
session_start();
if (empty($_SESSION['user'])) { header('Location: ../../../login.php'); exit; }

require '../../../config.php';
require_once '../../../fungsi/csrf.php';
csrf_guard();

$id_user = $_SESSION['user']['id_user'];
$tanggal = trim(strip_tags($_POST['tanggal'] ?? ''));
$itemsJson = $_POST['items'] ?? '[]';
$items = json_decode($itemsJson, true);

if ($tanggal === '' || empty($items)) {
    echo '<script>alert("Data transaksi tidak valid");history.go(-1);</script>';
    exit;
}

// Validasi tanggal
if (!preg_match('/^\d{4}-\d{2}-\d{2}$/', $tanggal)) {
    echo '<script>alert("Format tanggal tidak valid");history.go(-1);</script>';
    exit;
}

// Generate kode transaksi
$kode = 'TRX' . date('YmdHis') . rand(1000, 9999);
$periode = date('m-Y', strtotime($tanggal));
$total = 0;

// Hitung total dan validasi item
foreach ($items as $item) {
    $id_menu = filter_var($item['id_menu'] ?? 0, FILTER_VALIDATE_INT);
    $jumlah = filter_var($item['jumlah'] ?? 0, FILTER_VALIDATE_INT);

    if (!$id_menu || !$jumlah || $jumlah <= 0) {
        echo '<script>alert("Data item tidak valid");history.go(-1);</script>';
        exit;
    }

    // Ambil data menu
    $stmt = $config->prepare('SELECT * FROM menu WHERE id_menu = ?');
    $stmt->execute([$id_menu]);
    $menu = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$menu) {
        echo '<script>alert("Menu tidak ditemukan");history.go(-1);</script>';
        exit;
    }

    $subtotal = $menu['harga'] * $jumlah;
    $total += $subtotal;
}

try {
    // Start transaction
    $config->beginTransaction();

    // Insert transaksi
    $sql = 'INSERT INTO transaksi (kode_transaksi, id_user, total, tanggal, periode, created_at)
            VALUES (?, ?, ?, ?, ?, NOW())';
    $stmt = $config->prepare($sql);
    $stmt->execute([$kode, $id_user, $total, $tanggal, $periode]);

    // Get last insert ID
    $id_transaksi = $config->lastInsertId();

    // Insert detail transaksi
    foreach ($items as $item) {
        $id_menu = intval($item['id_menu']);
        $jumlah = intval($item['jumlah']);

        $stmt = $config->prepare('SELECT * FROM menu WHERE id_menu = ?');
        $stmt->execute([$id_menu]);
        $menu = $stmt->fetch(PDO::FETCH_ASSOC);

        $harga_satuan = $menu['harga'];
        $subtotal = $harga_satuan * $jumlah;
        $nama_menu = $menu['nama_menu'];

        $sql = 'INSERT INTO detail_transaksi (id_transaksi, id_menu, nama_menu, jumlah, harga_satuan, subtotal)
                VALUES (?, ?, ?, ?, ?, ?)';
        $config->prepare($sql)->execute([$id_transaksi, $id_menu, $nama_menu, $jumlah, $harga_satuan, $subtotal]);
    }

    // Commit transaction
    $config->commit();

    echo '<script>window.location="../../../index.php?page=transaksi&success=tambah-data"</script>';
} catch (Exception $e) {
    $config->rollBack();
    echo '<script>alert("Terjadi kesalahan: ' . addslashes($e->getMessage()) . '");history.go(-1);</script>';
    exit;
}
?>
