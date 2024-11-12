<?php
include_once '../dbconnect.php';

$id_penjual = $_GET['id_penjual'];

$query = "SELECT tb_pesanan.id_pesanan, tb_pesanan.id_penjual, tb_pesanan.id_konsumen, tb_pesanan.pesanan, tb_pesanan.jumlah, tb_pesanan.total_harga, tb_pesanan.status, tb_pesanan.histori, tb_pesanan.keterangan, tb_konsumen.name, tb_konsumen.nohp, tb_menu.namemenu, tb_menu.kategori, tb_menu.harga, tb_menu.gambar FROM tb_pesanan JOIN tb_konsumen ON tb_pesanan.id_konsumen = tb_konsumen.id_konsumen JOIN tb_menu ON tb_pesanan.id_makanan = tb_menu.id_makanan WHERE tb_pesanan.status='Selesai' AND tb_pesanan.id_penjual='$id_penjual' GROUP BY tb_pesanan.id_pesanan";
$execute = mysqli_query($conn, $query);
$check = mysqli_num_rows($execute);

if ($check > 0) {
    $response["code"] = 1;
    $response["message"] = "Data ditemukan";
    $response["data"] = array();

    while ($retrieve = mysqli_fetch_assoc($execute)) {
        $response["data"][] = $retrieve;
    }
} else {
    $response["code"] = 0;
    $response["message"] = "Data tidak ditemukan";
}

echo json_encode($response);
mysqli_close($conn);
?>