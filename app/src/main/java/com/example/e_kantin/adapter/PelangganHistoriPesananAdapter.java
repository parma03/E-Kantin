package com.example.e_kantin.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.model.CheckoutModel;
import com.example.e_kantin.util.ApiServer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PelangganHistoriPesananAdapter extends RecyclerView.Adapter<PelangganHistoriPesananAdapter.PesananModelViewHolder> {
    private Context context;
    List<CheckoutModel> checkoutModelList;

    public PelangganHistoriPesananAdapter(Context context, List<CheckoutModel> checkoutModelList) {
        this.context = context;
        this.checkoutModelList = checkoutModelList;
    }

    @NonNull
    @Override
    public PelangganHistoriPesananAdapter.PesananModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_histori_pesasanan_konsumen, null);
        return new PelangganHistoriPesananAdapter.PesananModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PelangganHistoriPesananAdapter.PesananModelViewHolder holder, int position) {
        CheckoutModel checkoutModel = checkoutModelList.get(position);

        Picasso.get()
                .load(ApiServer.site_url_gambar_menu + checkoutModel.getGambar())
                .into(holder.imgPesanan);
        holder.txtNamaPemesan.setText(checkoutModel.getName());
        holder.txtNohp.setText("("+checkoutModel.getNohp()+")");
        holder.txtPesanan.setText(checkoutModel.getNamemenu());
        holder.txtJumlah.setText("X "+checkoutModel.getJumlah());
        double harga = Double.parseDouble(checkoutModel.getTotal_harga());
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String hargaFormatted = formatRupiah.format(harga);
        switch (checkoutModel.status){
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

        holder.txtHarga.setText(hargaFormatted);
    }

    public int getItemCount() {
        return checkoutModelList.size();
    }

    public class PesananModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtPesanan, txtHarga, txtJumlah, txtStatus, txtNamaPemesan, txtNohp;
        ImageView imgPesanan;
        private Context context;

        public PesananModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNamaPemesan = itemView.findViewById(R.id.txtNamaPemesan);
            txtNohp = itemView.findViewById(R.id.txtNohp);
            txtPesanan = itemView.findViewById(R.id.txtPesanan);
            txtHarga = itemView.findViewById(R.id.txtHarga);
            txtJumlah = itemView.findViewById(R.id.txtJumlah);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imgPesanan = itemView.findViewById(R.id.imgPesanan);
        }
    }
}