package com.example.e_kantin.activity.penjual;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.activity.admin.AdminKonsumenFragment;
import com.example.e_kantin.databinding.FragmentAdminHomeBinding;
import com.example.e_kantin.databinding.FragmentPenjualHomeBinding;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PenjualHomeFragment extends Fragment {
    private FragmentPenjualHomeBinding binding;
    private PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPenjualHomeBinding.inflate(inflater, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AndroidNetworking.initialize(requireContext());
        prefManager = new PrefManager(requireContext());

        binding.cardMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment penjualMenuFragmen = new PenjualMenuFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, penjualMenuFragmen)
                        .addToBackStack(null)  // Optional: Adds the transaction to the back stack
                        .commit();
            }
        });

        binding.cardHistori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment penjualPesananFragment = new PenjualHistoriFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, penjualPesananFragment)
                        .addToBackStack(null)  // Optional: Adds the transaction to the back stack
                        .commit();
            }
        });

        binding.cardPesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment penjualHistoriPesanan = new PenjualPesananFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, penjualHistoriPesanan)
                        .addToBackStack(null)  // Optional: Adds the transaction to the back stack
                        .commit();
            }
        });

        AndroidNetworking.get(ApiServer.site_url_penjual+"checkJumlahMenu.php?id_penjual="+prefManager.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "response::" + prefManager.getId());
                        try {
                            if (response.getString("code").equals("1")){
                                JSONArray array = response.getJSONArray("data");
                                JSONObject data = array.getJSONObject(0);
                                binding.JLMMenu.setText(data.getString("JML"));
                                Log.d("response", "response::" + data);

                            }else {
                                binding.JLMMenu.setText("0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.JLMMenu.setText("0");
                    }
                });

        AndroidNetworking.get(ApiServer.site_url_penjual+"checkJumlahPesanan.php?id_penjual="+prefManager.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("code").equals("1")){
                                JSONArray array = response.getJSONArray("data");
                                JSONObject data = array.getJSONObject(0);
                                binding.JLMPesanan.setText(data.getString("JML"));
                            }else {
                                binding.JLMPesanan.setText("0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.JLMPesanan.setText("0");
                    }
                });

        AndroidNetworking.get(ApiServer.site_url_penjual+"checkHistoriPemesanan.php?id_penjual="+prefManager.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("code").equals("1")){
                                JSONArray array = response.getJSONArray("data");
                                JSONObject data = array.getJSONObject(0);
                                binding.JMLPenjualan.setText(data.getString("JML"));
                            }else {
                                binding.JMLPenjualan.setText("0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.JMLPenjualan.setText("0");
                    }
                });

        return binding.getRoot();
    }
}