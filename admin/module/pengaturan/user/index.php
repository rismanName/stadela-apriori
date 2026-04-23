<?php
$showSuccess     = is_string(filter_input(INPUT_GET, 'success',      FILTER_UNSAFE_RAW)) && filter_input(INPUT_GET, 'success',      FILTER_UNSAFE_RAW) !== '';
$showSuccessEdit = is_string(filter_input(INPUT_GET, 'success-edit', FILTER_UNSAFE_RAW)) && filter_input(INPUT_GET, 'success-edit', FILTER_UNSAFE_RAW) !== '';
$showRemove      = is_string(filter_input(INPUT_GET, 'remove',       FILTER_UNSAFE_RAW)) && filter_input(INPUT_GET, 'remove',       FILTER_UNSAFE_RAW) !== '';
?>

<h4>Pengguna</h4>
<br />

<?php if($showSuccess){?>
<div class="alert alert-success"><p>Tambah Data Berhasil !</p></div>
<?php }?>
<?php if($showSuccessEdit){?>
<div class="alert alert-success"><p>Ubah Data Berhasil !</p></div>
<?php }?>
<?php if($showRemove){?>
<div class="alert alert-danger"><p>Hapus Data Berhasil !</p></div>
<?php }?>

<?php if($showSuccess || $showSuccessEdit || $showRemove){ ?>
<script>
    history.replaceState(null, '', '?page=pengaturan/user');
    setTimeout(() => {
        document.querySelectorAll('.alert').forEach(el => $(el).alert('close'));
    }, 3000);
</script>
<?php } ?>

<!-- Tombol buka modal tambah -->
<button class="btn btn-primary mb-3" data-toggle="modal" data-target="#modalTambah">
    <i class="fa fa-plus mr-1"></i> Tambah Pengguna
</button>

