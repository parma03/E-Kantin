package com.example.e_kantin.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.activity.penjual.DialogUpdateMenuActivity;
import com.example.e_kantin.model.MenuModel;
import com.example.e_kantin.model.PesananModel;
import com.example.e_kantin.util.ApiServer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.PesananModelViewHolder> {
    private Context context;
    List<PesananModel> listPesananModel;

    public PesananAdapter(Context context, List<PesananModel> listPesananModel) {
        this.context = context;
        this.listPesananModel = listPesananModel;
    }

    @NonNull
    @Override
    public PesananAdapter.PesananModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_pesanan, null);
        return new PesananAdapter.PesananModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PesananAdapter.PesananModelViewHolder holder, int position) {
        PesananModel pesananModel = listPesananModel.get(position);

        Picasso.get()
                .load(ApiServer.site_url_gambar_menu + pesananModel.getGambar())
                .into(holder.imgPesanan);
        holder.txtNamaPemesan.setText(pesananModel.getName());
        holder.txtNohp.setText("("+pesananModel.getNohp()+")");
        holder.txtPesanan.setText(pesananModel.getPesanan());
        holder.txtJumlah.setText("X "+pesananModel.getJumlah());
        double harga = Double.parseDouble(pesananModel.getTotal_harga());
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String hargaFormatted = formatRupiah.format(harga);
        holder.txtHarga.setText(hargaFormatted);
        holder.txtKeterangan.setText(pesananModel.getKeterangan());
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
        holder.layoutUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Selesaikan Pesanan")
                        .setMessage("Pesanan Telah Selesai, lanjutkan ?")
                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selesaiData(pesananModel.id_pesanan);
                                listPesananModel.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "data diklik", Toast.LENGTH_SHORT).show();
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Konfigurasi Hapus")
                        .setMessage("Pesanan akan dihapus, lanjutkan ?")
                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                hapusData(pesananModel.id_pesanan);
                                listPesananModel.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
    }

    public void selesaiData(String id_pesanan) {
        if (listPesananModel.isEmpty()) {
            Toast.makeText(context, "Data Pesanan kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_penjual + "selesaiPesanan.php")
                .addBodyParameter("id_pesanan", id_pesanan)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Log.d("response", "response::" + message);
                            if ("Pesanan Selesai".equals(message)) {
                                Toast.makeText(context, "Pesanan Selesai", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "ERROR!!!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "ERROR!!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context, "Hapus gagal: " + anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public int getItemCount() {
        return listPesananModel.size();
    }

    public void hapusData(String id_pesanan) {
        if (listPesananModel.isEmpty()) {
            Toast.makeText(context, "Data Pesanan kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_penjual + "deletePesanan.php")
                .addBodyParameter("id_pesanan", id_pesanan)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if ("hapus berhasil".equals(message)) {
                                Toast.makeText(context, "Hapus berhasil", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Hapus gagal", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Hapus gagal", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context, "Hapus gagal: " + anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class PesananModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtNamaPemesan, txtPesanan, txtKeterangan, txtHarga, txtStatus, txtJumlah, txtNohp;
        ImageView imgPesanan;
        RelativeLayout layoutDelete, layoutUpdate;

        private Context context;

        public PesananModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNamaPemesan = itemView.findViewById(R.id.txtNamaPemesan);
            txtNohp = itemView.findViewById(R.id.txtNohp);
            txtPesanan = itemView.findViewById(R.id.txtPesanan);
            txtKeterangan = itemView.findViewById(R.id.txtKeterangan);
            txtHarga = itemView.findViewById(R.id.txtHarga);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtJumlah = itemView.findViewById(R.id.txtJumlah);
            imgPesanan = itemView.findViewById(R.id.imgPesanan);
            layoutDelete = itemView.findViewById(R.id.layoutDelete);
            layoutUpdate = itemView.findViewById(R.id.layoutEdit);
        }
    }
}