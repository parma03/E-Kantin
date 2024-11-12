package com.example.e_kantin.model;

public class CheckoutModel {
    public String id_checkout;
    public String id_makanan;
    public String id_konsumen;
    public String name;
    public String namemenu;
    public String harga;
    public String nohp;
    public String total_harga;
    public String status;
    public String jumlah;
    public String gambar;

    public CheckoutModel(String id_checkout, String id_makanan, String id_konsumen, String name, String namemenu, String harga, String total_harga, String status, String jumlah, String gambar, String nohp){
        this.id_checkout = id_checkout;
        this.id_makanan = id_makanan;
        this.id_konsumen = id_konsumen;
        this.name = name;
        this.namemenu = namemenu;
        this.harga = harga;
        this.total_harga = total_harga;
        this.status = status;
        this.jumlah = jumlah;
        this.gambar = gambar;
        this.nohp = nohp;
    }
    public String getId_checkout() {
        return id_checkout;
    }
    public void setId_checkout(String id_checkout) {
        this.id_checkout = id_checkout;
    }

    public String getId_makanan() {
        return id_makanan;
    }
    public void setId_makanan(String id_makanan) {
        this.id_makanan = id_makanan;
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

    public String getNamemenu() {
        return namemenu;
    }
    public void setNamemenu(String namemenu) {
        this.namemenu = namemenu;
    }

    public String getHarga() {
        return harga;
    }
    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getTotal_harga() {
        return total_harga;
    }
    public void setTotal_harga(String total_harga) {
        this.total_harga = total_harga;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getJumlah() {
        return jumlah;
    }
    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getGambar() {
        return gambar;
    }
    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getNohp() {
        return nohp;
    }
    public void setNohp(String nohp) {
        this.nohp = nohp;
    }
}
