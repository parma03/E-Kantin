package com.example.e_kantin.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_kantin.R;
import com.example.e_kantin.activity.konsumen.KonsumenHomeKantinFragment;
import com.example.e_kantin.activity.konsumen.KonsumenKantinFragment;
import com.example.e_kantin.model.KantinModel;

import java.util.List;

public class PelangganHomeKantinAdapter extends RecyclerView.Adapter<PelangganHomeKantinAdapter.KantinModelViewHolder> {
    private Context context;
    List<KantinModel> listKantinModel;

    public PelangganHomeKantinAdapter(Context context, List<KantinModel> listKantinModel) {
        this.context = context;
        this.listKantinModel = listKantinModel;
    }

    @NonNull
    @Override
    public PelangganHomeKantinAdapter.KantinModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_daf_kantin, null);
        return new PelangganHomeKantinAdapter.KantinModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PelangganHomeKantinAdapter.KantinModelViewHolder holder, int position) {
        KantinModel kantinModel = listKantinModel.get(position);
        holder.txtName.setText(kantinModel.getName());
        holder.txtJMLMenu.setText("Jumlah Menu: "+kantinModel.getJmlmenu());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of KonsumenKantinFragment
                KonsumenHomeKantinFragment konsumenHomeKantinFragment = new KonsumenHomeKantinFragment();

                // Get the FragmentManager and start a transaction
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with KonsumenKantinFragment
                fragmentTransaction.replace(R.id.frameLayout, konsumenHomeKantinFragment);

                // Add the transaction to the back stack
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();

                // Pass data to the fragment using Bundle
                Bundle args = new Bundle();
                args.putString("id_penjual", kantinModel.getId_penjual());
                konsumenHomeKantinFragment.setArguments(args);

            }
        });
    }

    public int getItemCount() {
        return listKantinModel.size();
    }

    public class KantinModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtJMLMenu;
        CardView card;

        private Context context;

        public KantinModelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtJMLMenu = itemView.findViewById(R.id.txtJMLMenu);
            card = itemView.findViewById(R.id.card);
        }
    }
}