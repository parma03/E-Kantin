package com.example.e_kantin.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.activity.admin.DialogUpdateKantinActivity;
import com.example.e_kantin.model.KantinModel;
import com.example.e_kantin.util.ApiServer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class KantinAdapter extends RecyclerView.Adapter<KantinAdapter.KantinModelViewHolder> {
    private Context context;
    List<KantinModel> listKantinModel;

    public KantinAdapter(Context context, List<KantinModel> listKantinModel) {
        this.context = context;
        this.listKantinModel = listKantinModel;
    }

    @NonNull
    @Override
    public KantinAdapter.KantinModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_kantin, null);
        return new KantinAdapter.KantinModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KantinAdapter.KantinModelViewHolder holder, int position) {
        KantinModel kantinModel = listKantinModel.get(position);
        holder.txtName.setText("Nama: "+kantinModel.getName());
        holder.txtNohp.setText("NOHP: "+kantinModel.getNohp());
        holder.txtUsername.setText("Username: "+kantinModel.getUsername());
        holder.txtPassword.setText(kantinModel.getPassword());
        holder.layoutUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog(kantinModel);
            }
        });

        holder.layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "data diklik", Toast.LENGTH_SHORT).show();
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Konfigurasi Hapus")
                        .setMessage("User akan dihapus, lanjutkan ?")
                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                hapusData(kantinModel.id_penjual);
                                listKantinModel.remove(holder.getAdapterPosition());
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

    private void showUpdateDialog(KantinModel kantinModel) {
        DialogUpdateKantinActivity dialog = new DialogUpdateKantinActivity();
        Bundle args = new Bundle();
        args.putString("id_penjual", kantinModel.getId_penjual());
        args.putString("name", kantinModel.getName());
        args.putString("nohp", kantinModel.getNohp());
        args.putString("username", kantinModel.getUsername());
        args.putString("password", kantinModel.getPassword());
        dialog.setArguments(args);

        AppCompatActivity activity = (AppCompatActivity) context;
        if (activity != null) {
            dialog.showNow(activity.getSupportFragmentManager(), "update_kantin");
        }
    }

    public int getItemCount() {
        return listKantinModel.size();
    }

    public void hapusData(String id_penjual) {
        if (listKantinModel.isEmpty()) {
            Toast.makeText(context, "Data Kantin kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_admin + "deleteKantin.php")
                .addBodyParameter("id_penjual", id_penjual)
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

    public class KantinModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNohp, txtUsername, txtPassword;
        CardView itemKantin;
        RelativeLayout layoutDelete, layoutUpdate;

        private Context context;

        public KantinModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtNohp = itemView.findViewById(R.id.txtNohp);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtPassword = itemView.findViewById(R.id.txtPassword);
            itemKantin = itemView.findViewById(R.id.card);
            layoutDelete = itemView.findViewById(R.id.layoutDelete);
            layoutUpdate = itemView.findViewById(R.id.layoutEdit);
        }
    }
}
