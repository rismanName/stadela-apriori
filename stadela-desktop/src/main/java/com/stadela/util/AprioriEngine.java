package com.stadela.util;

import com.stadela.dao.AprioriDAO;

import java.sql.Date;
import java.util.*;

/**
 * Port dari proses.php — Apriori sampai 3-itemset.
 */
public class AprioriEngine {

    public static class Rule {
        public final String antecedent;
        public final String consequent;
        public final float  support;
        public final float  confidence;
        public final float  lift;

        Rule(String ant, String con, float support, float confidence, float lift) {
            this.antecedent = ant;
            this.consequent = con;
            this.support    = support;
            this.confidence = confidence;
            this.lift       = lift;
        }
    }

    public static class Result {
        public final List<Rule> rules;
        public final int        totalTransaksi;
        public final String     message;

        Result(List<Rule> rules, int total, String msg) {
            this.rules          = rules;
            this.totalTransaksi = total;
            this.message        = msg;
        }
    }

    /**
     * Jalankan Apriori dan simpan hasil ke DB.
     * @return idProses hasil insert
     */
    public static int run(int idUser, float minSupport, float minConfidence,
                          Date dari, Date sampai, AprioriDAO dao) throws Exception {

        // 1. Catat proses
        int idProses = dao.insertProses(idUser, minSupport, minConfidence, dari, sampai);

        // 2. Ambil data transaksi
        Map<Integer, List<String>> rawData = dao.getTransaksiItems(dari, sampai);

        if (rawData.isEmpty()) {
            dao.updateStatusProses(idProses, "gagal", 0);
            throw new IllegalStateException("Tidak ada data transaksi pada periode tersebut.");
        }

        // Bersihkan: unik per transaksi
        Map<Integer, List<String>> transaksi = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<String>> e : rawData.entrySet()) {
            List<String> unique = new ArrayList<>(new LinkedHashSet<>(e.getValue()));
            transaksi.put(e.getKey(), unique);
        }

        int N = transaksi.size();

        // ── C1 → L1 ──────────────────────────────────────────────
        Map<String, Integer> c1 = new LinkedHashMap<>();
        for (List<String> items : transaksi.values()) {
            for (String item : items) {
                c1.merge(item, 1, Integer::sum);
            }
        }
        Map<String, Integer> l1 = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : c1.entrySet()) {
            if ((float) e.getValue() / N >= minSupport) l1.put(e.getKey(), e.getValue());
        }
        // Simpan L1
        for (Map.Entry<String, Integer> e : l1.entrySet()) {
            float sup = (float) e.getValue() / N;
            dao.insertItemset(idProses, e.getKey(), 1, e.getValue(), sup);
        }

        // ── C2 → L2 ──────────────────────────────────────────────
        List<String> l1Keys = new ArrayList<>(l1.keySet());
        Map<String, Integer> l2 = new LinkedHashMap<>();

        for (int i = 0; i < l1Keys.size(); i++) {
            for (int j = i + 1; j < l1Keys.size(); j++) {
                String[] pair = {l1Keys.get(i), l1Keys.get(j)};
                Arrays.sort(pair);
                String key = pair[0] + "||" + pair[1];
                int count = 0;
                for (List<String> items : transaksi.values()) {
                    if (items.contains(pair[0]) && items.contains(pair[1])) count++;
                }
                float sup = (float) count / N;
                if (sup >= minSupport) {
                    l2.put(key, count);
                    dao.insertItemset(idProses, pair[0] + ", " + pair[1], 2, count, sup);
                }
            }
        }

        // ── C3 → L3 ──────────────────────────────────────────────
        List<String> l2Keys = new ArrayList<>(l2.keySet());
        Map<String, Integer> l3 = new LinkedHashMap<>();

        for (int i = 0; i < l2Keys.size(); i++) {
            for (int j = i + 1; j < l2Keys.size(); j++) {
                List<String> union = new ArrayList<>(new LinkedHashSet<>(
                        concat(l2Keys.get(i).split("\\|\\|"), l2Keys.get(j).split("\\|\\|"))
                ));
                if (union.size() != 3) continue;
                Collections.sort(union);
                String key = String.join("||", union);
                if (l3.containsKey(key)) continue;
                int count = 0;
                for (List<String> items : transaksi.values()) {
                    if (items.containsAll(union)) count++;
                }
                float sup = (float) count / N;
                if (sup >= minSupport) {
                    l3.put(key, count);
                    dao.insertItemset(idProses, String.join(", ", union), 3, count, sup);
                }
            }
        }

        // ── Association Rules ────────────────────────────────────
        List<Rule> rules = new ArrayList<>();

        // Dari L2
        for (Map.Entry<String, Integer> e : l2.entrySet()) {
            String[] parts = e.getKey().split("\\|\\|");
            String a = parts[0], b = parts[1];
            float sup = (float) e.getValue() / N;
            // a → b
            float confAB = (float) e.getValue() / c1.get(a);
            if (confAB >= minConfidence) {
                float lift = confAB / ((float) c1.get(b) / N);
                rules.add(new Rule(a, b, sup, confAB, lift));
                dao.insertHasil(idProses, a, b, sup, confAB, lift);
            }
            // b → a
            float confBA = (float) e.getValue() / c1.get(b);
            if (confBA >= minConfidence) {
                float lift = confBA / ((float) c1.get(a) / N);
                rules.add(new Rule(b, a, sup, confBA, lift));
                dao.insertHasil(idProses, b, a, sup, confBA, lift);
            }
        }

        // Dari L3
        for (Map.Entry<String, Integer> e : l3.entrySet()) {
            List<String> items = new ArrayList<>(Arrays.asList(e.getKey().split("\\|\\|")));
            float sup = (float) e.getValue() / N;
            for (int idx = 0; idx < items.size(); idx++) {
                String con = items.get(idx);
                List<String> ant = new ArrayList<>(items);
                ant.remove(idx);
                // hitung count antecedent
                int antCount = 0;
                for (List<String> trx : transaksi.values()) {
                    if (trx.containsAll(ant)) antCount++;
                }
                if (antCount == 0) continue;
                float conf = (float) e.getValue() / antCount;
                if (conf >= minConfidence) {
                    float lift = conf / ((float) c1.getOrDefault(con, 0) / N);
                    String antStr = String.join(", ", ant);
                    rules.add(new Rule(antStr, con, sup, conf, lift));
                    dao.insertHasil(idProses, antStr, con, sup, conf, lift);
                }
            }
        }

        dao.updateStatusProses(idProses, "selesai", N);
        return idProses;
    }

    private static List<String> concat(String[] a, String[] b) {
        List<String> list = new ArrayList<>(Arrays.asList(a));
        list.addAll(Arrays.asList(b));
        return list;
    }
}
