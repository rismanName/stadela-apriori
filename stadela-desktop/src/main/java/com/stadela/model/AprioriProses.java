package com.stadela.model;

import java.sql.Date;
import java.sql.Timestamp;

public class AprioriProses {
    private int       idProses;
    private int       idUser;
    private float     minSupport;
    private float     minConfidence;
    private Date      tanggalDari;
    private Date      tanggalSampai;
    private int       totalTransaksi;
    private String    status; // proses | selesai | gagal
    private Timestamp createdAt;
    // join
    private String    namaUser;

    public int       getIdProses()       { return idProses; }
    public void      setIdProses(int v)  { idProses = v; }
    public int       getIdUser()         { return idUser; }
    public void      setIdUser(int v)    { idUser = v; }
    public float     getMinSupport()     { return minSupport; }
    public void      setMinSupport(float v)    { minSupport = v; }
    public float     getMinConfidence()  { return minConfidence; }
    public void      setMinConfidence(float v) { minConfidence = v; }
    public Date      getTanggalDari()    { return tanggalDari; }
    public void      setTanggalDari(Date v)    { tanggalDari = v; }
    public Date      getTanggalSampai()  { return tanggalSampai; }
    public void      setTanggalSampai(Date v)  { tanggalSampai = v; }
    public int       getTotalTransaksi() { return totalTransaksi; }
    public void      setTotalTransaksi(int v)  { totalTransaksi = v; }
    public String    getStatus()         { return status; }
    public void      setStatus(String v) { status = v; }
    public Timestamp getCreatedAt()      { return createdAt; }
    public void      setCreatedAt(Timestamp v) { createdAt = v; }
    public String    getNamaUser()       { return namaUser; }
    public void      setNamaUser(String v) { namaUser = v; }
}
