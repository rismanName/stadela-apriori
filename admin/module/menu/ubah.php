<?php
session_start();
if (empty($_SESSION['user'])) { header('Location: ../../../login.php'); exit; }

require '../../../config.php';
require_once '../../../fungsi/csrf.php';
csrf_guard();

$id = filter_input(INPUT_POST, 'id', FILTER_VALIDATE_INT);
$kode_menu = trim(strip_tags($_POST['kode_menu'] ?? ''));
$nama_menu = trim(strip_tags($_POST['nama_menu'] ?? ''));
$id_kategori = filter_input(INPUT_POST, 'id_kategori', FILTER_VALIDATE_INT);
$harga = filter_input(INPUT_POST, 'harga', FILTER_SANITIZE_NUMBER_FLOAT, ['flags' => FILTER_FLAG_ALLOW_FRACTION]);
$stok = filter_input(INPUT_POST, 'stok', FILTER_VALIDATE_INT);
$satuan = trim(strip_tags($_POST['satuan'] ?? ''));
$deskripsi = trim(strip_tags($_POST['deskripsi'] ?? ''));

if (!$id || $kode_menu === '' || $nama_menu === '' || !$id_kategori || $harga === false || $stok === false) {
    echo '<script>alert("Data menu tidak valid");history.go(-1);</script>';
    exit;
}

// Cek kode_menu sudah ada di menu lain atau belum
$cek = $config->prepare('SELECT COUNT(*) as total FROM menu WHERE kode_menu = ? AND id_menu != ?');
$cek->execute([$kode_menu, $id]);
if ($cek->fetch()['total'] > 0) {
    echo '<script>alert("Kode menu sudah ada di menu lain!");history.go(-1);</script>';
    exit;
}

$sql = 'UPDATE menu SET id_kategori = ?, kode_menu = ?, nama_menu = ?, harga = ?, stok = ?, satuan = ?, deskripsi = ?, tgl_update = NOW()
        WHERE id_menu = ?';
$config->prepare($sql)->execute([$id_kategori, $kode_menu, $nama_menu, $harga, $stok, $satuan, $deskripsi, $id]);
echo '<script>window.location="../../../index.php?page=menu&success-edit=edit-data"</script>';