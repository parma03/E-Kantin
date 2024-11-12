package com.example.e_kantin.activity.konsumen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.example.e_kantin.activity.admin.AdminKonsumenFragment;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

public class DialogAddPesananActivity extends AppCompatDialogFragment {

    private EditText editJumlah;
    private String idMakanan;
    PrefManager prefManager;

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_add_pesanan, null);
        prefManager = new PrefManager(requireContext());

        editJumlah = view.findViewById(R.id.editJML);

        Bundle arguments = getArguments();
        if (arguments != null) {
            idMakanan = arguments.getString("id_makanan");
        }

        builder.setView(view)
                .setTitle("Jumlah Pesanan")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddCheckout();
                        prosesAddCheckout();
                    }
                });
        return  builder.create();
    }

    private void AddCheckout(){
        String JumlahPesanan = editJumlah.getText().toString();
        String idKonsumen = prefManager.getId();

        if (JumlahPesanan.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Jumlah", Toast.LENGTH_SHORT).show();
            return;
        }

        AndroidNetworking.post(ApiServer.site_url_konsumen + "addCheckout.php")
                .addBodyParameter("id_makanan", idMakanan)
                .addBodyParameter("id_konsumen", idKonsumen)
                .addBodyParameter("jumlah", JumlahPesanan)
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
                                    showToast("Pesanan Telah Ditambahkan");
                                } else {
                                    showToast("Pesanan Gagal Ditambahkan");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showToast("PESANAN GAGAL DITAMBAHKAN");
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

    private void prosesAddCheckout() {
        Fragment konsumenPesananFragment = new KonsumenPesananFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, konsumenPesananFragment);
        transaction.commit();
    }
}