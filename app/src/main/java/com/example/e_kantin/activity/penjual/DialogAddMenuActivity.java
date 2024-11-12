package com.example.e_kantin.activity.penjual;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.activity.admin.AdminKantinFragment;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class DialogAddMenuActivity extends AppCompatDialogFragment {
    private EditText txtNamaMenu, txtHarga;
    private ImageView imgMenu;
    private Spinner spinnerKategori;
    private static final int REQUEST_IMAGE = 1;
    private Uri selectedImageUri;
    private PrefManager prefManager;
    private static final String URL_ADD_MENU =
            ApiServer.site_url_penjual + "addMenu.php";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_add_menu, null);
        prefManager = new PrefManager(requireContext());

        txtNamaMenu = view.findViewById(R.id.txtNamaMenu);
        spinnerKategori = view.findViewById(R.id.spinnerKategori);
        txtHarga = view.findViewById(R.id.txtHarga);
        imgMenu = view.findViewById(R.id.imgMenu);
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        builder.setView(view)
                .setTitle("Add Menu")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddMenu();
                    }
                });
        return  builder.create();
    }

    public interface OnMenuAddedListener {
        void onMenuAdded(String message);
        void onMenuError(String errorMessage);
    }

    private OnMenuAddedListener onMenuAddedListener;

    public void setOnMenuAddedListener(OnMenuAddedListener listener) {
        this.onMenuAddedListener = listener;
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imgMenu.setImageURI(selectedImageUri);
        }
    }

    private void AddMenu(){
        String addName = txtNamaMenu.getText().toString();
        String addKategori = spinnerKategori.getSelectedItem().toString();
        String addHarga = txtHarga.getText().toString();
        if (addName.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Nama Menu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (addKategori.isEmpty()) {
            Toast.makeText(getActivity(), "Pilih Kategori Menu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (addHarga.isEmpty()) {
            Toast.makeText(getActivity(), "Isi Kolom Harga Menu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImageUri != null) {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImageUri);
                File file = createFileFromInputStream(inputStream);
                if (file != null) {
                    AndroidNetworking.upload(URL_ADD_MENU)
                            .addMultipartFile("gambar", file)
                            .addMultipartParameter("id_penjual", prefManager.getId())
                            .addMultipartParameter("namemenu", addName)
                            .addMultipartParameter("kategori", addKategori)
                            .addMultipartParameter("harga", addHarga)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.d("response", "response::" + response);
                                        if (response.getString("code").equals("1")) {
                                            if (onMenuAddedListener != null) {
                                                onMenuAddedListener.onMenuAdded("Add Menu Berhasil");
                                            }
                                        } else {
                                            if (onMenuAddedListener != null) {
                                                onMenuAddedListener.onMenuError("Add Menu Gagal");
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        if (onMenuAddedListener != null) {
                                            onMenuAddedListener.onMenuError("Add Menu Gagal");
                                        }
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.d("response", "eror::" + anError);
                                    if (onMenuAddedListener != null) {
                                        onMenuAddedListener.onMenuError("Tambah Data Menu GAGAL");
                                    }
                                }
                            });
                } else {
                    if (onMenuAddedListener != null) {
                        onMenuAddedListener.onMenuError("Failed to create file");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (onMenuAddedListener != null) {
                    onMenuAddedListener.onMenuError("Failed to open input stream");
                }
            }
        } else {
            if (onMenuAddedListener != null) {
                onMenuAddedListener.onMenuError("Select an image first");
            }
        }
    }

    private File createFileFromInputStream(InputStream inputStream) {
        try {
            String fileName = getFileName(selectedImageUri);
            String newFileName = addRandomNumberToFileName(fileName);
            File file = new File(getContext().getCacheDir(), newFileName);
            FileUtils.copyInputStreamToFile(inputStream, file);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            fileName = cursor.getString(nameIndex);
            cursor.close();
        }
        return fileName;
    }

    private String addRandomNumberToFileName(String fileName) {
        if (fileName != null) {
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex != -1) {
                String extension = fileName.substring(dotIndex);
                String nameWithoutExtension = fileName.substring(0, dotIndex);
                Random random = new Random();
                int randomNumber = random.nextInt(10000);
                return nameWithoutExtension + "_" + randomNumber + extension;
            }
        }
        return fileName;
    }
}