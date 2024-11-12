<?php
include_once '../dbconnect.php';

$query = "SELECT * FROM tb_user,tb_penjual WHERE tb_user.id_user=tb_penjual.id_penjual";
$execute = mysqli_query($conn, $query);
$check = mysqli_affected_rows($conn);

if ($check>0){
    $response["code"]=1;
    $response["message"]="Data ditemukan";
    $response["data"]=array();
    $F = array();
    while($retrieve= mysqli_fetch_object($execute)) {
       $F[]= $retrieve;
    }
    $response["data"]=$F;
} else {
    $response["code"]=0;
    $response["message"]="Data tidak ditemukan";
}
echo json_encode($response);
mysqli_close($conn);