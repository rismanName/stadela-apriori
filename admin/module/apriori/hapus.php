<?php
session_start();
if (empty($_SESSION['user']) || $_SESSION['user']['role'] !== 'admin') {
    header('Location: ../../../login.php'); exit;
}

require '../../../config.php';
require_once '../../../fungsi/csrf.php';

$csrfToken = filter_input(INPUT_GET, 'csrf_token', FILTER_UNSAFE_RAW);
csrf_require_token($csrfToken ?? '');

$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
if (!$id) {
    echo '<script>alert("Data tidak valid");history.go(-1);</script>'; exit;
}

// CASCADE akan otomatis hapus apriori_itemset & apriori_hasil
$config->prepare('DELETE FROM apriori_proses WHERE id_proses = ?')->execute([$id]);
echo '<script>window.location="../../../index.php?page=apriori"</script>';