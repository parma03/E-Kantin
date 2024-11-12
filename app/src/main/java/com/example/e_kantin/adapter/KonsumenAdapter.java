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
import com.example.e_kantin.activity.admin.DialogUpdateKonsumenActivity;
import com.example.e_kantin.model.KonsumenModel;
import com.example.e_kantin.util.ApiServer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class KonsumenAdapter extends RecyclerView.Adapter<KonsumenAdapter.KonsumenModelViewHolder> {
    private Context context;
    List<KonsumenModel> listKonsumenModel;

    public KonsumenAdapter(Context context, List<KonsumenModel> listKonsumenModel) {
        this.context = context;
        this.listKonsumenModel = listKonsumenModel;
    }

    @NonNull
    @Override
    public KonsumenModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_konsumen, null);
        return new KonsumenModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KonsumenModelViewHolder holder, int position) {
        KonsumenModel konsumenModel = listKonsumenModel.get(position);
        holder.txtName.setText("Nama: "+konsumenModel.getName());
        holder.txtNohp.setText("NOHP: "+konsumenModel.getNohp());
        holder.txtUsername.setText("Username: "+konsumenModel.getUsername());
        holder.txtPassword.setText(konsumenModel.getPassword());
        holder.layoutUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog(konsumenModel);
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
                                hapusData(konsumenModel.id_konsumen);
                                listKonsumenModel.remove(holder.getAdapterPosition());
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

    private void showUpdateDialog(KonsumenModel konsumenModel) {
        DialogUpdateKonsumenActivity dialog = new DialogUpdateKonsumenActivity();
        Bundle args = new Bundle();
        args.putString("id_konsumen", konsumenModel.getId_konsumen());
        args.putString("name", konsumenModel.getName());
        args.putString("nohp", konsumenModel.getNohp());
        args.putString("username", konsumenModel.getUsername());
        args.putString("password", konsumenModel.getPassword());
        dialog.setArguments(args);

        AppCompatActivity activity = (AppCompatActivity) context;
        if (activity != null) {
            dialog.showNow(activity.getSupportFragmentManager(), "update_konsumen");
        }
    }

    public int getItemCount() {
        return listKonsumenModel.size();
    }

    public void hapusData(String id_konsumen) {
        if (listKonsumenModel.isEmpty()) {
            Toast.makeText(context, "Data konsumen kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_admin + "deleteKonsumen.php")
                .addBodyParameter("id_konsumen", id_konsumen)
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
    public class KonsumenModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNohp, txtUsername, txtPassword;
        CardView itemKonsumen;
        RelativeLayout layoutDelete, layoutUpdate;

        private Context context;

        public KonsumenModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtNohp = itemView.findViewById(R.id.txtNohp);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtPassword = itemView.findViewById(R.id.txtPassword);
            itemKonsumen = itemView.findViewById(R.id.card);
            layoutDelete = itemView.findViewById(R.id.layoutDelete);
            layoutUpdate = itemView.findViewById(R.id.layoutEdit);
        }
    }
}