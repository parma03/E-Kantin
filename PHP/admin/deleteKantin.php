<?php
include_once '../dbconnect.php';
$response=array();
if($_SERVER['REQUEST_METHOD']=='POST'){
    $id_penjual = $_POST['id_penjual'];

    $query = "DELETE tb_penjual,tb_user FROM tb_penjual,tb_user WHERE tb_penjual.id_penjual='$id_penjual' AND tb_user.id_user='$id_penjual'";

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