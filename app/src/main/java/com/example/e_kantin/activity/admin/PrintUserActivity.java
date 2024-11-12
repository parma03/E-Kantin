package com.example.e_kantin.activity.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.adapter.LaporanPenggunaAdapter;
import com.example.e_kantin.databinding.ActivityPrintUserBinding;
import com.example.e_kantin.model.UserModel;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PrintUserActivity extends AppCompatActivity {
    private ActivityPrintUserBinding binding;
    PrefManager prefManager;
    private List<UserModel> userModelList;
    private LaporanPenggunaAdapter adapter;
    private static final String URL_USER = ApiServer.site_url_admin + "getUser.php";
    private static final String URL_PRINT = ApiServer.site_url_admin + "printUser.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrintUserBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        AndroidNetworking.initialize(this);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(PrintUserActivity.this));
        userModelList = new ArrayList<>();

        dataUser();

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
                                binding.txtJMLKonsumen.setText("Konsumen : "+data.getString("JML"));
                            }else {
                                binding.txtJMLKonsumen.setText("0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.txtJMLKonsumen.setText("0");
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
                                binding.txtJMLKantin.setText("Kantin : "+data.getString("JML"));
                            }else {
                                binding.txtJMLKantin.setText("0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.txtJMLKantin.setText("0");
                    }
                });

        binding.btnCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);

                // Dapatkan WebView dan muat URL_PRINT
                WebView webView = new WebView(PrintUserActivity.this);
                webView.loadUrl(URL_PRINT);

                // Beri waktu pada WebView untuk memuat konten sebelum dicetak
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        // Setelah halaman selesai dimuat, lakukan pencetakan
                        PrintDocumentAdapter adapter = webView.createPrintDocumentAdapter("Laporan User");
                        printManager.print("Laporan User", adapter, null);
                    }
                });
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrintUserActivity.this, MainAdminActivity.class);
                startActivity(intent);
            }
        });
    }

    private void dataUser() {
        AndroidNetworking.get(URL_USER)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equalsIgnoreCase("1")) {
                                JSONArray array = response.getJSONArray("data");
                                userModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject beritaObject = array.getJSONObject(i);
                                    UserModel isi = gson.fromJson(beritaObject + "", UserModel.class);
                                    userModelList.add(isi);
                                }
                                LaporanPenggunaAdapter adapter = new LaporanPenggunaAdapter(PrintUserActivity.this, userModelList);
                                binding.recyclerView.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("catch", "konsumenmodel::" + e.toString());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error", "onError: " + anError.getErrorBody());
                    }
                });
    }
}