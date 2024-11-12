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
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.adapter.HomeRekAdapter;
import com.example.e_kantin.adapter.PelangganMenuAddAdapter;
import com.example.e_kantin.databinding.FragmentKonsumenHomeKantinBinding;
import com.example.e_kantin.databinding.FragmentKonsumenKantinBinding;
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

public class KonsumenHomeKantinFragment extends Fragment {
    private FragmentKonsumenHomeKantinBinding binding;
    private List<MenuModel> menuModelList;
    private AlertDialog dialog;
    PrefManager prefManager;
    String id_penjual;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKonsumenHomeKantinBinding.inflate(inflater, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        menuModelList = new ArrayList<>();

        prefManager = new PrefManager(requireContext());
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();

        Bundle arguments = getArguments();
        if (arguments != null) {
            id_penjual = arguments.getString("id_penjual");
        }

        dataKantin();
        dataMenu();

        return binding.getRoot();
    }

    private void dataKantin() {
        dialog.show();
        Log.d("res","url:: "+ ApiServer.site_url_konsumen + "getKantinHome.php?id_penjual="+id_penjual);
        AndroidNetworking.get(ApiServer.site_url_konsumen + "getKantinHome.php?id_penjual="+id_penjual)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("res", "kantin::" + response);
                            if(response.getString("code").equalsIgnoreCase("1")) {
                                JSONArray array = response.getJSONArray("data");
                                JSONObject object = array.getJSONObject(0);
                                binding.txtName.setText(object.getString("name"));
                                binding.txtJMLMenu.setText("Jumlah Menu: "+object.getString("JMLMenu"));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.hide();
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
        AndroidNetworking.get(ApiServer.site_url_konsumen + "getMenu.php?id_penjual="+id_penjual)
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
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject menuObject = array.getJSONObject(i);
                                    MenuModel isi = gson.fromJson(menuObject + "", MenuModel.class);
                                    menuModelList.add(isi);
                                }
                                PelangganMenuAddAdapter adapter = new PelangganMenuAddAdapter(requireContext(), menuModelList);
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