package com.laundry.smartlaundry.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundry.smartlaundry.app.models.Pelanggan;

public interface PelangganRepository extends JpaRepository<Pelanggan, Long> {

	Optional<Pelanggan> findByNoTelp(String noTelp);

	long countByMemberTrue();
	
	java.util.List<Pelanggan> findByMemberTrue();

	java.util.List<Pelanggan> findByNamaContainingIgnoreCaseOrNoTelpContainingIgnoreCaseOrderByIdDesc(String nama, String noTelp);
}
