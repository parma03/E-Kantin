<?php
include_once '../dbconnect.php';

$response=array();

if($_SERVER['REQUEST_METHOD']=='POST'){
    $name = $_POST['name'];
    $nohp     = $_POST['nohp'];
    $username = $_POST['username'];
    $password = $_POST['password'];

    $query = "SELECT * FROM tb_user WHERE username='$username'";
    $checkquery = mysqli_query($conn, $query);
    if(mysqli_num_rows($checkquery) > 0) {
        $response ["code"] = 3;
    }else{
        $response ["code"] = 1;
        $query = "INSERT INTO tb_user (username,password,role) VALUES('$username','$password','kantin');";
        $query .= "INSERT INTO tb_penjual (id_penjual,name,nohp) VALUES(LAST_INSERT_ID(),'$name','$nohp')";
        $execute = mysqli_multi_query($conn, $query);
    }
}else{
    $response ["code"] = 0;
}
echo json_encode($response);
mysqli_close($conn);