package com.example.e_kantin.activity.penjual;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.util.ApiServer;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class DialogUpdateMenuActivity extends AppCompatDialogFragment {
    private Context context;
    private EditText editNama, editHarga;
    private String id_makanan, namemenu, kategori, harga, gambar;
    private Spinner editSpinnerKategori;
    private ImageView editImgMenu;
    private static final int REQUEST_IMAGE = 1;
    private Uri selectedImageUri;
    private static final String URL_UPDATE_MENU =
            ApiServer.site_url_penjual + "updateMenu.php";
    private static final String URL_DELETE_GAMBAR =
            ApiServer.site_url_penjual + "deleteFotoMenu.php";

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog_update_menu, null);

        editNama = view.findViewById(R.id.editNama);
        editSpinnerKategori = view.findViewById(R.id.editSpinnerKategori);
        editHarga = view.findViewById(R.id.editHarga);
        editImgMenu = view.findViewById(R.id.editimgMenu);

        Bundle arguments = getArguments();
        if (arguments != null) {
            id_makanan = arguments.getString("id_makanan");
            namemenu = arguments.getString("namemenu");
            kategori = arguments.getString("kategori");
            harga = arguments.getString("harga");
            gambar = arguments.getString("gambar");

            editNama.setText(namemenu);
            int statusPosition = getIndexByValue(editSpinnerKategori, kategori);
            editSpinnerKategori.setSelection(statusPosition);
            editHarga.setText(harga);
            Picasso.get()
                    .load(ApiServer.site_url_gambar_menu + gambar)
                    .into(editImgMenu);
        }

        editImgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        builder.setView(view)
                .setTitle("Update Menu")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateMenu();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public interface OnMenuUpdatedListener {
        void onMenuAdded(String message);
        void onMenuError(String errorMessage);
    }

    private DialogUpdateMenuActivity.OnMenuUpdatedListener onMenuUpdatedListener;

    public void setOnMenuUpdatedListener(DialogUpdateMenuActivity.OnMenuUpdatedListener listener) {
        this.onMenuUpdatedListener = listener;
    }

    private void UpdateMenu() {
        if (selectedImageUri != null && gambar != null) {
            deleteFotoMenu();
        } else {
            updateMenu(null);
        }
    }

    private void deleteFotoMenu() {
        AndroidNetworking.post(URL_DELETE_GAMBAR)
                .addBodyParameter("gambar", gambar)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        updateMenu(selectedImageUri);
                    }
                    @Override
                    public void onError(ANError anError) {
                        if (onMenuUpdatedListener != null) {
                            onMenuUpdatedListener.onMenuError("Gagal Update Data Bus");
                        }
                    }
                });
    }

    private void updateMenu(Uri newImageUri) {
        String updatedName = editNama.getText().toString();
        String updatedKategori = editSpinnerKategori.getSelectedItem().toString();
        String updateHarga = editHarga.getText().toString();

        AndroidNetworking.upload(URL_UPDATE_MENU)
                .addMultipartParameter("id_makanan", id_makanan)
                .addMultipartParameter("namemenu", updatedName)
                .addMultipartParameter("kategori", updatedKategori)
                .addMultipartParameter("harga", updateHarga)
                .addMultipartFile("gambar", createFileFromUri(selectedImageUri))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equals("1")) {
                                if (onMenuUpdatedListener != null) {
                                    onMenuUpdatedListener.onMenuAdded("Update Data Menu Berhasil");
                                }
                            } else {
                                if (onMenuUpdatedListener != null) {
                                    onMenuUpdatedListener.onMenuError("Gagal Update Data Menu");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        if (getActivity() != null) {
                            if (onMenuUpdatedListener != null) {
                                onMenuUpdatedListener.onMenuError("Gagal Update Data Menu");
                            }
                        }
                    }
                });
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
            editImgMenu.setImageURI(selectedImageUri);
        }
    }

    private File createFileFromUri(Uri uri) {
        try {
            if (uri != null) {
                String fileName = getFileName(uri);
                if (fileName != null) {
                    String newFileName = addRandomNumberToFileName(fileName);
                    File file = new File(context.getCacheDir(), newFileName);
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    FileUtils.copyInputStreamToFile(inputStream, file);
                    return file;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri != null) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                fileName = cursor.getString(nameIndex);
                cursor.close();
            }
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

    private int getIndexByValue(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                return i;
            }
        }
        return 0;
    }
}