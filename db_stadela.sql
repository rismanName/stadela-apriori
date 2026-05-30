-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: mariadb:3306
-- Generation Time: May 30, 2026 at 04:11 AM
-- Server version: 12.2.2-MariaDB-ubu2404
-- PHP Version: 8.3.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_stadela`
--

-- --------------------------------------------------------

--
-- Table structure for table `apriori_itemset`
--

CREATE TABLE `apriori_itemset` (
  `id_itemset` int(11) NOT NULL,
  `id_proses` int(11) NOT NULL,
  `itemset` text NOT NULL,
  `ukuran` int(11) NOT NULL DEFAULT 1,
  `jumlah` int(11) NOT NULL DEFAULT 0,
  `support` float NOT NULL DEFAULT 0,
  `support_pct` float NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Dumping data for table `apriori_itemset`
--

INSERT INTO `apriori_itemset` (`id_itemset`, `id_proses`, `itemset`, `ukuran`, `jumlah`, `support`, `support_pct`) VALUES
(6, 6, 'Kopi Hitam', 1, 5, 0.5, 50),
(7, 6, 'Teh Manis', 1, 5, 0.5, 50),
(8, 6, 'Kopi Susu', 1, 4, 0.4, 40),
(9, 6, 'Roti Bakar', 1, 4, 0.4, 40),
(10, 6, 'Pisang Goreng', 1, 3, 0.3, 30),
(11, 6, 'Kopi Hitam, Pisang Goreng', 2, 3, 0.3, 30);

-- --------------------------------------------------------

--
-- Table structure for table `apriori_proses`
--

CREATE TABLE `apriori_proses` (
  `id_proses` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `min_support` float NOT NULL,
  `min_confidence` float NOT NULL,
  `tanggal_dari` date DEFAULT NULL,
  `tanggal_sampai` date DEFAULT NULL,
  `total_transaksi` int(11) NOT NULL DEFAULT 0,
  `status` enum('proses','selesai','gagal') NOT NULL DEFAULT 'proses',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Dumping data for table `apriori_proses`
--

INSERT INTO `apriori_proses` (`id_proses`, `id_user`, `min_support`, `min_confidence`, `tanggal_dari`, `tanggal_sampai`, `total_transaksi`, `status`, `created_at`) VALUES
(6, 1, 0.23, 0.5, '2026-03-01', '2026-03-31', 10, 'selesai', '2026-04-27 07:15:31');

-- --------------------------------------------------------

--
-- Table structure for table `detail_transaksi`
--

CREATE TABLE `detail_transaksi` (
  `id_detail` int(11) NOT NULL,
  `id_transaksi` int(11) NOT NULL,
  `id_menu` int(11) NOT NULL,
  `nama_menu` varchar(255) NOT NULL,
  `jumlah` int(11) NOT NULL DEFAULT 1,
  `harga_satuan` decimal(10,2) NOT NULL DEFAULT 0.00,
  `subtotal` decimal(12,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Dumping data for table `detail_transaksi`
--

INSERT INTO `detail_transaksi` (`id_detail`, `id_transaksi`, `id_menu`, `nama_menu`, `jumlah`, `harga_satuan`, `subtotal`) VALUES
(2, 2, 1, 'Kopi Hitam', 1, 5000.00, 5000.00),
(3, 2, 6, 'Pisang Goreng', 1, 5000.00, 5000.00),
(4, 3, 2, 'Kopi Susu', 1, 8000.00, 8000.00),
(5, 3, 4, 'Mie Rebus', 1, 8000.00, 8000.00),
(6, 3, 3, 'Teh Manis', 1, 4000.00, 4000.00),
(7, 4, 1, 'Kopi Hitam', 1, 5000.00, 5000.00),
(8, 4, 4, 'Mie Rebus', 1, 8000.00, 8000.00),
(9, 5, 2, 'Kopi Susu', 1, 8000.00, 8000.00),
(10, 5, 7, 'Roti Bakar', 1, 6000.00, 6000.00),
(11, 6, 1, 'Kopi Hitam', 1, 5000.00, 5000.00),
(12, 6, 6, 'Pisang Goreng', 1, 5000.00, 5000.00),
(13, 6, 5, 'Mie Goreng', 1, 8000.00, 8000.00),
(14, 7, 3, 'Teh Manis', 1, 4000.00, 4000.00),
(15, 7, 7, 'Roti Bakar', 1, 6000.00, 6000.00),
(16, 8, 3, 'Teh Manis', 1, 4000.00, 4000.00),
(17, 8, 7, 'Roti Bakar', 1, 6000.00, 6000.00),
(18, 9, 2, 'Kopi Susu', 1, 8000.00, 8000.00),
(19, 9, 1, 'Kopi Hitam', 1, 5000.00, 5000.00),
(20, 9, 3, 'Teh Manis', 1, 4000.00, 4000.00),
(21, 10, 1, 'Kopi Hitam', 1, 5000.00, 5000.00),
(22, 10, 2, 'Kopi Susu', 1, 8000.00, 8000.00),
(23, 10, 7, 'Roti Bakar', 1, 6000.00, 6000.00),
(24, 10, 6, 'Pisang Goreng', 1, 5000.00, 5000.00),
(25, 11, 3, 'Teh Manis', 1, 4000.00, 4000.00),
(26, 11, 5, 'Mie Goreng', 1, 8000.00, 8000.00);

-- --------------------------------------------------------

--
-- Table structure for table `hasil_apriori`
--

CREATE TABLE `hasil_apriori` (
  `id_hasil` int(11) NOT NULL,
  `id_proses` int(11) NOT NULL,
  `antecedent` text NOT NULL,
  `consequent` text NOT NULL,
  `support` float NOT NULL DEFAULT 0,
  `support_pct` float NOT NULL DEFAULT 0,
  `confidence` float NOT NULL DEFAULT 0,
  `confidence_pct` float NOT NULL DEFAULT 0,
  `lift` float NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Dumping data for table `hasil_apriori`
--

INSERT INTO `hasil_apriori` (`id_hasil`, `id_proses`, `antecedent`, `consequent`, `support`, `support_pct`, `confidence`, `confidence_pct`, `lift`) VALUES
(1, 6, 'Kopi Hitam', 'Pisang Goreng', 0.3, 30, 0.6, 60, 2),
(2, 6, 'Pisang Goreng', 'Kopi Hitam', 0.3, 30, 1, 100, 2);

-- --------------------------------------------------------

--
-- Table structure for table `kategori`
--

CREATE TABLE `kategori` (
  `id_kategori` int(11) NOT NULL,
  `nama_kategori` varchar(255) NOT NULL,
  `tgl_input` timestamp NOT NULL DEFAULT current_timestamp(),
  `tgl_update` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Dumping data for table `kategori`
--

INSERT INTO `kategori` (`id_kategori`, `nama_kategori`, `tgl_input`, `tgl_update`) VALUES
(1, 'Minuman Panas', '2026-04-22 09:17:19', '0000-00-00 00:00:00'),
(2, 'Minuman Dingin', '2026-04-22 09:17:19', '0000-00-00 00:00:00'),
(3, 'Makanan Berat', '2026-04-22 09:17:19', '0000-00-00 00:00:00'),
(4, 'Cemilan', '2026-04-22 09:17:19', '0000-00-00 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `menu`
--

CREATE TABLE `menu` (
  `id_menu` int(11) NOT NULL,
  `id_kategori` int(11) NOT NULL,
  `kode_menu` varchar(20) NOT NULL,
  `nama_menu` varchar(255) NOT NULL,
  `harga` decimal(10,2) NOT NULL DEFAULT 0.00,
  `stok` int(11) NOT NULL DEFAULT 0,
  `satuan` varchar(50) DEFAULT NULL,
  `deskripsi` text DEFAULT NULL,
  `foto` text DEFAULT NULL,
  `tgl_input` timestamp NOT NULL DEFAULT current_timestamp(),
  `tgl_update` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Dumping data for table `menu`
--

INSERT INTO `menu` (`id_menu`, `id_kategori`, `kode_menu`, `nama_menu`, `harga`, `stok`, `satuan`, `deskripsi`, `foto`, `tgl_input`, `tgl_update`) VALUES
(1, 1, 'MN001', 'Kopi Hitam', 5000.00, 100, 'Gelas', '', NULL, '2026-04-23 06:46:29', '2026-04-27 07:06:02'),
(2, 1, 'MN002', 'Kopi Susu', 8000.00, 100, 'Gelas', '', NULL, '2026-04-27 07:06:39', NULL),
(3, 1, 'MN003', 'Teh Manis', 4000.00, 100, 'Gelas', '', NULL, '2026-04-27 07:07:03', NULL),
(4, 3, 'MN004', 'Mie Rebus', 8000.00, 50, 'Porsi', '', NULL, '2026-04-27 07:07:31', NULL),
(5, 3, 'MN005', 'Mie Goreng', 8000.00, 50, 'Porsi', '', NULL, '2026-04-27 07:08:06', NULL),
(6, 4, 'MN006', 'Pisang Goreng', 5000.00, 50, 'Porsi', '', NULL, '2026-04-27 07:08:39', NULL),
(7, 4, 'MN007', 'Roti Bakar', 6000.00, 50, 'Porsi', '', NULL, '2026-04-27 07:08:59', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `toko`
--

CREATE TABLE `toko` (
  `id_toko` int(11) NOT NULL,
  `nama_toko` varchar(255) NOT NULL,
  `alamat_toko` text DEFAULT NULL,
  `tlp` varchar(20) DEFAULT NULL,
  `nama_pemilik` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Dumping data for table `toko`
--

INSERT INTO `toko` (`id_toko`, `nama_toko`, `alamat_toko`, `tlp`, `nama_pemilik`) VALUES
(1, 'Warkop Stadela', 'Depok, Jawa Barat', '0219', 'Ilham Rahman');

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `id_transaksi` int(11) NOT NULL,
  `kode_transaksi` varchar(30) NOT NULL,
  `id_user` int(11) NOT NULL,
  `tanggal_transaksi` date NOT NULL,
  `total` decimal(12,2) NOT NULL DEFAULT 0.00,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`id_transaksi`, `kode_transaksi`, `id_user`, `tanggal_transaksi`, `total`, `created_at`) VALUES
(2, 'TRX20260301-001', 1, '2026-03-01', 10000.00, '2026-04-27 07:10:29'),
(3, 'TRX20260301-002', 1, '2026-03-01', 20000.00, '2026-04-27 07:11:13'),
(4, 'TRX20260302-001', 1, '2026-03-02', 13000.00, '2026-04-27 07:11:38'),
(5, 'TRX20260303-001', 1, '2026-03-03', 14000.00, '2026-04-27 07:12:03'),
(6, 'TRX20260305-001', 1, '2026-03-05', 18000.00, '2026-04-27 07:12:31'),
(7, 'TRX20260306-001', 1, '2026-03-06', 10000.00, '2026-04-27 07:12:54'),
(8, 'TRX20260307-001', 1, '2026-03-07', 10000.00, '2026-04-27 07:13:37'),
(9, 'TRX20260308-001', 1, '2026-03-08', 17000.00, '2026-04-27 07:14:07'),
(10, 'TRX20260309-001', 1, '2026-03-09', 24000.00, '2026-04-27 07:14:33'),
(11, 'TRX20260310-001', 1, '2026-03-10', 12000.00, '2026-04-27 07:14:48');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id_user` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` enum('admin','kasir') NOT NULL DEFAULT 'kasir',
  `nama` varchar(255) NOT NULL,
  `alamat` text DEFAULT NULL,
  `telepon` varchar(20) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `foto` text DEFAULT NULL,
  `NIK` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id_user`, `username`, `password`, `role`, `nama`, `alamat`, `telepon`, `email`, `foto`, `NIK`, `created_at`) VALUES
