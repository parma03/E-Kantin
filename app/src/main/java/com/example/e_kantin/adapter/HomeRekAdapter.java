package com.example.e_kantin.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_kantin.R;
import com.example.e_kantin.activity.admin.DialogUpdateKonsumenActivity;
import com.example.e_kantin.activity.konsumen.DialogAddPesananActivity;
import com.example.e_kantin.model.KonsumenModel;
import com.example.e_kantin.model.MenuModel;
import com.example.e_kantin.util.ApiServer;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HomeRekAdapter extends RecyclerView.Adapter<HomeRekAdapter.MenuModelViewHolder> {
    private Context context;
    List<MenuModel> listMenuModel;

    public HomeRekAdapter(Context context, List<MenuModel> listMenuModel) {
        this.context = context;
        this.listMenuModel = listMenuModel;
    }

    @NonNull
    @Override
    public MenuModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_rekomendasi_home, null);
        return new MenuModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuModelViewHolder holder, int position) {
        MenuModel menuModel = listMenuModel.get(position);

        Picasso.get()
                .load(ApiServer.site_url_gambar_menu + menuModel.getGambar())
                .into(holder.imgMakanan);
        holder.textMenu.setText(menuModel.getNamemenu());
        holder.textKantin.setText(menuModel.getName());
        double harga = Double.parseDouble(menuModel.getHarga());
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String hargaFormatted = formatRupiah.format(harga);
        holder.textHarga.setText(hargaFormatted);
        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
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
        TextView textMenu, textKantin, textHarga;
        ImageView imgAdd, imgMakanan;

        private Context context;

        public MenuModelViewHolder(@NonNull View itemView) {
            super(itemView);
            textMenu = itemView.findViewById(R.id.textMenu);
            textKantin = itemView.findViewById(R.id.textKantin);
            textHarga = itemView.findViewById(R.id.textHarga);
            imgAdd = itemView.findViewById(R.id.imgAdd);
            imgMakanan = itemView.findViewById(R.id.imgMakanan);
        }
    }
}
