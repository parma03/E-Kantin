<?php
include_once '../dbconnect.php';

$query = "SELECT tb_penjual.id_penjual, tb_penjual.name, COUNT(tb_menu.id_makanan) AS JMLMenu FROM tb_penjual LEFT JOIN tb_menu ON tb_penjual.id_penjual = tb_menu.id_penjual GROUP BY tb_penjual.id_penjual";
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