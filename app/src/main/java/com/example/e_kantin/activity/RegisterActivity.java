package com.example.e_kantin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.WelcomeActivity;
import com.example.e_kantin.databinding.ActivityRegisterBinding;
import com.example.e_kantin.util.ApiServer;

import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    AlertDialog dialog;
    private static final String URL_INSERT_KONSUMEN =
            ApiServer.site_url_admin+"addKonsumen.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        });

        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        dialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage("Loading ......").setCancelable(false).build();
    }

    private void register() {
        dialog.show();
        String fullname = binding.txtName.getText().toString();
        String nohp = binding.txtNOHP.getText().toString();
        String username = binding.txtUsername.getText().toString();
        String password = binding.txtPassword.getText().toString();
        if (fullname.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Isi Kolom Nama", Toast.LENGTH_SHORT).show();
            dialog.hide();
            return;
        }

        if (nohp.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Isi Kolom Nohp", Toast.LENGTH_SHORT).show();
            dialog.hide();
            return;
        }

        if (username.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Isi Kolom Username", Toast.LENGTH_SHORT).show();
            dialog.hide();
            return;
        } else if (username.length() < 5) {
            Toast.makeText(RegisterActivity.this, "Username harus minimal 5 karakter", Toast.LENGTH_SHORT).show();
            dialog.hide();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Isi Kolom Password", Toast.LENGTH_SHORT).show();
            dialog.hide();
            return;
        } else if (password.length() < 5 || !containsNumber(password)) {
            Toast.makeText(RegisterActivity.this, "Password harus minimal 5 karakter dan mengandung angka", Toast.LENGTH_SHORT).show();
            dialog.hide();
            return;
        }

        AndroidNetworking.post(URL_INSERT_KONSUMEN)
                .addBodyParameter("name", fullname)
                .addBodyParameter("nohp", nohp)
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "response::" + response);
                        try {
                            dialog.hide();
                            Log.d("response", "response::" + response.getString("code"));
                            if (response.getString("code").equals("1")) {
                                Toast.makeText(RegisterActivity.this, "Berhasil Mendaftar", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else if (response.getString("code").equals("3")) {
                                Toast.makeText(RegisterActivity.this, "Username Telah Terdaftar", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Pendaftaran GAGAL!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.hide();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response", "error::" + anError.getErrorBody());
                        Log.d("response", "error detail::" + anError.getErrorDetail());
                        Log.d("response", "error code::" + anError.getErrorCode());
                        Toast.makeText(RegisterActivity.this, "Pendaftaran GAGAL!!!", Toast.LENGTH_SHORT).show();
                        dialog.hide();
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

    private void showEmptyFieldsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Please fill in all fields.");
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean containsNumber(String s) {
        // Fungsi untuk memeriksa apakah string mengandung angka
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
}