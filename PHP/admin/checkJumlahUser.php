<?php
include_once '../dbconnect.php';

$role = $_GET['role'];

$query = "SELECT COUNT(*) AS JML FROM tb_user WHERE role = '$role'";
$execute = mysqli_query($conn, $query);
$check = mysqli_num_rows($execute);

if ($check > 0) {
    $response["code"] = 1;
    $response["message"] = "Data ditemukan";
    $response["data"] = array();

    while ($retrieve = mysqli_fetch_assoc($execute)) {
        $response["data"][] = $retrieve;
    }
} else {
    $response["code"] = 0;
    $response["message"] = "Data tidak ditemukan";
}

echo json_encode($response);
mysqli_close($conn);
?>

