<?php
include_once '../dbconnect.php';

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
            <p>Semua Laporan Data Pembeli</p>
        </div>
        <div class="table-responsive">
            <table class="table table-bordered">
                <thead class="thead-light">
                    <tr>
                        <th scope="col">No</th>
                        <th scope="col">Nama</th>
                        <th scope="col">NOHP</th>
                        <th scope="col">Username</th>
                        <th scope="col">Role</th>
                    </tr>
                </thead>
                <tbody>
                    <?php 
                        $querykeluhan = $conn->prepare("SELECT tb_user.username, tb_user.role, tb_konsumen.name AS name, tb_konsumen.nohp AS nohp FROM tb_user LEFT JOIN tb_konsumen ON tb_user.id_user = tb_konsumen.id_konsumen WHERE tb_user.role='konsumen'");
                        $querykeluhan->execute();
                        $result = $querykeluhan->get_result();
                        while ($row = $result->fetch_assoc()) {
                    ?>
                    <tr>
                        <th scope="row"><?php echo $no++ ?></th>
                        <td><?php echo isset($row['name']) ? $row['name'] : 'Belum Ada Data'; ?></td>
                        <td><?php echo isset($row['nohp']) ? $row['nohp'] : 'Belum Ada Data'; ?></td>
                        <td><?php echo isset($row['username']) ? $row['username'] : 'Belum Ada Data'; ?></td>
                        <td><?php echo isset($row['role']) ? $row['role'] : 'Belum Ada Data'; ?></td>
                        </td>
                    </tr>
                    <?php 
                        }
                        // Tutup statement setelah digunakan
                        $querykeluhan->close();
                    ?>
                </tbody>
                <tfoot>
                    <?php
                        $querytKonsumen = $conn->prepare("SELECT COUNT(*) AS JML FROM `tb_user` WHERE role='konsumen'");
                        $querytKonsumen->execute();
                        $resultKonsumen = $querytKonsumen->get_result();
                        $rowKonsumen = $resultKonsumen->fetch_assoc();
                        $Konsumen = $rowKonsumen['JML'];
                    ?>
                    <td colspan="10">Total Pembeli : <?php echo ($Konsumen); ?><br>
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
                <p>PIMPINAN</p>
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