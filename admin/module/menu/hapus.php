<?php
session_start();
if (empty($_SESSION['user'])) { header('Location: ../../../login.php'); exit; }

require '../../../config.php';
require_once '../../../fungsi/csrf.php';

$csrfToken = filter_input(INPUT_GET, 'csrf_token', FILTER_UNSAFE_RAW);
csrf_require_token($csrfToken ?? '');

$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
if (!$id) {
    echo '<script>alert("Data menu tidak valid");history.go(-1);</script>';
    exit;
}

$config->prepare('DELETE FROM menu WHERE id_menu = ?')->execute([$id]);
echo '<script>window.location="../../../index.php?page=menu&remove=hapus-data"</script>';