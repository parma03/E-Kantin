package com.example.e_kantin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.WelcomeActivity;
import com.example.e_kantin.activity.admin.DialogAddKonsumenActivity;
import com.example.e_kantin.activity.admin.MainAdminActivity;
import com.example.e_kantin.activity.konsumen.MainKonsumenActivity;
import com.example.e_kantin.activity.penjual.DialogAddMenuActivity;
import com.example.e_kantin.activity.penjual.MainPenjualActivity;
import com.example.e_kantin.databinding.ActivityLoginBinding;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private PrefManager prefManager;
    AlertDialog dialog;
    private FirebaseAuth mAuth;
    private static final String URL_CHECK_LOGIN_ADMIN = ApiServer.site_url_admin + "checkLogin.php";
    private static final String URL_CHECK_LOGIN_KONSUMEN = ApiServer.site_url_konsumen + "checkLogin.php";
    private static final String URL_CHECK_LOGIN_PENJUAL = ApiServer.site_url_penjual + "checkLogin.php";
    String message = "is your code OTP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);
        prefManager = new PrefManager(this);
        mAuth = FirebaseAuth.getInstance();

        dialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Please Wait.").setCancelable(false).build();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        });

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        binding.lupaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });

        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        dialog.show();
        String username = binding.txtUsername.getText().toString().trim();
        String password = binding.txtPassword.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            showEmptyFieldsDialog();
            dialog.dismiss(); // Menyembunyikan dialog jika input kosong
            return;
        }

        AndroidNetworking.post(URL_CHECK_LOGIN_ADMIN)
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.hide();
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                JSONArray array = response.getJSONArray("data");
                                JSONObject object = array.getJSONObject(0);
                                String id = object.getString("id_user");
                                prefManager.setId(id);
                                prefManager.setUsername(object.getString("username"));
                                prefManager.setPassword(object.getString("password"));
                                prefManager.setImg(object.getString("gambar"));
                                prefManager.setLoginStatus(true);
                                prefManager.setTipe("admin");
                                Intent intent = new Intent(LoginActivity.this, MainAdminActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                loginKonsumen(); // Lanjut ke login konsumen jika login admin gagal
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.hide();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.hide();
                    }
                });
    }

    private void loginKonsumen() {
        String username = binding.txtUsername.getText().toString().trim();
        String password = binding.txtPassword.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            showEmptyFieldsDialog();
            dialog.dismiss(); // Menyembunyikan dialog jika input kosong
            return;
        }
        // Login Konsumen
        AndroidNetworking.post(URL_CHECK_LOGIN_KONSUMEN)
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.hide();
                            Log.d("response", "response::" + response);

                            if (response.getString("code").equals("1")) {
                                JSONArray array = response.getJSONArray("data");
                                JSONObject object = array.getJSONObject(0);
                                String id = object.getString("id_konsumen");
                                prefManager.setId(id);
                                prefManager.setNama(object.getString("name"));
                                prefManager.setUsername(object.getString("username"));
                                prefManager.setPassword(object.getString("password"));
                                prefManager.setNohp(object.getString("nohp"));
                                prefManager.setLoginStatus(true);
                                prefManager.setTipe("konsumen");
                                prefManager.setImg(object.getString("gambar"));
                                Intent intent = new Intent(LoginActivity.this, MainKonsumenActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                loginPenjual(); // Lanjut ke login penjual jika login konsumen gagal
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.hide();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.hide();
                    }
                });
    }

    private void loginPenjual() {
        String username = binding.txtUsername.getText().toString().trim();
        String password = binding.txtPassword.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            showEmptyFieldsDialog();
            dialog.dismiss(); // Menyembunyikan dialog jika input kosong
            return;
        }
        // Login Penjual
        AndroidNetworking.post(URL_CHECK_LOGIN_PENJUAL)
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.hide();
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                JSONArray array = response.getJSONArray("data");
                                JSONObject object = array.getJSONObject(0);
                                String id = object.getString("id_penjual");
                                prefManager.setId(id);
                                prefManager.setNama(object.getString("name"));
                                prefManager.setUsername(object.getString("username"));
                                prefManager.setPassword(object.getString("password"));
                                prefManager.setNohp(object.getString("nohp"));
                                prefManager.setLoginStatus(true);
                                prefManager.setTipe("penjual");
                                prefManager.setImg(object.getString("gambar"));
                                Intent intent = new Intent(LoginActivity.this, MainPenjualActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                loginGagal();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.hide();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.hide();
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    public void loginGagal() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_logingagal, (ViewGroup) findViewById(R.id.toastgagallogin));

        TextView toastText = layout.findViewById(R.id.toastxt);
        ImageView toastImage = layout.findViewById(R.id.toastimg);

        toastText.setText("Wrong Login Username or Password");
        toastImage.setImageResource(R.drawable.baseline_sentiment_very_dissatisfied_24);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void showEmptyFieldsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Please fill in all fields.");
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
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

    private void showForgotPasswordDialog() {
        // Membuat dialog untuk input username dan nohp
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");

        // Inflate layout untuk dialog custom
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_reset_password, null);
        builder.setView(view);

        // Mendapatkan referensi ke elemen-elemen di dalam dialog
        final EditText usernameEditText = view.findViewById(R.id.editUsername);
        final EditText nohpEditText = view.findViewById(R.id.editNohp);
        Button submitButton = view.findViewById(R.id.buttonSubmit);

        // Menambahkan listener untuk tombol submit
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mendapatkan nilai dari input username dan nohp
                String username = usernameEditText.getText().toString().trim();
                String nohp = nohpEditText.getText().toString().trim();

                if (username.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Isi Kolom Username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (nohp.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Isi Kolom Nohp", Toast.LENGTH_SHORT).show();
                    return;
                }
                AndroidNetworking.post(ApiServer.site_url_admin + "checkReset.php")
                        .addBodyParameter("username", username)
                        .addBodyParameter("nohp", nohp)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d("response", "response::" + response);
                                    if (response.getString("code").equals("1")) {
                                        JSONArray array = response.getJSONArray("data");
                                        JSONObject object = array.getJSONObject(0);
                                        String id = object.getString("id_user");
                                        prefManager.setId(id);
                                        sendVerificationSMS(nohp);
                                        PopupDialog.getInstance(LoginActivity.this)
                                                .setStyle(Styles.SUCCESS)
                                                .setHeading("BERHASIL !!!")
                                                .setDescription("Kode OTP Terkirim")
                                                .setCancelable(false)
                                                .showDialog(new OnDialogButtonClickListener() {
                                                    @Override
                                                    public void onDismissClicked(Dialog dialog) {
                                                        super.onDismissClicked(dialog);
                                                        reset();
                                                    }
                                                });
                                    } else if (response.getString("code").equals("2")) {
                                        PopupDialog.getInstance(LoginActivity.this)
                                                .setStyle(Styles.FAILED)
                                                .setHeading("GAGAL !!!")
                                                .setDescription("Nohp Salah")
                                                .setCancelable(false)
                                                .showDialog(new OnDialogButtonClickListener() {
                                                    @Override
                                                    public void onDismissClicked(Dialog dialog) {
                                                        super.onDismissClicked(dialog);
                                                    }
                                                });
                                    } else if (response.getString("code").equals("3")) {
                                        PopupDialog.getInstance(LoginActivity.this)
                                                .setStyle(Styles.FAILED)
                                                .setHeading("GAGAL !!!")
                                                .setDescription("Username Tidak Terdaftar")
                                                .setCancelable(false)
                                                .showDialog(new OnDialogButtonClickListener() {
                                                    @Override
                                                    public void onDismissClicked(Dialog dialog) {
                                                        super.onDismissClicked(dialog);
                                                    }
                                                });
                                    }else {
                                        PopupDialog.getInstance(LoginActivity.this)
                                                .setStyle(Styles.FAILED)
                                                .setHeading("GAGAL !!!")
                                                .setDescription("GAGAL Reset")
                                                .setCancelable(false)
                                                .showDialog(new OnDialogButtonClickListener() {
                                                    @Override
                                                    public void onDismissClicked(Dialog dialog) {
                                                        super.onDismissClicked(dialog);
                                                    }
                                                });
                                    }
                                } catch (JSONException e) {
                                    PopupDialog.getInstance(LoginActivity.this)
                                            .setStyle(Styles.FAILED)
                                            .setHeading("GAGAL !!!")
                                            .setDescription("GAGAL Reset")
                                            .setCancelable(false)
                                            .showDialog(new OnDialogButtonClickListener() {
                                                @Override
                                                public void onDismissClicked(Dialog dialog) {
                                                    super.onDismissClicked(dialog);
                                                }
                                            });
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                PopupDialog.getInstance(LoginActivity.this)
                                        .setStyle(Styles.FAILED)
                                        .setHeading("GAGAL !!!")
                                        .setDescription("Error: " + anError.getErrorDetail())
                                        .setCancelable(false)
                                        .showDialog(new OnDialogButtonClickListener() {
                                            @Override
                                            public void onDismissClicked(Dialog dialog) {
                                                super.onDismissClicked(dialog);
                                            }
                                        });
                            }
                        });
            dialog.dismiss();
            }
        });

        // Membuat dan menampilkan dialog
        dialog = builder.create();
        dialog.show();
    }

    private void sendVerificationSMS(String Nohp) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+62"+Nohp)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(LoginActivity.this, "Verifikasi gagal, coba lagi nanti.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            prefManager.setVerificationCode(verificationId);
            reset();
        }
    };

    private void reset() {
        // Membuat dialog untuk input username dan nohp
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Masukan Password Baru");

        // Inflate layout untuk dialog custom
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_new_password, null);
        builder.setView(view);

        // Mendapatkan referensi ke elemen-elemen di dalam dialog
        final EditText Password = view.findViewById(R.id.editPassword);
        final EditText enteredVerificationCode = view.findViewById(R.id.editVerificationCode);
        String savedVerificationCode = prefManager.getVerificationCode();

        Button submitButton = view.findViewById(R.id.buttonSubmit);

        // Menambahkan listener untuk tombol submit
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mendapatkan nilai dari input username dan nohp
                String newPassword = Password.getText().toString().trim();
                String OTP = enteredVerificationCode.getText().toString().trim();

                if (newPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Isi Kolom Password", Toast.LENGTH_SHORT).show();
                    return;
                } else if (newPassword.length() < 5 || !containsNumber(newPassword)) {
                    Toast.makeText(LoginActivity.this, "Password harus minimal 5 karakter dan mengandung angka", Toast.LENGTH_SHORT).show();
                }
                if (OTP.isEmpty() || !OTP.equals(OTP)) {
                    Toast.makeText(LoginActivity.this, "Kode Verifikasi Salah", Toast.LENGTH_SHORT).show();
                    return;
                }

                AndroidNetworking.post(ApiServer.site_url_admin + "resetPassword.php")
                        .addBodyParameter("password", newPassword)
                        .addBodyParameter("id_user", prefManager.getId())
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d("response", "response::" + response);
                                    if (response.getString("code").equals("1")) {
                                        PopupDialog.getInstance(LoginActivity.this)
                                                .setStyle(Styles.SUCCESS)
                                                .setHeading("BERHASIL !!!")
                                                .setDescription("Reset Password Berhasil")
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
                                    PopupDialog.getInstance(LoginActivity.this)
                                            .setStyle(Styles.FAILED)
                                            .setHeading("GAGAL !!!")
                                            .setDescription("GAGAL RESET !!!")
                                            .setCancelable(false)
                                            .showDialog(new OnDialogButtonClickListener() {
                                                @Override
                                                public void onDismissClicked(Dialog dialog) {
                                                    super.onDismissClicked(dialog);
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                PopupDialog.getInstance(LoginActivity.this)
                                        .setStyle(Styles.FAILED)
                                        .setHeading("GAGAL !!!")
                                        .setDescription("Error: " + anError.getErrorDetail())
                                        .setCancelable(false)
                                        .showDialog(new OnDialogButtonClickListener() {
                                            @Override
                                            public void onDismissClicked(Dialog dialog) {
                                                super.onDismissClicked(dialog);
                                            }
                                        });
                            }
                        });
                // Menutup dialog setelah tombol submit ditekan
                dialog.dismiss();
            }
        });

        // Membuat dan menampilkan dialog
        dialog = builder.create();
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