(1, 'admin', '$2y$10$54e91ZdIK9q6loGevZWxT.33TQMG.SXAa38ICWpqlbgVJItlliDiK', 'admin', 'Administrator', 'bekasi', '123', 'admin@gmail.com', '1776849809_e2f295da4fd13633.png', '123', '2026-04-22 09:17:19');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `apriori_itemset`
--
ALTER TABLE `apriori_itemset`
  ADD PRIMARY KEY (`id_itemset`),
  ADD KEY `idx_id_proses` (`id_proses`),
  ADD KEY `idx_ukuran` (`ukuran`);

--
-- Indexes for table `apriori_proses`
--
ALTER TABLE `apriori_proses`
  ADD PRIMARY KEY (`id_proses`),
  ADD KEY `idx_id_user` (`id_user`),
  ADD KEY `idx_status` (`status`);

--
-- Indexes for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD PRIMARY KEY (`id_detail`),
  ADD KEY `idx_id_transaksi` (`id_transaksi`),
  ADD KEY `idx_id_menu` (`id_menu`);

--
-- Indexes for table `hasil_apriori`
--
ALTER TABLE `hasil_apriori`
  ADD PRIMARY KEY (`id_hasil`),
  ADD KEY `idx_id_proses` (`id_proses`),
  ADD KEY `idx_confidence` (`confidence`);

