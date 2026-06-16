package com.stadela.util;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility untuk compile JRXML, fill data, lalu tampilkan preview
 * (JasperViewer) yang sudah built-in Print + Save PDF.
 */
public class ReportUtil {

    /**
     * Compile JRXML, isi data, lalu tampilkan JasperViewer (preview + print + download).
     *
     * @param jrxmlName nama file di /reports/ (contoh: "laporan_transaksi.jrxml")
     * @param params    parameter ekstra (boleh null)
     * @param data      koleksi bean sebagai data source
     * @param title     judul window preview
     */
    public static void preview(String jrxmlName,
                               Map<String, Object> params,
                               Collection<?> data,
                               String title) throws Exception {

        // 1. Load JRXML dari classpath
        String path = "/reports/" + jrxmlName;
        InputStream is = ReportUtil.class.getResourceAsStream(path);
        if (is == null) throw new Exception("Template tidak ditemukan: " + path);

        // 2. Compile → Fill
        JasperReport report = JasperCompileManager.compileReport(is);
        is.close();

        if (params == null) params = new HashMap<>();
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(data);
        JasperPrint print = JasperFillManager.fillReport(report, params, ds);

        // 3. Tampilkan JasperViewer (punya toolbar: Print + Save As PDF/XLS/dll)
        JasperViewer viewer = new JasperViewer(print, false);
        viewer.setTitle(title != null ? title : "Preview Laporan");
        viewer.setZoomRatio(1.0f);
        viewer.setVisible(true);
    }

    /** Shortcut tanpa params ekstra */
    public static void preview(String jrxmlName,
                               Collection<?> data,
                               String title) throws Exception {
        preview(jrxmlName, null, data, title);
    }
}
