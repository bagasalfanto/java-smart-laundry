package com.laundry.smartlaundry.app.controllers.workflow;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.laundry.smartlaundry.app.enums.OrderStatus;
import com.laundry.smartlaundry.app.repositories.TransaksiRepository;
import com.laundry.smartlaundry.app.services.order.OrderService;

@Controller
@RequestMapping("/workflow")
public class WorkflowController {

	private final TransaksiRepository transaksiRepository;
	private final OrderService orderService;

	public WorkflowController(TransaksiRepository transaksiRepository, OrderService orderService) {
		this.transaksiRepository = transaksiRepository;
		this.orderService = orderService;
	}

	@GetMapping
	public String index(Model model) {
		model.addAttribute("orders", transaksiRepository.findAllByOrderByCreatedAtDesc());
		return "workflow/index";
	}

	@PostMapping("/{id}/status")
	public String updateStatus(@PathVariable Long id, @RequestParam("status") OrderStatus status, RedirectAttributes redirectAttributes) {
		try {
			orderService.updateOrderStatus(id, status);
			redirectAttributes.addFlashAttribute("successMessage", "Status pesanan berhasil diperbarui!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Gagal memperbarui status: " + e.getMessage());
		}
		return "redirect:/workflow";
	}
}
