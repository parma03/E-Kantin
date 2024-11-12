<?php
include_once '../dbconnect.php';

$id_penjual = $_GET['id_penjual'];

$query = "UPDATE tb_penjual SET saldo='0' WHERE id_penjual='$id_penjual'";
if ($conn->query($query) === TRUE) {
    $response["code"] = 1;
    $response["message"] = "penarikan berhasil";
    $response["data"] = array();

    // Tambahkan query SELECT untuk mengambil data terbaru
    $selectQuery = "SELECT * FROM tb_penjual WHERE id_penjual='$id_penjual'";
    $result = $conn->query($selectQuery);
    while ($retrieve = mysqli_fetch_assoc($result)) {
        $response["data"][] = $retrieve;
    }
} else {
    $response["code"] = 0;
    $response["message"] = "Gagal melakukan penarikan saldo";
}

echo json_encode($response);
mysqli_close($conn);
?>