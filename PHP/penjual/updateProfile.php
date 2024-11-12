<?php
include_once '../dbconnect.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_user = $_POST['id_user'];
    $nama = $_POST['nama'];
    $nohp = $_POST['nohp'];
    $username = $_POST['username'];
    $password = $_POST['password'];
    $gambar = $_POST['gambar'];

    // Cek apakah ada file gambar yang diupload
    if (!empty($_FILES['gambar']['name'])) {
        $gambar = $_FILES['gambar']['name'];

        $targetDir = "../gambarProfile/";
        $fileMenu = basename($gambar);

        if (move_uploaded_file($_FILES["gambar"]["tmp_name"], $targetDir.$fileMenu)) {
            if (!empty($_POST['gambar'])) {
                $gambar_menu_lama = $_POST['gambar'];
                unlink($targetDir.$gambar_menu_lama);
            }

            $query = "UPDATE tb_user t1 JOIN tb_penjual t2 ON (t1.id_user = t2.id_penjual) SET t1.username='$username', t1.password='$password', t1.gambar='$gambar', t2.name = '$nama', t2.nohp = '$nohp' WHERE t1.id_user='$id_user'";
            $execute = mysqli_query($conn, $query);
            $check = mysqli_affected_rows($conn);

            if ($check > 0) {
                $response["code"] = 1;
                $response["message"] = "Update Gambar berhasil";
            } else {
                $response["code"] = 0;
                $response["message"] = "Update Gambar2 gagal";
            }
        } else {
            $response["code"] = 0;
            $response["message"] = "Gagal upload gambar";
        }
    } else {
        // Jika gambar tidak diedit, hanya update data teks
        $query = "UPDATE tb_user t1 JOIN tb_penjual t2 ON (t1.id_user = t2.id_penjual) SET t1.username='$username', t1.password='$password', t2.name = '$nama', t2.nohp = '$nohp' WHERE t1.id_user='$id_user'";
        $execute = mysqli_query($conn, $query);
        $check = mysqli_affected_rows($conn);

        if ($check > 0) {
            $response["code"] = 1;
            $response["message"] = "Update Gambar berhasil";
        } else {
            $response["code"] = 0;
            $response["message"] = "Update Gambar2 gagal";
        }
    }
} else {
    $response["code"] = 0;
    $response["message"] = "Tidak ada input Gambar";
}

ini_set("log_errors", 1);
ini_set("error_log", "file.log");

// Konversi array $response menjadi string
$log_message = print_r($response, true);

error_log($log_message);
echo json_encode($response);
?>