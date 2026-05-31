package com.laundry.smartlaundry.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundry.smartlaundry.app.models.Layanan;

public interface LayananRepository extends JpaRepository<Layanan, Long> {

	Optional<Layanan> findByNamaPaket(String namaPaket);

	List<Layanan> findByActiveTrue();

	long countByActiveTrue();
}
