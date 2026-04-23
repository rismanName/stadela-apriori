<?php
session_start();
if (empty($_SESSION['user'])) { header('Location: ../../../login.php'); exit; }

require '../../../config.php';
require_once '../../../fungsi/csrf.php';
csrf_guard();

$id   = filter_input(INPUT_POST, 'id', FILTER_VALIDATE_INT);
$nama = trim(strip_tags($_POST['kategori'] ?? ''));

if (!$id || $nama === '') {
    echo '<script>alert("Data kategori tidak valid");history.go(-1);</script>';
    exit;
}

$sql = 'UPDATE kategori SET nama_kategori = ? WHERE id_kategori = ?';
$config->prepare($sql)->execute([$nama, $id]);
echo '<script>window.location="../../../index.php?page=kategori&success-edit=edit-data"</script>';