--
-- Indexes for table `kategori`
--
ALTER TABLE `kategori`
  ADD PRIMARY KEY (`id_kategori`);

--
-- Indexes for table `menu`
--
ALTER TABLE `menu`
  ADD PRIMARY KEY (`id_menu`),
  ADD UNIQUE KEY `uq_kode_menu` (`kode_menu`),
  ADD KEY `id_kategori` (`id_kategori`);

--
-- Indexes for table `toko`
--
ALTER TABLE `toko`
  ADD PRIMARY KEY (`id_toko`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id_transaksi`),
  ADD UNIQUE KEY `uq_kode_transaksi` (`kode_transaksi`),
  ADD KEY `idx_tanggal` (`tanggal_transaksi`),
  ADD KEY `idx_id_user` (`id_user`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `uq_username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `apriori_itemset`
--
ALTER TABLE `apriori_itemset`
  MODIFY `id_itemset` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `apriori_proses`
--
ALTER TABLE `apriori_proses`
  MODIFY `id_proses` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  MODIFY `id_detail` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `hasil_apriori`
--
ALTER TABLE `hasil_apriori`
  MODIFY `id_hasil` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `kategori`
--
ALTER TABLE `kategori`
  MODIFY `id_kategori` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `menu`
--
ALTER TABLE `menu`
  MODIFY `id_menu` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `toko`
--
ALTER TABLE `toko`
  MODIFY `id_toko` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `id_transaksi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `apriori_itemset`
--
ALTER TABLE `apriori_itemset`
  ADD CONSTRAINT `1` FOREIGN KEY (`id_proses`) REFERENCES `apriori_proses` (`id_proses`) ON DELETE CASCADE;

--
-- Constraints for table `apriori_proses`
--
ALTER TABLE `apriori_proses`
  ADD CONSTRAINT `1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`);

--
-- Constraints for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD CONSTRAINT `1` FOREIGN KEY (`id_transaksi`) REFERENCES `transaksi` (`id_transaksi`) ON DELETE CASCADE,
  ADD CONSTRAINT `2` FOREIGN KEY (`id_menu`) REFERENCES `menu` (`id_menu`);

--
-- Constraints for table `hasil_apriori`
--
ALTER TABLE `hasil_apriori`
  ADD CONSTRAINT `1` FOREIGN KEY (`id_proses`) REFERENCES `apriori_proses` (`id_proses`) ON DELETE CASCADE;

--
-- Constraints for table `menu`
--
ALTER TABLE `menu`
  ADD CONSTRAINT `1` FOREIGN KEY (`id_kategori`) REFERENCES `kategori` (`id_kategori`);

--
-- Constraints for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
