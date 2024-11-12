package com.example.e_kantin.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.model.CheckoutModel;
import com.example.e_kantin.model.PesananModel;
import com.example.e_kantin.util.ApiServer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PelangganCheckoutAdapter extends RecyclerView.Adapter<PelangganCheckoutAdapter.PesananModelViewHolder> {
    private Context context;
    List<CheckoutModel> checkoutModelList;

    public PelangganCheckoutAdapter(Context context, List<CheckoutModel> checkoutModelList) {
        this.context = context;
        this.checkoutModelList = checkoutModelList;
    }

    @NonNull
    @Override
    public PelangganCheckoutAdapter.PesananModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_pesanan_konsumen, null);
        return new PelangganCheckoutAdapter.PesananModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PelangganCheckoutAdapter.PesananModelViewHolder holder, int position) {
        CheckoutModel checkoutModel = checkoutModelList.get(position);

        Picasso.get()
                .load(ApiServer.site_url_gambar_menu + checkoutModel.getGambar())
                .into(holder.imgPesanan);
        holder.txtPesanan.setText(checkoutModel.getNamemenu());
        holder.txtJumlah.setText("X "+checkoutModel.getJumlah());
        double harga = Double.parseDouble(checkoutModel.getTotal_harga());
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String hargaFormatted = formatRupiah.format(harga);

        holder.txtHarga.setText(hargaFormatted);
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
                                hapusData(checkoutModel.id_checkout);
                                checkoutModelList.remove(holder.getAdapterPosition());
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

    public int getItemCount() {
        return checkoutModelList.size();
    }

    public void hapusData(String id_checkout) {
        if (checkoutModelList.isEmpty()) {
            Toast.makeText(context, "Data Pesanan kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_konsumen + "deleteCheckout.php")
                .addBodyParameter("id_checkout", id_checkout)
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
        TextView txtPesanan, txtHarga, txtJumlah;
        ImageView imgPesanan;
        RelativeLayout layoutDelete;

        private Context context;

        public PesananModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPesanan = itemView.findViewById(R.id.txtPesanan);
            txtHarga = itemView.findViewById(R.id.txtHarga);
            txtJumlah = itemView.findViewById(R.id.txtJumlah);
            imgPesanan = itemView.findViewById(R.id.imgPesanan);
            layoutDelete = itemView.findViewById(R.id.layoutDelete);
        }
    }
}