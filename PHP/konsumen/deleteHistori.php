<?php
include_once '../dbconnect.php';
$response=array();
if($_SERVER['REQUEST_METHOD']=='POST'){
    $id_konsumen = $_POST['id_konsumen'];

    $query = "UPDATE tb_pesanan SET histori='1' WHERE id_konsumen='$id_konsumen'";

    $execute = mysqli_query($conn, $query);
    $check = mysqli_affected_rows($conn);
    if($check > 0) {
        $response ["code"] = 1;
        $response ["message"] = "hapus berhasil";
    }else{
        $response ["code"] = 0;
        $response ["message"] = "hapus gagal";
    }
}else{
        $response ["code"] = 0;
        $response ["message"] = "Tidak ada hapus";
}
echo json_encode($response);