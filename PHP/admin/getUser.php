<?php
include_once '../dbconnect.php';

$query = "SELECT tb_user.username, tb_user.role, COALESCE(tb_konsumen.name, tb_penjual.name) AS name, COALESCE(tb_konsumen.nohp, tb_penjual.nohp) AS nohp FROM tb_user LEFT JOIN tb_konsumen ON tb_user.id_user = tb_konsumen.id_konsumen LEFT JOIN tb_penjual ON tb_user.id_user = tb_penjual.id_penjual WHERE tb_user.role='konsumen' OR tb_user.role='kantin'";
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