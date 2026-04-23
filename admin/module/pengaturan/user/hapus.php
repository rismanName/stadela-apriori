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
$csrfToken = filter_input(INPUT_GET, 'csrf_token', FILTER_UNSAFE_RAW);
csrf_require_token($csrfToken ?? '');

// VALIDASI ID
$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
if (!$id) {
    echo '<script>alert("Data pengguna tidak valid");history.go(-1);</script>';
    exit;
}

// OPTIONAL: cegah hapus diri sendiri
if ($id == $_SESSION['user']['id_user']) {
    echo '<script>alert("Tidak bisa hapus akun sendiri");history.go(-1);</script>';
    exit;
}

// DELETE
$config->prepare('DELETE FROM users WHERE id_user = ?')->execute([$id]);

// REDIRECT (FIX)
header("Location: /index.php?page=pengaturan/user&remove=1");
exit;