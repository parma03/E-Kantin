<?php
include_once '../dbconnect.php';
$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_makanan = $_POST['id_makanan'];

    // Get the photo file name for deletion
    $query = "SELECT gambar FROM tb_menu WHERE id_makanan = '$id_makanan'";
    $result = mysqli_query($conn, $query);
    if (mysqli_num_rows($result) > 0) {
        $row = mysqli_fetch_assoc($result);
        $photoFileName = $row['gambar'];

        // Delete the photo file from the server
        $filePath = "../gambarMenu/" . $photoFileName;
        if (file_exists($filePath)) {
            unlink($filePath);
        }
    }

    // Delete the entry from the database
    $query = "DELETE FROM tb_menu WHERE id_makanan = '$id_makanan'";
    $execute = mysqli_query($conn, $query);
    $check = mysqli_affected_rows($conn);

    if ($check > 0) {
        $response["code"] = 1;
        $response["message"] = "Menu berhasil dihapus";
    } else {
        $response["code"] = 0;
        $response["message"] = "Menu gagal dihapus";
    }
} else {
    $response["code"] = 0;
    $response["message"] = "Tidak ada Menu yang dihapus";
}

echo json_encode($response);
?>