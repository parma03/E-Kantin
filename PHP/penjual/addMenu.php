<?php
include_once '../dbconnect.php';

$response=array();

if($_SERVER['REQUEST_METHOD']=='POST'){
    $id_penjual = $_POST['id_penjual'];
    $name = $_POST['namemenu'];
    $kategori = $_POST['kategori'];
    $harga = $_POST['harga'];
    $gambar = $_FILES['gambar']['name'];

    $targetDir = "../gambarMenu/";
    $gambarMenu = basename($gambar);

    $query = "SELECT * FROM tb_menu WHERE namemenu='$name' AND id_penjual='$id_penjual'";
    $checkquery = mysqli_query($conn, $query);
    if(mysqli_num_rows($checkquery) > 0) {
        $response ["code"] = 3;
    }else{
        $response ["code"] = 1;
        if (move_uploaded_file($_FILES["gambar"]["tmp_name"], $targetDir.$gambarMenu)) {
            $query = "INSERT INTO tb_menu (id_penjual,namemenu,kategori,harga,gambar) VALUES('$id_penjual','$name','$kategori','$harga','$gambarMenu')";
            $execute = mysqli_query($conn, $query);
            $check = mysqli_affected_rows($conn);
    
            if ($check > 0) {
                $response["code"] = 1;
                $response["message"] = "Input Menu berhasil";
            } else {
                $response["code"] = 0;
                $response["message"] = "Input Menu gagal";
            }
        } else {
            $response["code"] = 0;
            $response["message"] = "Gagal upload gambar";
        }
    }
}else{
    $response ["code"] = 0;
}
echo json_encode($response);
mysqli_close($conn);