package com.stadela.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Menu {
    private int        idMenu;
    private int        idKategori;
    private String     kodeMenu;
    private String     namaMenu;
    private BigDecimal harga;
    private int        stok;
    private String     satuan;
    private String     deskripsi;
    private String     foto;
    private Timestamp  tglInput;
    private Timestamp  tglUpdate;
    // join
    private String     namaKategori;

    public Menu() {}

    public int        getIdMenu()       { return idMenu; }
    public void       setIdMenu(int v)  { idMenu = v; }
    public int        getIdKategori()   { return idKategori; }
    public void       setIdKategori(int v) { idKategori = v; }
    public String     getKodeMenu()     { return kodeMenu; }
    public void       setKodeMenu(String v)    { kodeMenu = v; }
    public String     getNamaMenu()     { return namaMenu; }
    public void       setNamaMenu(String v)    { namaMenu = v; }
    public BigDecimal getHarga()        { return harga; }
    public void       setHarga(BigDecimal v)   { harga = v; }
    public int        getStok()         { return stok; }
    public void       setStok(int v)    { stok = v; }
    public String     getSatuan()       { return satuan; }
    public void       setSatuan(String v)      { satuan = v; }
    public String     getDeskripsi()    { return deskripsi; }
    public void       setDeskripsi(String v)   { deskripsi = v; }
    public String     getFoto()         { return foto; }
    public void       setFoto(String v) { foto = v; }
    public Timestamp  getTglInput()     { return tglInput; }
    public void       setTglInput(Timestamp v) { tglInput = v; }
    public Timestamp  getTglUpdate()    { return tglUpdate; }
    public void       setTglUpdate(Timestamp v){ tglUpdate = v; }
    public String     getNamaKategori() { return namaKategori; }
    public void       setNamaKategori(String v){ namaKategori = v; }

    @Override public String toString() { return namaMenu; }
}
