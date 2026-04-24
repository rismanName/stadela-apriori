<?php
session_start();
if (empty($_SESSION['user'])) { exit('Unauthorized'); }

require __DIR__ . '/../../../../config.php';

$id_transaksi = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);

if (!$id_transaksi) {
    echo '<div class="alert alert-danger">ID Transaksi tidak valid</div>';
    exit;
}

// Get transaksi data
$stmt = $config->prepare('SELECT * FROM transaksi WHERE id_transaksi = ?');
$stmt->execute([$id_transaksi]);
$transaksi = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$transaksi) {
    echo '<div class="alert alert-danger">Transaksi tidak ditemukan</div>';
    exit;
}

// Get detail transaksi
$stmt = $config->prepare('SELECT * FROM detail_transaksi WHERE id_transaksi = ? ORDER BY id_detail ASC');
$stmt->execute([$id_transaksi]);
$details = $stmt->fetchAll(PDO::FETCH_ASSOC);
?>

<div class="row mb-3">
    <div class="col-md-6">
        <table class="table table-sm">
            <tr>
                <td><strong>Kode Transaksi</strong></td>
                <td><?= htmlspecialchars($transaksi['kode_transaksi'], ENT_QUOTES, 'UTF-8'); ?></td>
            </tr>
            <tr>
                <td><strong>Tanggal</strong></td>
                <td><?= htmlspecialchars($transaksi['tanggal'], ENT_QUOTES, 'UTF-8'); ?></td>
            </tr>
            <tr>
                <td><strong>Periode</strong></td>
                <td><?= htmlspecialchars($transaksi['periode'], ENT_QUOTES, 'UTF-8'); ?></td>
            </tr>
        </table>
    </div>
</div>

<hr>

<h6 class="mb-3"><strong>Detail Item</strong></h6>

<div class="table-responsive">
    <table class="table table-bordered table-sm">
        <thead>
            <tr style="background:#f8f9fa;">
                <th>No.</th>
                <th>Nama Menu</th>
                <th>Jumlah</th>
                <th>Harga Satuan</th>
                <th>Subtotal</th>
            </tr>
        </thead>
        <tbody>
            <?php
            $no = 1;
            $total_item = 0;
            foreach($details as $detail) {
                $total_item += $detail['jumlah'];
            ?>
            <tr>
                <td><?= $no++; ?></td>
                <td><?= htmlspecialchars($detail['nama_menu'], ENT_QUOTES, 'UTF-8'); ?></td>
                <td><?= $detail['jumlah']; ?></td>
                <td>Rp. <?= number_format($detail['harga_satuan'], 0, ',', '.'); ?></td>
                <td>Rp. <?= number_format($detail['subtotal'], 0, ',', '.'); ?></td>
            </tr>
            <?php } ?>
            <tr style="background:#f8f9fa; font-weight: bold;">
                <td colspan="2" class="text-right">Total:</td>
                <td><?= $total_item; ?></td>
                <td colspan="2" class="text-right">Rp. <?= number_format($transaksi['total'], 0, ',', '.'); ?></td>
            </tr>
        </tbody>
    </table>
</div>
