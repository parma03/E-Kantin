package com.example.e_kantin.activity.konsumen;

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
import com.example.e_kantin.adapter.MenuAdapter;
import com.example.e_kantin.adapter.PelangganMenuAdd2Adapter;
import com.example.e_kantin.databinding.FragmentKonsumenMenuBinding;
import com.example.e_kantin.model.KantinModel;
import com.example.e_kantin.model.MenuModel;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class KonsumenMenuFragment extends Fragment {
    private FragmentKonsumenMenuBinding binding;
    private List<MenuModel> menuModelList;
    private List<KantinModel> kantinModelList;
    PelangganMenuAdd2Adapter adapter;
    private AlertDialog dialog;
    PrefManager prefManager;
    private String selectedCategory = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKonsumenMenuBinding.inflate(inflater, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        menuModelList = new ArrayList<>();
        kantinModelList = new ArrayList<>();

        prefManager = new PrefManager(requireContext());
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();


        dataMenu();

        binding.btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

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

    private void showFilterDialog() {
        final String[] categories = {"Makan", "Minuman"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Pilih Kategori");
        builder.setItems(categories, (dialog, which) -> {
            selectedCategory = categories[which];
            filterDataByCategory(selectedCategory);
        });

        builder.show();
    }

    private void filterDataByCategory(String category) {
        dialog.show();
        AndroidNetworking.post(ApiServer.site_url_konsumen + "filterMenu.php")
                .addBodyParameter("category", category)
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
                                menuModelList.clear();
                                kantinModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject menuObject = array.getJSONObject(i);
                                    JSONObject kantinObject = array.getJSONObject(i);
                                    MenuModel isi = gson.fromJson(menuObject + "", MenuModel.class);
                                    KantinModel name = gson.fromJson(kantinObject + "", KantinModel.class);
                                    menuModelList.add(isi);
                                    kantinModelList.add(name);
                                }
                                PelangganMenuAdd2Adapter adapter = new PelangganMenuAdd2Adapter(requireContext(), menuModelList, kantinModelList);
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                menuModelList.clear();
                                kantinModelList.clear();
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

    private void searchData(String keyword) {
        dialog.dismiss();
        AndroidNetworking.post(ApiServer.site_url_konsumen + "searchMenu.php")
                .addBodyParameter("keyword", keyword)
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
                                menuModelList.clear();
                                kantinModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject menuObject = array.getJSONObject(i);
                                    JSONObject kantinObject = array.getJSONObject(i);
                                    MenuModel isi = gson.fromJson(menuObject + "", MenuModel.class);
                                    KantinModel name = gson.fromJson(kantinObject + "", KantinModel.class);
                                    menuModelList.add(isi);
                                    kantinModelList.add(name);
                                }
                                PelangganMenuAdd2Adapter adapter = new PelangganMenuAdd2Adapter(requireContext(), menuModelList, kantinModelList);
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                menuModelList.clear();
                                kantinModelList.clear();
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

    private void dataMenu() {
        dialog.show();
        AndroidNetworking.get(ApiServer.site_url_konsumen + "getMenu2.php")
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
                                menuModelList.clear();
                                kantinModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject menuObject = array.getJSONObject(i);
                                    JSONObject kantinObject = array.getJSONObject(i);
                                    MenuModel isi = gson.fromJson(menuObject + "", MenuModel.class);
                                    KantinModel name = gson.fromJson(kantinObject + "", KantinModel.class);
                                    menuModelList.add(isi);
                                    kantinModelList.add(name);
                                }
                                PelangganMenuAdd2Adapter adapter = new PelangganMenuAdd2Adapter(requireContext(), menuModelList, kantinModelList);
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