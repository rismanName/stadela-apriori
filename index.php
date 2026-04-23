<?php
    @ob_start();
    session_start();

    if(!empty($_SESSION['user'])){
        require 'config.php';
        require_once 'fungsi/csrf.php';
        csrf_get_token();
        csrf_guard();
        include $view;
        $lihat = new view($config);
        $toko  = $lihat->toko();
        $role  = $_SESSION['user']['role'];

        // Halaman yang boleh diakses per role
        $allowedPages = [
            'admin' => [
                'dashboard',
                'kategori',
                'menu',
                'menu/details',
                'menu/edit',
                'penjualan',
                'penjualan/import',
                'apriori',
                'laporan',
                'pengaturan',
                'user',
            ],
            'kasir' => [
                'dashboard',
                'transaksi',
                'transaksi/riwayat',
                'user',
            ],
        ];

        $pages = $allowedPages[$role] ?? [];

        include 'admin/template/header.php';
        include 'admin/template/sidebar.php';

            if(!empty($_GET['page'])){
                $requestedPage = (string) $_GET['page'];

                if(in_array($requestedPage, $pages, true)){
                    $moduleRoot = realpath(__DIR__.'/admin/module');
                    $modulePath = realpath(__DIR__.'/admin/module/'.$requestedPage.'/index.php');

                    if(
                        $moduleRoot !== false &&
                        $modulePath !== false &&
                        strpos($modulePath, $moduleRoot) === 0 &&
                        is_file($modulePath)
                    ){
                        include $modulePath;
                    } else {
                        include 'admin/template/home.php';
                    }
                } else {
                    // Akses halaman yang tidak diizinkan untuk role ini
                    include 'admin/template/forbidden.php';
                }
            } else {
                include 'admin/template/home.php';
            }

        include 'admin/template/footer.php';

    } else {
        echo '<script>window.location="login.php";</script>';
        exit;
    }
?>