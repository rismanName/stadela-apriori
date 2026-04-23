<?php
session_start();
if (empty($_SESSION['user'])) { header('Location: ../../../login.php'); exit; }

require '../../../config.php';
require_once '../../../fungsi/csrf.php';

$csrfToken = filter_input(INPUT_GET, 'csrf_token', FILTER_UNSAFE_RAW);
csrf_require_token($csrfToken ?? '');

$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
if (!$id) {
    echo '<script>alert("Data kategori tidak valid");history.go(-1);</script>';
    exit;
}

// Cek apakah kategori masih dipakai di tabel menu
$cek = $config->prepare('SELECT COUNT(*) as total FROM menu WHERE id_kategori = ?');
$cek->execute([$id]);
if ($cek->fetch()['total'] > 0) {
    echo '<script>alert("Kategori masih digunakan oleh data menu!");history.go(-1);</script>';
    exit;
}

$config->prepare('DELETE FROM kategori WHERE id_kategori = ?')->execute([$id]);
echo '<script>window.location="../../../index.php?page=kategori&remove=hapus-data"</script>';