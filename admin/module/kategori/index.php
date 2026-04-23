<?php
$showSuccess     = is_string(filter_input(INPUT_GET, 'success',      FILTER_UNSAFE_RAW)) && filter_input(INPUT_GET, 'success',      FILTER_UNSAFE_RAW) !== '';
$showSuccessEdit = is_string(filter_input(INPUT_GET, 'success-edit', FILTER_UNSAFE_RAW)) && filter_input(INPUT_GET, 'success-edit', FILTER_UNSAFE_RAW) !== '';
$showRemove      = is_string(filter_input(INPUT_GET, 'remove',       FILTER_UNSAFE_RAW)) && filter_input(INPUT_GET, 'remove',       FILTER_UNSAFE_RAW) !== '';
?>

<h4>Kategori</h4>
<br />

<?php if($showSuccess){?>
<div class="alert alert-success"><p>Tambah Data Berhasil !</p></div>
<?php }?>
<?php if($showSuccessEdit){?>
<div class="alert alert-success"><p>Update Data Berhasil !</p></div>
<?php }?>
<?php if($showRemove){?>
<div class="alert alert-danger"><p>Hapus Data Berhasil !</p></div>
<?php }?>

<?php if($showSuccess || $showSuccessEdit || $showRemove){ ?>
<script>
    history.replaceState(null, '', '?page=kategori');
    setTimeout(() => {
        document.querySelectorAll('.alert').forEach(el => $(el).alert('close'));
    }, 3000);
</script>
<?php } ?>

<!-- Tombol buka modal tambah -->
<button class="btn btn-primary mb-3" data-toggle="modal" data-target="#modalTambah">
    <i class="fa fa-plus mr-1"></i> Tambah Kategori
</button>

<br />
<div class="card card-body">
    <div class="table-responsive">
        <table class="table table-bordered table-striped table-sm" id="example1">
            <thead>
                <tr style="background:#DFF0D8;color:#333;">
                    <th>No.</th>
                    <th>Kategori</th>
                    <th>Tanggal Input</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody>
                <?php
                $hasil = $lihat->kategori();
                $no = 1;
                foreach($hasil as $isi){ ?>
                <tr>
                    <td><?= $no++; ?></td>
                    <td><?= htmlspecialchars($isi['nama_kategori'], ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><?= htmlspecialchars($isi['tgl_input'], ENT_QUOTES, 'UTF-8'); ?></td>
                    <td>
                        <!-- Edit: buka modal, isi data lewat JS -->
                        <button class="btn btn-warning btn-sm"
                            onclick="bukaEdit(
                                '<?= $isi['id_kategori']; ?>',
                                '<?= htmlspecialchars(addslashes($isi['nama_kategori']), ENT_QUOTES, 'UTF-8'); ?>'
                            )">
                            Edit
                        </button>
                        <!-- Hapus: tetap pakai hapus.php seperti aslinya -->
                        <a href="fungsi/hapus/hapus.php?kategori=hapus&id=<?= urlencode($isi['id_kategori']); ?>&csrf_token=<?= urlencode(csrf_get_token()); ?>"
                            onclick="return confirm('Hapus Data Kategori ?');">
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
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fa fa-plus mr-2"></i>Tambah Kategori</h5>
                <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
            </div>
            <form method="POST" action="fungsi/tambah/tambah.php?kategori=tambah">
                <?php echo csrf_field(); ?>
                <div class="modal-body">
                    <div class="form-group mb-0">
                        <label>Nama Kategori <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="kategori"
                            placeholder="Masukan Kategori Barang Baru"
                            required maxlength="100" autocomplete="off">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Batal</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="fa fa-plus mr-1"></i> Insert Data
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- ===================== MODAL EDIT ===================== -->
<div class="modal fade" id="modalEdit" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fa fa-edit mr-2"></i>Edit Kategori</h5>
                <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
            </div>
            <form method="POST" action="fungsi/edit/edit.php?kategori=edit">
                <?php echo csrf_field(); ?>
                <div class="modal-body">
                    <div class="form-group mb-0">
                        <label>Nama Kategori <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="kategori"
                            id="edit-nama" placeholder="Masukan Kategori Barang Baru"
                            required maxlength="100" autocomplete="off">
                        <input type="hidden" name="id" id="edit-id">
                    </div>
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
function bukaEdit(id, nama) {
    document.getElementById('edit-id').value   = id;
    document.getElementById('edit-nama').value = nama;
    $('#modalEdit').modal('show');
}
</script>