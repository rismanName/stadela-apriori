<?php
$showSuccess     = is_string(filter_input(INPUT_GET, 'success',      FILTER_UNSAFE_RAW)) && filter_input(INPUT_GET, 'success',      FILTER_UNSAFE_RAW) !== '';
$showSuccessEdit = is_string(filter_input(INPUT_GET, 'success-edit', FILTER_UNSAFE_RAW)) && filter_input(INPUT_GET, 'success-edit', FILTER_UNSAFE_RAW) !== '';
$showRemove      = is_string(filter_input(INPUT_GET, 'remove',       FILTER_UNSAFE_RAW)) && filter_input(INPUT_GET, 'remove',       FILTER_UNSAFE_RAW) !== '';
?>

<h4>Menu</h4>
<br />

<?php if($showSuccess){?>
<div class="alert alert-success"><p>Tambah Data Menu Berhasil !</p></div>
<?php }?>
<?php if($showSuccessEdit){?>
<div class="alert alert-success"><p>Ubah Data Menu Berhasil !</p></div>
<?php }?>
<?php if($showRemove){?>
<div class="alert alert-danger"><p>Hapus Data Menu Berhasil !</p></div>
<?php }?>

<?php if($showSuccess || $showSuccessEdit || $showRemove){ ?>
<script>
    history.replaceState(null, '', '?page=menu');
    setTimeout(() => {
        document.querySelectorAll('.alert').forEach(el => $(el).alert('close'));
    }, 3000);
</script>
<?php } ?>

<!-- Tombol buka modal tambah -->
<button class="btn btn-primary mb-3" data-toggle="modal" data-target="#modalTambah">
    <i class="fa fa-plus mr-1"></i> Tambah Menu
</button>

