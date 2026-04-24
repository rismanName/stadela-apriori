<!--sidebar start-->
<?php
    $id = $_SESSION['user']['id_user'];
    $role = $_SESSION['user']['role'];
    $hasil_profil = $lihat->user_edit($id);
?>
<!-- Sidebar -->
<ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">

    <!-- Sidebar - Brand -->
    <a class="sidebar-brand d-flex align-items-center justify-content-center" href="index.php">
        <div class="sidebar-brand-icon rotate-n-15">
            <i class="fas fa-cash-register"></i>
        </div>
        <div class="sidebar-brand-text mx-3">Sata Priori</div>
    </a>

    <hr class="sidebar-divider my-0">

    <!-- Dashboard -->
    <li class="nav-item active">
        <a class="nav-link" href="index.php">
            <i class="fas fa-fw fa-tachometer-alt"></i>
            <span>Dashboard</span>
        </a>
    </li>

    <hr class="sidebar-divider">

    <?php if($role === 'admin'): ?>

        <!-- ADMIN MENU -->

        <!-- Data Master -->
        <li class="nav-item">
            <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseDataMaster"
                aria-expanded="false" aria-controls="collapseDataMaster">
                <i class="fas fa-fw fa-database"></i>
                <span>Data Master</span>
            </a>
            <div id="collapseDataMaster" class="collapse" aria-labelledby="headingDataMaster" data-parent="#accordionSidebar">
                <div class="bg-white py-2 collapse-inner rounded">
                    <a class="collapse-item" href="index.php?page=kategori">Kategori Menu</a>
                    <a class="collapse-item" href="index.php?page=menu">Kelola Menu</a>
                </div>
            </div>
        </li>

        <!-- Data Penjualan -->
        <li class="nav-item">
            <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseTransaksi"
                aria-expanded="false" aria-controls="collapseTransaksi">
                <i class="fas fa-fw fa-shopping-cart"></i>
                <span>Transaksi</span>
            </a>
            <div id="collapseTransaksi" class="collapse" aria-labelledby="headingTransaksi" data-parent="#accordionSidebar">
                <div class="bg-white py-2 collapse-inner rounded">
                    <a class="collapse-item" href="index.php?page=transaksi">Input Transaksi</a>
                    <a class="collapse-item" href="index.php?page=transaksi/riwayat">Riwayat Transaksi</a>
                </div>
            </div>
        </li>

        <!-- Apriori -->
        <li class="nav-item">
            <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseApriori">
                <i class="fas fa-fw fa-brain"></i>
                <span>Data Mining</span>
            </a>
            <div id="collapseApriori" class="collapse">
                <div class="bg-white py-2 collapse-inner rounded">
                    <a class="collapse-item" href="index.php?page=apriori">Proses Apriori</a>
                    <a class="collapse-item" href="index.php?page=apriori/itemset">Frequent Itemset</a>
                    <a class="collapse-item" href="index.php?page=apriori/rules">Association Rules</a>
                </div>
            </div>
        </li>

        <!-- Laporan -->
        <li class="nav-item">
            <a class="nav-link" href="index.php?page=laporan">
                <i class="fas fa-fw fa-chart-bar"></i>
                <span>Laporan</span>
            </a>
        </li>

        <hr class="sidebar-divider">

        <!-- Pengaturan -->
        <li class="nav-item">
            <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapsePengaturan"
                aria-expanded="false" aria-controls="collapsePengaturan">
                <i class="fas fa-fw fa-cogs"></i>
                <span>Pengaturan</span>
            </a>
            <div id="collapsePengaturan" class="collapse" aria-labelledby="headingPengaturan" data-parent="#accordionSidebar">
                <div class="bg-white py-2 collapse-inner rounded">
                    <a class="collapse-item" href="index.php?page=pengaturan/toko">Profil Toko</a>
                    <a class="collapse-item" href="index.php?page=pengaturan/user">Kelola User</a>
                </div>
            </div>
        </li>

    <?php elseif($role === 'kasir'): ?>

        <!-- KASIR MENU -->

        <!-- Transaksi -->
        <li class="nav-item">
            <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseTransaksi"
                aria-expanded="false" aria-controls="collapseTransaksi">
                <i class="fas fa-fw fa-desktop"></i>
                <span>Transaksi</span>
            </a>
            <div id="collapseTransaksi" class="collapse" aria-labelledby="headingTransaksi" data-parent="#accordionSidebar">
                <div class="bg-white py-2 collapse-inner rounded">
                    <a class="collapse-item" href="index.php?page=transaksi">Input Transaksi</a>
                    <a class="collapse-item" href="index.php?page=transaksi/riwayat">Riwayat Transaksi</a>
                </div>
            </div>
        </li>

    <?php endif; ?>

    <hr class="sidebar-divider d-none d-md-block">

    <div class="text-center d-none d-md-inline">
        <button class="rounded-circle border-0" id="sidebarToggle"></button>
    </div>

</ul>
<!-- End of Sidebar -->

<!-- Content Wrapper -->
<div id="content-wrapper" class="d-flex flex-column">
    <div id="content">

        <!-- Topbar -->
        <nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">

            <button id="sidebarToggleTop" class="btn btn-link d-md-none rounded-circle mr-3">
                <i class="fa fa-bars"></i>
            </button>

            <h5 class="d-lg-block d-none mt-2">
                <b><?php echo htmlspecialchars($toko['nama_toko'] ?? ''); ?>,
                   <?php echo htmlspecialchars($toko['alamat_toko'] ?? ''); ?></b>
            </h5>

            <ul class="navbar-nav ml-auto">
                <li class="nav-item dropdown no-arrow">
                    <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
                        data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <img class="img-profile rounded-circle"
                            src="assets/img/user/<?php echo htmlspecialchars($hasil_profil['foto'] ?? ''); ?>">
                        <span class="mr-2 d-none d-lg-inline text-gray-600 small ml-2">
                            <?php echo htmlspecialchars($hasil_profil['nama'] ?? ''); ?>
                        </span>
                        <!-- Badge role -->
                        <span class="badge badge-<?php echo $role === 'admin' ? 'danger' : 'info'; ?> ml-1">
                            <?php echo ucfirst($role); ?>
                        </span>
                        <i class="fas fa-angle-down"></i>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in"
                        aria-labelledby="userDropdown">
                        <a class="dropdown-item" href="index.php?page=profil">
                            <i class="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
                            Profil
                        </a>
                        <div class="dropdown-divider"></div>
                        <a class="dropdown-item" href="logout.php">
                            <i class="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i>
                            Logout
                        </a>
                    </div>
                </li>
            </ul>
        </nav>
        <!-- End of Topbar -->

        <div class="container-fluid">