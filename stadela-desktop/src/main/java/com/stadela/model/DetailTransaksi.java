package com.stadela.model;

import java.math.BigDecimal;

public class DetailTransaksi {
    private int        idDetail;
    private int        idTransaksi;
    private int        idMenu;
    private String     namaMenu;
    private int        jumlah;
    private BigDecimal hargaSatuan;
    private BigDecimal subtotal;

    public DetailTransaksi() {}

    public DetailTransaksi(int idMenu, String namaMenu, int jumlah, BigDecimal hargaSatuan) {
        this.idMenu      = idMenu;
        this.namaMenu    = namaMenu;
        this.jumlah      = jumlah;
        this.hargaSatuan = hargaSatuan;
        this.subtotal    = hargaSatuan.multiply(BigDecimal.valueOf(jumlah));
    }

    public int        getIdDetail()      { return idDetail; }
    public void       setIdDetail(int v) { idDetail = v; }
    public int        getIdTransaksi()   { return idTransaksi; }
    public void       setIdTransaksi(int v) { idTransaksi = v; }
    public int        getIdMenu()        { return idMenu; }
    public void       setIdMenu(int v)   { idMenu = v; }
    public String     getNamaMenu()      { return namaMenu; }
    public void       setNamaMenu(String v)    { namaMenu = v; }
    public int        getJumlah()        { return jumlah; }
    public void       setJumlah(int v)   { jumlah = v; if (hargaSatuan != null) this.subtotal = hargaSatuan.multiply(BigDecimal.valueOf(v)); }
    public BigDecimal getHargaSatuan()   { return hargaSatuan; }
    public void       setHargaSatuan(BigDecimal v) { hargaSatuan = v; }
    public BigDecimal getSubtotal()      { return subtotal; }
    public void       setSubtotal(BigDecimal v) { subtotal = v; }
}
