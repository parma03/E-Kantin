<?php
include_once '../dbconnect.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $category = $_POST['category'];
    
    $query = "SELECT * FROM tb_menu WHERE kategori LIKE '%$category%'";
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