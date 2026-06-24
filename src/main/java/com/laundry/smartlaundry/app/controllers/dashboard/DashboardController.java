package com.laundry.smartlaundry.app.controllers.dashboard;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.laundry.smartlaundry.app.repositories.TransaksiRepository;
import com.laundry.smartlaundry.app.services.dashboard.DashboardService;

@Controller
public class DashboardController {

	private final DashboardService dashboardService;
	private final TransaksiRepository transaksiRepository;

	public DashboardController(DashboardService dashboardService, TransaksiRepository transaksiRepository) {
		this.dashboardService = dashboardService;
		this.transaksiRepository = transaksiRepository;
	}

	@GetMapping("/dashboard")
	public String dashboard(Authentication authentication, Model model) {
		boolean admin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		model.addAttribute("username", authentication.getName());
		model.addAttribute("role", admin ? "ADMIN" : "STAFF");
		model.addAttribute("summary", dashboardService.summary());

		return "dashboard/index";
	}
}
