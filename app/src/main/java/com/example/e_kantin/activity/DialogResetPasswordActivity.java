package com.example.e_kantin.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.WelcomeActivity;
import com.example.e_kantin.activity.konsumen.KonsumenPesananFragment;
import com.example.e_kantin.activity.penjual.DialogAddMenuActivity;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Random;

public class DialogResetPasswordActivity extends AppCompatDialogFragment {
    private EditText editNohp, editUsername;
    private PrefManager prefManager;

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_reset_password, null);
        prefManager = new PrefManager(getActivity());

        editNohp = view.findViewById(R.id.editNohp);
        editUsername = view.findViewById(R.id.editUsername);

        builder.setView(view)
                .setTitle("Masukan Informasi NOHP & Username")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                            // Kirim SMS
                            CheckReset();
                        } else {
                            // Jika belum, minta izin secara dinamis
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, 100);
                        }
                    }
                });
        return  builder.create();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, kirim SMS
                sendVerificationSMS(editNohp.getText().toString());
            } else {
                // Izin ditolak, beri tahu pengguna
                Toast.makeText(getActivity(), "Izin SMS ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnPasswordResetListener {
        void onPasswordReset(String message);
        void onPasswordResetError(String errorMessage);
    }

    private DialogResetPasswordActivity.OnPasswordResetListener onPasswordResetListener;

    public void setOnPasswordResetListener(OnPasswordResetListener listener) {
        this.onPasswordResetListener = listener;
    }

    private void CheckReset(){
        String Nohp = editNohp.getText().toString();
        String Username = editUsername.getText().toString();

        if (Username.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Nohp.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Nohp", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_admin + "checkReset.php")
                .addBodyParameter("username", Username)
                .addBodyParameter("nohp", Nohp)
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
                                    if (onPasswordResetListener != null) {
                                        onPasswordResetListener.onPasswordReset("Lakukan Reset Password");
                                        sendVerificationSMS(Nohp);
                                    }
                                } else if (response.getString("code").equals("2")) {
                                    if (onPasswordResetListener != null) {
                                        onPasswordResetListener.onPasswordResetError("Nohp Salah");
                                    }
                                } else if (response.getString("code").equals("3")) {
                                    if (onPasswordResetListener != null) {
                                        onPasswordResetListener.onPasswordResetError("Username Tidak Terdaftar");
                                    }
                                }else {
                                    if (onPasswordResetListener != null) {
                                        onPasswordResetListener.onPasswordResetError("GAGAL Reset");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (onPasswordResetListener != null) {
                                    onPasswordResetListener.onPasswordResetError("GAGAL RESET !!!");
                                }
                            }
                        }

                    @Override
                    public void onError(ANError anError) {
                        if (getActivity() != null) {
                            Log.e("ERROR", "Error: " + anError.getErrorDetail());
                            if (onPasswordResetListener != null) {
                                onPasswordResetListener.onPasswordResetError("Error: " + anError.getErrorDetail());
                            }
                        }
                    }
                });
    }

    private void sendVerificationSMS(String Nohp) {
        // Generate kode verifikasi secara acak
        Log.d("MyApp", "Mengirim SMS ke nomor: " + Nohp);

        String verificationCode = generateVerificationCode();

        // Kirim SMS
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(Nohp, null, "Kode Verifikasi: " + verificationCode, null, null);

        // Simpan kode verifikasi di PrefManager atau variabel global
        prefManager.setVerificationCode(verificationCode);
    }

    private String generateVerificationCode() {
        // Implementasi pembangkitan kode verifikasi, contoh sederhana:
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        return String.valueOf(code);
    }
}