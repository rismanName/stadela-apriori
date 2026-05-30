 <?php
// Ambil data laporan penjualan dari database
$bulan_tes = array(
    '01' => "Januari",
    '02' => "Februari",
    '03' => "Maret",
    '04' => "April",
    '05' => "Mei",
    '06' => "Juni",
    '07' => "Juli",
    '08' => "Agustus",
    '09' => "September",
    '10' => "Oktober",
    '11' => "November",
    '12' => "Desember"
);

// Filter parameters
$cariParamRaw = filter_input(INPUT_GET, 'cari', FILTER_UNSAFE_RAW, ['flags' => FILTER_FLAG_NO_ENCODE_QUOTES]);
$cariActive = is_string($cariParamRaw) && $cariParamRaw !== '';

$hariParamRaw = filter_input(INPUT_GET, 'hari', FILTER_UNSAFE_RAW, ['flags' => FILTER_FLAG_NO_ENCODE_QUOTES]);
$hariActive = ($hariParamRaw === 'cek');

$bulanPostRaw = filter_input(INPUT_POST, 'bln', FILTER_UNSAFE_RAW, ['flags' => FILTER_FLAG_NO_ENCODE_QUOTES]);
$bulanPost = (is_string($bulanPostRaw) && preg_match('/^(0[1-9]|1[0-2])$/', $bulanPostRaw)) ? $bulanPostRaw : '';

$tahunPostRaw = filter_input(INPUT_POST, 'thn', FILTER_UNSAFE_RAW, ['flags' => FILTER_FLAG_NO_ENCODE_QUOTES]);
$tahunPost = (is_string($tahunPostRaw) && preg_match('/^\d{4}$/', $tahunPostRaw)) ? $tahunPostRaw : '';

$hariPostRaw = filter_input(INPUT_POST, 'hari', FILTER_UNSAFE_RAW, ['flags' => FILTER_FLAG_NO_ENCODE_QUOTES]);
$hariPost = is_string($hariPostRaw) ? trim($hariPostRaw) : '';

// Query builder untuk laporan
$hasil = [];
$jumlah = 0;
$bayar = 0;
$modal = 0;

