package com.laundry.smartlaundry.app.controllers.order;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.laundry.smartlaundry.app.dto.order.BookingRequest;
import com.laundry.smartlaundry.app.models.User;
import com.laundry.smartlaundry.app.repositories.LayananRepository;
import com.laundry.smartlaundry.app.repositories.UserRepository;
import com.laundry.smartlaundry.app.services.order.OrderService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/orders")
public class OrderController {

	private final OrderService orderService;
	private final LayananRepository layananRepository;
	private final UserRepository userRepository;

	public OrderController(OrderService orderService, LayananRepository layananRepository, UserRepository userRepository) {
		this.orderService = orderService;
		this.layananRepository = layananRepository;
		this.userRepository = userRepository;
	}

	@GetMapping("/new")
	public String newOrder(Model model) {
		if (!model.containsAttribute("bookingRequest")) {
			model.addAttribute("bookingRequest", new BookingRequest());
		}
		model.addAttribute("layanans", layananRepository.findByActiveTrue());
		return "order/booking";
	}

	@PostMapping
	public String createOrder(@Valid @ModelAttribute("bookingRequest") BookingRequest request, 
			BindingResult bindingResult, 
			Authentication authentication, 
			RedirectAttributes redirectAttributes,
			Model model) {
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("layanans", layananRepository.findByActiveTrue());
			return "order/booking";
		}

		User staff = userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new IllegalStateException("User not found"));

		try {
			orderService.createBooking(request, staff);
			redirectAttributes.addFlashAttribute("successMessage", "Order berhasil dibuat!");
			return "redirect:/dashboard";
		} catch (Exception e) {
			model.addAttribute("layanans", layananRepository.findByActiveTrue());
			model.addAttribute("errorMessage", "Gagal membuat order: " + e.getMessage());
			return "order/booking";
		}
	}

	@GetMapping("/{id}/edit")
	public String editOrder(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
		com.laundry.smartlaundry.app.models.Transaksi transaksi = orderService.getOrder(id);
		
		if (!model.containsAttribute("orderUpdateRequest")) {
			com.laundry.smartlaundry.app.dto.order.OrderUpdateRequest request = new com.laundry.smartlaundry.app.dto.order.OrderUpdateRequest();
			request.setNamaCustomer(transaksi.getPelanggan().getNama());
			request.setNoHp(transaksi.getPelanggan().getNoTelp());
			request.setLayananId(transaksi.getLayanan().getId());
			request.setBeratKg(transaksi.getBerat());
			request.setMember(transaksi.getPelanggan().getMember());
			request.setOrderStatus(transaksi.getOrderStatus());
			request.setPaymentStatus(transaksi.getPaymentStatus());
			
			model.addAttribute("orderUpdateRequest", request);
		}
		
		model.addAttribute("layanans", layananRepository.findByActiveTrue());
		model.addAttribute("transaksiId", id);
		return "order/edit";
	}

	@PostMapping("/{id}")
	public String updateOrder(@org.springframework.web.bind.annotation.PathVariable Long id,
			@Valid @ModelAttribute("orderUpdateRequest") com.laundry.smartlaundry.app.dto.order.OrderUpdateRequest request, 
			BindingResult bindingResult, 
			RedirectAttributes redirectAttributes,
			Model model) {
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("layanans", layananRepository.findByActiveTrue());
			model.addAttribute("transaksiId", id);
			return "order/edit";
		}

		try {
			orderService.updateOrder(id, request);
			redirectAttributes.addFlashAttribute("successMessage", "Order berhasil diperbarui!");
			return "redirect:/dashboard";
		} catch (Exception e) {
			model.addAttribute("layanans", layananRepository.findByActiveTrue());
			model.addAttribute("transaksiId", id);
			model.addAttribute("errorMessage", "Gagal memperbarui order: " + e.getMessage());
			return "order/edit";
		}
	}

	@PostMapping("/{id}/delete")
	public String deleteOrder(@org.springframework.web.bind.annotation.PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			orderService.deleteOrder(id);
			redirectAttributes.addFlashAttribute("successMessage", "Order berhasil dihapus secara permanen!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Gagal menghapus order: " + e.getMessage());
		}
		return "redirect:/dashboard";
	}
}
