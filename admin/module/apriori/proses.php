<?php
session_start();
if (empty($_SESSION['user']) || $_SESSION['user']['role'] !== 'admin') {
    header('Location: ../../../login.php'); exit;
}

require '../../../config.php';
require_once '../../../fungsi/csrf.php';
csrf_guard();

$min_support    = (float)($_POST['min_support']    ?? 0);
$min_confidence = (float)($_POST['min_confidence'] ?? 0);
$tanggal_dari   = trim($_POST['tanggal_dari']   ?? '');  // DATE langsung
$tanggal_sampai = trim($_POST['tanggal_sampai'] ?? '');  // DATE langsung
$id_user        = (int)$_SESSION['user']['id_user'];

if ($min_support <= 0 || $min_support > 1 || $min_confidence <= 0 || $min_confidence > 1) {
    echo '<script>alert("Min Support dan Confidence harus antara 0.01 - 1.00");history.go(-1);</script>';
    exit;
}

// ── 1. Catat proses ──────────────────────────────────────
$sql = 'INSERT INTO apriori_proses
            (id_user, min_support, min_confidence, tanggal_dari, tanggal_sampai, status)
        VALUES (?, ?, ?, ?, ?, "proses")';
$config->prepare($sql)->execute([
    $id_user, $min_support, $min_confidence,
    $tanggal_dari  !== '' ? $tanggal_dari  : null,
    $tanggal_sampai !== '' ? $tanggal_sampai : null,
]);
$id_proses = $config->lastInsertId();

// ── 2. Ambil transaksi sesuai filter ─────────────────────
$sql    = "SELECT dt.id_transaksi, dt.nama_menu
           FROM detail_transaksi dt
           JOIN transaksi t ON t.id_transaksi = dt.id_transaksi
           WHERE 1=1";
$params = [];

if ($tanggal_dari !== '') {
    $sql     .= " AND t.tanggal_transaksi >= ?";
    $params[] = $tanggal_dari;
}
if ($tanggal_sampai !== '') {
    $sql     .= " AND t.tanggal_transaksi <= ?";
    $params[] = $tanggal_sampai;
}

$stmt = $config->prepare($sql);
$stmt->execute($params);
$rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

// ── 3. Susun data per transaksi ──────────────────────────
$transaksi = [];
foreach ($rows as $row) {
    $transaksi[$row['id_transaksi']][] = $row['nama_menu'];
}
$total_transaksi = count($transaksi);

if ($total_transaksi === 0) {
    $config->prepare('UPDATE apriori_proses SET status="gagal", total_transaksi=0 WHERE id_proses=?')
           ->execute([$id_proses]);
    echo '<script>alert("Tidak ada data transaksi pada periode tersebut");history.go(-1);</script>';
    exit;
}

// ── 4. C1 ────────────────────────────────────────────────
$c1 = [];
foreach ($transaksi as $items) {
    foreach (array_unique($items) as $item) {
        $c1[$item] = ($c1[$item] ?? 0) + 1;
    }
}
$l1 = array_filter($c1, fn($count) => ($count / $total_transaksi) >= $min_support);
arsort($l1);

