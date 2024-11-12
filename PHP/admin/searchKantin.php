<?php
include_once '../dbconnect.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $keyword = $_POST['keyword'];

    $query = "SELECT * FROM tb_user INNER JOIN tb_penjual ON tb_user.id_user = tb_penjual.id_penjual WHERE tb_penjual.name LIKE '%$keyword%' OR tb_user.username LIKE '%$keyword%' AND role = 'kantin'";
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
} else {
    $response["code"] = 0;
    $response["message"] = "Permintaan tidak valid";
}

echo json_encode($response);
mysqli_close($conn);
?>