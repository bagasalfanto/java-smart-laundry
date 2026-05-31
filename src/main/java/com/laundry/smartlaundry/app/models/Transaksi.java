package com.laundry.smartlaundry.app.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.laundry.smartlaundry.app.enums.OrderStatus;
import com.laundry.smartlaundry.app.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "transaksi")
public class Transaksi {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "invoice_number", nullable = false, unique = true, length = 50)
	private String invoiceNumber;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pelanggan_id", nullable = false)
	private Pelanggan pelanggan;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "layanan_id", nullable = false)
	private Layanan layanan;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "staff_id", nullable = false)
	private User staff;

	@Column(name = "berat", nullable = false, precision = 8, scale = 2)
	private BigDecimal berat;

	@Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
	private BigDecimal subtotal;

	@Column(name = "diskon", nullable = false, precision = 12, scale = 2)
	private BigDecimal diskon = BigDecimal.ZERO;

	@Column(name = "total_bayar", nullable = false, precision = 12, scale = 2)
	private BigDecimal totalBayar;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_status", nullable = false, length = 20)
	private OrderStatus orderStatus = OrderStatus.ANTRIAN;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false, length = 20)
	private PaymentStatus paymentStatus = PaymentStatus.BELUM_LUNAS;

	@Column(name = "tanggal_masuk", nullable = false, insertable = false, updatable = false)
	private LocalDateTime tanggalMasuk;

	@Column(name = "tanggal_selesai")
	private LocalDateTime tanggalSelesai;

	@Column(name = "paid_at")
	private LocalDateTime paidAt;

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime updatedAt;
}