if ($cariActive && $bulanPost !== '' && $tahunPost !== '') {
    // Filter by month and year
    $bulanInt = intval($bulanPost);
    $tahunInt = intval($tahunPost);

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
    $stmt->execute([$tahunInt, $bulanInt]);
    $hasil = $stmt->fetchAll(PDO::FETCH_ASSOC);
} elseif ($hariActive && $hariPost !== '') {
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
    $stmt->execute([$hariPost]);
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

// Calculate totals
foreach ($hasil as $isi) {
    $jumlah += intval($isi['jumlah']);
    $bayar += floatval($isi['subtotal']);
    $modal += floatval($isi['harga_satuan']) * intval($isi['jumlah']);
}
?>

<div class="row">
	<div class="col-md-12">
		<h4>
			<!--<a  style="padding-left:2pc;" href="fungsi/hapus/hapus.php?laporan=jual" onclick="javascript:return confirm('Data Laporan akan di Hapus ?');">
						<button class="btn btn-danger">RESET</button>
					</a>-->
                        <?php if($cariActive && $bulanPost !== '' && $tahunPost !== ''){ ?>
                        Data Laporan Penjualan <?= htmlspecialchars($bulan_tes[$bulanPost] ?? $bulanPost, ENT_QUOTES, 'UTF-8');?> <?= htmlspecialchars($tahunPost, ENT_QUOTES, 'UTF-8');?>
                        <?php }elseif($hariActive && $hariPost !== ''){?>
                        Data Laporan Penjualan <?= htmlspecialchars($hariPost, ENT_QUOTES, 'UTF-8');?>
                        <?php }else{?>
                        Data Laporan Penjualan <?= htmlspecialchars($bulan_tes[date('m')], ENT_QUOTES, 'UTF-8');?> <?= date('Y');?>
                        <?php }?>
		</h4>
		<br />
		<div class="card">
			<div class="card-header">
				<h5 class="card-title mt-2">Cari Laporan Per Bulan</h5>
			</div>
			<div class="card-body p-0">
                                <form method="post" action="index.php?page=laporan&cari=ok">
                                        <?php echo csrf_field(); ?>
					<table class="table table-striped">
						<tr>
							<th>
								Pilih Bulan
							</th>
							<th>
								Pilih Tahun
							</th>
							<th>
								Aksi
							</th>
						</tr>
						<tr>
							<td>
								<select name="bln" class="form-control">
									<option selected="selected">Bulan</option>
									<?php
								$bulan=array("Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember");
								$jlh_bln=count($bulan);
								$bln1 = array('01','02','03','04','05','06','07','08','09','10','11','12');
								$no=1;
								for($c=0; $c<$jlh_bln; $c+=1){
									$selected = ($bulanPost === $bln1[$c]) ? 'selected' : '';
									echo"<option value='$bln1[$c]' $selected> $bulan[$c] </option>";
								$no++;}
							?>
								</select>
							</td>
							<td>
							<?php
								$now=date('Y');
								echo "<select name='thn' class='form-control'>";
								echo '
								<option selected="selected">Tahun</option>';
								for ($a=2017;$a<=$now;$a++)
								{
									$selected = ($tahunPost == $a) ? 'selected' : '';
									echo "<option value='$a' $selected>$a</option>";
								}
								echo "</select>";
							?>
							</td>
							<td>
								<input type="hidden" name="periode" value="ya">
								<button class="btn btn-primary">
									<i class="fa fa-search"></i> Cari
								</button>
								<a href="index.php?page=laporan" class="btn btn-success">
									<i class="fa fa-refresh"></i> Refresh</a>

                                                                <?php if($cariActive && $bulanPost !== '' && $tahunPost !== ''){?>
                                                                <a href="excel.php?cari=yes&bln=<?= urlencode($bulanPost);?>&thn=<?= urlencode($tahunPost);?>"
                                                                        class="btn btn-info"><i class="fa fa-download"></i>
                                                                        Excel</a>
								<?php }else{?>
								<a href="excel.php" class="btn btn-info"><i class="fa fa-download"></i>
									Excel</a>
								<?php }?>
							</td>
						</tr>
					</table>
				</form>
                <form method="post" action="index.php?page=laporan&hari=cek">
                    <?php echo csrf_field(); ?>
                    <table class="table table-striped">
						<tr>
							<th>
								Pilih Hari
							</th>
							<th>
								Aksi
							</th>
						</tr>
						<tr>
							<td>
								<input type="date" value="<?= date('Y-m-d');?>" class="form-control" name="hari">
							</td>
							<td>
								<input type="hidden" name="periode" value="ya">
								<button class="btn btn-primary">
									<i class="fa fa-search"></i> Cari
								</button>
								<a href="index.php?page=laporan" class="btn btn-success">
									<i class="fa fa-refresh"></i> Refresh</a>

                                                                <?php if($hariActive && $hariPost !== ''){?>
                                                                <a href="excel.php?hari=cek&tgl=<?= urlencode($hariPost);?>" class="btn btn-info"><i
                                                                                class="fa fa-download"></i>
                                                                        Excel</a>
								<?php }else{?>
								<a href="excel.php" class="btn btn-info"><i class="fa fa-download"></i>
									Excel</a>
								<?php }?>
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
         <br />
         <br />
         <!-- view barang -->
		<div class="card">
			<div class="card-body">
				<div class="table-responsive">
					<table class="table table-bordered w-100 table-sm" id="example1">
						<thead>
							<tr style="background:#DFF0D8;color:#333;">
								<th style="width: 5%;">No</th>
								<th style="width: 10%;">Kode Transaksi</th>
								<th style="width: 15%;">Menu</th>
								<th style="width: 8%;">Jumlah</th>
								<th style="width: 12%;">Harga Satuan</th>
								<th style="width: 12%;">Subtotal</th>
								<th style="width: 12%;">Kasir</th>
								<th style="width: 12%;">Tanggal</th>
							</tr>
						</thead>
						<tbody>
							<?php
								$no = 1;
								if (!empty($hasil)) {
									foreach ($hasil as $isi) {
							?>
							<tr>
								<td><?= htmlspecialchars((string) $no, ENT_QUOTES, 'UTF-8'); ?></td>
								<td><?= htmlspecialchars($isi['kode_transaksi'], ENT_QUOTES, 'UTF-8'); ?></td>
								<td><?= htmlspecialchars($isi['nama_menu'], ENT_QUOTES, 'UTF-8'); ?></td>
								<td><?= htmlspecialchars((string) $isi['jumlah'], ENT_QUOTES, 'UTF-8'); ?></td>
								<td>Rp.<?= number_format(floatval($isi['harga_satuan'])); ?>,-</td>
								<td>Rp.<?= number_format(floatval($isi['subtotal'])); ?>,-</td>
								<td><?= htmlspecialchars($isi['kasir_nama'], ENT_QUOTES, 'UTF-8'); ?></td>
								<td><?= htmlspecialchars($isi['tanggal_transaksi'], ENT_QUOTES, 'UTF-8'); ?></td>
							</tr>
							<?php
										$no++;
									}
								} else {
							?>
							<tr>
								<td colspan="8" class="text-center text-muted py-3">
									<i class="fas fa-inbox mr-2"></i>Tidak ada data transaksi
								</td>
							</tr>
							<?php } ?>
						</tbody>
						<tfoot>
							<tr>
								<th colspan="3">Total Terjual</td>
								<th><?= htmlspecialchars($jumlah, ENT_QUOTES, 'UTF-8');?></td>
								<th>Rp.<?php echo number_format($modal);?>,-</th>
								<th>Rp.<?php echo number_format($bayar);?>,-</th>
								<th colspan="2"></th>
							</tr>
						</tfoot>
					</table>
				</div>
			</div>
		</div>
     </div>
 </div>