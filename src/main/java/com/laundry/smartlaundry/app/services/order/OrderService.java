package com.laundry.smartlaundry.app.services.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.smartlaundry.app.dto.order.BookingRequest;
import com.laundry.smartlaundry.app.models.Layanan;
import com.laundry.smartlaundry.app.models.Pelanggan;
import com.laundry.smartlaundry.app.models.Transaksi;
import com.laundry.smartlaundry.app.models.User;
import com.laundry.smartlaundry.app.repositories.LayananRepository;
import com.laundry.smartlaundry.app.repositories.PelangganRepository;
import com.laundry.smartlaundry.app.repositories.TransaksiRepository;

@Service
public class OrderService {

	private final TransaksiRepository transaksiRepository;
	private final PelangganRepository pelangganRepository;
	private final LayananRepository layananRepository;

	public OrderService(TransaksiRepository transaksiRepository, PelangganRepository pelangganRepository, LayananRepository layananRepository) {
		this.transaksiRepository = transaksiRepository;
		this.pelangganRepository = pelangganRepository;
		this.layananRepository = layananRepository;
	}

	@Transactional
	public Transaksi createBooking(BookingRequest request, User staff) {
		// 1. Handle Pelanggan
		Pelanggan pelanggan = pelangganRepository.findByNoTelp(request.getNoHp())
				.orElseGet(() -> {
					Pelanggan p = new Pelanggan();
					p.setNoTelp(request.getNoHp());
					p.setPoin(0);
					return p;
				});

		pelanggan.setNama(request.getNamaCustomer());
		pelanggan.setMember(request.isMember());
		pelanggan = pelangganRepository.save(pelanggan);

		// 2. Handle Layanan
		Layanan layanan = layananRepository.findById(request.getLayananId())
				.orElseThrow(() -> new IllegalArgumentException("Layanan tidak valid"));

		// 3. Calculation
		BigDecimal berat = request.getBeratKg();
		BigDecimal subtotal = berat.multiply(layanan.getHargaPerKg());
		BigDecimal diskon = BigDecimal.ZERO;
		
		if (Boolean.TRUE.equals(pelanggan.getMember())) {
			diskon = subtotal.multiply(new BigDecimal("0.10")); // 10% diskon
		}
		
		BigDecimal totalBayar = subtotal.subtract(diskon);

		// 4. Create Transaksi
		Transaksi transaksi = new Transaksi();
		transaksi.setInvoiceNumber("INV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
		transaksi.setPelanggan(pelanggan);
		transaksi.setLayanan(layanan);
		transaksi.setStaff(staff);
		transaksi.setBerat(berat);
		transaksi.setSubtotal(subtotal);
		transaksi.setDiskon(diskon);
		transaksi.setTotalBayar(totalBayar);
		
		return transaksiRepository.save(transaksi);
	}

	public Transaksi getOrder(Long id) {
		return transaksiRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Order tidak ditemukan"));
	}

	@Transactional
	public Transaksi updateOrder(Long id, com.laundry.smartlaundry.app.dto.order.OrderUpdateRequest request) {
		Transaksi transaksi = getOrder(id);

		// Update Pelanggan
		Pelanggan pelanggan = transaksi.getPelanggan();
		pelanggan.setNama(request.getNamaCustomer());
		pelanggan.setNoTelp(request.getNoHp());
		pelanggan.setMember(request.isMember());
		pelangganRepository.save(pelanggan);

		// Update Layanan
		Layanan layanan = layananRepository.findById(request.getLayananId())
				.orElseThrow(() -> new IllegalArgumentException("Layanan tidak valid"));

		// Recalculate
		BigDecimal berat = request.getBeratKg();
		BigDecimal subtotal = berat.multiply(layanan.getHargaPerKg());
		BigDecimal diskon = BigDecimal.ZERO;
		
		if (Boolean.TRUE.equals(pelanggan.getMember())) {
			diskon = subtotal.multiply(new BigDecimal("0.10"));
		}
		
		BigDecimal totalBayar = subtotal.subtract(diskon);

		transaksi.setLayanan(layanan);
		transaksi.setBerat(berat);
		transaksi.setSubtotal(subtotal);
		transaksi.setDiskon(diskon);
		transaksi.setTotalBayar(totalBayar);
		transaksi.setOrderStatus(request.getOrderStatus());
		transaksi.setPaymentStatus(request.getPaymentStatus());

		if (request.getPaymentStatus() == com.laundry.smartlaundry.app.enums.PaymentStatus.LUNAS && transaksi.getPaidAt() == null) {
			transaksi.setPaidAt(LocalDateTime.now());
		} else if (request.getPaymentStatus() == com.laundry.smartlaundry.app.enums.PaymentStatus.BELUM_LUNAS) {
			transaksi.setPaidAt(null);
		}

		return transaksiRepository.save(transaksi);
	}

	@Transactional
	public void deleteOrder(Long id) {
		Transaksi transaksi = getOrder(id);
		transaksiRepository.delete(transaksi);
	}

	@Transactional
	public void updateOrderStatus(Long id, com.laundry.smartlaundry.app.enums.OrderStatus status) {
		Transaksi transaksi = getOrder(id);
		transaksi.setOrderStatus(status);
		transaksiRepository.save(transaksi);
	}
}
