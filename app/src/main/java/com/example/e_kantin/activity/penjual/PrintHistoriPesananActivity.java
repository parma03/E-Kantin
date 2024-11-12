package com.example.e_kantin.activity.penjual;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.activity.RegisterActivity;
import com.example.e_kantin.activity.admin.MainAdminActivity;
import com.example.e_kantin.activity.admin.PrintUserActivity;
import com.example.e_kantin.adapter.LaporanPenggunaAdapter;
import com.example.e_kantin.adapter.LaporanPesananAdapter;
import com.example.e_kantin.databinding.ActivityPrintHistoriPesananBinding;
import com.example.e_kantin.databinding.ActivityPrintUserBinding;
import com.example.e_kantin.model.PesananModel;
import com.example.e_kantin.model.UserModel;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PrintHistoriPesananActivity extends AppCompatActivity {
    private ActivityPrintHistoriPesananBinding binding;
    PrefManager prefManager;
    private List<PesananModel> pesananModelList;
    private LaporanPesananAdapter adapter;
    private int tahun,bulan,tanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrintHistoriPesananBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        AndroidNetworking.initialize(this);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(PrintHistoriPesananActivity.this));
        pesananModelList = new ArrayList<>();

        dataPesanan();

        AndroidNetworking.get(ApiServer.site_url_penjual+"checkJumlahPrint.php?id_penjual="+prefManager.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("code").equals("1")){
                                JSONArray array = response.getJSONArray("data");
                                JSONObject data = array.getJSONObject(0);
                                binding.txtJMLPemesanan.setText("Pesanan : "+data.getString("JML"));
                            }else {
                                binding.txtJMLPemesanan.setText("Pesanan : 0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.txtJMLPemesanan.setText("Pesanan : 0");
                    }
                });

        AndroidNetworking.get(ApiServer.site_url_penjual+"getPendapatan.php?id_penjual="+prefManager.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("code").equals("1")){
                                JSONArray array = response.getJSONArray("data");
                                JSONObject data = array.getJSONObject(0);
                                double harga = Double.parseDouble(data.getString("JML"));
                                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                                String hargaFormatted = formatRupiah.format(harga);
                                binding.txtJMLPendapatan.setText("Pendapatan : "+hargaFormatted);
                            }else {
                                binding.txtJMLPendapatan.setText("Pendapatan : Rp. 0");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.txtJMLPendapatan.setText("Rp. 0");
                    }
                });

        binding.tanggalDari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                tahun = calendar.get(Calendar.YEAR);
                bulan = calendar.get(Calendar.MONTH);
                tanggal = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog;
                dialog = new DatePickerDialog(PrintHistoriPesananActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tahun = year;
                        bulan = month;
                        tanggal = dayOfMonth;

                        binding.tanggalDari.setText(String.format("%04d-%02d-%02d", tahun, bulan + 1, tanggal));
                    }
                }, tahun,bulan,tanggal);
                dialog.show();
            }
        });

        binding.tanggalSampai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                tahun = calendar.get(Calendar.YEAR);
                bulan = calendar.get(Calendar.MONTH);
                tanggal = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog;
                dialog = new DatePickerDialog(PrintHistoriPesananActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tahun = year;
                        bulan = month;
                        tanggal = dayOfMonth;

                        binding.tanggalSampai.setText(String.format("%04d-%02d-%02d", tahun, bulan + 1, tanggal));
                    }
                }, tahun,bulan,tanggal);
                dialog.show();
            }
        });

        binding.btnCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);

                // Dapatkan WebView dan muat URL_PRINT
                WebView webView = new WebView(PrintHistoriPesananActivity.this);
                String url = ApiServer.site_url_penjual + "printPesanan.php?id_penjual=" + prefManager.getId();

                String tanggalDariStr = binding.tanggalDari.getText().toString();
                String tanggalSampaiStr = binding.tanggalSampai.getText().toString();

                if (tanggalDariStr != null && tanggalSampaiStr != null) {
                    url += "&tanggal_dari=" + tanggalDariStr + "&tanggal_sampai=" + tanggalSampaiStr;
                }

                webView.loadUrl(url);

                // Beri waktu pada WebView untuk memuat konten sebelum dicetak
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        // Setelah halaman selesai dimuat, lakukan pencetakan
                        PrintDocumentAdapter adapter = webView.createPrintDocumentAdapter("Laporan Pesanan");
                        printManager.print("Laporan Pesanan", adapter, null);
                    }
                });
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrintHistoriPesananActivity.this, MainPenjualActivity.class);
                startActivity(intent);
            }
        });
    }

    private void dataPesanan() {
        AndroidNetworking.get(ApiServer.site_url_penjual+"getPrintPesanan.php?id_penjual=" + prefManager.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equalsIgnoreCase("1")) {
                                JSONArray array = response.getJSONArray("data");
                                pesananModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject beritaObject = array.getJSONObject(i);
                                    PesananModel isi = gson.fromJson(beritaObject + "", PesananModel.class);
                                    pesananModelList.add(isi);
                                }
                                LaporanPesananAdapter adapter = new LaporanPesananAdapter(PrintHistoriPesananActivity.this, pesananModelList);
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