<br />
<div class="card card-body">
    <div class="table-responsive">
        <table class="table table-bordered table-striped table-sm" id="example1">
            <thead>
                <tr style="background:#DFF0D8;color:#333;">
                    <th>No.</th>
                    <th>Kode Menu</th>
                    <th>Nama Menu</th>
                    <th>Kategori</th>
                    <th>Harga</th>
                    <th>Stok</th>
                    <th>Satuan</th>
                    <th>Tanggal Input</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody>
                <?php
                $stmt = $config->prepare('
                    SELECT m.*, k.nama_kategori
                    FROM menu m
                        LEFT JOIN kategori k ON m.id_kategori = k.id_kategori
                    ORDER BY m.tgl_input DESC
                ');
                $stmt->execute();
                $hasil = $stmt->fetchAll(PDO::FETCH_ASSOC);
                $no = 1;
                foreach($hasil as $isi){ ?>
                <tr>
                    <td><?= $no++; ?></td>
                    <td><?= htmlspecialchars($isi['kode_menu'], ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><?= htmlspecialchars($isi['nama_menu'], ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><?= htmlspecialchars($isi['nama_kategori'] ?? '-', ENT_QUOTES, 'UTF-8'); ?></td>
                    <td>Rp. <?= number_format($isi['harga'], 0, ',', '.'); ?></td>
                    <td><?= htmlspecialchars($isi['stok'], ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><?= htmlspecialchars($isi['satuan'] ?? '-', ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><?= htmlspecialchars($isi['tgl_input'], ENT_QUOTES, 'UTF-8'); ?></td>
                    <td>
                        <!-- Edit: buka modal, isi data lewat JS -->
                        <button class="btn btn-warning btn-sm"
                            onclick="bukaEdit(
                                '<?= $isi['id_menu']; ?>',
                                '<?= htmlspecialchars(addslashes($isi['kode_menu']), ENT_QUOTES, 'UTF-8'); ?>',
                                '<?= htmlspecialchars(addslashes($isi['nama_menu']), ENT_QUOTES, 'UTF-8'); ?>',
                                '<?= $isi['id_kategori']; ?>',
                                '<?= $isi['harga']; ?>',
                                '<?= $isi['stok']; ?>',
                                '<?= htmlspecialchars(addslashes($isi['satuan'] ?? ''), ENT_QUOTES, 'UTF-8'); ?>',
                                '<?= htmlspecialchars(addslashes($isi['deskripsi'] ?? ''), ENT_QUOTES, 'UTF-8'); ?>'
                            )">
                            Ubah
                        </button>
                        <!-- Hapus -->
                        <a href="admin/module/menu/hapus.php?id=<?= urlencode($isi['id_menu']); ?>&csrf_token=<?= urlencode(csrf_get_token()); ?>"
                            onclick="return confirm('Hapus Data Menu ?');">
                            <button class="btn btn-danger btn-sm">Hapus</button>
                        </a>
                    </td>
                </tr>
                <?php } ?>
            </tbody>
        </table>
    </div>
</div>

<!-- ===================== MODAL TAMBAH ===================== -->
<div class="modal fade" id="modalTambah" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fa fa-plus mr-2"></i>Tambah Menu</h5>
                <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
            </div>
            <form method="POST" action="admin/module/menu/tambah.php">
                <?php echo csrf_field(); ?>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Kode Menu <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" name="kode_menu"
                                    placeholder="Kode Menu" required maxlength="20" autocomplete="off">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Nama Menu <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" name="nama_menu"
                                    placeholder="Nama Menu" required maxlength="255" autocomplete="off">
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Kategori <span class="text-danger">*</span></label>
                                <select class="form-control" name="id_kategori" required>
                                    <option value="">-- Pilih Kategori --</option>
                                    <?php
                                    $stmt = $config->prepare('SELECT * FROM kategori ORDER BY nama_kategori');
                                    $stmt->execute();
                                    $kat = $stmt->fetchAll(PDO::FETCH_ASSOC);
                                    foreach($kat as $k){
                                    ?>
                                    <option value="<?= $k['id_kategori']; ?>"><?= htmlspecialchars($k['nama_kategori'], ENT_QUOTES, 'UTF-8'); ?></option>
                                    <?php } ?>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Harga <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" name="harga"
                                    placeholder="0" required step="0.01" min="0" autocomplete="off">
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Stok <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" name="stok"
                                    placeholder="0" required min="0" autocomplete="off">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Satuan</label>
                                <input type="text" class="form-control" name="satuan"
                                    placeholder="Satuan (Pcs, Kg, dll)" maxlength="50" autocomplete="off">
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Deskripsi</label>
                        <textarea class="form-control" name="deskripsi"
                            placeholder="Deskripsi Menu" rows="3"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Batal</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="fa fa-plus mr-1"></i> Tambah Data
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- ===================== MODAL EDIT ===================== -->
<div class="modal fade" id="modalEdit" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-dialog-centered modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fa fa-edit mr-2"></i>Ubah Menu</h5>
                <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
            </div>
            <form method="POST" action="admin/module/menu/ubah.php">
                <?php echo csrf_field(); ?>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Kode Menu <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" name="kode_menu" id="edit-kode"
                                    placeholder="Kode Menu" required maxlength="20" autocomplete="off">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Nama Menu <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" name="nama_menu" id="edit-nama"
                                    placeholder="Nama Menu" required maxlength="255" autocomplete="off">
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Kategori <span class="text-danger">*</span></label>
                                <select class="form-control" name="id_kategori" id="edit-kategori" required>
                                    <option value="">-- Pilih Kategori --</option>
                                    <?php
                                    $stmt = $config->prepare('SELECT * FROM kategori ORDER BY nama_kategori');
                                    $stmt->execute();
                                    $kat = $stmt->fetchAll(PDO::FETCH_ASSOC);
                                    foreach($kat as $k){
                                    ?>
                                    <option value="<?= $k['id_kategori']; ?>"><?= htmlspecialchars($k['nama_kategori'], ENT_QUOTES, 'UTF-8'); ?></option>
                                    <?php } ?>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Harga <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" name="harga" id="edit-harga"
                                    placeholder="0" required step="0.01" min="0" autocomplete="off">
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Stok <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" name="stok" id="edit-stok"
                                    placeholder="0" required min="0" autocomplete="off">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Satuan</label>
                                <input type="text" class="form-control" name="satuan" id="edit-satuan"
                                    placeholder="Satuan (Pcs, Kg, dll)" maxlength="50" autocomplete="off">
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Deskripsi</label>
                        <textarea class="form-control" name="deskripsi" id="edit-deskripsi"
                            placeholder="Deskripsi Menu" rows="3"></textarea>
                    </div>
                    <input type="hidden" name="id" id="edit-id">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Batal</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="fa fa-edit mr-1"></i> Ubah Data
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
function bukaEdit(id, kode, nama, idKat, harga, stok, satuan, deskripsi) {
    document.getElementById('edit-id').value         = id;
    document.getElementById('edit-kode').value       = kode;
    document.getElementById('edit-nama').value       = nama;
    document.getElementById('edit-kategori').value   = idKat;
    document.getElementById('edit-harga').value      = harga;
    document.getElementById('edit-stok').value       = stok;
    document.getElementById('edit-satuan').value     = satuan;
    document.getElementById('edit-deskripsi').value  = deskripsi;
    $('#modalEdit').modal('show');
}
</script>