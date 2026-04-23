-- ============================================================
-- DATABASE: db_stadela
-- Warkop Stadela Depok - Data Mining Apriori
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_stadela
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE db_stadela;

-- ------------------------------------------------------------
-- 1. USERS (Admin + Kasir)
-- ------------------------------------------------------------
CREATE TABLE `users` (
  `id_user`    INT(11)                        NOT NULL AUTO_INCREMENT,
  `username`   VARCHAR(255)                   NOT NULL,
  `password`   CHAR(32)                       NOT NULL,           -- MD5
  `role`       ENUM('admin','kasir')          NOT NULL DEFAULT 'kasir',
  `nama`       VARCHAR(255)                   NOT NULL,
  `alamat`     TEXT                           DEFAULT NULL,
  `telepon`    VARCHAR(20)                    DEFAULT NULL,
  `email`      VARCHAR(255)                   DEFAULT NULL,
  `foto`       TEXT                           DEFAULT NULL,
  `NIK`        TEXT                           DEFAULT NULL,
  `created_at` TIMESTAMP                      NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (`id_user`),
  UNIQUE KEY `uq_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- 2. TOKO
-- ------------------------------------------------------------
CREATE TABLE `toko` (
  `id_toko`      INT(11)      NOT NULL AUTO_INCREMENT,
  `nama_toko`    VARCHAR(255) NOT NULL,
  `alamat_toko`  TEXT         DEFAULT NULL,
  `tlp`          VARCHAR(20)  DEFAULT NULL,
  `nama_pemilik` VARCHAR(255) DEFAULT NULL,

  PRIMARY KEY (`id_toko`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- 3. KATEGORI MENU
-- ------------------------------------------------------------
CREATE TABLE `kategori_menu` (
  `id_kategori`   INT(11)      NOT NULL AUTO_INCREMENT,
  `nama_kategori` VARCHAR(255) NOT NULL,
  `tgl_input`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (`id_kategori`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- 4. MENU (barang yang dijual warkop)
-- ------------------------------------------------------------
CREATE TABLE `menu` (
  `id_menu`     INT(11)        NOT NULL AUTO_INCREMENT,
  `id_kategori` INT(11)        NOT NULL,
  `kode_menu`   VARCHAR(20)    NOT NULL,
  `nama_menu`   VARCHAR(255)   NOT NULL,
  `harga`       DECIMAL(10,2)  NOT NULL DEFAULT 0,
  `stok`        INT(11)        NOT NULL DEFAULT 0,
  `satuan`      VARCHAR(50)    DEFAULT NULL,
  `deskripsi`   TEXT           DEFAULT NULL,
  `foto`        TEXT           DEFAULT NULL,
  `tgl_input`   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tgl_update`  TIMESTAMP      NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id_menu`),
  UNIQUE KEY `uq_kode_menu` (`kode_menu`),
  FOREIGN KEY (`id_kategori`) REFERENCES `kategori_menu`(`id_kategori`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- 5. TRANSAKSI (header penjualan)
-- ------------------------------------------------------------
CREATE TABLE `transaksi` (
  `id_transaksi`  INT(11)       NOT NULL AUTO_INCREMENT,
  `kode_transaksi`VARCHAR(30)   NOT NULL,
  `id_user`       INT(11)       NOT NULL,              -- kasir yang input
  `total`         DECIMAL(12,2) NOT NULL DEFAULT 0,
  `tanggal`       DATE          NOT NULL,
  `periode`       VARCHAR(10)   NOT NULL,              -- format: MM-YYYY
  `created_at`    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (`id_transaksi`),
  UNIQUE KEY `uq_kode_transaksi` (`kode_transaksi`),
  FOREIGN KEY (`id_user`) REFERENCES `users`(`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- 6. DETAIL TRANSAKSI (item per transaksi)
-- Tabel kunci untuk Apriori — satu baris = satu item dalam satu transaksi
-- ------------------------------------------------------------
CREATE TABLE `detail_transaksi` (
  `id_detail`     INT(11)       NOT NULL AUTO_INCREMENT,
  `id_transaksi`  INT(11)       NOT NULL,
  `id_menu`       INT(11)       NOT NULL,
  `nama_menu`     VARCHAR(255)  NOT NULL,              -- snapshot nama saat transaksi
  `jumlah`        INT(11)       NOT NULL DEFAULT 1,
  `harga_satuan`  DECIMAL(10,2) NOT NULL DEFAULT 0,
  `subtotal`      DECIMAL(12,2) NOT NULL DEFAULT 0,

  PRIMARY KEY (`id_detail`),
  FOREIGN KEY (`id_transaksi`) REFERENCES `transaksi`(`id_transaksi`) ON DELETE CASCADE,
  FOREIGN KEY (`id_menu`)      REFERENCES `menu`(`id_menu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- 7. APRIORI PROSES (log setiap kali admin menjalankan mining)
-- ------------------------------------------------------------
CREATE TABLE `apriori_proses` (
  `id_proses`      INT(11)      NOT NULL AUTO_INCREMENT,
  `id_user`        INT(11)      NOT NULL,              -- admin yang menjalankan
  `min_support`    FLOAT        NOT NULL,              -- misal: 0.30 = 30%
  `min_confidence` FLOAT        NOT NULL,              -- misal: 0.50 = 50%
  `periode_dari`   VARCHAR(10)  DEFAULT NULL,          -- filter periode awal
  `periode_sampai` VARCHAR(10)  DEFAULT NULL,          -- filter periode akhir
  `total_transaksi`INT(11)      NOT NULL DEFAULT 0,
  `status`         ENUM('proses','selesai','gagal') NOT NULL DEFAULT 'proses',
  `tgl_proses`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (`id_proses`),
  FOREIGN KEY (`id_user`) REFERENCES `users`(`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- 8. APRIORI FREQUENT ITEMSET (hasil C1, C2, C3...)
-- ------------------------------------------------------------
CREATE TABLE `apriori_itemset` (
  `id_itemset`  INT(11)      NOT NULL AUTO_INCREMENT,
  `id_proses`   INT(11)      NOT NULL,
  `itemset`     TEXT         NOT NULL,                 -- misal: "Kopi Hitam, Pisang Goreng"
  `ukuran`      INT(11)      NOT NULL DEFAULT 1,       -- C1=1, C2=2, C3=3
  `jumlah`      INT(11)      NOT NULL DEFAULT 0,       -- jumlah transaksi yang mengandung
  `support`     FLOAT        NOT NULL DEFAULT 0,       -- nilai support (0.0 - 1.0)
  `support_pct` FLOAT        NOT NULL DEFAULT 0,       -- dalam persen (0 - 100)

  PRIMARY KEY (`id_itemset`),
  FOREIGN KEY (`id_proses`) REFERENCES `apriori_proses`(`id_proses`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- 9. APRIORI HASIL (association rules final)
-- ------------------------------------------------------------
CREATE TABLE `apriori_hasil` (
  `id_hasil`    INT(11)      NOT NULL AUTO_INCREMENT,
  `id_proses`   INT(11)      NOT NULL,
  `antecedent`  TEXT         NOT NULL,                 -- IF: "Kopi Hitam"
  `consequent`  TEXT         NOT NULL,                 -- THEN: "Pisang Goreng"
  `support`     FLOAT        NOT NULL DEFAULT 0,
  `support_pct` FLOAT        NOT NULL DEFAULT 0,
  `confidence`  FLOAT        NOT NULL DEFAULT 0,
  `confidence_pct` FLOAT     NOT NULL DEFAULT 0,
  `lift`        FLOAT        NOT NULL DEFAULT 0,

  PRIMARY KEY (`id_hasil`),
  FOREIGN KEY (`id_proses`) REFERENCES `apriori_proses`(`id_proses`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- SEED DATA
-- ============================================================

INSERT INTO `toko` (`nama_toko`, `alamat_toko`, `tlp`, `nama_pemilik`)
VALUES ('Warkop Stadela Depok', 'Depok, Jawa Barat', NULL, NULL);

INSERT INTO `users` (`username`, `password`, `role`, `nama`)
VALUES
  ('admin',  MD5('admin123'),  'admin',  'Administrator'),
  ('kasir',  MD5('kasir123'),  'kasir',  'Kasir');

INSERT INTO `kategori_menu` (`nama_kategori`) VALUES
  ('Minuman Panas'),
  ('Minuman Dingin'),
  ('Makanan Berat'),
  ('Cemilan');