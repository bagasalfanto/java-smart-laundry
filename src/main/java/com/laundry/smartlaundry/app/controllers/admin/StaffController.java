package com.laundry.smartlaundry.app.controllers.admin;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.laundry.smartlaundry.app.dto.admin.StaffForm;
import com.laundry.smartlaundry.app.services.admin.StaffManagementService;

@Controller
@RequestMapping("/admin/staff")
public class StaffController {

	private final StaffManagementService staffManagementService;

	public StaffController(StaffManagementService staffManagementService) {
		this.staffManagementService = staffManagementService;
	}

	@GetMapping
	public String index(Model model) {
		model.addAttribute("staffList", staffManagementService.findAll());
		return "admin/staff/index";
	}

	@GetMapping("/create")
	public String create(Model model) {
		model.addAttribute("staffForm", staffManagementService.createForm());
		model.addAttribute("mode", "create");
		model.addAttribute("formAction", "/admin/staff");
		return "admin/staff/form";
	}

	@PostMapping
	public String store(
			@Valid @ModelAttribute("staffForm") StaffForm staffForm,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		validatePasswordRequired(staffForm, bindingResult);
		if (staffManagementService.usernameExistsForCreate(staffForm.getUsername())) {
			bindingResult.rejectValue("username", "duplicate", "Username sudah dipakai");
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("mode", "create");
			model.addAttribute("formAction", "/admin/staff");
			return "admin/staff/form";
		}

		staffManagementService.create(staffForm);
		redirectAttributes.addFlashAttribute("success", "Staff berhasil ditambahkan");
		return "redirect:/admin/staff";
	}

	@GetMapping("/{id}/edit")
	public String edit(@PathVariable Long id, Model model) {
		model.addAttribute("staffForm", staffManagementService.editForm(id));
		model.addAttribute("staffId", id);
		model.addAttribute("mode", "edit");
		model.addAttribute("formAction", "/admin/staff/" + id);
		return "admin/staff/form";
	}

	@PostMapping("/{id}")
	public String update(
			@PathVariable Long id,
			@Valid @ModelAttribute("staffForm") StaffForm staffForm,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (staffManagementService.usernameExistsForUpdate(id, staffForm.getUsername())) {
			bindingResult.rejectValue("username", "duplicate", "Username sudah dipakai");
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("staffId", id);
			model.addAttribute("mode", "edit");
			model.addAttribute("formAction", "/admin/staff/" + id);
			return "admin/staff/form";
		}

		staffManagementService.update(id, staffForm);
		redirectAttributes.addFlashAttribute("success", "Staff berhasil diperbarui");
		return "redirect:/admin/staff";
	}

	@PostMapping("/{id}/deactivate")
	public String deactivate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		staffManagementService.deactivate(id);
		redirectAttributes.addFlashAttribute("success", "Staff berhasil dinonaktifkan");
		return "redirect:/admin/staff";
	}

	private void validatePasswordRequired(StaffForm staffForm, BindingResult bindingResult) {
		if (staffForm.getPassword() == null || staffForm.getPassword().isBlank()) {
			bindingResult.rejectValue("password", "required", "Password wajib diisi");
		}
	}
}
