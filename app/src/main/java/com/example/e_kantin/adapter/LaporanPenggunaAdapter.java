package com.example.e_kantin.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_kantin.R;
import com.example.e_kantin.activity.admin.DialogUpdateKonsumenActivity;
import com.example.e_kantin.model.KonsumenModel;
import com.example.e_kantin.model.UserModel;

import java.util.List;

public class LaporanPenggunaAdapter extends RecyclerView.Adapter<LaporanPenggunaAdapter.KonsumenModelViewHolder> {
    private Context context;
    List<UserModel> userModelList;

    public LaporanPenggunaAdapter(Context context, List<UserModel> userModelList) {
        this.context = context;
        this.userModelList = userModelList;
    }

    @NonNull
    @Override
    public LaporanPenggunaAdapter.KonsumenModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_user, null);
        return new LaporanPenggunaAdapter.KonsumenModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaporanPenggunaAdapter.KonsumenModelViewHolder holder, int position) {
        UserModel userModel = userModelList.get(position);
        holder.txtName.setText("Nama: "+userModel.getName());
        holder.txtNohp.setText("NOHP: "+userModel.getNohp());
        holder.txtUsername.setText("Username: "+userModel.getUsername());
        holder.txtPassword.setText(userModel.getPassword());
        holder.txtRole.setText("Role: "+userModel.getRole());
    }

    public int getItemCount() {
        return userModelList.size();
    }

    public class KonsumenModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNohp, txtUsername, txtPassword, txtRole;
        CardView itemKonsumen;

        private Context context;

        public KonsumenModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtNohp = itemView.findViewById(R.id.txtNohp);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtPassword = itemView.findViewById(R.id.txtPassword);
            itemKonsumen = itemView.findViewById(R.id.card);
            txtRole = itemView.findViewById(R.id.txtRole);
        }
    }
}