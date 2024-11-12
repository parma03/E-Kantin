package com.example.e_kantin.activity.konsumen;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.example.e_kantin.WelcomeActivity;
import com.example.e_kantin.activity.RegisterActivity;
import com.example.e_kantin.adapter.HomeRekAdapter;
import com.example.e_kantin.adapter.KantinAdapter;
import com.example.e_kantin.adapter.MenuAdapter;
import com.example.e_kantin.adapter.PelangganKantinAdapter;
import com.example.e_kantin.databinding.FragmentKonsumenHomeBinding;
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

public class KonsumenHomeFragment extends Fragment {
    private FragmentKonsumenHomeBinding binding;
    private List<MenuModel> menuModelList;
    private List<KantinModel> kantinModelList;
    private AlertDialog dialog;
    PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKonsumenHomeBinding.inflate(inflater, container, false);

        binding.rvKantin.setHasFixedSize(true);
        binding.rvKantin.setLayoutManager(new LinearLayoutManager(requireContext()));
        kantinModelList = new ArrayList<>();

        binding.rvRec.setHasFixedSize(true);
        binding.rvRec.setLayoutManager(new LinearLayoutManager(requireContext()));
        menuModelList = new ArrayList<>();

        prefManager = new PrefManager(requireContext());
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();

        dataRec();
        dataKantin();

        binding.txtSeemorekantin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of KonsumenKantinFragment
                KonsumenKantinFragment konsumenFragment = new KonsumenKantinFragment();

                // Get the FragmentManager and start a transaction
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with KonsumenKantinFragment
                fragmentTransaction.replace(R.id.frameLayout, konsumenFragment);

                // Add the transaction to the back stack
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        binding.txtseemorerek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of KonsumenKantinFragment
                KonsumenMenuFragment konsumenFragment = new KonsumenMenuFragment();

                // Get the FragmentManager and start a transaction
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with KonsumenKantinFragment
                fragmentTransaction.replace(R.id.frameLayout, konsumenFragment);

                // Add the transaction to the back stack
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        return binding.getRoot();
    }

    private void dataRec() {
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false);
        binding.rvRec.setLayoutManager(linearLayoutManager);
        dialog.show();
        AndroidNetworking.get(ApiServer.site_url_konsumen + "getRec.php")
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
                                HomeRekAdapter adapter = new HomeRekAdapter(requireContext(), menuModelList);
                                binding.rvRec.setAdapter(adapter);
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

    private void dataKantin() {
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false);
        binding.rvKantin.setLayoutManager(linearLayoutManager);
        dialog.show();
        AndroidNetworking.get(ApiServer.site_url_konsumen + "getKantin.php")
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
                                PelangganKantinAdapter adapter = new PelangganKantinAdapter(requireContext(), kantinModelList);
                                binding.rvKantin.setAdapter(adapter);
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