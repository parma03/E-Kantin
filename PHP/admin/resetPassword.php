<?php
include_once '../dbconnect.php';
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $password = $_POST['password'];
    $id_user = $_POST['id_user'];

    $query = "UPDATE tb_user SET password='$password' WHERE id_user='$id_user'";
    if ($conn->query($query) === TRUE) {
        $response["code"] = 1;
        $response["message"] = "berhasil";
    } else {
        $response["code"] = 0;
        $response["message"] = "Gagal";
    }
} else {
    $response["code"] = 0;
}

ini_set("log_errors", 1);
ini_set("error_log", "file.log");

// Konversi array $response menjadi string
$log_message = print_r($response, true);

error_log($log_message);
echo json_encode($response);
?>