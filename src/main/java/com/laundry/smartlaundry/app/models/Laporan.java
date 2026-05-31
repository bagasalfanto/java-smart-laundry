package com.laundry.smartlaundry.app.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
}
