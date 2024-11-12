package com.example.e_kantin.activity.penjual;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.activity.admin.DialogAddKantinActivity;
import com.example.e_kantin.adapter.KantinAdapter;
import com.example.e_kantin.adapter.MenuAdapter;
import com.example.e_kantin.databinding.FragmentPenjualMenuBinding;
import com.example.e_kantin.model.KantinModel;
import com.example.e_kantin.model.MenuModel;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;
import com.google.gson.Gson;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PenjualMenuFragment extends Fragment implements DialogAddMenuActivity.OnMenuAddedListener, DialogUpdateMenuActivity.OnMenuUpdatedListener{
    private FragmentPenjualMenuBinding binding;
    private List<MenuModel> listmenuModel;
    private MenuAdapter adapter;
    private AlertDialog dialog;
    PrefManager prefManager;
    private String selectedCategory = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPenjualMenuBinding.inflate(inflater, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        listmenuModel = new ArrayList<>();
        prefManager = new PrefManager(requireContext());
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();

        dataMenu();

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });

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
        String id_penjual = prefManager.getId();
        AndroidNetworking.post(ApiServer.site_url_penjual + "filterMenu.php")
                .addBodyParameter("category", category)
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
                                listmenuModel.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject menuObject = array.getJSONObject(i);
                                    MenuModel menu = gson.fromJson(menuObject.toString(), MenuModel.class);
                                    listmenuModel.add(menu);
                                }
                                adapter = new MenuAdapter(requireContext(), listmenuModel);
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                listmenuModel.clear();
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
        String id_penjual = prefManager.getId();
        AndroidNetworking.post(ApiServer.site_url_penjual + "searchMenu.php")
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
                                listmenuModel.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject menuObject = array.getJSONObject(i);
                                    MenuModel menu = gson.fromJson(menuObject.toString(), MenuModel.class);
                                    listmenuModel.add(menu);
                                }
                                adapter = new MenuAdapter(requireContext(), listmenuModel);
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                listmenuModel.clear();
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
        AndroidNetworking.get(ApiServer.site_url_penjual + "getMenu.php?id_penjual="+prefManager.getId())
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
                                listmenuModel.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject menuObject = array.getJSONObject(i);
                                    MenuModel isi = gson.fromJson(menuObject + "", MenuModel.class);
                                    listmenuModel.add(isi);
                                }
                                MenuAdapter adapter = new MenuAdapter(requireContext(), listmenuModel);
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

    @Override
    public void onResume() {
        super.onResume();
        dataMenu();
    }

    private void showAddDialog() {
        DialogAddMenuActivity dialog = new DialogAddMenuActivity();
        dialog.setOnMenuAddedListener(this);
        dialog.showNow(getActivity().getSupportFragmentManager(), "add_konsumen");
    }

    @Override
    public void onMenuAdded(String message) {
        showSuccessNotification(message);
    }

    @Override
    public void onMenuError(String errorMessage) {
        showErrorNotification(errorMessage);
    }

    private void showSuccessNotification(String message) {
        PopupDialog.getInstance(getActivity())
                .setStyle(Styles.SUCCESS)
                .setHeading("BERHASIL !!!")
                .setDescription(message)
                .setCancelable(false)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        dataMenu();
                        super.onDismissClicked(dialog);
                    }
                });
    }

    private void showErrorNotification(String message) {
        PopupDialog.getInstance(getActivity())
                .setStyle(Styles.FAILED)
                .setHeading("GAGAL !!!")
                .setDescription(message)
                .setCancelable(false)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        dataMenu();
                        super.onDismissClicked(dialog);
                    }
                });
    }
}