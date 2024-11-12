package com.example.e_kantin.activity.konsumen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.androidnetworking.AndroidNetworking;
import com.example.e_kantin.R;
import com.example.e_kantin.activity.LoginActivity;
import com.example.e_kantin.activity.admin.AdminHomeFragment;
import com.example.e_kantin.activity.admin.AdminKantinFragment;
import com.example.e_kantin.activity.admin.AdminKonsumenFragment;
import com.example.e_kantin.activity.admin.DialogProfileAdminActivity;
import com.example.e_kantin.activity.admin.MainAdminActivity;
import com.example.e_kantin.databinding.ActivityMainAdminBinding;
import com.example.e_kantin.databinding.ActivityMainKonsumenBinding;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;
import com.squareup.picasso.Picasso;

public class MainKonsumenActivity extends AppCompatActivity {
    private ActivityMainKonsumenBinding binding;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainKonsumenBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        AndroidNetworking.initialize(this);

        binding.txtName.setText(prefManager.getNama());
        binding.txtRole.setText(prefManager.getTipe());
        if (prefManager.getImg() != null && !prefManager.getImg().equals("null")) {
            Picasso.get()
                    .load(ApiServer.site_url_img_profile + prefManager.getImg())
                    .into(binding.imgProfile);
        } else {
            binding.imgProfile.setImageResource(R.drawable.man);
        }

        replaceFragment(new KonsumenHomeFragment());
        binding.ButtomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.Home:
                    replaceFragment(new KonsumenHomeFragment());
                    break;
                case R.id.Menu:
                    replaceFragment(new KonsumenMenuFragment());
                    break;
                case R.id.Cart:
                    replaceFragment(new KonsumenPesananFragment());
                    break;
                case R.id.Histori:
                    replaceFragment(new KonsumenHistoriFragment());
                    break;
                case R.id.Kantin:
                    replaceFragment(new KonsumenKantinFragment());
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

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.popup_menu_konsumen);
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
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void openSettingsActivity() {
        DialogProfileKonsumenActivity dialog = new DialogProfileKonsumenActivity();
        if (this != null) {
            dialog.showNow(this.getSupportFragmentManager(), "profile");
        }
    }

    private void performLogout() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainKonsumenActivity.this).create();
        alertDialog.setTitle("Keluar Admin");
        alertDialog.setMessage("Yakin Untuk Keluar Akun?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PrefManager prefManager = new PrefManager(MainKonsumenActivity.this);
                prefManager.setLoginStatus(false);
                Intent intent = new Intent(MainKonsumenActivity.this, LoginActivity.class);
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

}