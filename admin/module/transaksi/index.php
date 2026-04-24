<?php
$showSuccess = is_string(filter_input(INPUT_GET, 'success', FILTER_UNSAFE_RAW)) && filter_input(INPUT_GET, 'success', FILTER_UNSAFE_RAW) !== '';
?>

<h4>Input Transaksi</h4>
<br />

<?php if($showSuccess){?>
<div class="alert alert-success"><p>Transaksi Berhasil Disimpan !</p></div>
<?php }?>

<?php if($showSuccess){ ?>
<script>
    history.replaceState(null, '', '?page=transaksi');
    setTimeout(() => {
        document.querySelectorAll('.alert').forEach(el => $(el).alert('close'));
    }, 3000);
</script>
<?php } ?>

<div class="card">
    <div class="card-header bg-primary text-white">
        <h5>Buat Transaksi Baru</h5>
    </div>
    <div class="card-body">
        <form method="post" action="admin/module/transaksi/tambah.php" onsubmit="return validasiForm()">
            <?php echo csrf_field(); ?>

            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label>Kode Transaksi <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="kodeTransaksi"
                            placeholder="Auto Generate" readonly style="background-color: #e9ecef; font-weight: bold;">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label>Tanggal <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" name="tanggal" id="tanggalInput"
                            value="<?= date('Y-m-d'); ?>" required onchange="generateKodeTransaksi()">
                    </div>
                </div>
            </div>

            <hr>
            <h6 class="mb-3"><strong>Pilih Menu</strong></h6>

            <div class="table-responsive mb-3">
                <table class="table table-bordered table-sm">
                    <thead>
                        <tr style="background:#DFF0D8;color:#333;">
                            <th style="width: 40%;">Menu</th>
                            <th style="width: 20%;">Harga</th>
                            <th style="width: 15%;">Jumlah</th>
                            <th style="width: 20%;">Subtotal</th>
                            <th style="width: 5%;">Aksi</th>
                        </tr>
                    </thead>
                    <tbody id="itemsContainer">
                        <tr id="emptyRow">
                            <td colspan="5" class="text-center text-muted py-3">
                                <em>Belum ada item. Klik tombol "Tambah Item" untuk menambahkan.</em>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <button type="button" class="btn btn-success mb-3" onclick="tambahItem()">
                <i class="fa fa-plus mr-1"></i> Tambah Item
            </button>

            <hr>
            <div class="row">
                <div class="col-md-6 ml-auto">
                    <div class="card bg-light">
                        <div class="card-body">
                            <div class="row mb-2">
                                <div class="col-8">
                                    <strong>Total Item:</strong>
                                </div>
                                <div class="col-4 text-right">
                                    <strong id="totalItems">0</strong>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-8">
                                    <strong>Total Harga:</strong>
                                </div>
                                <div class="col-4 text-right">
                                    <strong>Rp. <span id="totalPrice">0</span></strong>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <input type="hidden" id="itemsData" name="items" value="">

            <div class="form-group mt-4">
                <button type="submit" class="btn btn-primary" id="submitBtn">
                    <i class="fa fa-save mr-1"></i> Simpan Transaksi
                </button>
                <a href="index.php?page=transaksi/riwayat" class="btn btn-secondary">
                    <i class="fa fa-history mr-1"></i> Riwayat Transaksi
                </a>
            </div>
        </form>
    </div>
</div>

<script>
let itemCount = 0;
let menuOptions = [];

// Generate Kode Transaksi
function generateKodeTransaksi() {
    const tanggal = document.getElementById('tanggalInput').value;
    if (!tanggal) return;

    const tanggalFormat = tanggal.replace(/-/g, ''); // Convert YYYY-MM-DD to YYYYMMDD
    const prefix = 'TRX' + tanggalFormat;

    // Placeholder - kode sebenarnya akan di-generate di server saat submit
    // Ini hanya untuk preview
    document.getElementById('kodeTransaksi').value = prefix + '-###';
}

// Get menu data dari PHP
<?php
$stmt = $config->prepare('SELECT id_menu, nama_menu, harga FROM menu ORDER BY nama_menu');
$stmt->execute();
$menus = $stmt->fetchAll(PDO::FETCH_ASSOC);
foreach($menus as $m) {
    echo "menuOptions.push({id: " . intval($m['id_menu']) . ", nama: '" . addslashes($m['nama_menu']) . "', harga: " . floatval($m['harga']) . "});";
}
?>

// Init kode transaksi saat halaman dimuat
document.addEventListener('DOMContentLoaded', function() {
    generateKodeTransaksi();
});

