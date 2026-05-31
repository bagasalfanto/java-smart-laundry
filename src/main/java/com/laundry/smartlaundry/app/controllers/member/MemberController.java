package com.laundry.smartlaundry.app.controllers.member;

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

import com.laundry.smartlaundry.app.dto.member.MemberForm;
import com.laundry.smartlaundry.app.services.member.MemberManagementService;

@Controller
@RequestMapping("/members")
public class MemberController {

	private final MemberManagementService memberManagementService;

	public MemberController(MemberManagementService memberManagementService) {
		this.memberManagementService = memberManagementService;
	}

	@GetMapping
	public String index(Model model) {
		model.addAttribute("members", memberManagementService.findAll());
		return "member/index";
	}

	@GetMapping("/create")
	public String create(Model model) {
		model.addAttribute("memberForm", memberManagementService.createForm());
		model.addAttribute("mode", "create");
		model.addAttribute("formAction", "/members");
		return "member/form";
	}

	@PostMapping
	public String store(
			@Valid @ModelAttribute("memberForm") MemberForm memberForm,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (memberManagementService.phoneExistsForCreate(memberForm.getNoTelp())) {
			bindingResult.rejectValue("noTelp", "duplicate", "Nomor telepon sudah dipakai");
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("mode", "create");
			model.addAttribute("formAction", "/members");
			return "member/form";
		}

		memberManagementService.create(memberForm);
		redirectAttributes.addFlashAttribute("success", "Member berhasil ditambahkan");
		return "redirect:/members";
	}

	@GetMapping("/{id}/edit")
	public String edit(@PathVariable Long id, Model model) {
		model.addAttribute("memberForm", memberManagementService.editForm(id));
		model.addAttribute("memberId", id);
		model.addAttribute("mode", "edit");
		model.addAttribute("formAction", "/members/" + id);
		return "member/form";
	}

	@PostMapping("/{id}")
	public String update(
			@PathVariable Long id,
			@Valid @ModelAttribute("memberForm") MemberForm memberForm,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (memberManagementService.phoneExistsForUpdate(id, memberForm.getNoTelp())) {
			bindingResult.rejectValue("noTelp", "duplicate", "Nomor telepon sudah dipakai");
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("memberId", id);
			model.addAttribute("mode", "edit");
			model.addAttribute("formAction", "/members/" + id);
			return "member/form";
		}

		memberManagementService.update(id, memberForm);
		redirectAttributes.addFlashAttribute("success", "Member berhasil diperbarui");
		return "redirect:/members";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			memberManagementService.delete(id);
			redirectAttributes.addFlashAttribute("success", "Member berhasil dihapus");
		} catch (IllegalStateException exception) {
			redirectAttributes.addFlashAttribute("error", exception.getMessage());
		}
		return "redirect:/members";
	}
}
