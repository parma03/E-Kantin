<?php
include_once '../dbconnect.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_konsumen = $_POST['id_konsumen'];
    $name = $_POST['name'];
    $nohp = $_POST['nohp'];
    $username = $_POST['username'];
    $password = $_POST['password'];

    $query = "SELECT * FROM tb_user WHERE username='$username'";
    $checkquery = mysqli_query($conn, $query);
    
    if (mysqli_num_rows($checkquery) > 1) {
        // Jika ada lebih dari satu baris dengan username yang sama
        $response["code"] = 3;
    } elseif (mysqli_num_rows($checkquery) == 1) {
        // Jika hanya ada satu baris dengan username yang sama
        $row = mysqli_fetch_assoc($checkquery);
        if ($row['id_user'] != $id_konsumen) {
            // Jika id_user tidak sama dengan id_konsumen yang sedang diubah
            $response["code"] = 3;
        } else {
            // Jika id_user sama dengan id_konsumen yang sedang diubah
            $response["code"] = 1;
            $query = "UPDATE tb_user t1 JOIN tb_konsumen t2 ON (t1.id_user = t2.id_konsumen) SET t1.username='$username',t1.password='$password',t2.name='$name',t2.nohp='$nohp' WHERE t1.id_user='$id_konsumen'";
            $execute = mysqli_query($conn, $query);
            $check = mysqli_affected_rows($conn);
        }
    } else {
        // Jika username tidak ditemukan
        $response["code"] = 1;
        $query = "UPDATE tb_user t1 JOIN tb_konsumen t2 ON (t1.id_user = t2.id_konsumen) SET t1.username='$username',t1.password='$password',t2.name='$name',t2.nohp='$nohp' WHERE t1.id_user='$id_konsumen'";
        $execute = mysqli_query($conn, $query);
        $check = mysqli_affected_rows($conn);
    }
} else {
    $response["code"] = 0;
    $response["message"] = "Tidak ada input Konsumen";
}

echo json_encode($response);
mysqli_close($conn);
?>
