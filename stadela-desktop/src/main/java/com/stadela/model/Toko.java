package com.stadela.model;

public class Toko {
    private int    idToko;
    private String namaToko;
    private String alamatToko;
    private String tlp;
    private String namaPemilik;

    public Toko() {}

    public int    getIdToko()      { return idToko; }
    public void   setIdToko(int v) { idToko = v; }
    public String getNamaToko()    { return namaToko; }
    public void   setNamaToko(String v)    { namaToko = v; }
    public String getAlamatToko()  { return alamatToko; }
    public void   setAlamatToko(String v)  { alamatToko = v; }
    public String getTlp()         { return tlp; }
    public void   setTlp(String v) { tlp = v; }
    public String getNamaPemilik() { return namaPemilik; }
    public void   setNamaPemilik(String v) { namaPemilik = v; }
}
