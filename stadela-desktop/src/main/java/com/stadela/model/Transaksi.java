package com.stadela.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Transaksi {
    private int                  idTransaksi;
    private String               kodeTransaksi;
    private int                  idUser;
    private Date                 tanggalTransaksi;
    private BigDecimal           total;
    private Timestamp            createdAt;
    // join
    private String               namaUser;
    private List<DetailTransaksi> details = new ArrayList<>();

    public Transaksi() {}

    public int         getIdTransaksi()     { return idTransaksi; }
    public void        setIdTransaksi(int v){ idTransaksi = v; }
    public String      getKodeTransaksi()   { return kodeTransaksi; }
    public void        setKodeTransaksi(String v) { kodeTransaksi = v; }
    public int         getIdUser()          { return idUser; }
    public void        setIdUser(int v)     { idUser = v; }
    public Date        getTanggalTransaksi(){ return tanggalTransaksi; }
    public void        setTanggalTransaksi(Date v) { tanggalTransaksi = v; }
    public BigDecimal  getTotal()           { return total; }
    public void        setTotal(BigDecimal v) { total = v; }
    public Timestamp   getCreatedAt()       { return createdAt; }
    public void        setCreatedAt(Timestamp v) { createdAt = v; }
    public String      getNamaUser()        { return namaUser; }
    public void        setNamaUser(String v){ namaUser = v; }
    public List<DetailTransaksi> getDetails(){ return details; }
    public void        setDetails(List<DetailTransaksi> v) { details = v; }
    public void        addDetail(DetailTransaksi d) { details.add(d); }
}
