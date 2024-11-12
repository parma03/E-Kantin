<?php
include_once '../dbconnect.php';

$response=array();

if($_SERVER['REQUEST_METHOD']=='POST'){
    $id_makanan = $_POST['id_makanan'];
    $id_konsumen = $_POST['id_konsumen'];
    $jumlah = $_POST['jumlah'];

    $query = "INSERT INTO tb_checkout (id_konsumen,id_makanan,jumlah) VALUES('$id_konsumen','$id_makanan','$jumlah')";
    $checkquery = mysqli_query($conn, $query);
    $check = mysqli_affected_rows($conn);
    if ($check > 0) {
        $response["code"] = 1;
        $response["message"] = "tambah berhasil";
    } else {
        $response["code"] = 0;
        $response["message"] = "tambah gagal";
    }
} else {
    $response["code"] = 0;
    $response["message"] = "tambah gagal";
}
echo json_encode($response);
mysqli_close($conn);