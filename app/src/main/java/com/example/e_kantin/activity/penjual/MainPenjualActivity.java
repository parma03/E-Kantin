package com.example.e_kantin.activity.penjual;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.activity.LoginActivity;
import com.example.e_kantin.activity.admin.MainAdminActivity;
import com.example.e_kantin.activity.admin.PrintUserActivity;
import com.example.e_kantin.activity.konsumen.DialogProfileKonsumenActivity;
import com.example.e_kantin.databinding.ActivityMainPenjualBinding;
import com.example.e_kantin.model.MenuModel;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

public class MainPenjualActivity extends AppCompatActivity {
    private ActivityMainPenjualBinding binding;
    PrefManager prefManager;
    String Saldo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPenjualBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        AndroidNetworking.initialize(this);

        saldo();
        binding.txtName.setText("Welcome "+prefManager.getNama());
        binding.txtRole.setText(prefManager.getTipe());
        if (prefManager.getImg() != null && !prefManager.getImg().equals("null")) {
            Picasso.get()
                    .load(ApiServer.site_url_img_profile + prefManager.getImg())
                    .into(binding.imgProfile);
        } else {
            binding.imgProfile.setImageResource(R.drawable.penjual);
        }

        replaceFragment(new PenjualHomeFragment());
        binding.ButtomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.Home:
                    replaceFragment(new PenjualHomeFragment());
                    break;
                case R.id.Menu:
                    replaceFragment(new PenjualMenuFragment());
                    break;
                case R.id.Pesanan:
                    replaceFragment(new PenjualPesananFragment());
                    break;
                case R.id.Histori:
                    replaceFragment(new PenjualHistoriFragment());
                    break;
            }
            return true;
        });

        binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(binding.imgProfile);
            }
        });

        binding.saveSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PenarikanSaldo();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_profile) {
                    openSettingsActivity();
                    return true;
                } else if (itemId == R.id.menu_logout) {
                    performLogout();
                    return true;
                } else if (itemId == R.id.laporan) {
                    setTitle("Laporan Pemesanan");
                    performLaporan();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void performLaporan() {
        Intent intent = new Intent(MainPenjualActivity.this, PrintHistoriPesananActivity.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {
        DialogProfilePenjualActivity dialog = new DialogProfilePenjualActivity();
        if (this != null) {
            dialog.showNow(this.getSupportFragmentManager(), "profile");
        }
    }

    private void performLogout() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainPenjualActivity.this).create();
        alertDialog.setTitle("Keluar Admin");
        alertDialog.setMessage("Yakin Untuk Keluar Akun?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PrefManager prefManager = new PrefManager(MainPenjualActivity.this);
                prefManager.setLoginStatus(false);
                Intent intent = new Intent(MainPenjualActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Batal",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

    private void saldo() {
        Log.d("res","url:: "+ ApiServer.site_url_penjual + "getSaldo.php?id_penjual="+prefManager.getId());
        AndroidNetworking.get(ApiServer.site_url_penjual + "getSaldo.php?id_penjual="+prefManager.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("res", "kantin::" + response);
                            if(response.getString("code").equalsIgnoreCase("1")) {
                                JSONArray array = response.getJSONArray("data");
                                if (array.length() > 0) {
                                    JSONObject object = array.getJSONObject(0);
                                    Saldo = object.getString("saldo");
                                    double harga = Double.parseDouble(Saldo);
                                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                                    String hargaFormatted = formatRupiah.format(harga);
                                    binding.txtSaldo.setText(hargaFormatted);
                                    Log.d("res", "kantin::" +Saldo);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error", "onError: " + anError.getErrorBody());
                    }
                });
    }

    private void PenarikanSaldo() {
        AndroidNetworking.post(ApiServer.site_url_penjual + "tarikSaldo.php?id_penjual=" + prefManager.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    public void onResponse(JSONObject response) {
                        try {
                            int code = response.getInt("code");
                            Log.d("response", "response::" + response);

                            if (code == 1) {
                                JSONArray dataArray = response.getJSONArray("data");
                                JSONObject dataObject = dataArray.getJSONObject(0);

                                // Ambil data terbaru setelah penarikan saldo
                                String saldoTerbaru = dataObject.getString("saldo");
                                double harga = Double.parseDouble(saldoTerbaru);
                                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                                String hargaFormatted = formatRupiah.format(harga);
                                binding.txtSaldo.setText(hargaFormatted);

                                PopupDialog.getInstance(MainPenjualActivity.this)
                                        .setStyle(Styles.SUCCESS)
                                        .setHeading("Berhasil !!!")
                                        .setDescription("Penarikan Saldo Anda Berhasil. Saldo Anda Sekarang: " + saldoTerbaru)
                                        .setCancelable(false)
                                        .showDialog(new OnDialogButtonClickListener() {
                                            @Override
                                            public void onDismissClicked(Dialog dialog) {
                                                super.onDismissClicked(dialog);
                                            }
                                        });
                            } else {
                                PopupDialog.getInstance(MainPenjualActivity.this)
                                        .setStyle(Styles.FAILED)
                                        .setHeading("Uh-Oh")
                                        .setDescription("Gagal Penarikan Saldo Anda. Coba Lagi.")
                                        .setCancelable(false)
                                        .showDialog(new OnDialogButtonClickListener() {
                                            @Override
                                            public void onDismissClicked(Dialog dialog) {
                                                super.onDismissClicked(dialog);
                                            }
                                        });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("catch", "gambarModel::" + e.toString());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error", "onError: " + anError.getErrorBody());
                    }
                });
    }
}