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
import com.example.e_kantin.adapter.KonsumenAdapter;
import com.example.e_kantin.databinding.FragmentAdminKonsumenBinding;
import com.example.e_kantin.model.KonsumenModel;
import com.example.e_kantin.util.ApiServer;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AdminKonsumenFragment extends Fragment {

    private FragmentAdminKonsumenBinding binding;
    private List<KonsumenModel> listkonsumenModel;
    private KonsumenAdapter adapter;
    private AlertDialog dialog;
    private static final String URL_KONSUMEN = ApiServer.site_url_admin + "getKonsumen.php";
    private static final String URL_SEARCH_KONSUMEN = ApiServer.site_url_admin + "searchKonsumen.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminKonsumenBinding.inflate(inflater, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        listkonsumenModel = new ArrayList<>();
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();

        dataKonsumen();

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
        AndroidNetworking.post(URL_SEARCH_KONSUMEN)
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
                                listkonsumenModel.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject konsumenObject = array.getJSONObject(i);
                                    KonsumenModel konsumenn = gson.fromJson(konsumenObject.toString(), KonsumenModel.class);
                                    listkonsumenModel.add(konsumenn);
                                }
                                adapter = new KonsumenAdapter(requireContext(), listkonsumenModel);
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                listkonsumenModel.clear();
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

    private void dataKonsumen() {
        dialog.show();
        AndroidNetworking.get(URL_KONSUMEN)
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
                                listkonsumenModel.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject beritaObject = array.getJSONObject(i);
                                    KonsumenModel isi = gson.fromJson(beritaObject + "", KonsumenModel.class);
                                    listkonsumenModel.add(isi);
                                }
                                KonsumenAdapter adapter = new KonsumenAdapter(requireContext(), listkonsumenModel);
                                binding.recyclerView.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("catch", "konsumenmodel::" + e.toString());
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
        DialogAddKonsumenActivity dialog = new DialogAddKonsumenActivity();
        if (getActivity() != null) {
            dialog.showNow(getActivity().getSupportFragmentManager(), "add_konsumen");
        }
    }
}