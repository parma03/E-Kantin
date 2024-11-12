package com.example.e_kantin.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.activity.penjual.DialogAddMenuActivity;
import com.example.e_kantin.activity.penjual.DialogUpdateMenuActivity;
import com.example.e_kantin.activity.penjual.PenjualMenuFragment;
import com.example.e_kantin.model.MenuModel;
import com.example.e_kantin.util.ApiServer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuModelViewHolder> implements DialogUpdateMenuActivity.OnMenuUpdatedListener {
    private Context context;
    List<MenuModel> listMenuModel;

    public MenuAdapter(Context context, List<MenuModel> listMenuModel) {
        this.context = context;
        this.listMenuModel = listMenuModel;
    }

    @NonNull
    @Override
    public MenuAdapter.MenuModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_menu, null);
        return new MenuAdapter.MenuModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.MenuModelViewHolder holder, int position) {
        MenuModel menuModel = listMenuModel.get(position);

        Picasso.get()
                .load(ApiServer.site_url_gambar_menu + menuModel.getGambar())
                .into(holder.imgMenu, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Picasso", "Gambar berhasil dimuat: " + ApiServer.site_url_gambar_menu + menuModel.getGambar());
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("Picasso", "Gagal memuat gambar: " + ApiServer.site_url_gambar_menu + menuModel.getGambar(), e);
                    }
                });
        holder.txtNamaMenu.setText(menuModel.getNamemenu());
        double harga = Double.parseDouble(menuModel.getHarga());
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String hargaFormatted = formatRupiah.format(harga);
        holder.txtHarga.setText(hargaFormatted);
        holder.txtKategori.setText(menuModel.getKategori());
        holder.layoutUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog(menuModel);
            }
        });

        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "data diklik", Toast.LENGTH_SHORT).show();
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Konfigurasi Hapus")
                        .setMessage("Menu akan dihapus, lanjutkan ?")
                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                hapusData(menuModel.id_makanan);
                                listMenuModel.remove(holder.getAdapterPosition());
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

    private void showUpdateDialog(MenuModel menuModel) {
        DialogUpdateMenuActivity dialog = new DialogUpdateMenuActivity();
        Bundle args = new Bundle();
        args.putString("id_makanan", menuModel.getId_makanan());
        args.putString("namemenu", menuModel.getNamemenu());
        args.putString("kategori", menuModel.getKategori());
        args.putString("harga", menuModel.getHarga());
        args.putString("gambar", menuModel.getGambar());
        dialog.setArguments(args);

        AppCompatActivity activity = (AppCompatActivity) context;
        dialog.setOnMenuUpdatedListener(this);
        dialog.showNow(activity.getSupportFragmentManager(), "update_menu");
    }

    @Override
    public void onMenuAdded(String message) {
        showSuccessNotification(message);
    }

    @Override
    public void onMenuError(String errorMessage) {
        showErrorNotification(errorMessage);
    }

    private void UpdateMenuList() {
        Fragment PenjualMenuFragment = new PenjualMenuFragment();
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, PenjualMenuFragment);
        transaction.commit();
    }

    private void showSuccessNotification(String message) {
        PopupDialog.getInstance(context)
                .setStyle(Styles.SUCCESS)
                .setHeading("BERHASIL !!!")
                .setDescription(message)
                .setCancelable(false)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        UpdateMenuList();
                        super.onDismissClicked(dialog);
                    }
                });
    }

    private void showErrorNotification(String message) {
        PopupDialog.getInstance(context)
                .setStyle(Styles.FAILED)
                .setHeading("GAGAL !!!")
                .setDescription(message)
                .setCancelable(false)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        UpdateMenuList();
                        super.onDismissClicked(dialog);
                    }
                });
    }

    public int getItemCount() {
        return listMenuModel.size();
    }

    public void hapusData(String id_makanan) {
        if (listMenuModel.isEmpty()) {
            Toast.makeText(context, "Data Makanan kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_penjual + "deleteMenu.php")
                .addBodyParameter("id_makanan", id_makanan)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if ("Menu berhasil dihapus".equals(message)) {
                                PopupDialog.getInstance(context)
                                        .setStyle(Styles.SUCCESS)
                                        .setHeading("BERHASIL !!!")
                                        .setDescription("Hapus Data Menu Berhasil")
                                        .setCancelable(false)
                                        .showDialog(new OnDialogButtonClickListener() {
                                            @Override
                                            public void onDismissClicked(Dialog dialog) {
                                                super.onDismissClicked(dialog);
                                            }
                                        });
                            } else {
                                PopupDialog.getInstance(context)
                                        .setStyle(Styles.FAILED)
                                        .setHeading("GAGAL !!!")
                                        .setDescription("Gagal Hapus Data Menu")
                                        .setCancelable(false)
                                        .showDialog(new OnDialogButtonClickListener() {
                                            @Override
                                            public void onDismissClicked(Dialog dialog) {
                                                super.onDismissClicked(dialog);
                                            }
                                        });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            PopupDialog.getInstance(context)
                                    .setStyle(Styles.FAILED)
                                    .setHeading("GAGAL !!!")
                                    .setDescription("Gagal Hapus Data Menu")
                                    .setCancelable(false)
                                    .showDialog(new OnDialogButtonClickListener() {
                                        @Override
                                        public void onDismissClicked(Dialog dialog) {
                                            super.onDismissClicked(dialog);
                                        }
                                    });                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        PopupDialog.getInstance(context)
                                .setStyle(Styles.FAILED)
                                .setHeading("GAGAL !!!")
                                .setDescription("Gagal Hapus : "+ anError.getErrorBody())
                                .setCancelable(false)
                                .showDialog(new OnDialogButtonClickListener() {
                                    @Override
                                    public void onDismissClicked(Dialog dialog) {
                                        super.onDismissClicked(dialog);
                                    }
                                });
                        Toast.makeText(context, "Hapus gagal: " + anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class MenuModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtHarga, txtNamaMenu, txtKategori;
        ImageView imgMenu;
        RelativeLayout layoutDelete, layoutUpdate;

        private Context context;

        public MenuModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNamaMenu = itemView.findViewById(R.id.txtNamaMenu);
            txtHarga = itemView.findViewById(R.id.txtHarga);
            imgMenu = itemView.findViewById(R.id.imgMenu);
            txtKategori = itemView.findViewById(R.id.txtKategori);
            layoutDelete = itemView.findViewById(R.id.layoutDelete);
            layoutUpdate = itemView.findViewById(R.id.layoutEdit);
        }
    }
}