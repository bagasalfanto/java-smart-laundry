package com.laundry.smartlaundry.app.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundry.smartlaundry.app.models.Transaksi;
import com.laundry.smartlaundry.app.enums.OrderStatus;
import com.laundry.smartlaundry.app.enums.PaymentStatus;

public interface TransaksiRepository extends JpaRepository<Transaksi, Long> {

	Optional<Transaksi> findByInvoiceNumber(String invoiceNumber);

	List<Transaksi> findByOrderStatus(OrderStatus orderStatus);

	List<Transaksi> findByPaymentStatus(PaymentStatus paymentStatus);

	List<Transaksi> findByPaymentStatusAndPaidAtBetween(
			PaymentStatus paymentStatus,
			LocalDateTime start,
			LocalDateTime end);

	boolean existsByPelangganId(Long pelangganId);

	boolean existsByStaffId(Long staffId);

	long countByOrderStatus(OrderStatus orderStatus);

	long countByPaymentStatus(PaymentStatus paymentStatus);

	List<Transaksi> findTop10ByOrderByCreatedAtDesc();

	List<Transaksi> findAllByOrderByCreatedAtDesc();
}
