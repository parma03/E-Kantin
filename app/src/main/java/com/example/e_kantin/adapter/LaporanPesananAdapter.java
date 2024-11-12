package com.example.e_kantin.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_kantin.R;
import com.example.e_kantin.activity.admin.DialogUpdateKonsumenActivity;
import com.example.e_kantin.model.KonsumenModel;
import com.example.e_kantin.model.PesananModel;
import com.example.e_kantin.model.UserModel;
import com.example.e_kantin.util.ApiServer;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class LaporanPesananAdapter extends RecyclerView.Adapter<LaporanPesananAdapter.KonsumenModelViewHolder> {
    private Context context;
    List<PesananModel> pesananModelList;

    public LaporanPesananAdapter(Context context, List<PesananModel> pesananModelList) {
        this.context = context;
        this.pesananModelList = pesananModelList;
    }

    @NonNull
    @Override
    public LaporanPesananAdapter.KonsumenModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_histori, null);
        return new LaporanPesananAdapter.KonsumenModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaporanPesananAdapter.KonsumenModelViewHolder holder, int position) {
        PesananModel pesananModel = pesananModelList.get(position);

        Picasso.get()
                .load(ApiServer.site_url_gambar_menu + pesananModel.getGambar())
                .into(holder.imgPesanan);
        holder.txtNamaPemesan.setText("Nama Pemesan: "+pesananModel.getName());
        holder.txtPesanan.setText("Pesanan: "+pesananModel.getPesanan());
        holder.txtJumlah.setText("X "+pesananModel.getJumlah());
        double harga = Double.parseDouble(pesananModel.getTotal_harga());
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String hargaFormatted = formatRupiah.format(harga);
        holder.txtHarga.setText(hargaFormatted);
        switch (pesananModel.status){
            case "dibayar":{
                holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple_200)));
                holder.txtStatus.setText("Sedang Diproses (Dibayar)");
                break;
            }
            case "Selesai":{
                holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
                holder.txtStatus.setText("Selesai");
                break;
            }
            case "ditolak":{
                holder.txtStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                holder.txtStatus.setText("Ditolak (Refaund)");
                break;
            }
        }
    }

    public int getItemCount() {
        return pesananModelList.size();
    }

    public class KonsumenModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtNamaPemesan, txtPesanan, txtJumlah, txtStatus, txtHarga;
        ImageView imgPesanan;
        CardView itemKonsumen;

        private Context context;

        public KonsumenModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNamaPemesan = itemView.findViewById(R.id.txtNamaPemesan);
            txtPesanan = itemView.findViewById(R.id.txtPesanan);
            txtJumlah = itemView.findViewById(R.id.txtJumlah);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            itemKonsumen = itemView.findViewById(R.id.card);
            txtHarga = itemView.findViewById(R.id.txtHarga);
            imgPesanan = itemView.findViewById(R.id.imgPesanan);
        }
    }
}