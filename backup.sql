-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versi server:                 8.4.3 - MySQL Community Server - GPL
-- OS Server:                    Win64
-- HeidiSQL Versi:               12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Membuang struktur basisdata untuk spkhop
CREATE DATABASE IF NOT EXISTS `spkhop` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `spkhop`;

-- membuang struktur untuk table spkhop.alternatif
CREATE TABLE IF NOT EXISTS `alternatif` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nik` varchar(50) DEFAULT NULL,
  `nama` varchar(50) DEFAULT NULL,
  `jabatan` varchar(50) DEFAULT NULL,
  `alamat` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Membuang data untuk tabel spkhop.alternatif: ~5 rows (lebih kurang)
INSERT INTO `alternatif` (`id`, `nik`, `nama`, `jabatan`, `alamat`) VALUES
	(1, '1', 'GUNAWAN', 'Manager Teknik', 'Jakarta Timur'),
	(2, '2', 'Yahya Ayyash', 'Tenaga Ahli', 'Jakarta Timur'),
	(3, '3', 'IRAWATI', 'Manager Keuangan', 'Jakarta'),
	(4, '4', 'Coba', 'Tenaga Ahli', 'sdafda'),
	(5, '5', 'Coba lagi', 'Tenaga Ahli', 'dsafdaf');

-- membuang struktur untuk table spkhop.hasil_alternatif
CREATE TABLE IF NOT EXISTS `hasil_alternatif` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_alternatif` int NOT NULL,
  `skor` double NOT NULL,
  `peringkat` int NOT NULL,
  `rekomendasi` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_alternatif` (`id_alternatif`),
  CONSTRAINT `hasil_alternatif_ibfk_1` FOREIGN KEY (`id_alternatif`) REFERENCES `alternatif` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Membuang data untuk tabel spkhop.hasil_alternatif: ~5 rows (lebih kurang)
INSERT INTO `hasil_alternatif` (`id`, `id_alternatif`, `skor`, `peringkat`, `rekomendasi`) VALUES
	(6, 5, 0.2302484697102328, 1, 'Direkomendasikan'),
	(7, 2, 0.21204711207389382, 2, 'Direkomendasikan'),
	(8, 1, 0.1990688878974564, 3, 'Direkomendasikan'),
	(9, 3, 0.18192960359017224, 4, '-'),
	(10, 4, 0.17670592672824484, 5, '-');

-- membuang struktur untuk table spkhop.kriteria
CREATE TABLE IF NOT EXISTS `kriteria` (
  `id` int NOT NULL AUTO_INCREMENT,
  `kode` varchar(50) DEFAULT NULL,
  `nama` varchar(50) DEFAULT NULL,
  `keterangan` text,
  `bobot` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Membuang data untuk tabel spkhop.kriteria: ~4 rows (lebih kurang)
INSERT INTO `kriteria` (`id`, `kode`, `nama`, `keterangan`, `bobot`) VALUES
	(1, 'K1', 'Kompetensi', '...', 0.5578933225526516),
	(2, 'K2', 'Disiplin', 'Tentang disiplin Karyawan', 0.26334054922065064),
	(3, 'K3', 'Tanggung Jawab', 'Tanggung Jawb Karyawan', 0.12187083577649552),
	(4, 'K4', 'Kerja sama', 'Tentang Kerjasama Karyawan', 0.056895292450202285);

-- membuang struktur untuk table spkhop.matrix_alternatif
CREATE TABLE IF NOT EXISTS `matrix_alternatif` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_alternatif` int NOT NULL,
  `id_kriteria` int NOT NULL,
  `nilai` float NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_alternatif` (`id_alternatif`),
  KEY `id_kriteria` (`id_kriteria`),
  CONSTRAINT `matrix_alternatif_ibfk_1` FOREIGN KEY (`id_alternatif`) REFERENCES `alternatif` (`id`) ON DELETE CASCADE,
  CONSTRAINT `matrix_alternatif_ibfk_2` FOREIGN KEY (`id_kriteria`) REFERENCES `kriteria` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Membuang data untuk tabel spkhop.matrix_alternatif: ~20 rows (lebih kurang)
INSERT INTO `matrix_alternatif` (`id`, `id_alternatif`, `id_kriteria`, `nilai`) VALUES
	(1, 1, 1, 80),
	(2, 1, 2, 90),
	(3, 1, 3, 90),
	(4, 1, 4, 90),
	(5, 2, 1, 90),
	(6, 2, 2, 90),
	(7, 2, 3, 90),
	(8, 2, 4, 90),
	(9, 3, 1, 78),
	(10, 3, 2, 67),
	(11, 3, 3, 89),
	(12, 3, 4, 88),
	(13, 5, 1, 99),
	(14, 5, 2, 99),
	(15, 5, 3, 90),
	(16, 5, 4, 99),
	(17, 4, 1, 75),
	(18, 4, 2, 75),
	(19, 4, 3, 75),
	(20, 4, 4, 75);

-- membuang struktur untuk table spkhop.matrix_normalisasi
CREATE TABLE IF NOT EXISTS `matrix_normalisasi` (
  `id` int NOT NULL AUTO_INCREMENT,
  `kriteria_row` varchar(50) DEFAULT NULL,
  `kriteria_col` varchar(50) DEFAULT NULL,
  `nilai` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Membuang data untuk tabel spkhop.matrix_normalisasi: ~16 rows (lebih kurang)
INSERT INTO `matrix_normalisasi` (`id`, `kriteria_row`, `kriteria_col`, `nilai`) VALUES
	(17, 'Kompetensi', 'Kompetensi', 0.5965875193890944),
	(18, 'Kompetensi', 'Disiplin', 0.6617695718350869),
	(19, 'Kompetensi', 'Tanggung Jawab', 0.535716198986425),
	(20, 'Kompetensi', 'Kerja Sama', 0.4375),
	(21, 'Disiplin', 'Kompetensi', 0.19884262021238516),
	(22, 'Disiplin', 'Disiplin', 0.2205898572783623),
	(23, 'Disiplin', 'Tanggung Jawab', 0.321429719391855),
	(24, 'Disiplin', 'Kerja Sama', 0.3125),
	(25, 'Tanggung Jawab', 'Kompetensi', 0.11931750387781889),
	(26, 'Tanggung Jawab', 'Disiplin', 0.07352259943087816),
	(27, 'Tanggung Jawab', 'Tanggung Jawab', 0.107143239797285),
	(28, 'Tanggung Jawab', 'Kerja Sama', 0.1875),
	(29, 'Kerja Sama', 'Kompetensi', 0.0852523565207016),
	(30, 'Kerja Sama', 'Disiplin', 0.044117971455672464),
	(31, 'Kerja Sama', 'Tanggung Jawab', 0.03571084182443509),
	(32, 'Kerja Sama', 'Kerja Sama', 0.0625);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
