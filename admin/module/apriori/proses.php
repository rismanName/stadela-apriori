<?php
session_start();
if (empty($_SESSION['user'])) { header('Location: ../../../login.php'); exit; }

require '../../../config.php';
require_once '../../../fungsi/csrf.php';
csrf_guard();

$id_user = $_SESSION['user']['id_user'];
$min_support = filter_input(INPUT_POST, 'min_support', FILTER_SANITIZE_NUMBER_FLOAT, ['flags' => FILTER_FLAG_ALLOW_FRACTION]);
$min_confidence = filter_input(INPUT_POST, 'min_confidence', FILTER_SANITIZE_NUMBER_FLOAT, ['flags' => FILTER_FLAG_ALLOW_FRACTION]);
$periode_dari = trim(strip_tags($_POST['periode_dari'] ?? ''));
$periode_sampai = trim(strip_tags($_POST['periode_sampai'] ?? ''));

if ($min_support === false || $min_confidence === false || $min_support <= 0 || $min_confidence <= 0) {
    echo '<script>alert("Parameter tidak valid");history.go(-1);</script>';
    exit;
}

// Convert percentage to decimal
$min_support_decimal = $min_support / 100;
$min_confidence_decimal = $min_confidence / 100;

// Insert into apriori_proses
$sql = 'INSERT INTO apriori_proses (id_user, min_support, min_confidence, periode_dari, periode_sampai, status, tgl_proses)
        VALUES (?, ?, ?, ?, ?, ?, NOW())';
$stmt = $config->prepare($sql);
$stmt->execute([$id_user, $min_support_decimal, $min_confidence_decimal, $periode_dari, $periode_sampai, 'selesai']);

echo '<script>alert("Proses apriori selesai!");window.location="../../../index.php?page=apriori"</script>';
?>
