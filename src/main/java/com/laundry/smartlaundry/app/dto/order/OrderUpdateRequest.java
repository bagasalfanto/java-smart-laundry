package com.laundry.smartlaundry.app.dto.order;

import java.math.BigDecimal;

import com.laundry.smartlaundry.app.enums.OrderStatus;
import com.laundry.smartlaundry.app.enums.PaymentStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderUpdateRequest {

	@NotBlank(message = "Nama customer tidak boleh kosong")
	private String namaCustomer;

	@NotBlank(message = "Nomor HP tidak boleh kosong")
	private String noHp;

	@NotNull(message = "Jenis layanan harus dipilih")
	private Long layananId;

	@NotNull(message = "Berat tidak boleh kosong")
	@DecimalMin(value = "0.1", message = "Berat minimal 0.1 kg")
	private BigDecimal beratKg;

	private boolean isMember;

	@NotNull(message = "Status pesanan harus dipilih")
	private OrderStatus orderStatus;

	@NotNull(message = "Status pembayaran harus dipilih")
	private PaymentStatus paymentStatus;
}
