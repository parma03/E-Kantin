package com.example.e_kantin.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

public class DialogNewPasswordActivity extends AppCompatDialogFragment {
    private PrefManager prefManager;
    private EditText editPassword, editVerificationCode;

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_new_password, null);
        prefManager = new PrefManager(getActivity());

        editPassword = view.findViewById(R.id.editPassword);
        editVerificationCode = view.findViewById(R.id.editVerificationCode);
        builder.setView(view)
                .setTitle("Masukan Password Baru")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Reset();
                    }
                });
        return builder.create();
    }

    public interface OnNewPasswordListener {
        void onNewPassword(String message);
        void onNewPasswordError(String errorMessage);
    }

    private DialogNewPasswordActivity.OnNewPasswordListener onNewPasswordListener;

    public void setOnNewPasswordListener(OnNewPasswordListener listener) {
        this.onNewPasswordListener = listener;
    }

    private void Reset() {
        String Password = editPassword.getText().toString();
        String enteredVerificationCode = editVerificationCode.getText().toString();
        String savedVerificationCode = prefManager.getVerificationCode();

        if (Password.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Password", Toast.LENGTH_SHORT).show();
            return;
        } else if (Password.length() < 5 || !containsNumber(Password)) {
            Toast.makeText(getActivity(), "Password harus minimal 5 karakter dan mengandung angka", Toast.LENGTH_SHORT).show();
        }
        if (enteredVerificationCode.isEmpty() || !enteredVerificationCode.equals(savedVerificationCode)) {
            Toast.makeText(getActivity(), "Kode Verifikasi Salah", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_admin + "resetPassword.php")
                .addBodyParameter("password", Password)
                .addBodyParameter("id_user", prefManager.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                if (onNewPasswordListener != null) {
                                    onNewPasswordListener.onNewPassword("Reset Password Berhasil");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (onNewPasswordListener != null) {
                                onNewPasswordListener.onNewPasswordError("GAGAL RESET !!!");
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (getActivity() != null) {
                            Log.e("ERROR", "Error: " + anError.getErrorDetail());
                            if (onNewPasswordListener != null) {
                                onNewPasswordListener.onNewPasswordError("Error: " + anError.getErrorDetail());
                            }
                        }
                    }
                });
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