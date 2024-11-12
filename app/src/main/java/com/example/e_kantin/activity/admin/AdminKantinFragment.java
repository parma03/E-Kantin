package com.example.e_kantin.activity.admin;

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
import com.example.e_kantin.adapter.KantinAdapter;
import com.example.e_kantin.databinding.FragmentAdminKantinBinding;
import com.example.e_kantin.model.KantinModel;
import com.example.e_kantin.util.ApiServer;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AdminKantinFragment extends Fragment {
    private FragmentAdminKantinBinding binding;
    private List<KantinModel> listkantinModel;
    private KantinAdapter adapter;
    private AlertDialog dialog;
    private static final String URL_KANTIN = ApiServer.site_url_admin + "getKantin.php";
    private static final String URL_SEARCH_KANTIN = ApiServer.site_url_admin + "searchKantin.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminKantinBinding.inflate(inflater, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        listkantinModel = new ArrayList<>();
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();

        dataKantin();

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
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

    private void searchData(String keyword) {
        dialog.dismiss();
        AndroidNetworking.post(URL_SEARCH_KANTIN)
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
                                listkantinModel.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject kantinObject = array.getJSONObject(i);
                                    KantinModel kantin = gson.fromJson(kantinObject.toString(), KantinModel.class);
                                    listkantinModel.add(kantin);
                                }
                                adapter = new KantinAdapter(requireContext(), listkantinModel);
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                listkantinModel.clear();
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

    private void dataKantin() {
        dialog.show();
        AndroidNetworking.get(URL_KANTIN)
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
                                listkantinModel.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject beritaObject = array.getJSONObject(i);
                                    KantinModel isi = gson.fromJson(beritaObject + "", KantinModel.class);
                                    listkantinModel.add(isi);
                                }
                                KantinAdapter adapter = new KantinAdapter(requireContext(), listkantinModel);
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

    private void showAddDialog() {
        DialogAddKantinActivity dialog = new DialogAddKantinActivity();
        if (getActivity() != null) {
            dialog.showNow(getActivity().getSupportFragmentManager(), "add_kantin");
        }
    }
}