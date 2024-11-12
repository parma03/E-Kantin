package com.example.e_kantin.activity.konsumen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.e_kantin.R;
import com.example.e_kantin.adapter.PelangganCheckoutAdapter;
import com.example.e_kantin.databinding.FragmentKonsumenPesananBinding;
import com.example.e_kantin.model.CheckoutModel;
import com.example.e_kantin.util.ApiServer;
import com.example.e_kantin.util.PrefManager;
import com.google.gson.Gson;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.BillingAddress;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.ShippingAddress;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import dmax.dialog.SpotsDialog;

public class KonsumenPesananFragment extends Fragment implements TransactionFinishedCallback {
    private FragmentKonsumenPesananBinding binding;
    private AlertDialog dialog;
    private List<CheckoutModel> checkoutModelList;
    PrefManager prefManager;
    String totalharga;
    private String keteranganPesanan = "";
    private String transactionID;
    private double totalHargaSemuaItem;
    private static final String MIDTRANS_CLIENT_KEY = "SB-Mid-client-qz4dsSPdnxp1Ztqd";
    private static final String base_url = ApiServer.site_url_charge + "presponse.php/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKonsumenPesananBinding.inflate(inflater, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        checkoutModelList = new ArrayList<>();
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();
        prefManager = new PrefManager(requireContext());

