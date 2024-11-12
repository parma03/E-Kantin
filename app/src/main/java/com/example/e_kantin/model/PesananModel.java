package com.example.e_kantin.model;

public class PesananModel {
    public String id_pesanan;
    public String id_penjual;
    public String id_konsumen;
    public String name;
    public String keterangan;
    public String nohp;
    public String pesanan;
    public String jumlah;
    public String total_harga;
    public String status;
    public String gambar;

    public PesananModel(String id_pesanan, String id_penjual, String id_konsumen, String name, String pesanan, String jumlah, String total_harga, String status, String keterangan, String gambar, String nohp){
        this.id_pesanan = id_pesanan;
        this.id_penjual = id_penjual;
        this.id_konsumen = id_konsumen;
        this.name = name;
        this.pesanan = pesanan;
        this.jumlah = jumlah;
        this.total_harga = total_harga;
        this.status = status;
        this.keterangan = keterangan;
        this.gambar = gambar;
        this.nohp = nohp;
    }
    public String getId_pesanan() {
        return id_pesanan;
    }
    public void setId_pesanan(String id_pesanan) {
        this.id_pesanan = id_pesanan;
    }

    public String getId_penjual() {
        return id_penjual;
    }
    public void setId_penjual(String id_penjual) {
        this.id_penjual = id_penjual;
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

    public String getPesanan() {
        return pesanan;
    }
    public void setPesanan(String pesanan) {
        this.pesanan = pesanan;
    }

    public String getJumlah() {
        return jumlah;
    }
    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
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

    public String getKeterangan() {
        return keterangan;
    }
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
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
