<?php
declare(strict_types=1);

if (!function_exists('stadela_clean_text')) {
    function stadela_clean_text(?string $value): string
    {
        return trim(strip_tags((string) $value));
    }
}

if (!function_exists('stadela_normalize_transaction_date')) {
    function stadela_normalize_transaction_date(?string $value): string
    {
        $raw = trim((string) $value);
        $date = DateTimeImmutable::createFromFormat('Y-m-d', $raw);
        if (!$date || $date->format('Y-m-d') !== $raw) {
            return date('Y-m-d');
        }

        return $date->format('Y-m-d');
    }
}

if (!function_exists('stadela_transaction_period')) {
    function stadela_transaction_period(string $date): string
    {
        $normalized = stadela_normalize_transaction_date($date);
        return date('m-Y', strtotime($normalized));
    }
}

if (!function_exists('stadela_transaction_affects_stock')) {
    function stadela_transaction_affects_stock(string $kodeTransaksi): bool
    {
        return strpos(strtoupper($kodeTransaksi), 'IMP-') !== 0;
    }
}

if (!function_exists('stadela_generate_transaction_code')) {
    function stadela_generate_transaction_code(PDO $db, string $date, string $prefix = 'TRX'): string
    {
        $normalizedDate = stadela_normalize_transaction_date($date);
        $datePart = date('Ymd', strtotime($normalizedDate));
        $prefix = strtoupper(preg_replace('/[^A-Za-z]/', '', $prefix) ?: 'TRX');
        $baseCode = $prefix . '-' . $datePart . '-';

        $stmt = $db->prepare('
            SELECT kode_transaksi
            FROM transaksi
            WHERE kode_transaksi LIKE ?
            ORDER BY id_transaksi DESC
            LIMIT 1
        ');
        $stmt->execute([$baseCode . '%']);
        $lastCode = (string) ($stmt->fetchColumn() ?: '');

        $sequence = 1;
        if (preg_match('/-(\d{4})$/', $lastCode, $matches) === 1) {
            $sequence = ((int) $matches[1]) + 1;
        }

        return $baseCode . str_pad((string) $sequence, 4, '0', STR_PAD_LEFT);
    }
}

if (!function_exists('stadela_fetch_menu_map')) {
    function stadela_fetch_menu_map(PDO $db, array $menuIds): array
    {
        $menuIds = array_values(array_unique(array_map('intval', $menuIds)));
        $menuIds = array_filter($menuIds, static fn ($id): bool => $id > 0);
        if ($menuIds === []) {
            return [];
        }

        $placeholders = implode(',', array_fill(0, count($menuIds), '?'));
        $stmt = $db->prepare("
            SELECT id_menu, kode_menu, nama_menu, harga, stok
            FROM menu
            WHERE id_menu IN ($placeholders)
        ");
        $stmt->execute($menuIds);

        $menuMap = [];
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $menuMap[(int) $row['id_menu']] = $row;
        }

        return $menuMap;
    }
}

if (!function_exists('stadela_normalize_transaction_lines')) {
    function stadela_normalize_transaction_lines(PDO $db, array $menuIds, array $quantities): array
    {
        $aggregated = [];
        $totalInput = max(count($menuIds), count($quantities));

        for ($index = 0; $index < $totalInput; $index++) {
            $menuId = isset($menuIds[$index]) ? (int) $menuIds[$index] : 0;
            $quantity = isset($quantities[$index]) ? (int) $quantities[$index] : 0;

            if ($menuId <= 0 || $quantity <= 0) {
                continue;
            }

            $aggregated[$menuId] = ($aggregated[$menuId] ?? 0) + $quantity;
        }

        if ($aggregated === []) {
            throw new InvalidArgumentException('Minimal satu item transaksi wajib diisi.');
        }

        $menuMap = stadela_fetch_menu_map($db, array_keys($aggregated));
        $lines = [];

        foreach ($aggregated as $menuId => $quantity) {
            if (!isset($menuMap[$menuId])) {
                throw new InvalidArgumentException('Menu yang dipilih tidak ditemukan.');
            }

            $menu = $menuMap[$menuId];
            $price = (float) $menu['harga'];
            $lines[] = [
                'id_menu' => $menuId,
                'kode_menu' => (string) $menu['kode_menu'],
                'nama_menu' => (string) $menu['nama_menu'],
                'jumlah' => $quantity,
                'harga_satuan' => $price,
                'subtotal' => $price * $quantity,
            ];
        }

        usort($lines, static function (array $left, array $right): int {
            return strcmp($left['nama_menu'], $right['nama_menu']);
        });

        return $lines;
    }
}

if (!function_exists('stadela_calculate_transaction_total')) {
    function stadela_calculate_transaction_total(array $lines): float
    {
        return array_reduce(
            $lines,
            static fn (float $carry, array $line): float => $carry + (float) $line['subtotal'],
            0.0
        );
    }
}

if (!function_exists('stadela_get_transaction')) {
    function stadela_get_transaction(PDO $db, int $transactionId): ?array
    {
        $stmt = $db->prepare('
            SELECT t.*, u.nama AS nama_user, u.username, u.role
            FROM transaksi t
            INNER JOIN users u ON u.id_user = t.id_user
            WHERE t.id_transaksi = ?
            LIMIT 1
        ');
        $stmt->execute([$transactionId]);
        $transaction = $stmt->fetch(PDO::FETCH_ASSOC);

        return $transaction ?: null;
    }
}

if (!function_exists('stadela_get_transaction_details')) {
    function stadela_get_transaction_details(PDO $db, int $transactionId): array
    {
        $stmt = $db->prepare('
            SELECT dt.*, m.kode_menu
            FROM detail_transaksi dt
            LEFT JOIN menu m ON m.id_menu = dt.id_menu
            WHERE dt.id_transaksi = ?
            ORDER BY dt.nama_menu ASC, dt.id_detail ASC
        ');
        $stmt->execute([$transactionId]);

        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }
}

if (!function_exists('stadela_apply_stock_delta')) {
    function stadela_apply_stock_delta(PDO $db, array $deltaMap): void
    {
        if ($deltaMap === []) {
            return;
        }

        $menuMap = stadela_fetch_menu_map($db, array_keys($deltaMap));
        foreach ($deltaMap as $menuId => $delta) {
            if ($delta <= 0) {
                continue;
            }

            $availableStock = isset($menuMap[$menuId]) ? (int) $menuMap[$menuId]['stok'] : 0;
            if ($availableStock < $delta) {
                $menuName = isset($menuMap[$menuId]) ? (string) $menuMap[$menuId]['nama_menu'] : 'Menu';
                throw new RuntimeException('Stok menu "' . $menuName . '" tidak mencukupi untuk transaksi ini.');
            }
        }

        $stmtIncrease = $db->prepare('UPDATE menu SET stok = stok + ? WHERE id_menu = ?');
        $stmtDecrease = $db->prepare('UPDATE menu SET stok = stok - ? WHERE id_menu = ?');

        foreach ($deltaMap as $menuId => $delta) {
            if ($delta > 0) {
                $stmtDecrease->execute([$delta, $menuId]);
            } elseif ($delta < 0) {
                $stmtIncrease->execute([abs($delta), $menuId]);
            }
        }
    }
}

if (!function_exists('stadela_insert_transaction_details')) {
    function stadela_insert_transaction_details(PDO $db, int $transactionId, array $lines): void
    {
        $stmt = $db->prepare('
            INSERT INTO detail_transaksi
                (id_transaksi, id_menu, nama_menu, jumlah, harga_satuan, subtotal)
            VALUES (?, ?, ?, ?, ?, ?)
        ');

        foreach ($lines as $line) {
            $stmt->execute([
                $transactionId,
                $line['id_menu'],
                $line['nama_menu'],
                $line['jumlah'],
                $line['harga_satuan'],
                $line['subtotal'],
            ]);
        }
    }
}

if (!function_exists('stadela_create_transaction')) {
    function stadela_create_transaction(
        PDO $db,
        int $userId,
        string $date,
        array $lines,
        ?string $kodeTransaksi = null,
        bool $adjustStock = true
    ): int {
        if ($userId <= 0) {
            throw new InvalidArgumentException('Pengguna transaksi tidak valid.');
        }
        if ($lines === []) {
            throw new InvalidArgumentException('Detail transaksi kosong.');
        }

        $normalizedDate = stadela_normalize_transaction_date($date);
        $code = trim((string) $kodeTransaksi);
        if ($code === '') {
            $code = stadela_generate_transaction_code($db, $normalizedDate, $adjustStock ? 'TRX' : 'IMP');
        }

        $total = stadela_calculate_transaction_total($lines);
        $periode = stadela_transaction_period($normalizedDate);
        $affectsStock = $adjustStock && stadela_transaction_affects_stock($code);
        $stockDelta = [];

        if ($affectsStock) {
            foreach ($lines as $line) {
                $stockDelta[(int) $line['id_menu']] = ($stockDelta[(int) $line['id_menu']] ?? 0) + (int) $line['jumlah'];
            }
        }

        $db->beginTransaction();

        try {
            if ($affectsStock) {
                stadela_apply_stock_delta($db, $stockDelta);
            }

            $stmt = $db->prepare('
                INSERT INTO transaksi (kode_transaksi, id_user, total, tanggal, periode, created_at)
                VALUES (?, ?, ?, ?, ?, NOW())
            ');
            $stmt->execute([$code, $userId, $total, $normalizedDate, $periode]);
            $transactionId = (int) $db->lastInsertId();

            stadela_insert_transaction_details($db, $transactionId, $lines);
            $db->commit();

            return $transactionId;
        } catch (Throwable $exception) {
            if ($db->inTransaction()) {
                $db->rollBack();
            }
            throw $exception;
        }
    }
}

if (!function_exists('stadela_update_transaction')) {
    function stadela_update_transaction(PDO $db, int $transactionId, string $date, array $lines): void
    {
        $transaction = stadela_get_transaction($db, $transactionId);
        if ($transaction === null) {
            throw new RuntimeException('Transaksi tidak ditemukan.');
        }
        if ($lines === []) {
            throw new InvalidArgumentException('Detail transaksi kosong.');
        }

        $normalizedDate = stadela_normalize_transaction_date($date);
        $oldDetails = stadela_get_transaction_details($db, $transactionId);
        $oldQtyMap = [];
        foreach ($oldDetails as $detail) {
            $menuId = (int) $detail['id_menu'];
            $oldQtyMap[$menuId] = ($oldQtyMap[$menuId] ?? 0) + (int) $detail['jumlah'];
        }

        $newQtyMap = [];
        foreach ($lines as $line) {
            $menuId = (int) $line['id_menu'];
            $newQtyMap[$menuId] = ($newQtyMap[$menuId] ?? 0) + (int) $line['jumlah'];
        }

        $deltaMap = [];
        $menuIds = array_values(array_unique(array_merge(array_keys($oldQtyMap), array_keys($newQtyMap))));
        foreach ($menuIds as $menuId) {
            $deltaMap[$menuId] = ($newQtyMap[$menuId] ?? 0) - ($oldQtyMap[$menuId] ?? 0);
        }

        $db->beginTransaction();

        try {
            if (stadela_transaction_affects_stock((string) $transaction['kode_transaksi'])) {
                stadela_apply_stock_delta($db, $deltaMap);
            }

            $db->prepare('DELETE FROM detail_transaksi WHERE id_transaksi = ?')->execute([$transactionId]);
            stadela_insert_transaction_details($db, $transactionId, $lines);

            $stmt = $db->prepare('
                UPDATE transaksi
                SET total = ?, tanggal = ?, periode = ?
                WHERE id_transaksi = ?
            ');
            $stmt->execute([
                stadela_calculate_transaction_total($lines),
                $normalizedDate,
                stadela_transaction_period($normalizedDate),
                $transactionId,
            ]);

            $db->commit();
        } catch (Throwable $exception) {
            if ($db->inTransaction()) {
                $db->rollBack();
            }
            throw $exception;
        }
    }
}

if (!function_exists('stadela_delete_transaction')) {
    function stadela_delete_transaction(PDO $db, int $transactionId): void
    {
        $transaction = stadela_get_transaction($db, $transactionId);
        if ($transaction === null) {
            throw new RuntimeException('Transaksi tidak ditemukan.');
        }

        $details = stadela_get_transaction_details($db, $transactionId);
        $stockDelta = [];
        foreach ($details as $detail) {
            $menuId = (int) $detail['id_menu'];
            $stockDelta[$menuId] = ($stockDelta[$menuId] ?? 0) - (int) $detail['jumlah'];
        }

        $db->beginTransaction();

        try {
            if (stadela_transaction_affects_stock((string) $transaction['kode_transaksi'])) {
                stadela_apply_stock_delta($db, $stockDelta);
            }

            $db->prepare('DELETE FROM transaksi WHERE id_transaksi = ?')->execute([$transactionId]);
            $db->commit();
        } catch (Throwable $exception) {
            if ($db->inTransaction()) {
                $db->rollBack();
            }
            throw $exception;
        }
    }
}
