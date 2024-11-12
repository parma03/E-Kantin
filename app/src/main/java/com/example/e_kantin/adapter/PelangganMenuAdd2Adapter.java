package com.example.e_kantin.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.activity.konsumen.DialogAddPesananActivity;
import com.example.e_kantin.model.KantinModel;
import com.example.e_kantin.model.MenuModel;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PelangganMenuAdd2Adapter extends RecyclerView.Adapter<PelangganMenuAdd2Adapter.MenuModelViewHolder> {
    private Context context;
    List<MenuModel> listMenuModel;
    List<KantinModel> kantinModelList;
    PrefManager prefManager;

    public PelangganMenuAdd2Adapter(Context context, List<MenuModel> listMenuModel, List<KantinModel> kantinModelList) {
        this.context = context;
        this.listMenuModel = listMenuModel;
        this.kantinModelList = kantinModelList;
        prefManager = new PrefManager(context);
    }

    @NonNull
    @Override
    public PelangganMenuAdd2Adapter.MenuModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_menu_add2, null);
        return new PelangganMenuAdd2Adapter.MenuModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PelangganMenuAdd2Adapter.MenuModelViewHolder holder, int position) {
        MenuModel menuModel = listMenuModel.get(position);
        KantinModel kantinModel = kantinModelList.get(position);

        Picasso.get()
                .load(ApiServer.site_url_gambar_menu + menuModel.getGambar())
                .into(holder.imgMenu);
        holder.txtNamaMenu.setText(menuModel.getNamemenu());
        double harga = Double.parseDouble(menuModel.getHarga());
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String hargaFormatted = formatRupiah.format(harga);

        holder.txtHarga.setText(hargaFormatted);
        holder.txtNamaKantin.setText(kantinModel.getName());
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPesananDialog(menuModel);
            }
        });
    }

    private void showPesananDialog(MenuModel menuModel) {
        DialogAddPesananActivity dialog = new DialogAddPesananActivity();
        Bundle args = new Bundle();
        args.putString("id_makanan", menuModel.getId_makanan());
        dialog.setArguments(args);

        AppCompatActivity activity = (AppCompatActivity) context;
        if (activity != null) {
            dialog.showNow(activity.getSupportFragmentManager(), "add_pesanan_konsumen");
        }
    }

    public int getItemCount() {
        return listMenuModel.size();
    }

    public class MenuModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtHarga, txtNamaMenu, txtNamaKantin;
        ImageView imgMenu,btnAdd;

        private Context context;

        public MenuModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNamaMenu = itemView.findViewById(R.id.txtNamaMenu);
            txtHarga = itemView.findViewById(R.id.txtHarga);
            txtNamaKantin = itemView.findViewById(R.id.txtNamaKantin);
            imgMenu = itemView.findViewById(R.id.imgMenu);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}