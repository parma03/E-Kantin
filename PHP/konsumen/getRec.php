<?php
include_once '../dbconnect.php';

$query = "SELECT * FROM tb_menu INNER JOIN tb_penjual ON tb_menu.id_penjual=tb_penjual.id_penjual ORDER BY RAND() LIMIT 10";
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