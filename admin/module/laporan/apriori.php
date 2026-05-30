<?php
session_start();
if (empty($_SESSION['user']) || $_SESSION['user']['role'] !== 'admin') {
    header('Location: ../../../login.php'); exit;
}

require '../../../config.php';

// Get all apriori processes
$stmt = $config->prepare('SELECT ap.*, u.nama,
                         (SELECT COUNT(*) FROM apriori_itemset WHERE id_proses = ap.id_proses) as jumlah_itemset,
                         (SELECT COUNT(*) FROM hasil_apriori WHERE id_proses = ap.id_proses) as jumlah_rules
                         FROM apriori_proses ap
                         JOIN users u ON u.id_user = ap.id_user
                         ORDER BY ap.created_at DESC');
$stmt->execute();
$laporan_proses = $stmt->fetchAll();

$total_proses = count($laporan_proses);
$proses_berhasil = count(array_filter($laporan_proses, fn($p) => $p['status'] === 'selesai'));
$proses_gagal = count(array_filter($laporan_proses, fn($p) => $p['status'] === 'gagal'));
?>

<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Laporan Analisis Apriori</title>
    <link rel="stylesheet" href="../../../sb-admin/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="../../../sb-admin/vendor/fontawesome-free/css/all.min.css">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            padding: 20px 0;
        }
        .page-header {
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        .page-header h1 {
            color: #333;
            font-weight: 700;
            margin-bottom: 5px;
            font-size: 32px;
        }
        .page-header .breadcrumb {
            background: transparent;
            margin-bottom: 0;
        }
        .stat-card {
            background: white;
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            text-align: center;
            margin-bottom: 20px;
            transition: all 0.3s ease;
        }
        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 16px rgba(0,0,0,0.15);
        }
        .stat-icon {
            font-size: 32px;
            margin-bottom: 10px;
        }
        .stat-icon.green { color: #28a745; }
        .stat-icon.red { color: #dc3545; }
        .stat-icon.blue { color: #007bff; }
        .stat-value {
            font-size: 28px;
            font-weight: 700;
            color: #333;
        }
        .stat-label {
            color: #999;
            font-size: 13px;
            text-transform: uppercase;
            font-weight: 600;
        }
        .container { max-width: 1200px; margin: 0 auto; }
        .table-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            overflow: hidden;
        }
        .table-card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            font-weight: 600;
            font-size: 16px;
        }
        .table-card-header i {
            margin-right: 10px;
        }
        .table {
            margin-bottom: 0;
        }
        .table thead th {
            background: #f8f9fa;
            color: #333;
            font-weight: 600;
            border-bottom: 2px solid #dee2e6;
            padding: 12px;
            font-size: 13px;
            text-transform: uppercase;
        }
        .table tbody td {
            padding: 12px;
            vertical-align: middle;
            border-color: #e9ecef;
        }
        .table tbody tr {
            border-bottom: 1px solid #e9ecef;
            transition: all 0.3s ease;
        }
        .table tbody tr:hover {
            background: #f8f9fa;
            box-shadow: inset 4px 0 0 #667eea;
        }
        .badge {
            padding: 6px 12px;
            font-size: 12px;
            font-weight: 600;
            border-radius: 4px;
        }
        .badge-selesai {
            background: #d4edda;
            color: #155724;
            border: 1px solid #28a745;
        }
        .badge-gagal {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #dc3545;
        }
        .badge-proses {
            background: #fff3cd;
            color: #856404;
            border: 1px solid #ffc107;
        }
        .action-btn {
            padding: 5px 10px;
            font-size: 12px;
            margin: 0 3px;
            border-radius: 4px;
            transition: all 0.3s ease;
        }
        .action-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
        }
        .no-data {
            text-align: center;
            padding: 40px;
            color: #999;
        }
        .no-data i {
            font-size: 48px;
            margin-bottom: 15px;
            opacity: 0.5;
        }
        .parameter-badge {
            display: inline-block;
            background: #f0f0f0;
            padding: 4px 8px;
            border-radius: 3px;
            font-size: 11px;
            margin-right: 5px;
        }
        @media (max-width: 768px) {
            .page-header { padding: 20px; }
            .page-header h1 { font-size: 24px; }
            .stat-card { margin-bottom: 15px; }
            .table { font-size: 12px; }
            .table thead th, .table tbody td { padding: 8px; }
            .action-btn { padding: 4px 6px; font-size: 11px; margin: 2px; }
        }
        @media print {
            body { background: white; }
            .page-header { box-shadow: none; border: 1px solid #ddd; }
            .stat-card { box-shadow: none; border: 1px solid #ddd; }
            .table-card { box-shadow: none; border: 1px solid #ddd; }
            .action-btn { display: none; }
        }
    </style>
</head>
<body>
<div class="container">
    <!-- Page Header -->
    <div class="page-header">
        <div class="d-flex justify-content-between align-items-start">
            <div>
                <h1><i class="fas fa-chart-line mr-3"></i>Laporan Analisis Apriori</h1>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="index.php?page=home">Dashboard</a></li>
                        <li class="breadcrumb-item"><a href="index.php?page=apriori">Apriori</a></li>
                        <li class="breadcrumb-item active">Laporan</li>
                    </ol>
                </nav>
            </div>
            <div>
                <button onclick="window.print()" class="btn btn-sm btn-secondary mr-2">
                    <i class="fas fa-print mr-1"></i> Print
                </button>
                <a href="index.php?page=apriori" class="btn btn-sm btn-primary">
                    <i class="fas fa-arrow-left mr-1"></i> Kembali
                </a>
            </div>
        </div>
    </div>

    <!-- Statistics -->
    <div class="row mb-4">
        <div class="col-md-3">
            <div class="stat-card">
                <div class="stat-icon blue">
                    <i class="fas fa-flask-vial"></i>
                </div>
                <div class="stat-value"><?= $total_proses; ?></div>
                <div class="stat-label">Total Proses</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stat-card">
                <div class="stat-icon green">
                    <i class="fas fa-check-circle"></i>
                </div>
                <div class="stat-value"><?= $proses_berhasil; ?></div>
                <div class="stat-label">Proses Berhasil</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stat-card">
                <div class="stat-icon red">
                    <i class="fas fa-times-circle"></i>
                </div>
                <div class="stat-value"><?= $proses_gagal; ?></div>
                <div class="stat-label">Proses Gagal</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stat-card">
                <div class="stat-icon">
                    <i class="fas fa-project-diagram" style="color: #ffc107;"></i>
                </div>
                <div class="stat-value"><?= array_sum(array_column($laporan_proses, 'jumlah_rules')); ?></div>
                <div class="stat-label">Total Rules</div>
            </div>
        </div>
    </div>

    <!-- Table -->
    <div class="table-card">
        <div class="table-card-header">
            <i class="fas fa-table"></i>Daftar Proses Analisis
        </div>
        <div class="table-responsive">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th width="8%">ID</th>
                        <th width="15%">User</th>
                        <th width="15%">Status</th>
                        <th width="10%">Parameter</th>
                        <th width="12%">Itemset</th>
                        <th width="12%">Rules</th>
                        <th width="15%">Tanggal</th>
                        <th width="13%">Aksi</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if(!empty($laporan_proses)): ?>
                        <?php foreach($laporan_proses as $proses): ?>
                        <tr>
                            <td>
                                <strong>#<?= $proses['id_proses']; ?></strong>
                            </td>
                            <td>
                                <small><?= htmlspecialchars($proses['nama']); ?></small>
                            </td>
                            <td>
                                <span class="badge badge-<?= strtolower($proses['status']); ?>">
                                    <i class="fas fa-<?= $proses['status'] === 'selesai' ? 'check-circle' : ($proses['status'] === 'gagal' ? 'times-circle' : 'spinner'); ?> mr-1"></i>
                                    <?= ucfirst($proses['status']); ?>
                                </span>
                            </td>
                            <td>
                                <span class="parameter-badge" title="Min. Support">S: <?= ($proses['min_support'] * 100); ?>%</span>
                                <span class="parameter-badge" title="Min. Confidence">C: <?= ($proses['min_confidence'] * 100); ?>%</span>
                            </td>
                            <td>
                                <span class="badge" style="background: #e7f3ff; color: #0066cc;">
                                    <i class="fas fa-boxes mr-1"></i><?= $proses['jumlah_itemset']; ?>
                                </span>
                            </td>
                            <td>
                                <span class="badge" style="background: #f0e7ff; color: #5a3db8;">
                                    <i class="fas fa-link mr-1"></i><?= $proses['jumlah_rules']; ?>
                                </span>
                            </td>
                            <td>
                                <small class="text-muted">
                                    <?= date('d/m/Y H:i', strtotime($proses['created_at'])); ?>
                                </small>
                            </td>
                            <td>
                                <?php if($proses['status'] === 'selesai'): ?>
                                <a href="../apriori/laporan.php?id=<?= $proses['id_proses']; ?>" class="btn btn-sm btn-primary action-btn" target="_blank" title="Lihat Laporan Detail">
                                    <i class="fas fa-eye"></i>
                                </a>
                                <button class="btn btn-sm btn-success action-btn" onclick="exportCSV(<?= $proses['id_proses']; ?>)" title="Export CSV">
                                    <i class="fas fa-download"></i>
                                </button>
                                <?php endif; ?>
                            </td>
                        </tr>
                        <?php endforeach; ?>
                    <?php else: ?>
                        <tr>
                            <td colspan="8" class="no-data">
                                <div>
                                    <i class="fas fa-inbox"></i>
                                    <p>Belum ada proses analisis Apriori</p>
                                </div>
                            </td>
                        </tr>
                    <?php endif; ?>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Footer -->
    <div style="text-align: center; margin-top: 30px; color: white;">
        <small>Laporan Analisis Apriori — <?= date('Y'); ?></small>
    </div>
</div>

<script src="../../../sb-admin/vendor/jquery/jquery.min.js"></script>
<script src="../../../sb-admin/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script>
function exportCSV(id_proses) {
    window.location.href = '../apriori/laporan.php?id=' + id_proses + '&format=csv';
}
</script>
</body>
</html>