        dataPesanan();
        dataTotal();
        initMidtransSdk();

        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
                MidtransSDK.getInstance().startPaymentUiFlow(requireContext());            }
        });

        return binding.getRoot();
    }

    private TransactionRequest initTransactionRequest() {
        ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
        totalHargaSemuaItem = 0.0;

        Map<String, CheckoutModel> checkoutModelMap = new HashMap<>();
        for (CheckoutModel checkoutModel : checkoutModelList) {
            checkoutModelMap.put(checkoutModel.getId_makanan(), checkoutModel);
        }

        long timestamp = System.currentTimeMillis();

        for (String id_menu : checkoutModelMap.keySet()) {
            CheckoutModel checkoutModel = checkoutModelMap.get(id_menu);
            String NamaMenu = checkoutModel.getNamemenu();
            String Harga = checkoutModel.getHarga();
            String Jumlah = checkoutModel.getJumlah();

            double hargaPerItem = Double.parseDouble(Harga);
            int jumlahPerItem = Integer.parseInt(Jumlah);

            for (int i = 0; i < jumlahPerItem; i++) {
                double totalHargaPerItem = hargaPerItem;
                totalHargaSemuaItem += totalHargaPerItem;

                String itemID = "Item_" + id_menu + "_" + (i + 1);

                ItemDetails itemDetails = new ItemDetails(itemID, hargaPerItem, 1, NamaMenu);
                itemDetailsList.add(itemDetails);
            }
        }

        transactionID = "E-Kantin-" + timestamp;

        TransactionRequest transactionRequestNew = new TransactionRequest(transactionID, totalHargaSemuaItem);
        transactionRequestNew.setCustomerDetails(initCustomerDetails());
        transactionRequestNew.setItemDetails(itemDetailsList);

        return transactionRequestNew;
    }


    private CustomerDetails initCustomerDetails() {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setCustomerIdentifier("JOHNDOew");
        customerDetails.setPhone("081234567890");
        customerDetails.setFirstName("John");
        customerDetails.setLastName("Osha");
        customerDetails.setEmail("email@example.com");

        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setAddress("Padang Timur, Jati");  // perbaikan fungsi setAddress
        shippingAddress.setCity("Padang");
        shippingAddress.setPostalCode("51193");
        customerDetails.setShippingAddress(shippingAddress);

        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setAddress("Padang Timur, Jati");  // perbaikan fungsi setAddress
        billingAddress.setCity("Padang");
        billingAddress.setPostalCode("51193");
        customerDetails.setBillingAddress(billingAddress);

        return customerDetails;
    }

    private void tampilkanDialogKeterangan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Lokasi Antar Pesanan");

        // Tambahkan input text ke dalam dialog
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Simpan keterangan yang diinput oleh pengguna
                keteranganPesanan = input.getText().toString();
                // Panggil metode untuk menyimpan pesanan
                BuatPesanan();
                // Tampilkan invoice setelah pesanan berhasil disimpan
                tampilkanInvoice();
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void initMidtransSdk() {
        SdkUIFlowBuilder sdkUIFlowBuilder = SdkUIFlowBuilder.init()
                .setClientKey(MIDTRANS_CLIENT_KEY) // client_key is mandatory
                .setContext(requireContext()) // context is mandatory
                .setTransactionFinishedCallback(this) // set transaction finish callback (sdk callback)
                .setMerchantBaseUrl(base_url)//set merchant url
                .enableLog(true) // enable sdk log
                .setColorTheme(new CustomColorTheme("#FFE51255", "#B61548", "#FFE51255")) // will replace theme on snap theme on MAP
                .setLanguage("id");
        sdkUIFlowBuilder.buildSDK();
    }

    @Override
    public void onTransactionFinished(TransactionResult transactionResult) {
        if (transactionResult.getResponse() != null) {
            switch (transactionResult.getStatus()) {
                case TransactionResult.STATUS_SUCCESS:
                    Toast.makeText(requireContext(), "Transaction Finished. ID: " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    tampilkanDialogKeterangan();
                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(requireContext(), "Transaction Pending. ID: " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(requireContext(), "Transaction Failed. ID: " + transactionResult.getResponse().getTransactionId() + ". Message: " + transactionResult.getResponse().getStatusMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
        } else if (transactionResult.isTransactionCanceled()) {
            Toast.makeText(requireContext(), "Transaction Canceled", Toast.LENGTH_LONG).show();
        } else {
            if (transactionResult.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                Toast.makeText(requireContext(), "Transaction Invalid", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(requireContext(), "Transaction Finished with failure.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dataPesanan() {
        dialog.show();
        Log.d("res","url:: "+ ApiServer.site_url_konsumen + "getCheckout.php?id_konsumen="+prefManager.getId());
        AndroidNetworking.get(ApiServer.site_url_konsumen + "getCheckout.php?id_konsumen="+prefManager.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equalsIgnoreCase("1")) {
                                JSONArray array = response.getJSONArray("data");
                                checkoutModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject pesananObject = array.getJSONObject(i);
                                    CheckoutModel isi = gson.fromJson(pesananObject + "", CheckoutModel.class);
                                    checkoutModelList.add(isi);
                                }
                                PelangganCheckoutAdapter adapter = new PelangganCheckoutAdapter(getActivity(), checkoutModelList);
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                binding.recyclerView.setVisibility(View.GONE);
                                binding.txtBelumAdaPesanan.setVisibility(View.VISIBLE);
                                binding.btnCheckout.setVisibility(View.GONE);
                                binding.txtTotal.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("catch", "kantinmodel::" + e.toString());
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Log.e("error", "onError: " + anError.getErrorBody());
                    }
                });
    }

    private void dataTotal() {
        dialog.show();
        Log.d("res","url:: "+ ApiServer.site_url_konsumen + "getTotalHarga.php?id_konsumen="+prefManager.getId());
        AndroidNetworking.get(ApiServer.site_url_konsumen + "getTotalHarga.php?id_konsumen="+prefManager.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("res", "kantin::" + response);
                            if(response.getString("code").equalsIgnoreCase("1")) {
                                JSONArray array = response.getJSONArray("data");
                                if (array.length() > 0) {
                                    JSONObject object = array.getJSONObject(0);
                                    String jmlHargaString = object.getString("JMLHarga");

                                    if (jmlHargaString != null && !jmlHargaString.equalsIgnoreCase("null")) {
                                        totalharga = jmlHargaString;
                                        double harga = Double.parseDouble(totalharga);
                                        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                                        String hargaFormatted = formatRupiah.format(harga);
                                        binding.txtBelumAdaPesanan.setVisibility(View.GONE);
                                        binding.txtTotal.setText(hargaFormatted);
                                    } else {
                                        binding.txtBelumAdaPesanan.setVisibility(View.VISIBLE);
                                        binding.txtTotal.setText("Rp. 0");
                                    }
                                } else {
                                    binding.txtBelumAdaPesanan.setVisibility(View.VISIBLE);
                                    binding.txtTotal.setText("Rp. 0");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.hide();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Log.e("error", "onError: " + anError.getErrorBody());
                    }
                });
    }

    private void BuatPesanan() {
        dialog.show();

        for (CheckoutModel checkoutModel : checkoutModelList) {
            String pesanan = checkoutModel.getNamemenu();
            String status = "dibayar";
            String id_Menu = checkoutModel.getId_makanan();
            String jumlah = checkoutModel.getJumlah();
            String Harga = checkoutModel.getHarga();

            double totalHargaPerItem = Double.parseDouble(Harga) * Integer.parseInt(jumlah);

            Map<String, String> params = new HashMap<>();
            params.put("id_konsumen", prefManager.getId());
            params.put("id_menu", id_Menu);
            params.put("jumlah", jumlah);
            params.put("pesanan", pesanan);
            params.put("totalHargaPerItem", String.valueOf(totalHargaPerItem));
            params.put("status", status);
            params.put("keterangan", keteranganPesanan);

            AndroidNetworking.post(ApiServer.site_url_konsumen + "simpanPesanan.php")
                    .addBodyParameter(params)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                dialog.dismiss();
                                if (response.getString("code").equalsIgnoreCase("1")) {
                                    dataPesanan();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            dialog.dismiss();
                            Log.e("error", "onError: " + anError.getErrorBody());
                        }
                    });
        }
    }

    private void tampilkanInvoice() {
        // Menampilkan invoice menggunakan WebView
        WebView webView = new WebView(requireContext());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                cetakInvoice(view);
            }
        });

        // Load konten invoice ke WebView
        webView.loadDataWithBaseURL(null, generateInvoiceHTML(), "text/html", "utf-8", null);

        // Tambahkan WebView ke tata letak atau tampilkan dalam dialog
        // Contoh: Tampilkan dalam AlertDialog
        new AlertDialog.Builder(requireContext())
                .setView(webView)
                .setPositiveButton("Cetak", (dialog, which) -> {
                    // Panggil fungsi untuk mencetak invoice
                    cetakInvoice(webView);
                })
                .setNegativeButton("Tutup", null)
                .show();
        dataPesanan();
    }

    private String generateInvoiceHTML() {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>\n");
        htmlBuilder.append("<html>\n");
        htmlBuilder.append("<head>\n");
        htmlBuilder.append("    <style>\n");
        htmlBuilder.append("        body {\n");
        htmlBuilder.append("            font-family: Arial, sans-serif;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .invoice {\n");
        htmlBuilder.append("            width: 100%;\n");
        htmlBuilder.append("            border-collapse: collapse;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .invoice th, .invoice td {\n");
        htmlBuilder.append("            border: 1px solid black;\n");
        htmlBuilder.append("            padding: 8px;\n");
        htmlBuilder.append("            text-align: left;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .invoice th {\n");
        htmlBuilder.append("            background-color: #f2f2f2;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .invoice tr:nth-child(even) {\n");
        htmlBuilder.append("            background-color: #f2f2f2;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .invoice tr:nth-child(odd) {\n");
        htmlBuilder.append("            background-color: #fff;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .invoice .total {\n");
        htmlBuilder.append("            background-color: #4CAF50;\n");
        htmlBuilder.append("            color: white;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("        .invoice .right {\n");
        htmlBuilder.append("            text-align: right;\n");
        htmlBuilder.append("        }\n");
        htmlBuilder.append("    </style>\n");
        htmlBuilder.append("</head>\n");
        htmlBuilder.append("<body>\n");
        htmlBuilder.append("\n");
        htmlBuilder.append("<h2>INVOICE (").append(transactionID).append(")</h2>\n");
        htmlBuilder.append("<h4>").append(prefManager.getNama()).append("</h4>\n");
        htmlBuilder.append("\n");
        htmlBuilder.append("<table class=\"invoice\">\n");
        htmlBuilder.append("<thead>\n");
        htmlBuilder.append("    <tr>\n");
        htmlBuilder.append("        <th>Pesanan</th>\n");
        htmlBuilder.append("        <th>Jumlah</th>\n");
        htmlBuilder.append("        <th>Harga</th>\n");
        htmlBuilder.append("    </tr>\n");
        htmlBuilder.append("</thead>\n");
        htmlBuilder.append("<tbody>\n");

        for (CheckoutModel checkoutModel : checkoutModelList) {
            String NamaMenu = checkoutModel.getNamemenu();
            String Jumlah = checkoutModel.getJumlah();
            String Harga = checkoutModel.getHarga();

            double hargaPerItem = Double.parseDouble(Harga);
            int jumlahPerItem = Integer.parseInt(Jumlah);
            double totalHargaPerItem = hargaPerItem * jumlahPerItem;

            htmlBuilder.append("    <tr>\n");
            htmlBuilder.append("        <td>").append(NamaMenu).append("</td>\n");
            htmlBuilder.append("        <td>").append(Jumlah).append("</td>\n");
            htmlBuilder.append("        <td style=\"text-align: right;\">").append(formatRupiah(totalHargaPerItem)).append("</td>\n");
            htmlBuilder.append("    </tr>\n");
        }

        htmlBuilder.append("</tbody>\n");
        htmlBuilder.append("<tfoot>\n");
        htmlBuilder.append("<td style=\"text-align: right;\" colspan=\"3\">").append(formatRupiah(totalHargaSemuaItem)).append("</td>\n");
        htmlBuilder.append("</tfoot>\n");
        htmlBuilder.append("</table>\n");
        htmlBuilder.append("\n");
        htmlBuilder.append("</body>\n");
        htmlBuilder.append("</html>");

        return htmlBuilder.toString();
    }

    private void cetakInvoice(WebView webView) {
        // Fungsi untuk mencetak invoice menggunakan sistem cetak Android
        PrintManager printManager = (PrintManager) requireContext().getSystemService(Context.PRINT_SERVICE);

        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("Invoice");

        String jobName = getString(R.string.app_name) + " Invoice";

        if (printManager != null) {
            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        }
    }

    private String formatRupiah(double value) {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatRupiah.format(value);
    }

    @Override
    public void onResume() {
        super.onResume();
        dataPesanan();
    }
}