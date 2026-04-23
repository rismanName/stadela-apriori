<?php
session_start();
if (empty($_SESSION['user'])) { header('Location: ../../../../login.php'); exit; }

require '../../../../config.php';
require_once '../../../../fungsi/csrf.php';
csrf_guard();

$nama = trim(strip_tags($_POST['namatoko'] ?? ''));
$alamat = trim(strip_tags($_POST['alamat'] ?? ''));
$kontak = trim(strip_tags($_POST['kontak'] ?? ''));
$pemilik = trim(strip_tags($_POST['pemilik'] ?? ''));
$id = '1';

if ($nama === '' || $alamat === '' || $kontak === '' || $pemilik === '') {
    echo '<script>alert("Data toko tidak valid");history.go(-1);</script>';
    exit;
}

$sql = 'UPDATE toko SET nama_toko = ?, alamat_toko = ?, tlp = ?, nama_pemilik = ? WHERE id_toko = ?';
$config->prepare($sql)->execute([$nama, $alamat, $kontak, $pemilik, $id]);
echo '<script>window.location="../../../../index.php?page=pengaturan/toko&success-edit=edit-data"</script>';