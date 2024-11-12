package com.example.e_kantin.activity.konsumen;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.adapter.HomeRekAdapter;
import com.example.e_kantin.adapter.PelangganHomeKantinAdapter;
import com.example.e_kantin.databinding.FragmentKonsumenHomeBinding;
import com.example.e_kantin.databinding.FragmentKonsumenKantinBinding;
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

public class KonsumenKantinFragment extends Fragment {
    private FragmentKonsumenKantinBinding binding;
    private List<KantinModel> kantinModelList;
    private AlertDialog dialog;
    PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKonsumenKantinBinding.inflate(inflater, container, false);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        kantinModelList = new ArrayList<>();

        prefManager = new PrefManager(requireContext());
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();

        dataKantin();

        return binding.getRoot();
    }

    private void dataKantin() {
        dialog.show();
        AndroidNetworking.get(ApiServer.site_url_konsumen + "getKantin2.php")
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
                                kantinModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject kantinObject = array.getJSONObject(i);
                                    KantinModel isi = gson.fromJson(kantinObject + "", KantinModel.class);
                                    kantinModelList.add(isi);
                                }
                                PelangganHomeKantinAdapter adapter = new PelangganHomeKantinAdapter(requireContext(), kantinModelList);
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