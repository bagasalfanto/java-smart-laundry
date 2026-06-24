package com.laundry.smartlaundry.app.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inventaris")
public class Inventaris {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Transient
	private String idBarang;

	@Column(name = "nama_barang", nullable = false, unique = true, length = 100)
	private String namaBarang;

	@Column(name = "stok", nullable = false)
	private Integer stok = 0;

	@Column(name = "satuan", nullable = false, length = 30)
	private String satuan = "pcs";

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	// --- PBO Methods ---
	public void kurangiStok(int jumlah) {
		if (jumlah > 0 && this.stok >= jumlah) {
			this.stok -= jumlah;
			System.out.println("Stok " + this.namaBarang + " berhasil dikurangi. Sisa stok: " + this.stok);
		} else {
			System.out.println("Peringatan: Stok " + this.namaBarang + " tidak mencukupi untuk dikurangi!");
		}
	}

	public void tambahStok(int jumlah) {
		if (jumlah > 0) {
			this.stok += jumlah;
			System.out.println("Stok " + this.namaBarang + " berhasil ditambah. Total stok: " + this.stok);
		} else {
			System.out.println("Gagal: Jumlah tambahan stok harus lebih dari 0.");
		}
	}
}
