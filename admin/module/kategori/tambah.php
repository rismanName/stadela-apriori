<?php
session_start();
if (empty($_SESSION['user'])) { header('Location: ../../../login.php'); exit; }

require '../../../config.php';
require_once '../../../fungsi/csrf.php';
csrf_guard();

$nama = trim(strip_tags($_POST['kategori'] ?? ''));
if ($nama === '') {
    echo '<script>alert("Kategori tidak valid");history.go(-1);</script>';
    exit;
}

$sql = 'INSERT INTO kategori (nama_kategori, tgl_input) VALUES (?, NOW())';
$config->prepare($sql)->execute([$nama]);
echo '<script>window.location="../../../index.php?page=kategori&success=tambah-data"</script>';