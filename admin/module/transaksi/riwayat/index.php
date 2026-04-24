<?php
$showSuccess = is_string(filter_input(INPUT_GET, 'success', FILTER_UNSAFE_RAW)) && filter_input(INPUT_GET, 'success', FILTER_UNSAFE_RAW) !== '';
$id_user = $_SESSION['user']['id_user'];
?>

<h4>Riwayat Transaksi</h4>
<br />

<?php if($showSuccess){?>
<div class="alert alert-success"><p>Transaksi Berhasil Disimpan !</p></div>
<?php }?>

<?php if($showSuccess){ ?>
<script>
    history.replaceState(null, '', '?page=transaksi/riwayat');
    setTimeout(() => {
        document.querySelectorAll('.alert').forEach(el => $(el).alert('close'));
    }, 3000);
</script>
<?php } ?>

<div class="card card-body">
    <div class="table-responsive">
        <table class="table table-bordered table-striped table-sm" id="example1">
            <thead>
                <tr style="background:#DFF0D8;color:#333;">
                    <th>No.</th>
                    <th>Kode Transaksi</th>
                    <th>Tanggal</th>
                    <th>Total Item</th>
                    <th>Total Harga</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody>
                <?php
                $stmt = $config->prepare('
                    SELECT t.*, COUNT(dt.id_detail) as total_item
                    FROM transaksi t
                    LEFT JOIN detail_transaksi dt ON t.id_transaksi = dt.id_transaksi
                    WHERE t.id_user = ?
                    GROUP BY t.id_transaksi
                    ORDER BY t.created_at DESC
                ');
                $stmt->execute([$id_user]);
                $hasil = $stmt->fetchAll(PDO::FETCH_ASSOC);
                $no = 1;

                if (empty($hasil)) {
                    echo '<tr><td colspan="7" class="text-center text-muted">Belum ada transaksi</td></tr>';
                } else {
                    foreach($hasil as $isi){ ?>
                    <tr>
                        <td><?= $no++; ?></td>
                        <td><strong><?= htmlspecialchars($isi['kode_transaksi'], ENT_QUOTES, 'UTF-8'); ?></strong></td>
                        <td><?= htmlspecialchars($isi['tanggal_transaksi'], ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?= htmlspecialchars($isi['total_item'] ?? 0, ENT_QUOTES, 'UTF-8'); ?></td>
                        <td>Rp. <?= number_format($isi['total'], 0, ',', '.'); ?></td>
                        <td>
                            <button class="btn btn-info btn-sm" data-toggle="modal" data-target="#detailModal"
                                onclick="lihatDetail(<?= $isi['id_transaksi']; ?>)">
                                <i class="fa fa-eye mr-1"></i> Lihat
                            </button>
                        </td>
                    </tr>
                    <?php }
                }
                ?>
            </tbody>
        </table>
    </div>
</div>

<!-- Modal Detail -->
<div class="modal fade" id="detailModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Detail Transaksi</h5>
                <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
            </div>
            <div class="modal-body" id="detailContent">
                <div class="text-center">
                    <div class="spinner-border" role="status">
                        <span class="sr-only">Loading...</span>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Tutup</button>
            </div>
        </div>
    </div>
</div>

<script>
function lihatDetail(idTransaksi) {
    const content = document.getElementById('detailContent');

    // Fetch data dengan AJAX
    fetch('admin/module/transaksi/riwayat/detail.php?id=' + idTransaksi)
        .then(response => response.text())
        .then(html => {
            content.innerHTML = html;
        })
        .catch(error => {
            content.innerHTML = '<div class="alert alert-danger">Error loading detail</div>';
        });
}
</script>
