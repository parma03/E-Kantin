<?php
include_once '../dbconnect.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $gambar = $_POST['gambar'];

    $targetDir = "../gambarProfile/";

    if (unlink($targetDir.$gambar)) {
        $response["code"] = 1;
        $response["message"] = "Foto Menu berhasil dihapus";
    } else {
        $response["code"] = 0;
        $response["message"] = "Gagal menghapus Foto Menu";
    }
} else {
    $response["code"] = 0;
    $response["message"] = "Tidak ada input Foto Menu";
}

ini_set("log_errors", 1);
ini_set("error_log", "file.log");

// Konversi array $response menjadi string
$log_message = print_r($response, true);

error_log($log_message);
echo json_encode($response);
?>