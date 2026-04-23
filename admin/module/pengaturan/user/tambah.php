<?php
session_start();
if (empty($_SESSION['user'])) {
    header('Location: /login.php');
    exit;
}

// PATH AMAN
require __DIR__ . '/../../../../config.php';
require __DIR__ . '/../../../../fungsi/csrf.php';

// VALIDASI CSRF
csrf_guard();

// ================== AMBIL INPUT ==================
$username = trim($_POST['username'] ?? '');
$password = $_POST['password'] ?? '';
$nama     = trim($_POST['nama'] ?? '');
$email    = trim($_POST['email'] ?? '');
$telepon  = trim($_POST['telepon'] ?? '');
$nik      = trim($_POST['nik'] ?? '');
$alamat   = trim($_POST['alamat'] ?? '');
$role     = $_POST['role'] ?? 'kasir';

// ================== VALIDASI ==================
if ($username === '' || $password === '' || $nama === '' || $email === '') {
    header("Location: /index.php?page=pengaturan/user&error=1");
    exit;
}

// ================== CEK DUPLIKAT ==================
$cek = $config->prepare("SELECT id_user FROM users WHERE username = ?");
$cek->execute([$username]);

if ($cek->fetch()) {
    header("Location: /index.php?page=pengaturan/user&error=duplicate");
    exit;
}

// ================== HASH PASSWORD ==================
$hashPassword = password_hash($password, PASSWORD_DEFAULT);

// ================== INSERT ==================
$sql = "INSERT INTO users
        (username, password, nama, email, telepon, NIK, alamat, role)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

$config->prepare($sql)->execute([
    $username,
    $hashPassword,
    $nama,
    $email,
    $telepon,
    $nik,
    $alamat,
    $role
]);

// ================== REDIRECT ==================
header("Location: /index.php?page=pengaturan/user&success=1");
exit;