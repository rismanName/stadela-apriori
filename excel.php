<?php
declare(strict_types=1);
@ob_start();
session_start();

if (empty($_SESSION['user'])) {
    header('Location: login.php');
    exit;
}

require 'config.php';

// Set headers untuk Excel/XLS export
header("Content-Type: application/vnd.ms-excel; charset=utf-8");
header("Content-Disposition: attachment; filename=Laporan_" . date('YmdHis') . ".xls");
header("Expires: 0");
header("Cache-Control: must-revalidate, post-check=0, pre-check=0");
header("Cache-Control: private", false);

// Get filter parameters
$cariParamRaw = filter_input(INPUT_GET, 'cari', FILTER_UNSAFE_RAW, ['flags' => FILTER_FLAG_NO_ENCODE_QUOTES]);
$cariParam = is_string($cariParamRaw) ? trim($cariParamRaw) : '';
$cariActive = in_array($cariParam, ['yes', 'ok'], true);

$hariParamRaw = filter_input(INPUT_GET, 'hari', FILTER_UNSAFE_RAW, ['flags' => FILTER_FLAG_NO_ENCODE_QUOTES]);
$hariParam = is_string($hariParamRaw) ? trim($hariParamRaw) : '';
$hariActive = ($hariParam === 'cek');

$bulanRaw = filter_input(INPUT_GET, 'bln', FILTER_UNSAFE_RAW, ['flags' => FILTER_FLAG_NO_ENCODE_QUOTES]);
$bulanParam = (is_string($bulanRaw) && preg_match('/^(0[1-9]|1[0-2])$/', $bulanRaw)) ? $bulanRaw : '';

$tahunRaw = filter_input(INPUT_GET, 'thn', FILTER_UNSAFE_RAW, ['flags' => FILTER_FLAG_NO_ENCODE_QUOTES]);
$tahunParam = (is_string($tahunRaw) && preg_match('/^\d{4}$/', $tahunRaw)) ? $tahunRaw : '';

$tanggalRaw = filter_input(INPUT_GET, 'tgl', FILTER_UNSAFE_RAW, ['flags' => FILTER_FLAG_NO_ENCODE_QUOTES]);
$tanggalParam = (is_string($tanggalRaw) && preg_match('/^\d{4}-\d{2}-\d{2}$/', $tanggalRaw)) ? $tanggalRaw : '';

// Query data
$hasil = [];
$jumlah = 0;
$bayar = 0;
$modal = 0;