foreach ($l1 as $item => $count) {
    $sup = $count / $total_transaksi;
    $config->prepare('INSERT INTO apriori_itemset (id_proses, itemset, ukuran, jumlah, support, support_pct)
                      VALUES (?, ?, 1, ?, ?, ?)')
           ->execute([$id_proses, $item, $count, $sup, $sup * 100]);
}

// ── 5. C2 ────────────────────────────────────────────────
$l1_keys = array_keys($l1);
$l2 = [];

for ($i = 0; $i < count($l1_keys); $i++) {
    for ($j = $i + 1; $j < count($l1_keys); $j++) {
        $pair = [$l1_keys[$i], $l1_keys[$j]];
        sort($pair);
        $key = implode('||', $pair);
        $count = 0;
        foreach ($transaksi as $items) {
            $items = array_unique($items);
            if (in_array($pair[0], $items) && in_array($pair[1], $items)) $count++;
        }
        if (($count / $total_transaksi) >= $min_support) {
            $l2[$key] = $count;
            $sup = $count / $total_transaksi;
            $config->prepare('INSERT INTO apriori_itemset (id_proses, itemset, ukuran, jumlah, support, support_pct)
                              VALUES (?, ?, 2, ?, ?, ?)')
                   ->execute([$id_proses, implode(', ', $pair), $count, $sup, $sup * 100]);
        }
    }
}

// ── 6. C3 ────────────────────────────────────────────────
$l2_keys = array_keys($l2);
$l3 = [];

for ($i = 0; $i < count($l2_keys); $i++) {
    for ($j = $i + 1; $j < count($l2_keys); $j++) {
        $union = array_unique(array_merge(explode('||', $l2_keys[$i]), explode('||', $l2_keys[$j])));
        if (count($union) !== 3) continue;
        sort($union);
        $key = implode('||', $union);
        if (isset($l3[$key])) continue;
        $count = 0;
        foreach ($transaksi as $items) {
            $items = array_unique($items);
            if (count(array_intersect($union, $items)) === 3) $count++;
        }
        if (($count / $total_transaksi) >= $min_support) {
            $l3[$key] = $count;
            $sup = $count / $total_transaksi;
            $config->prepare('INSERT INTO apriori_itemset (id_proses, itemset, ukuran, jumlah, support, support_pct)
                              VALUES (?, ?, 3, ?, ?, ?)')
                   ->execute([$id_proses, implode(', ', $union), $count, $sup, $sup * 100]);
        }
    }
}

// ── 7. Association Rules ─────────────────────────────────
$rules = [];

foreach ($l2 as $key => $count) {
    [$a, $b] = explode('||', $key);
    $sup = $count / $total_transaksi;
    foreach ([[$a,$b], [$b,$a]] as [$ant, $con]) {
        $conf = $count / $c1[$ant];
        if ($conf >= $min_confidence) {
            $rules[] = [
                'antecedent'     => $ant,
                'consequent'     => $con,
                'support'        => $sup,
                'support_pct'    => $sup * 100,
                'confidence'     => $conf,
                'confidence_pct' => $conf * 100,
                'lift'           => $conf / ($c1[$con] / $total_transaksi),
            ];
        }
    }
}

foreach ($l3 as $key => $count) {
    $items = explode('||', $key);
    $sup   = $count / $total_transaksi;
    foreach ($items as $idx => $con) {
        $ant_arr = array_values(array_filter($items, fn($v, $k) => $k !== $idx, ARRAY_FILTER_USE_BOTH));
        $ant_count = 0;
        foreach ($transaksi as $trx) {
            $trx = array_unique($trx);
            if (count(array_intersect($ant_arr, $trx)) === count($ant_arr)) $ant_count++;
        }
        if ($ant_count === 0) continue;
        $conf = $count / $ant_count;
        if ($conf >= $min_confidence) {
            $rules[] = [
                'antecedent'     => implode(', ', $ant_arr),
                'consequent'     => $con,
                'support'        => $sup,
                'support_pct'    => $sup * 100,
                'confidence'     => $conf,
                'confidence_pct' => $conf * 100,
                'lift'           => $conf / ($c1[$con] / $total_transaksi),
            ];
        }
    }
}

// ── 8. Simpan ke hasil_apriori  ← nama tabel sesuai DB ──
foreach ($rules as $rule) {
    $config->prepare('INSERT INTO hasil_apriori
                        (id_proses, antecedent, consequent, support, support_pct, confidence, confidence_pct, lift)
                      VALUES (?, ?, ?, ?, ?, ?, ?, ?)')
           ->execute([
               $id_proses,
               $rule['antecedent'], $rule['consequent'],
               $rule['support'],    $rule['support_pct'],
               $rule['confidence'], $rule['confidence_pct'],
               $rule['lift'],
           ]);
}

// ── 9. Selesai ───────────────────────────────────────────
$config->prepare('UPDATE apriori_proses SET status="selesai", total_transaksi=? WHERE id_proses=?')
       ->execute([$total_transaksi, $id_proses]);

echo '<script>window.location="../../../index.php?page=apriori&success=proses&id='.$id_proses.'"</script>';