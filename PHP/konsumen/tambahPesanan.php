<?php
include_once '../dbconnect.php';

$response=array();

if($_SERVER['REQUEST_METHOD']=='POST'){
    $id_menu = $_POST['id_menu'];
    $id_pembeli = $_POST['id_pembeli'];

    $query = "INSERT INTO tb_menu (id_konsumen,id_menu) VALUES('$id_pembeli','$id_menu')";
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