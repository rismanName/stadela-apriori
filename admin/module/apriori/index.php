<?php
$id_proses_param = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
$showSuccess     = filter_input(INPUT_GET, 'success', FILTER_UNSAFE_RAW) !== null;

$riwayat_stmt = $config->prepare('SELECT ap.*, u.nama
                                   FROM apriori_proses ap
                                   JOIN users u ON u.id_user = ap.id_user
                                   ORDER BY ap.created_at DESC');
$riwayat_stmt->execute();
$riwayat = $riwayat_stmt->fetchAll();

$hasil_itemset = [];
$hasil_rules   = [];
$proses_aktif  = null;

if ($id_proses_param) {
    $stmt = $config->prepare('SELECT * FROM apriori_proses WHERE id_proses = ?');
    $stmt->execute([$id_proses_param]);
    $proses_aktif = $stmt->fetch();

    $stmt2 = $config->prepare('SELECT * FROM apriori_itemset WHERE id_proses = ? ORDER BY ukuran, support_pct DESC');
    $stmt2->execute([$id_proses_param]);
    $hasil_itemset = $stmt2->fetchAll();

    $stmt3 = $config->prepare('SELECT * FROM hasil_apriori WHERE id_proses = ? ORDER BY confidence DESC');
    $stmt3->execute([$id_proses_param]);
    $hasil_rules = $stmt3->fetchAll();
}
?>

<h4>Proses Apriori</h4>
<br />

<?php if($showSuccess){ ?>
<div class="alert alert-success">Proses Apriori Berhasil !</div>
<script>
    history.replaceState(null, '', '?page=apriori&id=<?= $id_proses_param ?>');
    setTimeout(() => document.querySelectorAll('.alert').forEach(el => $(el).alert('close')), 3000);
</script>
<?php } ?>

<div class="row">
    <div class="col-md-4">
        <div class="card shadow-sm mb-4">
            <div class="card-header bg-primary text-white">
                <h6 class="mb-0"><i class="fas fa-cogs mr-2"></i>Parameter Apriori</h6>
            </div>
            <div class="card-body">
                <form method="POST" action="admin/module/apriori/proses.php">
                    <?php echo csrf_field(); ?>
                    <div class="form-group">
                        <label>Min. Support <small class="text-muted">(0.01 - 1.00)</small></label>
                        <input type="number" class="form-control" name="min_support"
                            value="0.3" min="0.01" max="1" step="0.01" required>
                        <small class="text-muted">Contoh: 0.3 = 30%</small>
                    </div>
                    <div class="form-group">
                        <label>Min. Confidence <small class="text-muted">(0.01 - 1.00)</small></label>
                        <input type="number" class="form-control" name="min_confidence"
                            value="0.5" min="0.01" max="1" step="0.01" required>
                        <small class="text-muted">Contoh: 0.5 = 50%</small>
                    </div>
                    <div class="form-group">
                        <label>Tanggal Dari</label>
                        <input type="date" class="form-control" name="tanggal_dari">
                    </div>
                    <div class="form-group">
                        <label>Tanggal Sampai</label>
                        <input type="date" class="form-control" name="tanggal_sampai">
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">
                        <i class="fas fa-play mr-1"></i> Jalankan Proses
                    </button>
                </form>
            </div>
        </div>

        <!-- Riwayat Proses -->
        <div class="card shadow-sm">
            <div class="card-header bg-secondary text-white">
                <h6 class="mb-0"><i class="fas fa-history mr-2"></i>Riwayat Proses</h6>
            </div>
            <div class="card-body p-0">
                <div class="list-group list-group-flush">
                <?php foreach($riwayat as $r){ ?>
                    <a href="index.php?page=apriori&id=<?= $r['id_proses']; ?>"
                        class="list-group-item list-group-item-action <?= $id_proses_param == $r['id_proses'] ? 'active' : ''; ?>">
                        <div class="d-flex justify-content-between">
                            <small><b>#<?= $r['id_proses']; ?></b> — <?= htmlspecialchars($r['nama']); ?></small>
                            <span class="badge badge-<?= $r['status'] === 'selesai' ? 'success' : ($r['status'] === 'gagal' ? 'danger' : 'warning'); ?>">
                                <?= $r['status']; ?>
                            </span>
                        </div>
                        <small class="text-muted">
                            Sup: <?= ($r['min_support'] * 100); ?>% |
                            Conf: <?= ($r['min_confidence'] * 100); ?>% |
                            <?= $r['total_transaksi']; ?> transaksi
                        </small><br>
                        <small class="text-muted">
                            <?= $r['tanggal_dari'] ?? 'semua' ?> s/d <?= $r['tanggal_sampai'] ?? 'semua' ?>
                        </small>
                    </a>
                <?php } ?>
                <?php if(empty($riwayat)){ ?>
                    <div class="list-group-item text-muted text-center">Belum ada riwayat</div>
                <?php } ?>
                </div>
            </div>
        </div>
    </div>

    <!-- Hasil -->
    <div class="col-md-8">
        <?php if($proses_aktif){ ?>
        <div class="card shadow-sm mb-3">
            <div class="card-body py-2">
                <div class="row text-center">
                    <div class="col-3">
                        <small class="text-muted d-block">Total Transaksi</small>
                        <b><?= $proses_aktif['total_transaksi']; ?></b>
                    </div>
                    <div class="col-3">
                        <small class="text-muted d-block">Min. Support</small>
                        <b><?= ($proses_aktif['min_support'] * 100); ?>%</b>
                    </div>
                    <div class="col-3">
                        <small class="text-muted d-block">Min. Confidence</small>
                        <b><?= ($proses_aktif['min_confidence'] * 100); ?>%</b>
                    </div>
                    <div class="col-3">
                        <small class="text-muted d-block">Rules Ditemukan</small>
                        <b><?= count($hasil_rules); ?></b>
                    </div>
                </div>
            </div>
        </div>

        <!-- Frequent Itemset -->
        <div class="card shadow-sm mb-3">
            <div class="card-header">
                <h6 class="mb-0"><i class="fas fa-layer-group mr-2"></i>Frequent Itemset</h6>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-sm table-bordered table-striped mb-0">
                        <thead style="background:#DFF0D8;color:#333;">
                            <tr>
                                <th>No.</th>
                                <th>Itemset</th>
                                <th>Ukuran</th>
                                <th>Jumlah</th>
                                <th>Support</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php $no = 1; foreach($hasil_itemset as $item){ ?>
                            <tr>
                                <td><?= $no++; ?></td>
                                <td><?= htmlspecialchars($item['itemset'], ENT_QUOTES, 'UTF-8'); ?></td>
                                <td><span class="badge badge-info">C<?= $item['ukuran']; ?></span></td>
                                <td><?= $item['jumlah']; ?></td>
                                <td><?= number_format($item['support_pct'], 2); ?>%</td>
                            </tr>
                            <?php } ?>
                            <?php if(empty($hasil_itemset)){ ?>
                            <tr><td colspan="5" class="text-center text-muted py-3">Tidak ada itemset yang memenuhi min. support</td></tr>
                            <?php } ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Association Rules -->
        <div class="card shadow-sm">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h6 class="mb-0"><i class="fas fa-project-diagram mr-2"></i>Association Rules</h6>
                <?php if(!empty($hasil_rules)){ ?>
                <a href="admin/module/apriori/hapus.php?id=<?= $id_proses_param; ?>&csrf_token=<?= urlencode(csrf_get_token()); ?>"
                    onclick="return confirm('Hapus hasil proses ini?');"
                    class="btn btn-danger btn-sm">
                    <i class="fas fa-trash mr-1"></i> Hapus Proses
                </a>
                <?php } ?>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-sm table-bordered table-striped mb-0">
                        <thead style="background:#DFF0D8;color:#333;">
                            <tr>
                                <th>No.</th>
                                <th>IF (Antecedent)</th>
                                <th>THEN (Consequent)</th>
                                <th>Support</th>
                                <th>Confidence</th>
                                <th>Lift</th>
                                <th>Keterangan</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php $no = 1; foreach($hasil_rules as $rule){ ?>
                            <tr>
                                <td><?= $no++; ?></td>
                                <td><?= htmlspecialchars($rule['antecedent'], ENT_QUOTES, 'UTF-8'); ?></td>
                                <td><?= htmlspecialchars($rule['consequent'], ENT_QUOTES, 'UTF-8'); ?></td>
                                <td><?= number_format($rule['support_pct'],    2); ?>%</td>
                                <td><?= number_format($rule['confidence_pct'], 2); ?>%</td>
                                <td><?= number_format($rule['lift'], 4); ?></td>
                                <td>
                                    <?php if($rule['lift'] > 1){ ?>
                                        <span class="badge badge-success">Positif</span>
                                    <?php } elseif($rule['lift'] == 1){ ?>
                                        <span class="badge badge-secondary">Independen</span>
                                    <?php } else { ?>
                                        <span class="badge badge-danger">Negatif</span>
                                    <?php } ?>
                                </td>
                            </tr>
                            <?php } ?>
                            <?php if(empty($hasil_rules)){ ?>
                            <tr><td colspan="7" class="text-center text-muted py-3">Tidak ada rule yang memenuhi min. confidence</td></tr>
                            <?php } ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <?php } else { ?>
        <div class="card shadow-sm">
            <div class="card-body text-center py-5 text-muted">
                <i class="fas fa-brain fa-3x mb-3 d-block"></i>
                Pilih riwayat proses atau jalankan proses baru
            </div>
        </div>
        <?php } ?>
    </div>
</div>