package com.laundry.smartlaundry.app.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

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
@Table(name = "laporan")
public class Laporan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Transient
	private String idLaporan;

	@Column(name = "periode", nullable = false, length = 30)
	private String periode;

	@Column(name = "tanggal_mulai", nullable = false)
	private LocalDate tanggalMulai;

	@Column(name = "tanggal_selesai", nullable = false)
	private LocalDate tanggalSelesai;

	@Column(name = "total_pendapatan", nullable = false, precision = 12, scale = 2)
	private BigDecimal totalPendapatan = BigDecimal.ZERO;

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	// --- PBO Methods ---
	public void generateLaporanHarian(List<Transaksi> daftarTransaksi) {
		double pendapatanSatuHari = 0.0;
		for (Transaksi transaksi : daftarTransaksi) {
			if (transaksi.getPaymentStatus() == com.laundry.smartlaundry.app.enums.PaymentStatus.LUNAS) {
				pendapatanSatuHari += transaksi.getTotalBayar().doubleValue();
			}
		}
		this.totalPendapatan = BigDecimal.valueOf(pendapatanSatuHari);
		System.out.println("Laporan periode " + this.periode + " berhasil di-generate. Total Pendapatan: Rp" + this.totalPendapatan);
	}

	public void eksporKePDF() {
		System.out.println("Mengekspor laporan " + (this.idLaporan != null ? this.idLaporan : this.id) + " ke dalam format PDF...");
		// Logika library pembuat PDF (seperti OpenPDF) dapat disematkan di sini
	}
}
