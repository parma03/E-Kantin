package com.example.e_kantin.activity.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.databinding.ActivityLaporanBinding;
import com.example.e_kantin.databinding.ActivityPrintUserBinding;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LaporanActivity extends AppCompatActivity {
    private ActivityLaporanBinding binding;
    PrefManager prefManager;
    private static final String URL_PRINT_PEMBELI = ApiServer.site_url_admin + "printPembeli.php";
    private static final String URL_PRINT_KANTIN = ApiServer.site_url_admin + "printKantin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLaporanBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        AndroidNetworking.initialize(this);

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
                                binding.txtJMLPembeli.setText("Pembeli : "+data.getString("JML"));
                            }else {
                                binding.txtJMLPembeli.setText("0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.txtJMLPembeli.setText("0");
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

        binding.btnCetakKantin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);

                // Dapatkan WebView dan muat URL_PRINT
                WebView webView = new WebView(LaporanActivity.this);
                webView.loadUrl(URL_PRINT_KANTIN);

                // Beri waktu pada WebView untuk memuat konten sebelum dicetak
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        // Setelah halaman selesai dimuat, lakukan pencetakan
                        PrintDocumentAdapter adapter = webView.createPrintDocumentAdapter("Laporan Kantin");
                        printManager.print("Laporan Kantin", adapter, null);
                    }
                });
            }
        });

        binding.btnCetakPembeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);

                // Dapatkan WebView dan muat URL_PRINT
                WebView webView = new WebView(LaporanActivity.this);
                webView.loadUrl(URL_PRINT_PEMBELI);

                // Beri waktu pada WebView untuk memuat konten sebelum dicetak
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        // Setelah halaman selesai dimuat, lakukan pencetakan
                        PrintDocumentAdapter adapter = webView.createPrintDocumentAdapter("Laporan Pembeli");
                        printManager.print("Laporan Pembeli", adapter, null);
                    }
                });
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaporanActivity.this, MainAdminActivity.class);
                startActivity(intent);
            }
        });
    }
}