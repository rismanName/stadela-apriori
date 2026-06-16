package com.stadela.model;

import java.sql.Timestamp;

public class Kategori {
    private int       idKategori;
    private String    namaKategori;
    private Timestamp tglInput;
    private Timestamp tglUpdate;

    public Kategori() {}
    public Kategori(int idKategori, String namaKategori) {
        this.idKategori   = idKategori;
        this.namaKategori = namaKategori;
    }

    public int       getIdKategori()      { return idKategori; }
    public void      setIdKategori(int v) { idKategori = v; }
    public String    getNamaKategori()    { return namaKategori; }
    public void      setNamaKategori(String v) { namaKategori = v; }
    public Timestamp getTglInput()        { return tglInput; }
    public void      setTglInput(Timestamp v)  { tglInput = v; }
    public Timestamp getTglUpdate()       { return tglUpdate; }
    public void      setTglUpdate(Timestamp v) { tglUpdate = v; }

    @Override public String toString() { return namaKategori; }
}
