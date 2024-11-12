<?php
include_once '../dbconnect.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $nohp = $_POST['nohp'];
    $username = $_POST['username'];

    $query = "SELECT tb_user.id_user, tb_user.username, COALESCE(tb_konsumen.name, tb_penjual.name) AS name, COALESCE(tb_konsumen.nohp, tb_penjual.nohp) AS nohp FROM tb_user LEFT JOIN tb_konsumen ON tb_user.id_user = tb_konsumen.id_konsumen LEFT JOIN tb_penjual ON tb_user.id_user = tb_penjual.id_penjual WHERE tb_user.username LIKE '%$username%' AND (tb_user.role = 'konsumen' OR tb_user.role = 'kantin')";
    $checkquery = mysqli_query($conn, $query);

    if (mysqli_num_rows($checkquery) > 0) {
        $query = "SELECT tb_user.id_user, tb_user.username, COALESCE(tb_konsumen.name, tb_penjual.name) AS name, COALESCE(tb_konsumen.nohp, tb_penjual.nohp) AS nohp FROM tb_user LEFT JOIN tb_konsumen ON tb_user.id_user = tb_konsumen.id_konsumen LEFT JOIN tb_penjual ON tb_user.id_user = tb_penjual.id_penjual WHERE tb_user.username LIKE '%$username%' AND (COALESCE(tb_konsumen.nohp, tb_penjual.nohp) LIKE '%$nohp%') AND (tb_user.role = 'konsumen' OR tb_user.role = 'kantin')";
        $checkquery = mysqli_query($conn, $query);
        if (mysqli_num_rows($checkquery) > 0) {
            $response["code"] = 1;
            $response["data"] = array();
            while ($retrieve = mysqli_fetch_assoc($checkquery)) {
                $response["data"][] = $retrieve;
            }
        } else {
            $response["code"] = 2;
        }
    } else {
        $response["code"] = 3;
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
