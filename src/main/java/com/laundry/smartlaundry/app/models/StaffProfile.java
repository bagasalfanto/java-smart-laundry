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
@Table(name = "staff_profiles")
public class StaffProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "nama", nullable = false, length = 150)
	private String nama;

	@Column(name = "jumlah_shift", nullable = false)
	private Integer jumlahShift = 0;

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	// --- PBO Methods ---
	public void inputOrder(Transaksi transaksiBaru, List<Inventaris> bahanBakuYangDipakai) {
		System.out.println("Memproses pesanan baru: " + transaksiBaru.getInvoiceNumber());
		for (Inventaris bahan : bahanBakuYangDipakai) {
			bahan.kurangiStok(1); // Asumsi 1 pesanan mengurangi 1 satuan bahan
		}
	}

	public void selesaikanPembayaran(Transaksi transaksi) {
		transaksi.setPaymentStatus(com.laundry.smartlaundry.app.enums.PaymentStatus.LUNAS);
		System.out.println("Pembayaran diterima oleh Staff.");
		transaksi.cetakStruk();
	}
}
