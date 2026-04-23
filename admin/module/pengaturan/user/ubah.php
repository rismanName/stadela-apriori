<?php
session_start();
if (empty($_SESSION['user'])) {
    header('Location: /login.php');
    exit;
}

// PATH AMAN
require __DIR__ . '/../../../../config.php';
require __DIR__ . '/../../../../fungsi/csrf.php';

// CSRF
csrf_guard();

// ================== INPUT ==================
$id       = filter_input(INPUT_POST, 'id', FILTER_VALIDATE_INT);
$username = trim($_POST['username'] ?? '');
$nama     = trim($_POST['nama'] ?? '');
$email    = trim($_POST['email'] ?? '');
$telepon  = trim($_POST['telepon'] ?? '');
$nik      = trim($_POST['nik'] ?? '');
$alamat   = trim($_POST['alamat'] ?? '');
$role     = $_POST['role'] ?? 'kasir';

// ================== VALIDASI ==================
if (!$id || $username === '' || $nama === '' || $email === '') {
    header("Location: /index.php?page=pengaturan/user&error=1");
    exit;
}

// ================== UPDATE ==================
$sql = "UPDATE users SET
            username = ?,
            nama = ?,
            email = ?,
            telepon = ?,
            NIK = ?,
            alamat = ?,
            role = ?
        WHERE id_user = ?";

$config->prepare($sql)->execute([
    $username,
    $nama,
    $email,
    $telepon,
    $nik,
    $alamat,
    $role,
    $id
]);

// ================== REDIRECT ==================
header("Location: /index.php?page=pengaturan/user&success-edit=1");
exit;