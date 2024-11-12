package com.example.e_kantin.activity.konsumen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.adapter.HomeRekAdapter;
import com.example.e_kantin.adapter.PelangganHistoriPesananAdapter;
import com.example.e_kantin.databinding.FragmentKonsumenHistoriBinding;
import com.example.e_kantin.databinding.FragmentKonsumenPesananBinding;
import com.example.e_kantin.model.CheckoutModel;
import com.example.e_kantin.model.MenuModel;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class KonsumenHistoriFragment extends Fragment {
    private FragmentKonsumenHistoriBinding binding;
    private AlertDialog dialog;
    private List<CheckoutModel> checkoutModelList;
    PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKonsumenHistoriBinding.inflate(inflater, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        checkoutModelList = new ArrayList<>();
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();
        prefManager = new PrefManager(requireContext());

        dataHistori();

        binding.btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Konfigurasi Hapus")
                        .setMessage("Histori Pesanan akan dihapus, lanjutkan ?")
                        .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                hapusData(prefManager.getId());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        })
                        .show();
            }
        });
        return binding.getRoot();
    }

    private void dataHistori() {
        dialog.show();
        AndroidNetworking.get(ApiServer.site_url_konsumen + "getHistori.php?id_konsumen="+prefManager.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equalsIgnoreCase("1")) {
                                JSONArray array = response.getJSONArray("data");
                                checkoutModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject menuObject = array.getJSONObject(i);
                                    CheckoutModel isi = gson.fromJson(menuObject + "", CheckoutModel.class);
                                    checkoutModelList.add(isi);
                                }
                                PelangganHistoriPesananAdapter adapter = new PelangganHistoriPesananAdapter(getActivity(), checkoutModelList);
                                binding.recyclerView.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("catch", "kantinmodel::" + e.toString());
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Log.e("error", "onError: " + anError.getErrorBody());
                    }
                });
    }

    public void hapusData(String id_konsumen) {
        AndroidNetworking.post(ApiServer.site_url_konsumen + "deleteHistori.php")
                .addBodyParameter("id_konsumen", id_konsumen)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if ("hapus berhasil".equals(message)) {
                                Toast.makeText(requireContext(), "Hapus berhasil", Toast.LENGTH_SHORT).show();
                                checkoutModelList.clear();
                                dataHistori();
                            } else {
                                Toast.makeText(requireContext(), "Hapus gagal", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Hapus gagal", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(requireContext(), "Hapus gagal: " + anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}