package com.example.e_kantin.model;

public class KantinModel {
    public String id_penjual;
    public String name;
    public String nohp;
    public String username;
    public String password;
    public String JMLMenu;
    public String status;
    public String saldo;
    public KantinModel(String id_penjual, String name, String nohp, String username, String password, String JMLMenu, String status, String Saldo) {
        this.id_penjual = id_penjual;
        this.name = name;
        this.nohp = nohp;
        this.username = username;
        this.password = password;
        this.JMLMenu = JMLMenu;
        this.status = status;
        this.saldo = saldo;
    }

    public String getId_penjual() {
        return id_penjual;
    }

    public void setId_penjual(String id_penjual) {
        this.id_penjual = id_penjual;
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

    public String getJmlmenu() {
        return JMLMenu;
    }

    public void setJmlmenu(String JMLMenu) {
        this.JMLMenu = JMLMenu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }
}
