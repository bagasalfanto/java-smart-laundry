package com.laundry.smartlaundry.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundry.smartlaundry.app.models.Inventaris;

public interface InventarisRepository extends JpaRepository<Inventaris, Long> {

	Optional<Inventaris> findByNamaBarang(String namaBarang);
}
