 <?php
include_once '../dbconnect.php';
$response=array();
if($_SERVER['REQUEST_METHOD']=='POST'){
    $id_pesanan = $_POST['id_pesanan'];

    $query = "UPDATE tb_pesanan SET status = 'Selesai' WHERE id_pesanan = '$id_pesanan'";

    $execute = mysqli_query($conn, $query);
    $check = mysqli_affected_rows($conn);
    if($check > 0) {
        $response ["code"] = 1;
        $response ["message"] = "Pesanan Selesai";
    }else{
        $response ["code"] = 0;
        $response ["message"] = "Pesanan Gagal";
    }
}else{
        $response ["code"] = 0;
        $response ["message"] = "Tidak ada input Pesanan";
}
echo json_encode($response);