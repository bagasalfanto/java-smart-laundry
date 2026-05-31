package com.laundry.smartlaundry.app.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundry.smartlaundry.app.models.Laporan;

public interface LaporanRepository extends JpaRepository<Laporan, Long> {

	List<Laporan> findByTanggalMulaiGreaterThanEqualAndTanggalSelesaiLessThanEqual(
			LocalDate tanggalMulai,
			LocalDate tanggalSelesai);
}
