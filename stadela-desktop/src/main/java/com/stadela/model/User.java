package com.stadela.model;

public class User {
    private int idUser;
    private String username;
    private String password;
    private String role; // admin | kasir
    private String nama;
    private String alamat;
    private String telepon;
    private String email;
    private String foto;
    private String nik;

    public User() {}

    public User(int idUser, String username, String role, String nama,
                String alamat, String telepon, String email, String foto, String nik) {
        this.idUser   = idUser;
        this.username = username;
        this.role     = role;
        this.nama     = nama;
        this.alamat   = alamat;
        this.telepon  = telepon;
        this.email    = email;
        this.foto     = foto;
        this.nik      = nik;
    }

    // Getters & Setters
    public int    getIdUser()   { return idUser; }
    public void   setIdUser(int v)    { idUser = v; }
    public String getUsername() { return username; }
    public void   setUsername(String v) { username = v; }
    public String getPassword() { return password; }
    public void   setPassword(String v) { password = v; }
    public String getRole()     { return role; }
    public void   setRole(String v)    { role = v; }
    public String getNama()     { return nama; }
    public void   setNama(String v)    { nama = v; }
    public String getAlamat()   { return alamat; }
    public void   setAlamat(String v)  { alamat = v; }
    public String getTelepon()  { return telepon; }
    public void   setTelepon(String v) { telepon = v; }
    public String getEmail()    { return email; }
    public void   setEmail(String v)   { email = v; }
    public String getFoto()     { return foto; }
    public void   setFoto(String v)    { foto = v; }
    public String getNik()      { return nik; }
    public void   setNik(String v)     { nik = v; }

    @Override public String toString() { return nama; }
}
