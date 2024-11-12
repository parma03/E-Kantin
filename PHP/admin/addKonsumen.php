<?php
include_once '../dbconnect.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $name = $_POST['name'];
    $nohp = $_POST['nohp'];
    $username = $_POST['username'];
    $password = $_POST['password'];

    $query = "SELECT * FROM tb_user WHERE username='$username'";
    $checkquery = mysqli_query($conn, $query);

    if (mysqli_num_rows($checkquery) > 0) {
        $response["code"] = 3;
    } else {
        $queryUser = "INSERT INTO tb_user (username, password, role) VALUES ('$username', '$password', 'konsumen')";
        $resultUser = mysqli_query($conn, $queryUser);

        if ($resultUser) {
            $lastInsertId = mysqli_insert_id($conn);
            $queryKonsumen = "INSERT INTO tb_konsumen (id_konsumen, name, nohp) VALUES ('$lastInsertId', '$name', '$nohp')";
            $resultKonsumen = mysqli_query($conn, $queryKonsumen);

            if ($resultKonsumen) {
                $response["code"] = 1;
            } else {
                $response["code"] = 2;
            }
        } else {
            $response["code"] = 2;
        }
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
