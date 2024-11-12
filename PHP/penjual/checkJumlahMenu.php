<?php
include_once '../dbconnect.php';

$id_penjual = $_GET['id_penjual'];

$query = "SELECT COUNT(*) AS JML FROM tb_menu WHERE id_penjual = '$id_penjual'";
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