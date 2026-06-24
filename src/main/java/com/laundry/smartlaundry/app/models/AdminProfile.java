package com.laundry.smartlaundry.app.models;

import java.time.LocalDateTime;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "admin_profiles")
public class AdminProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "nama", nullable = false, length = 150)
	private String nama;

	@Column(name = "kode_otoritas", nullable = false, length = 50)
	private String kodeOtoritas;

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	// --- PBO Methods ---
	public void kelolaStokBahan(Inventaris barang, String aksi, int jumlah) {
		if (aksi.equalsIgnoreCase("TAMBAH")) {
			barang.tambahStok(jumlah);
		} else if (aksi.equalsIgnoreCase("KURANG")) {
			barang.kurangiStok(jumlah);
		}
	}

	public void generateLaporanHarian(Laporan laporan, List<Transaksi> transaksiHarian) {
		laporan.generateLaporanHarian(transaksiHarian);
	}

	public void eksporLaporan(Laporan laporan) {
		laporan.eksporKePDF();
	}
}
