<?php
declare(strict_types=1);

if (!function_exists('stadela_parse_period_value')) {
    function stadela_parse_period_value(?string $value): ?array
    {
        $raw = trim((string) $value);
        if ($raw === '') {
            return null;
        }

        if (preg_match('/^(0[1-9]|1[0-2])-(\d{4})$/', $raw, $matches) !== 1) {
            throw new InvalidArgumentException('Format periode harus MM-YYYY.');
        }

        $month = (int) $matches[1];
        $year = (int) $matches[2];
        $startDate = sprintf('%04d-%02d-01', $year, $month);
        $endDate = date('Y-m-t', strtotime($startDate));

        return [
            'label' => $raw,
            'start_date' => $startDate,
            'end_date' => $endDate,
        ];
    }
}

if (!function_exists('stadela_resolve_period_range')) {
    function stadela_resolve_period_range(?string $from, ?string $to): array
    {
        $fromPeriod = stadela_parse_period_value($from);
        $toPeriod = stadela_parse_period_value($to);

        $dateFrom = $fromPeriod['start_date'] ?? null;
        $dateTo = $toPeriod['end_date'] ?? null;

        if ($dateFrom !== null && $dateTo !== null && strtotime($dateFrom) > strtotime($dateTo)) {
            throw new InvalidArgumentException('Periode dari tidak boleh lebih besar dari periode sampai.');
        }

        return [
            'periode_dari' => $fromPeriod['label'] ?? null,
            'periode_sampai' => $toPeriod['label'] ?? null,
            'date_from' => $dateFrom,
            'date_to' => $dateTo,
        ];
    }
}

if (!function_exists('stadela_fetch_apriori_transactions')) {
    function stadela_fetch_apriori_transactions(PDO $db, ?string $dateFrom = null, ?string $dateTo = null): array
    {
        $conditions = [];
        $params = [];

        $where = $conditions === [] ? '' : 'WHERE ' . implode(' AND ', $conditions);

        $stmt = $db->prepare("
            SELECT
                t.id_transaksi,
                t.kode_transaksi,
                dt.id_menu,
                dt.nama_menu
            FROM transaksi t
            INNER JOIN detail_transaksi dt ON dt.id_transaksi = t.id_transaksi
            $where
            ORDER BY t.id_transaksi ASC, dt.nama_menu ASC, dt.id_menu ASC
        ");
        $stmt->execute($params);

        $transactions = [];
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $transactionId = (int) $row['id_transaksi'];
            if (!isset($transactions[$transactionId])) {
                $transactions[$transactionId] = [
                    'id_transaksi' => $transactionId,
                    'kode_transaksi' => (string) $row['kode_transaksi'],
                    'items' => [],
                ];
            }

            $itemId = (int) $row['id_menu'];
            $transactions[$transactionId]['items'][$itemId] = (string) $row['nama_menu'];
        }

        $normalized = [];
        foreach ($transactions as $transaction) {
            if ($transaction['items'] === []) {
                continue;
            }

            ksort($transaction['items']);
            $normalized[] = $transaction;
        }

        return $normalized;
    }
}

if (!function_exists('stadela_combination_recursive')) {
    function stadela_combination_recursive(array $items, int $size, int $offset = 0, array $current = []): array
    {
        if (count($current) === $size) {
            return [$current];
        }

        $combinations = [];
        $remaining = count($items) - ($size - count($current));
        for ($index = $offset; $index <= $remaining; $index++) {
            $next = $current;
            $next[] = $items[$index];
            foreach (stadela_combination_recursive($items, $size, $index + 1, $next) as $combination) {
                $combinations[] = $combination;
            }
        }

        return $combinations;
    }
}

if (!function_exists('stadela_itemset_key')) {
    function stadela_itemset_key(array $itemIds): string
    {
        sort($itemIds, SORT_NUMERIC);
        return implode('|', $itemIds);
    }
}

if (!function_exists('stadela_itemset_names')) {
    function stadela_itemset_names(array $itemIds, array $nameMap): array
    {
        $names = [];
        foreach ($itemIds as $itemId) {
            $names[] = $nameMap[$itemId] ?? ('Menu #' . $itemId);
        }

        return $names;
    }
}

