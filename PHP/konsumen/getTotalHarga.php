<?php
include_once '../dbconnect.php';

$id_konsumen = $_GET['id_konsumen'];

$query = "SELECT SUM(tb_menu.harga * tb_checkout.jumlah) AS JMLHarga FROM tb_checkout INNER JOIN tb_menu ON tb_checkout.id_makanan = tb_menu.id_makanan WHERE tb_checkout.id_konsumen = '$id_konsumen'";
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