function tambahItem() {
    const container = document.getElementById('itemsContainer');
    const emptyRow = document.getElementById('emptyRow');

    if (emptyRow) emptyRow.remove();

    itemCount++;
    const rowId = 'item-' + itemCount;

    // Build menu options
    let optionsHtml = '<option value="">-- Pilih Menu --</option>';
    menuOptions.forEach(menu => {
        optionsHtml += '<option value="' + menu.id + '" data-harga="' + menu.harga + '">'
                    + menu.nama + ' (Rp. ' + menu.harga.toLocaleString('id-ID') + ')</option>';
    });

    const html = `
        <tr id="${rowId}">
            <td>
                <select class="form-control form-control-sm menu-select" onchange="updateSubtotal('${rowId}')" required>
                    ${optionsHtml}
                </select>
            </td>
            <td>
                <div class="input-group input-group-sm">
                    <div class="input-group-prepend">
                        <span class="input-group-text">Rp.</span>
                    </div>
                    <input type="text" class="form-control form-control-sm menu-harga" readonly value="0">
                </div>
            </td>
            <td>
                <input type="number" class="form-control form-control-sm menu-jumlah" value="1" min="1" max="999" onchange="updateSubtotal('${rowId}')" required>
            </td>
            <td>
                <div class="input-group input-group-sm">
                    <div class="input-group-prepend">
                        <span class="input-group-text">Rp.</span>
                    </div>
                    <input type="text" class="form-control form-control-sm menu-subtotal" readonly value="0">
                </div>
            </td>
            <td>
                <button type="button" class="btn btn-danger btn-sm" onclick="hapusItem('${rowId}')" title="Hapus item">
                    <i class="fa fa-trash"></i>
                </button>
            </td>
        </tr>
    `;

    container.innerHTML += html;
    updateTotal();
}

function updateSubtotal(rowId) {
    const row = document.getElementById(rowId);
    if (!row) return;

    const select = row.querySelector('.menu-select');
    const hargaInput = row.querySelector('.menu-harga');
    const jumlahInput = row.querySelector('.menu-jumlah');
    const subtotalInput = row.querySelector('.menu-subtotal');

    const option = select.options[select.selectedIndex];
    const harga = parseFloat(option.dataset.harga) || 0;
    const jumlah = parseInt(jumlahInput.value) || 0;
    const subtotal = harga * jumlah;

    hargaInput.value = harga.toLocaleString('id-ID');
    subtotalInput.value = subtotal.toLocaleString('id-ID');

    updateTotal();
}

function hapusItem(rowId) {
    const row = document.getElementById(rowId);
    if (row) row.remove();

    if (document.querySelectorAll('#itemsContainer tr').length === 0) {
        document.getElementById('itemsContainer').innerHTML = '<tr id="emptyRow"><td colspan="5" class="text-center text-muted py-3"><em>Belum ada item</em></td></tr>';
    }

    updateTotal();
}

function updateTotal() {
    let totalItems = 0;
    let totalPrice = 0;

    document.querySelectorAll('#itemsContainer tr').forEach(row => {
        if (!row.id || !row.id.startsWith('item-')) return;

        const jumlahInput = row.querySelector('.menu-jumlah');
        const subtotalInput = row.querySelector('.menu-subtotal');

        if (!jumlahInput || !subtotalInput) return;

        const jumlah = parseInt(jumlahInput.value) || 0;
        const subtotalText = subtotalInput.value.replace(/\./g, '');
        const subtotalNum = parseInt(subtotalText) || 0;

        totalItems += jumlah;
        totalPrice += subtotalNum;
    });

    document.getElementById('totalItems').textContent = totalItems;
    document.getElementById('totalPrice').textContent = totalPrice.toLocaleString('id-ID');

    updateItemsData();
}

function updateItemsData() {
    const items = [];
    document.querySelectorAll('#itemsContainer tr').forEach(row => {
        if (!row.id || !row.id.startsWith('item-')) return;

        const select = row.querySelector('.menu-select');
        const jumlahInput = row.querySelector('.menu-jumlah');

        if (select && select.value && jumlahInput) {
            items.push({
                id_menu: select.value,
                jumlah: jumlahInput.value
            });
        }
    });

    document.getElementById('itemsData').value = JSON.stringify(items);
}

function validasiForm() {
    const itemsContainer = document.getElementById('itemsContainer');
    const rows = itemsContainer.querySelectorAll('tr[id^="item-"]');

    if (rows.length === 0) {
        alert('Tambahkan minimal 1 item transaksi!');
        return false;
    }

    // Validasi setiap item sudah memilih menu
    for (let row of rows) {
        const select = row.querySelector('.menu-select');
        if (!select.value) {
            alert('Pilih menu untuk semua item!');
            return false;
        }
    }

    return true;
}
</script>
