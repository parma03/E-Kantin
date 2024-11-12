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

public class DialogAddKonsumenActivity extends AppCompatDialogFragment {
    private EditText editNama, editNohp, editUsername, editPassword;
    private static final String URL_ADD_KONSUMEN =
            ApiServer.site_url_admin + "addKonsumen.php";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_add_konsumen, null);

        editNama = view.findViewById(R.id.editNama);
        editNohp = view.findViewById(R.id.editNohp);
        editUsername = view.findViewById(R.id.editUsername);
        editPassword = view.findViewById(R.id.editPassword);

        builder.setView(view)
                .setTitle("Add Konsumen")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddKonsumen();
                        updateKonsumenList();
                    }
                });
        return  builder.create();
    }

    private void AddKonsumen(){
        String addName = editNama.getText().toString();
        String addNohp = editNohp.getText().toString();
        String addUsername = editUsername.getText().toString();
        String addPassword = editPassword.getText().toString();
        if (addName.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Nama", Toast.LENGTH_SHORT).show();
            return;
        }
        if (addNohp.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom NOHP", Toast.LENGTH_SHORT).show();
            return;
        }
        if (addUsername.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (addPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Password", Toast.LENGTH_SHORT).show();
            return;
        }
        AndroidNetworking.post(URL_ADD_KONSUMEN)
                .addBodyParameter("name", addName)
                .addBodyParameter("nohp", addNohp)
                .addBodyParameter("username", addUsername)
                .addBodyParameter("password", addPassword)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (getActivity() != null) {
                            Log.d("response", "response::" + response);
                            try {
                                int code = response.getInt("code");
                                if (code == 1) {
                                    showToast("Add Konsumen Berhasil");
                                } else if (code == 3) {
                                    showToast("Username Sudah Terpakai");
                                } else {
                                    showToast("Add Konsumen Gagal");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showToast("Add Konsumen Gagal");
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