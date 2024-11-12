<?php
include_once '../dbconnect.php';

$id_penjual = $_GET['id_penjual'];
$tanggal_dari = $_GET['tanggal_dari'];
$tanggal_sampai = $_GET['tanggal_sampai'];

$no = 1;
?>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Laporan Data</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.1.3/dist/css/bootstrap.min.css"
        integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
    <style>
        .logo {
            text-align: center;
        }

        .logo img {
            max-width: 100%;
            height: auto;
            display: block;
            margin: 0 auto;
        }
    </style>
</head>

<body>
    <div class="container">
        <div class="logo">

            <h2>E-Kantin</h2>
            <p>Semua Laporan Data User</p>
        </div>
        <div class="table-responsive">
            <table class="table table-bordered">
                <thead class="thead-light">
                    <tr>
                        <th scope="col">No</th>
                        <th scope="col">Nama Pemesan</th>
                        <th scope="col">Pesanan</th>
                        <th scope="col">Jumlah</th>
                        <th scope="col">Status Pesanan</th>
                        <th scope="col">Tanggal Pesanan</th>
                        <th scope="col">Total</th>
                    </tr>
                </thead>
                <tbody>
                    <?php 
                        $querykeluhan = $conn->prepare("SELECT tb_pesanan.id_pesanan, tb_pesanan.tanggal, tb_pesanan.id_penjual, tb_pesanan.id_konsumen, tb_pesanan.pesanan, tb_pesanan.jumlah, tb_pesanan.total_harga, tb_pesanan.status, tb_pesanan.histori, tb_pesanan.keterangan, tb_konsumen.name, tb_konsumen.nohp, tb_menu.namemenu, tb_menu.kategori, tb_menu.harga, tb_menu.gambar FROM tb_pesanan JOIN tb_konsumen ON tb_pesanan.id_konsumen = tb_konsumen.id_konsumen JOIN tb_menu ON tb_pesanan.id_penjual = tb_menu.id_penjual WHERE (tb_pesanan.tanggal BETWEEN '$tanggal_dari' AND '$tanggal_sampai') AND (tb_pesanan.status='dibayar' OR tb_pesanan.status='Selesai') AND tb_pesanan.id_penjual='$id_penjual' GROUP BY tb_pesanan.id_pesanan;");
                        $querykeluhan->execute();
                        $result = $querykeluhan->get_result();
                        while ($row = $result->fetch_assoc()) {
                    ?>
                    <tr>
                        <th scope="row"><?php echo $no++ ?></th>
                        <td><?php echo isset($row['name']) ? $row['name'] : 'Belum Ada Data'; ?></td>
                        <td><?php echo isset($row['pesanan']) ? $row['pesanan'] : 'Belum Ada Data'; ?></td>
                        <td><?php echo isset($row['jumlah']) ? $row['jumlah'] : 'Belum Ada Data'; ?></td>
                        <td><?php echo isset($row['status']) ? $row['status'] : 'Belum Ada Data'; ?></td>
                        <td><?php echo isset($row['tanggal']) ? $row['tanggal'] : 'Belum Ada Data'; ?></td>
                        <td><?php echo isset($row['total_harga']) ? number_format($row['total_harga'], 0, ',', '.') : '0'; ?></td>
                    </tr>
                    <?php 
                        }
                        // Tutup statement setelah digunakan
                        $querykeluhan->close();
                    ?>
                </tbody>
                <tfoot>
                    <?php
                        $querytKonsumen = $conn->prepare("SELECT COUNT(*) AS JML FROM `tb_pesanan` WHERE status='dibayar' AND id_penjual='$id_penjual' AND tanggal BETWEEN '$tanggal_dari' AND '$tanggal_sampai'");
                        $querytKonsumen->execute();
                        $resultKonsumen = $querytKonsumen->get_result();
                        $rowKonsumen = $resultKonsumen->fetch_assoc();
                        $Konsumen = $rowKonsumen['JML'];
                        
                        $querytKantin = $conn->prepare("SELECT COUNT(*) AS JML FROM `tb_pesanan` WHERE status='Selesai' AND id_penjual='$id_penjual' AND tanggal BETWEEN '$tanggal_dari' AND '$tanggal_sampai'");
                        $querytKantin->execute();
                        $resultKantin = $querytKantin->get_result();
                        $rowKantin = $resultKantin->fetch_assoc();
                        $Kantin = $rowKantin['JML'];

                        $queryTotal = $conn->prepare("SELECT SUM(total_harga) AS JML FROM tb_pesanan WHERE tanggal BETWEEN '$tanggal_dari' AND '$tanggal_sampai' AND (status='dibayar' OR status='Selesai') AND id_penjual='$id_penjual'");
                        $queryTotal->execute();
                        $resultTotal = $queryTotal->get_result();
                        $rowTotal = $resultTotal->fetch_assoc();
                        $Total = $rowTotal['JML'];
                    ?>
                    <td colspan="10">Total Pesanan Dibayar : <?php echo ($Konsumen); ?><br>
                                     Total Pesenan Selesai : <?php echo ($Kantin); ?><br>
                                     Total Pendapatan      : <?php echo number_format($Total); ?><br>
                                    </td>
                </tfoot>
            </table>
        </div>

        <!-- Tanda tangan kiri -->
        <div class="row mt-4">
            <div class="col-12 col-md-6 text-center">
            </div>

            <!-- Tanda tangan kanan -->
            <div class="col-12 col-md-6 text-center">
            <p><?php echo date("Y-m-d") ?>, Diketahui</p>
                <p>OWNER</p>
                <br>
                <br>
                <br>
                <p>(......................................)</p>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
        integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous">
    </script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.3/dist/umd/popper.min.js"
        integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous">
    </script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.1.3/dist/js/bootstrap.min.js"
        integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous">
    </script>
</body>

</html>