<br />
<div class="card card-body">
    <div class="table-responsive">
        <table class="table table-bordered table-striped table-sm" id="example1">
            <thead>
                <tr style="background:#DFF0D8;color:#333;">
                    <th>No.</th>
                    <th>Username</th>
                    <th>Nama</th>
                    <th>Telepon</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody>
                <?php
                $hasil = $lihat->user();
                $no = 1;
                foreach($hasil as $isi){ ?>
                <tr>
                    <td><?= $no++; ?></td>
                    <td><?= htmlspecialchars($isi['username'], ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><?= htmlspecialchars($isi['nama'], ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><?= htmlspecialchars($isi['telepon'], ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><?= htmlspecialchars($isi['email'], ENT_QUOTES, 'UTF-8'); ?></td>
                    <td><?= htmlspecialchars($isi['role'], ENT_QUOTES, 'UTF-8'); ?></td>
                    <td>
                        <!-- EDIT -->
                        <button class="btn btn-warning btn-sm"
                            onclick='bukaEdit(<?= json_encode($isi); ?>)'>
                            Ubah
                        </button>

                        <!-- DELETE -->
                        <a href="admin/module/pengaturan/user/hapus.php?id=<?= urlencode($isi['id_user']); ?>&csrf_token=<?= urlencode(csrf_get_token()); ?>"
                            onclick="return confirm('Hapus Data Pengguna ?');">
                            <button class="btn btn-danger btn-sm">Hapus</button>
                        </a>
                    </td>
                </tr>
                <?php } ?>
            </tbody>
        </table>
    </div>
</div>

<!-- ===================== MODAL TAMBAH USER ===================== -->
<div class="modal fade" id="modalTambah" tabindex="-1">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">

      <div class="modal-header">
        <h5 class="modal-title">
          <i class="fa fa-user-plus mr-2"></i>Tambah Pengguna
        </h5>
        <button type="button" class="close" data-dismiss="modal">&times;</button>
      </div>

      <form method="POST" action="admin/module/pengaturan/user/tambah.php" enctype="multipart/form-data">
        <?php echo csrf_field(); ?>

        <div class="modal-body">
          <div class="row">

            <!-- KIRI -->
            <div class="col-md-6">

              <div class="form-group">
                <label>Username *</label>
                <input type="text" name="username" class="form-control" required>
              </div>

              <div class="form-group">
                <label>Password *</label>
                <input type="password" name="password" class="form-control" required>
              </div>

              <div class="form-group">
                <label>Nama *</label>
                <input type="text" name="nama" class="form-control" required>
              </div>

              <div class="form-group">
                <label>Email *</label>
                <input type="email" name="email" class="form-control" required>
              </div>

              <div class="form-group">
                <label>Telepon *</label>
                <input type="text" name="telepon" class="form-control" required>
              </div>

            </div>

            <!-- KANAN -->
            <div class="col-md-6">

              <div class="form-group">
                <label>NIK</label>
                <input type="text" name="nik" class="form-control">
              </div>

              <div class="form-group">
                <label>Role *</label>
                <select name="role" class="form-control" required>
                  <option value="admin">Admin</option>
                  <option value="kasir">Kasir</option>
                </select>
              </div>

              <div class="form-group">
                <label>Alamat *</label>
                <textarea name="alamat" class="form-control" rows="3" required></textarea>
              </div>

            </div>

          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-secondary" data-dismiss="modal">Batal</button>
          <button type="submit" class="btn btn-primary">
            <i class="fa fa-save mr-1"></i> Simpan
          </button>
        </div>

      </form>
    </div>
  </div>
</div>

<!-- ===================== MODAL EDIT USER ===================== -->
<div class="modal fade" id="modalEdit" tabindex="-1">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">

      <div class="modal-header">
        <h5 class="modal-title">
          <i class="fa fa-edit mr-2"></i>Ubah Pengguna
        </h5>
        <button type="button" class="close" data-dismiss="modal">&times;</button>
      </div>

      <form method="POST" action="admin/module/pengaturan/user/ubah.php" enctype="multipart/form-data">
        <?php echo csrf_field(); ?>

        <div class="modal-body">
          <div class="row">

            <input type="hidden" name="id" id="edit-id">

            <!-- KIRI -->
            <div class="col-md-6">

              <div class="form-group">
                <label>Username *</label>
                <input type="text" name="username" id="edit-username" class="form-control" required>
              </div>

              <div class="form-group">
                <label>Nama *</label>
                <input type="text" name="nama" id="edit-nama" class="form-control" required>
              </div>

              <div class="form-group">
                <label>Email *</label>
                <input type="email" name="email" id="edit-email" class="form-control" required>
              </div>

              <div class="form-group">
                <label>Telepon *</label>
                <input type="text" name="telepon" id="edit-telepon" class="form-control" required>
              </div>

            </div>

            <!-- KANAN -->
            <div class="col-md-6">

              <div class="form-group">
                <label>NIK</label>
                <input type="text" name="nik" id="edit-nik" class="form-control">
              </div>

              <div class="form-group">
                <label>Role *</label>
                <select name="role" id="edit-role" class="form-control">
                  <option value="admin">Admin</option>
                  <option value="kasir">Kasir</option>
                </select>
              </div>

              <div class="form-group">
                <label>Alamat *</label>
                <textarea name="alamat" id="edit-alamat" class="form-control" rows="3" required></textarea>
              </div>

            </div>

          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-secondary" data-dismiss="modal">Batal</button>
          <button type="submit" class="btn btn-primary">
            <i class="fa fa-save mr-1"></i> Ubah
          </button>
        </div>

      </form>
    </div>
  </div>
</div>

<script>
function bukaEdit(user) {
    document.getElementById('edit-id').value = user.id_user || '';
    document.getElementById('edit-username').value = user.username || '';
    document.getElementById('edit-nama').value = user.nama || '';
    document.getElementById('edit-email').value = user.email || '';
    document.getElementById('edit-telepon').value = user.telepon || '';
    document.getElementById('edit-nik').value = user.NIK || '';
    document.getElementById('edit-role').value = user.role || 'kasir';
    document.getElementById('edit-alamat').value = user.alamat || '';

    $('#modalEdit').modal('show');
}
</script>