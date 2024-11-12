package com.example.e_kantin.activity.penjual;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.adapter.MenuAdapter;
import com.example.e_kantin.adapter.PesananAdapter;
import com.example.e_kantin.databinding.FragmentPenjualMenuBinding;
import com.example.e_kantin.databinding.FragmentPenjualPesananBinding;
import com.example.e_kantin.model.MenuModel;
import com.example.e_kantin.model.PesananModel;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PenjualPesananFragment extends Fragment {
    private FragmentPenjualPesananBinding binding;
    private AlertDialog dialog;
    private List<PesananModel> pesananModelList;
    PesananAdapter adapter;
    PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPenjualPesananBinding.inflate(inflater, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        pesananModelList = new ArrayList<>();
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();
        prefManager = new PrefManager(requireContext());

        dataPesanan();

        binding.kolomSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                searchData(keyword);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return binding.getRoot();
    }

    private void searchData(String keyword) {
        dialog.dismiss();
        String id_penjual = prefManager.getId();
        AndroidNetworking.post(ApiServer.site_url_penjual + "searchPesanan.php")
                .addBodyParameter("keyword", keyword)
                .addBodyParameter("id_penjual", id_penjual)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();
                            int code = response.getInt("code");
                            Log.d("response", "response::" + response);
                            if (code == 1) {
                                JSONArray array = response.getJSONArray("data");
                                pesananModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject pesananObject = array.getJSONObject(i);
                                    PesananModel pesanan = gson.fromJson(pesananObject.toString(), PesananModel.class);
                                    pesananModelList.add(pesanan);
                                }
                                adapter = new PesananAdapter(requireContext(), pesananModelList);
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                pesananModelList.clear();
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("catch", "gambarModel::" + e.toString());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Log.e("error", "onError: " + anError.getErrorBody());
                    }
                });
    }

    private void dataPesanan() {
        dialog.show();
        AndroidNetworking.get(ApiServer.site_url_penjual + "getPesanan.php?id_penjual="+prefManager.getId())
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
                                pesananModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject pesananObject = array.getJSONObject(i);
                                    PesananModel isi = gson.fromJson(pesananObject + "", PesananModel.class);
                                    pesananModelList.add(isi);
                                }
                                PesananAdapter adapter = new PesananAdapter(requireContext(), pesananModelList);
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

}