package com.example.e_kantin.model;

public class MenuModel {
    public String id_makanan;
    public String id_penjual;
    public String namemenu;
    public String name;
    public String kategori;
    public String harga;
    public String gambar;
    public MenuModel(String id_makanan, String id_penjual, String namemenu, String name, String kategori, String harga, String gambar) {
        this.id_makanan = id_makanan;
        this.id_penjual = id_penjual;
        this.namemenu = namemenu;
        this.name = name;
        this.kategori = kategori;
        this.harga = harga;
        this.gambar = gambar;
    }
    public String getId_makanan() {
        return id_makanan;
    }
    public void setId_makanan(String id_makanan) {
        this.id_makanan = id_makanan;
    }

    public String getId_penjual() {
        return id_penjual;
    }
    public void setId_penjual(String id_penjual) {
        this.id_penjual = id_penjual;
    }

    public String getNamemenu() {
        return namemenu;
    }
    public void setNamemenu(String namemenu) {
        this.namemenu = namemenu;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getKategori() {
        return kategori;
    }
    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getHarga() {
        return harga;
    }
    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getGambar() {
        return gambar;
    }
    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