if ($cariActive && $bulanParam && $tahunParam) {
    // Filter by month and year
    $stmt = $config->prepare("
        SELECT
            t.id_transaksi,
            t.kode_transaksi,
            t.tanggal_transaksi,
            dt.id_menu,
            dt.nama_menu,
            dt.jumlah,
            dt.harga_satuan,
            dt.subtotal,
            u.nama as kasir_nama
        FROM transaksi t
        INNER JOIN detail_transaksi dt ON t.id_transaksi = dt.id_transaksi
        INNER JOIN users u ON t.id_user = u.id_user
        WHERE YEAR(t.tanggal_transaksi) = ? AND MONTH(t.tanggal_transaksi) = ?
        ORDER BY t.tanggal_transaksi DESC, t.id_transaksi DESC
    ");
    $stmt->execute([intval($tahunParam), intval($bulanParam)]);
    $hasil = $stmt->fetchAll(PDO::FETCH_ASSOC);
} elseif ($hariActive && $tanggalParam) {
    // Filter by specific date
    $stmt = $config->prepare("
        SELECT
            t.id_transaksi,
            t.kode_transaksi,
            t.tanggal_transaksi,
            dt.id_menu,
            dt.nama_menu,
            dt.jumlah,
            dt.harga_satuan,
            dt.subtotal,
            u.nama as kasir_nama
        FROM transaksi t
        INNER JOIN detail_transaksi dt ON t.id_transaksi = dt.id_transaksi
        INNER JOIN users u ON t.id_user = u.id_user
        WHERE DATE(t.tanggal_transaksi) = ?
        ORDER BY t.tanggal_transaksi DESC, t.id_transaksi DESC
    ");
    $stmt->execute([$tanggalParam]);
    $hasil = $stmt->fetchAll(PDO::FETCH_ASSOC);
} else {
    // Default: current month
    $stmt = $config->prepare("
        SELECT
            t.id_transaksi,
            t.kode_transaksi,
            t.tanggal_transaksi,
            dt.id_menu,
            dt.nama_menu,
            dt.jumlah,
            dt.harga_satuan,
            dt.subtotal,
            u.nama as kasir_nama
        FROM transaksi t
        INNER JOIN detail_transaksi dt ON t.id_transaksi = dt.id_transaksi
        INNER JOIN users u ON t.id_user = u.id_user
        WHERE YEAR(t.tanggal_transaksi) = YEAR(CURDATE()) AND MONTH(t.tanggal_transaksi) = MONTH(CURDATE())
        ORDER BY t.tanggal_transaksi DESC, t.id_transaksi DESC
    ");
    $stmt->execute();
    $hasil = $stmt->fetchAll(PDO::FETCH_ASSOC);
}

// Output HTML table format (compatible with Excel import)
echo "<table border='1' cellpadding='5' cellspacing='0'>\n";
echo "<thead>\n";
echo "<tr style='background-color:#4472C4; color:white; font-weight:bold;'>\n";
echo "<th>No</th>\n";
echo "<th>Kode Transaksi</th>\n";
echo "<th>Menu</th>\n";
echo "<th>Jumlah</th>\n";
echo "<th>Harga Satuan</th>\n";
echo "<th>Subtotal</th>\n";
echo "<th>Kasir</th>\n";
echo "<th>Tanggal</th>\n";
echo "</tr>\n";
echo "</thead>\n";
echo "<tbody>\n";

// Write data rows
$no = 1;
foreach ($hasil as $isi) {
    $jumlah += intval($isi['jumlah']);
    $bayar += floatval($isi['subtotal']);
    $modal += floatval($isi['harga_satuan']) * intval($isi['jumlah']);

    echo "<tr>\n";
    echo "<td>" . $no . "</td>\n";
    echo "<td>" . htmlspecialchars($isi['kode_transaksi'], ENT_QUOTES, 'UTF-8') . "</td>\n";
    echo "<td>" . htmlspecialchars($isi['nama_menu'], ENT_QUOTES, 'UTF-8') . "</td>\n";
    echo "<td>" . intval($isi['jumlah']) . "</td>\n";
    echo "<td align='right'>" . number_format(floatval($isi['harga_satuan']), 0) . "</td>\n";
    echo "<td align='right'>" . number_format(floatval($isi['subtotal']), 0) . "</td>\n";
    echo "<td>" . htmlspecialchars($isi['kasir_nama'], ENT_QUOTES, 'UTF-8') . "</td>\n";
    echo "<td>" . htmlspecialchars($isi['tanggal_transaksi'], ENT_QUOTES, 'UTF-8') . "</td>\n";
    echo "</tr>\n";

    $no++;
}

// Write summary rows
echo "<tr style='background-color:#E7E6E6; font-weight:bold;'>\n";
echo "<td colspan='3'>Total Terjual</td>\n";
echo "<td>" . $jumlah . "</td>\n";
echo "<td colspan='4'></td>\n";
echo "</tr>\n";

echo "<tr style='background-color:#E7E6E6; font-weight:bold;'>\n";
echo "<td colspan='3'>Modal</td>\n";
echo "<td colspan='2'></td>\n";
echo "<td align='right'>" . number_format($modal, 0) . "</td>\n";
echo "<td colspan='2'></td>\n";
echo "</tr>\n";

echo "<tr style='background-color:#E7E6E6; font-weight:bold;'>\n";
echo "<td colspan='3'>Total Penjualan</td>\n";
echo "<td colspan='2'></td>\n";
echo "<td align='right'>" . number_format($bayar, 0) . "</td>\n";
echo "<td colspan='2'></td>\n";
echo "</tr>\n";

echo "<tr style='background-color:#E7E6E6; font-weight:bold;'>\n";
echo "<td colspan='3'>Keuntungan</td>\n";
echo "<td colspan='2'></td>\n";
echo "<td align='right'>" . number_format($bayar - $modal, 0) . "</td>\n";
echo "<td colspan='2'></td>\n";
echo "</tr>\n";

echo "</tbody>\n";
echo "</table>\n";

exit;