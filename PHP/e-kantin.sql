-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 12, 2024 at 07:29 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `e-kantin`
--

-- --------------------------------------------------------

--
-- Table structure for table `tb_checkout`
--

CREATE TABLE `tb_checkout` (
  `id_checkout` int(11) NOT NULL,
  `id_konsumen` int(11) NOT NULL,
  `id_makanan` int(11) NOT NULL,
  `jumlah` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_checkout`
--

INSERT INTO `tb_checkout` (`id_checkout`, `id_konsumen`, `id_makanan`, `jumlah`) VALUES
(94, 71, 21, 1);

-- --------------------------------------------------------

--
-- Table structure for table `tb_konsumen`
--

CREATE TABLE `tb_konsumen` (
  `id_konsumen` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `nohp` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_konsumen`
--

INSERT INTO `tb_konsumen` (`id_konsumen`, `name`, `nohp`) VALUES
(71, 'annisa yola', '88888'),
(72, 'tri setiawan', '81282833415'),
(74, 'zulfahmi', '082170710632'),
(76, 'renita', '82170710632'),
(77, 'hafiz', '84545'),
(78, 'intan', '88454'),
(79, 'tes123', '123123');

-- --------------------------------------------------------

--
-- Table structure for table `tb_menu`
--

CREATE TABLE `tb_menu` (
  `id_makanan` int(11) NOT NULL,
  `id_penjual` int(11) NOT NULL,
  `namemenu` varchar(255) NOT NULL,
  `kategori` varchar(255) NOT NULL,
  `harga` int(11) NOT NULL,
  `gambar` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_menu`
--

INSERT INTO `tb_menu` (`id_makanan`, `id_penjual`, `namemenu`, `kategori`, `harga`, `gambar`) VALUES
(21, 64, 'mie rebus', 'Makanan', 10000, '1000016139_6056.jpg'),
(22, 64, 'jamur crispy ', 'Makanan', 5000, '1000016140_1572.jpg'),
(23, 64, 'pop ice', 'Minuman', 5000, '1000016143_5365.jpg'),
(24, 64, 'kentang goreng', 'Makanan', 7000, '1000016141_9287.jpg'),
(25, 64, 'teh es', 'Minuman', 3000, '1000028213_5358.jpg'),
(26, 65, 'teh es', 'Minuman', 3000, '1000016144_1789.jpg'),
(27, 65, 'nasi uduk', 'Makanan', 10000, '1000016137_2698.jpg'),
(28, 64, 'mi goreng', 'Makanan', 12000, '1000016138_8962.jpg'),
(29, 64, 'Batagor', 'Makanan', 15000, 'images (1)_4709.jpeg'),
(30, 73, 'nasi gorenng', 'Makanan', 10, '614dc6865eb24_6604.jpg'),
(31, 65, 'martabak mie', 'Makanan', 10000, '1000028202_9590.jpg'),
(32, 65, 'jus jeruk', 'Minuman', 12000, '1000028201_6937.jpg'),
(33, 65, 'fanta susu', 'Minuman', 5000, '1000028199_9753.jpg'),
(34, 64, 'lemon tea', 'Minuman', 6000, '1000028214_8032.jpg'),
(35, 65, 'jus pinang', 'Minuman', 8000, '1000028216_1222.jpg'),
(36, 65, 'jus alpukat ', 'Minuman', 10000, '1000028215_6209.jpg'),
(37, 75, 'sate', 'Makanan', 15000, '1000028219_2375.jpg'),
(38, 75, 'jus alpukat', 'Minuman', 10000, '1000028215_6066.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `tb_penjual`
--

CREATE TABLE `tb_penjual` (
  `id_penjual` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `nohp` varchar(255) NOT NULL,
  `saldo` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_penjual`
--

INSERT INTO `tb_penjual` (`id_penjual`, `name`, `nohp`, `saldo`) VALUES
(64, 'kantin1', '11111', 5000),
(65, 'kantin2', '22222', 20000);

-- --------------------------------------------------------

--
-- Table structure for table `tb_pesanan`
--

CREATE TABLE `tb_pesanan` (
  `id_pesanan` int(11) NOT NULL,
  `id_penjual` int(11) NOT NULL,
  `id_konsumen` int(11) NOT NULL,
  `id_makanan` int(11) NOT NULL,
  `pesanan` varchar(255) NOT NULL,
  `jumlah` int(11) NOT NULL,
  `total_harga` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `histori` int(11) NOT NULL,
  `keterangan` text NOT NULL,
  `tanggal` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_pesanan`
--

INSERT INTO `tb_pesanan` (`id_pesanan`, `id_penjual`, `id_konsumen`, `id_makanan`, `pesanan`, `jumlah`, `total_harga`, `status`, `histori`, `keterangan`, `tanggal`) VALUES
(67, 64, 71, 29, 'Batagor', 2, '30000.0', 'Selesai', 0, 'koto', '2024-02-19'),
(68, 64, 71, 28, 'mi goreng', 1, '12000.0', 'Selesai', 0, 'jati', '2024-02-19'),
(70, 64, 74, 28, 'mi goreng', 2, '24000.0', 'Selesai', 0, 'C32', '2024-02-19'),
(71, 64, 74, 25, 'teh es', 3, '9000.0', 'Selesai', 0, 'C32', '2024-02-19'),
(73, 64, 71, 34, 'lemon tea', 1, '6000.0', 'Selesai', 0, 'labor', '2024-02-17'),
(75, 64, 72, 23, 'pop ice', 1, '5000.0', 'Selesai', 0, 'c21', '2024-02-18');

-- --------------------------------------------------------

--
-- Table structure for table `tb_user`
--

CREATE TABLE `tb_user` (
  `id_user` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  `gambar` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_user`
--

INSERT INTO `tb_user` (`id_user`, `username`, `password`, `role`, `gambar`) VALUES
(1, 'admin', 'anisa123', 'admin', '1000015676_2358.webp'),
(64, 'kantin1', 'anisa123', 'kantin', '1000017564_2587.jpg'),
(65, 'kantin2', 'anisa123', 'kantin', '1000027777_7818.jpg'),
(71, 'annisa', 'anisa123', 'konsumen', NULL),
(72, 'wawan', 'anisa123', 'konsumen', NULL),
(74, 'zulfahmi', 'anisa123', 'konsumen', NULL),
(76, 'renita', 'anisa123', 'konsumen', NULL),
(77, 'hafiz12', 'anisa123', 'konsumen', NULL),
(78, 'intan', 'anisa123', 'konsumen', NULL),
(79, 'tes123', 'tes123456', 'konsumen', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tb_checkout`
--
ALTER TABLE `tb_checkout`
  ADD PRIMARY KEY (`id_checkout`);

--
-- Indexes for table `tb_konsumen`
--
ALTER TABLE `tb_konsumen`
  ADD PRIMARY KEY (`id_konsumen`);

--
-- Indexes for table `tb_menu`
--
ALTER TABLE `tb_menu`
  ADD PRIMARY KEY (`id_makanan`);

--
-- Indexes for table `tb_penjual`
--
ALTER TABLE `tb_penjual`
  ADD PRIMARY KEY (`id_penjual`);

--
-- Indexes for table `tb_pesanan`
--
ALTER TABLE `tb_pesanan`
  ADD PRIMARY KEY (`id_pesanan`);

--
-- Indexes for table `tb_user`
--
ALTER TABLE `tb_user`
  ADD PRIMARY KEY (`id_user`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tb_checkout`
--
ALTER TABLE `tb_checkout`
  MODIFY `id_checkout` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=95;

--
-- AUTO_INCREMENT for table `tb_konsumen`
--
ALTER TABLE `tb_konsumen`
  MODIFY `id_konsumen` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=80;

--
-- AUTO_INCREMENT for table `tb_menu`
--
ALTER TABLE `tb_menu`
  MODIFY `id_makanan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT for table `tb_penjual`
--
ALTER TABLE `tb_penjual`
  MODIFY `id_penjual` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=76;

--
-- AUTO_INCREMENT for table `tb_pesanan`
--
ALTER TABLE `tb_pesanan`
  MODIFY `id_pesanan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=76;

--
-- AUTO_INCREMENT for table `tb_user`
--
ALTER TABLE `tb_user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=80;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
