package com.example.e_kantin.model;

public class KonsumenModel {
    public String id_konsumen;
    public String name;
    public String nohp;
    public String username;
    public String password;

    public KonsumenModel(String id_konsumen, String name, String nohp, String username, String password) {
        this.id_konsumen = id_konsumen;
        this.name = name;
        this.nohp = nohp;
        this.username = username;
        this.password = password;
    }

    public String getId_konsumen() {
        return id_konsumen;
    }

    public void setId_konsumen(String id_konsumen) {
        this.id_konsumen = id_konsumen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}