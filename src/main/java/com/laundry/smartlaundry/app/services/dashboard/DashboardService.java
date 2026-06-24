package com.laundry.smartlaundry.app.services.dashboard;

import org.springframework.stereotype.Service;

import com.laundry.smartlaundry.app.dto.dashboard.DashboardSummary;
import com.laundry.smartlaundry.app.enums.OrderStatus;
import com.laundry.smartlaundry.app.enums.PaymentStatus;
import com.laundry.smartlaundry.app.enums.Role;
import com.laundry.smartlaundry.app.repositories.InventarisRepository;
import com.laundry.smartlaundry.app.repositories.LayananRepository;
import com.laundry.smartlaundry.app.repositories.PelangganRepository;
import com.laundry.smartlaundry.app.repositories.TransaksiRepository;
import com.laundry.smartlaundry.app.repositories.UserRepository;

@Service
public class DashboardService {

	private final UserRepository userRepository;
	private final PelangganRepository pelangganRepository;
	private final LayananRepository layananRepository;
	private final InventarisRepository inventarisRepository;
	private final TransaksiRepository transaksiRepository;

	public DashboardService(
			UserRepository userRepository,
			PelangganRepository pelangganRepository,
			LayananRepository layananRepository,
			InventarisRepository inventarisRepository,
			TransaksiRepository transaksiRepository) {
		this.userRepository = userRepository;
		this.pelangganRepository = pelangganRepository;
		this.layananRepository = layananRepository;
		this.inventarisRepository = inventarisRepository;
		this.transaksiRepository = transaksiRepository;
	}

	public DashboardSummary summary() {
		return new DashboardSummary(
				userRepository.countByRole(Role.STAFF),
				userRepository.countByRoleAndActiveTrue(Role.STAFF),
				pelangganRepository.count(),
				pelangganRepository.countByMemberTrue(),
				layananRepository.countByActiveTrue(),
				inventarisRepository.count(),
				transaksiRepository.count(),
				transaksiRepository.countByOrderStatus(OrderStatus.ANTRIAN),
				transaksiRepository.countByOrderStatus(OrderStatus.PROSES_CUCI) + transaksiRepository.countByOrderStatus(OrderStatus.PROSES_SETRIKA),
				transaksiRepository.countByOrderStatus(OrderStatus.SELESAI),
				transaksiRepository.countByPaymentStatus(PaymentStatus.LUNAS));
	}
}
