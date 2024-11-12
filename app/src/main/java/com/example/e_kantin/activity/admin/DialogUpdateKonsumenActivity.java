package com.example.e_kantin.activity.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import org.json.JSONException;
import org.json.JSONObject;

public class DialogUpdateKonsumenActivity extends AppCompatDialogFragment {
    private EditText editNama, editNohp, editUsername, editPassword;
    private String idKonsumen, name, nohp, username, password;
    private static final String URL_UPDATE_KONSUMEN =
            ApiServer.site_url_admin + "updateKonsumen.php";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_update_konsumen, null);

        editNama = view.findViewById(R.id.editNama);
        editNohp = view.findViewById(R.id.editNohp);
        editUsername = view.findViewById(R.id.editUsername);
        editPassword = view.findViewById(R.id.editPassword);

        Bundle arguments = getArguments();
        if (arguments != null) {
            idKonsumen = arguments.getString("id_konsumen");
            name = arguments.getString("name");
            nohp = arguments.getString("nohp");
            username = arguments.getString("username");
            password = arguments.getString("password");

            // Pre-fill the edit fields with the existing customer details
            editNama.setText(name);
            editNohp.setText(nohp);
            editUsername.setText(username);
            editPassword.setText(password);
        }

        builder.setView(view)
                .setTitle("Update Konsumen")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateKonsumen();
                        updateKonsumenList();
                    }
                });
        return  builder.create();
    }

    private void UpdateKonsumen(){
        String updatedName = editNama.getText().toString();
        String updatedNohp = editNohp.getText().toString();
        String updatedUsername = editUsername.getText().toString();
        String updatedPassword = editPassword.getText().toString();

        if (updatedName.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Nama", Toast.LENGTH_SHORT).show();
            return;
        }
        if (updatedNohp.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom NOHP", Toast.LENGTH_SHORT).show();
            return;
        }
        if (updatedUsername.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (updatedPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Password", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(URL_UPDATE_KONSUMEN)
                .addBodyParameter("id_konsumen", idKonsumen)
                .addBodyParameter("name", updatedName)
                .addBodyParameter("nohp", updatedNohp)
                .addBodyParameter("username", updatedUsername)
                .addBodyParameter("password", updatedPassword)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (getActivity() != null) {
                            try {
                                Log.d("response", "response::" + response);
                                int code = response.getInt("code");
                                if (code == 1) {
                                    showToast("Update Konsumen Berhasil");
                                } else if (code == 3) {
                                    showToast("Username Sudah Terpakai");
                                } else {
                                    showToast("Update Konsumen Gagal");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showToast("Update Konsumen Gagal");
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (getActivity() != null) {
                            Log.e("ERROR", "Error: " + anError.getErrorDetail());
                            showToast("Error: " + anError.getErrorDetail());
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void updateKonsumenList() {
        Fragment adminKonsumenFragment = new AdminKonsumenFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, adminKonsumenFragment);
        transaction.commit();
    }
}