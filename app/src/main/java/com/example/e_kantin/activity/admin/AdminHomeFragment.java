package com.example.e_kantin.activity.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.databinding.FragmentAdminHomeBinding;
import com.example.e_kantin.util.ApiServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminHomeFragment extends Fragment {
    private FragmentAdminHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AndroidNetworking.initialize(requireContext());

        binding.linearKonsumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment adminKonsumenFragment = new AdminKonsumenFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, adminKonsumenFragment)
                        .addToBackStack(null)  // Optional: Adds the transaction to the back stack
                        .commit();
            }
        });

        binding.linearKantin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment adminKantinFragment = new AdminKantinFragment();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, adminKantinFragment)
                        .addToBackStack(null)  // Optional: Adds the transaction to the back stack
                        .commit();
            }
        });

        AndroidNetworking.get(ApiServer.site_url_admin+"checkJumlahUser.php?role=konsumen")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("code").equals("1")){
                                JSONArray array = response.getJSONArray("data");
                                JSONObject data = array.getJSONObject(0);
                                binding.JMLuser.setText(data.getString("JML"));
                            }else {
                                binding.JMLuser.setText("0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.JMLuser.setText("0");
                    }
                });

        AndroidNetworking.get(ApiServer.site_url_admin+"checkJumlahUser.php?role=kantin")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("code").equals("1")){
                                JSONArray array = response.getJSONArray("data");
                                JSONObject data = array.getJSONObject(0);
                                binding.JMLpenjual.setText(data.getString("JML"));
                            }else {
                                binding.JMLpenjual.setText("0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.JMLpenjual.setText("0");
                    }
                });

        return binding.getRoot();
    }
}