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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.activity.admin.DialogUpdateKantinActivity;
import com.example.e_kantin.activity.konsumen.KonsumenHomeKantinFragment;
import com.example.e_kantin.model.KantinModel;
import com.example.e_kantin.util.ApiServer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PelangganKantinAdapter extends RecyclerView.Adapter<PelangganKantinAdapter.KantinModelViewHolder> {
    private Context context;
    List<KantinModel> listKantinModel;

    public PelangganKantinAdapter(Context context, List<KantinModel> listKantinModel) {
        this.context = context;
        this.listKantinModel = listKantinModel;
    }

    @NonNull
    @Override
    public PelangganKantinAdapter.KantinModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_kantin_home, null);
        return new PelangganKantinAdapter.KantinModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PelangganKantinAdapter.KantinModelViewHolder holder, int position) {
        KantinModel kantinModel = listKantinModel.get(position);
        holder.textKantin.setText(kantinModel.getName());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KonsumenHomeKantinFragment konsumenHomeKantinFragment = new KonsumenHomeKantinFragment();

                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.frameLayout, konsumenHomeKantinFragment);

                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit();

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
        TextView textKantin;
        RelativeLayout layoutDelete, layoutUpdate;
        CardView card;

        private Context context;

        public KantinModelViewHolder(@NonNull View itemView) {
            super(itemView);
            textKantin = itemView.findViewById(R.id.textKantin);
            card = itemView.findViewById(R.id.card);
        }
    }
}