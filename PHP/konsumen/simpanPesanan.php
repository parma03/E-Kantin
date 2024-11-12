<?php
include_once '../dbconnect.php';

$response=array();

if($_SERVER['REQUEST_METHOD']=='POST'){
    $id_konsumen = $_POST['id_konsumen'];
    $id_menu = $_POST['id_menu'];
    $pesanan = $_POST['pesanan'];
    $jumlah = $_POST['jumlah'];
    $total_harga = $_POST['totalHargaPerItem'];
    $status = $_POST['status'];
    $keterangan = $_POST['keterangan'];
    $timezone = new DateTimeZone('Asia/Jakarta');
    $dateTime = new DateTime('now', $timezone);
    $currentDate = $dateTime->format('Y-m-d');
    $tanggal = $currentDate;

    $sql_menu = "SELECT * FROM tb_menu WHERE id_makanan = '$id_menu'";
    $result_menu = $conn->query($sql_menu);

    if ($result_menu->num_rows > 0) {
        $row_menu = $result_menu->fetch_assoc();
        $id_penjual = $row_menu['id_penjual'];
        $saldoawal = $row_menu['saldo'];

        $sql_pesanan = "INSERT INTO tb_pesanan (id_penjual, id_konsumen, id_makanan, pesanan, jumlah, total_harga, status, histori, keterangan, tanggal) 
                        VALUES ('$id_penjual', '$id_konsumen', '$id_menu', '$pesanan', '$jumlah', '$total_harga', '$status', '0', '$keterangan', '$tanggal')";

        if ($conn->query($sql_pesanan) === TRUE) {
            $response['code'] = 1;
            $response['message'] = "Data pesanan berhasil disimpan.";
            $saldoakhir = $saldoawal + $total_harga;
            $query = "DELETE tb_checkout FROM tb_checkout WHERE id_konsumen='$id_konsumen';";
            $query .= "UPDATE tb_penjual SET saldo='$saldoakhir' WHERE id_penjual='$id_penjual'";
            $execute = mysqli_multi_query($conn, $query);
        } else {
            $response['code'] = 0;
            $response['message'] = "Error: " . $sql_pesanan . "<br>" . $conn->error;
        }
    } else {
        $response['code'] = 0;
        $response['message'] = "ID Penjual tidak ditemukan untuk menu: $pesanan";
    }
} else {
    $response["code"] = 0;
    $response["message"] = "tambah gagal";
}
echo json_encode($response);
mysqli_close($conn);