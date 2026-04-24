<?php
?>
<h4>Proses Apriori</h4>
<br />

<div class="row">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h5>Konfigurasi Apriori</h5>
            </div>
            <div class="card-body">
                <form method="post" action="admin/module/apriori/proses.php">
                    <?php echo csrf_field(); ?>

                    <div class="form-group">
                        <label>Minimum Support (%) <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" name="min_support"
                            placeholder="Contoh: 10" min="0.1" max="100" step="0.1" required>
                        <small class="form-text text-muted">Persentase minimum support untuk itemset</small>
                    </div>

                    <div class="form-group">
                        <label>Minimum Confidence (%) <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" name="min_confidence"
                            placeholder="Contoh: 50" min="0.1" max="100" step="0.1" required>
                        <small class="form-text text-muted">Persentase minimum confidence untuk rules</small>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Periode Dari (MM-YYYY)</label>
                                <input type="text" class="form-control" name="periode_dari"
                                    placeholder="Contoh: 01-2024">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Periode Sampai (MM-YYYY)</label>
                                <input type="text" class="form-control" name="periode_sampai"
                                    placeholder="Contoh: 03-2024">
                            </div>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary">
                        <i class="fa fa-play mr-1"></i> Jalankan Proses Apriori
                    </button>
                </form>
            </div>
        </div>
    </div>

    <div class="col-md-4">
        <div class="card bg-light">
            <div class="card-header">
                <h6>Informasi</h6>
            </div>
            <div class="card-body">
                <p><strong>Apriori Algorithm</strong> adalah algoritma data mining untuk menemukan frequent itemsets dan association rules.</p>
                <hr>
                <p><strong>Support:</strong> Proporsi transaksi yang mengandung itemset</p>
                <p><strong>Confidence:</strong> Seberapa sering item B muncul saat A muncul</p>
                <p><strong>Lift:</strong> Kekuatan hubungan antar item</p>
            </div>
        </div>
    </div>
</div>

<hr>

<div class="card mt-4">
    <div class="card-header bg-secondary text-white">
        <h5>Riwayat Proses</h5>
    </div>
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-bordered table-striped table-sm" id="example1">
                <thead>
                    <tr style="background:#DFF0D8;color:#333;">
                        <th>No.</th>
                        <th>Support (%)</th>
                        <th>Confidence (%)</th>
                        <th>Periode</th>
                        <th>Total Transaksi</th>
                        <th>Status</th>
                        <th>Tanggal Proses</th>
                        <th>Aksi</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td colspan="8" class="text-center text-muted">Belum ada proses apriori</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
