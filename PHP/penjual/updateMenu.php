<?php
include_once '../dbconnect.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_makanan = $_POST['id_makanan'];
    $name = $_POST['namemenu'];
    $kategori = $_POST['kategori'];
    $harga = $_POST['harga'];

    // Cek apakah ada file gambar yang diupload
    if (!empty($_FILES['gambar']['name'])) {
        $gambar = $_FILES['gambar']['name'];

        $targetDir = "../gambarMenu/";
        $fileMenu = basename($gambar);

        if (move_uploaded_file($_FILES["gambar"]["tmp_name"], $targetDir.$fileMenu)) {
            if (!empty($_POST['gambar'])) {
                $gambar_menu_lama = $_POST['gambar'];
                unlink($targetDir.$gambar_menu_lama);
            }

            $query = "UPDATE tb_menu SET namemenu='$name',kategori='$kategori',harga='$harga', gambar='$gambar' WHERE id_makanan='$id_makanan'";
            $execute = mysqli_query($conn, $query);
            $check = mysqli_affected_rows($conn);

            if ($check > 0) {
                $response["code"] = 1;
                $response["message"] = "Update Gambar berhasil";
            } else {
                $response["code"] = 0;
                $response["message"] = "Update Gambar gagal";
            }
        } else {
            $response["code"] = 0;
            $response["message"] = "Gagal upload gambar";
        }
    } else {
        // Jika gambar tidak diedit, hanya update data teks
        $query = "UPDATE tb_menu SET namemenu='$name',kategori='$kategori',harga='$harga' WHERE id_makanan='$id_makanan'";
        $execute = mysqli_query($conn, $query);
        $check = mysqli_affected_rows($conn);

        if ($check > 0) {
            $response["code"] = 1;
            $response["message"] = "Update Gambar berhasil";
        } else {
            $response["code"] = 0;
            $response["message"] = "Update Gambar gagal";
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