if (!function_exists('stadela_run_apriori')) {
    function stadela_run_apriori(array $transactions, float $minSupport, float $minConfidence): array
    {
        if ($minSupport <= 0 || $minSupport > 1 || $minConfidence <= 0 || $minConfidence > 1) {
            throw new InvalidArgumentException('Parameter support dan confidence harus berada di antara 0 dan 1.');
        }

        $totalTransactions = count($transactions);
        if ($totalTransactions === 0) {
            return [
                'total_transaksi' => 0,
                'frequent_itemsets' => [],
                'association_rules' => [],
            ];
        }

        $transactionItems = [];
        $nameMap = [];
        $maxSize = 0;

        foreach ($transactions as $transaction) {
            $items = array_map('intval', array_keys($transaction['items']));
            sort($items, SORT_NUMERIC);
            if ($items === []) {
                continue;
            }

            $transactionItems[] = $items;
            $maxSize = max($maxSize, count($items));

            foreach ($transaction['items'] as $itemId => $itemName) {
                $nameMap[(int) $itemId] = (string) $itemName;
            }
        }

        $supportCountMap = [];
        $supportValueMap = [];
        $frequentItemsets = [];

        for ($size = 1; $size <= $maxSize; $size++) {
            $candidateCounts = [];

            foreach ($transactionItems as $items) {
                if (count($items) < $size) {
                    continue;
                }

                foreach (stadela_combination_recursive($items, $size) as $combination) {
                    $key = stadela_itemset_key($combination);
                    $candidateCounts[$key] = ($candidateCounts[$key] ?? 0) + 1;
                }
            }

            $sizeHasFrequent = false;
            foreach ($candidateCounts as $key => $count) {
                $support = $count / $totalTransactions;
                if ($support + 1e-12 < $minSupport) {
                    continue;
                }

                $itemIds = array_map('intval', explode('|', $key));
                $names = stadela_itemset_names($itemIds, $nameMap);
                $sizeHasFrequent = true;
                $supportCountMap[$key] = $count;
                $supportValueMap[$key] = $support;
                $frequentItemsets[$key] = [
                    'item_ids' => $itemIds,
                    'itemset' => implode(', ', $names),
                    'ukuran' => count($itemIds),
                    'jumlah' => $count,
                    'support' => $support,
                    'support_pct' => $support * 100,
                ];
            }

            if (!$sizeHasFrequent) {
                break;
            }
        }

        $associationRules = [];
        foreach ($frequentItemsets as $key => $itemset) {
            $itemIds = $itemset['item_ids'];
            $itemCount = count($itemIds);
            if ($itemCount < 2) {
                continue;
            }

            for ($antecedentSize = 1; $antecedentSize < $itemCount; $antecedentSize++) {
                foreach (stadela_combination_recursive($itemIds, $antecedentSize) as $antecedent) {
                    $antecedentKey = stadela_itemset_key($antecedent);
                    if (!isset($supportValueMap[$antecedentKey])) {
                        continue;
                    }

                    $consequent = array_values(array_diff($itemIds, $antecedent));
                    $consequentKey = stadela_itemset_key($consequent);
                    if (!isset($supportValueMap[$consequentKey]) || $supportValueMap[$antecedentKey] <= 0) {
                        continue;
                    }

                    $confidence = $supportValueMap[$key] / $supportValueMap[$antecedentKey];
                    if ($confidence + 1e-12 < $minConfidence) {
                        continue;
                    }

                    $consequentSupport = $supportValueMap[$consequentKey];
                    $lift = $consequentSupport > 0 ? ($confidence / $consequentSupport) : 0.0;

                    $ruleKey = $antecedentKey . '=>' . $consequentKey;
                    $associationRules[$ruleKey] = [
                        'antecedent' => implode(', ', stadela_itemset_names($antecedent, $nameMap)),
                        'consequent' => implode(', ', stadela_itemset_names($consequent, $nameMap)),
                        'support' => $supportValueMap[$key],
                        'support_pct' => $supportValueMap[$key] * 100,
                        'confidence' => $confidence,
                        'confidence_pct' => $confidence * 100,
                        'lift' => $lift,
                    ];
                }
            }
        }

        usort($frequentItemsets, static function (array $left, array $right): int {
            return [$left['ukuran'], -$left['jumlah'], $left['itemset']]
                <=> [$right['ukuran'], -$right['jumlah'], $right['itemset']];
        });

        usort($associationRules, static function (array $left, array $right): int {
            return [-$left['confidence'], -$left['lift'], -$left['support'], $left['antecedent'], $left['consequent']]
                <=> [-$right['confidence'], -$right['lift'], -$right['support'], $right['antecedent'], $right['consequent']];
        });

        return [
            'total_transaksi' => $totalTransactions,
            'frequent_itemsets' => array_values($frequentItemsets),
            'association_rules' => array_values($associationRules),
        ];
    }
}
