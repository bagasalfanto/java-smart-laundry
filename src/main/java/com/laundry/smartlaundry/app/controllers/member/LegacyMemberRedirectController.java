package com.laundry.smartlaundry.app.controllers.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LegacyMemberRedirectController {

	@GetMapping("/admin/members")
	public String index() {
		return "redirect:/members";
	}

	@GetMapping("/admin/members/create")
	public String create() {
		return "redirect:/members/create";
	}

	@GetMapping("/admin/members/{id}/edit")
	public String edit(@PathVariable Long id) {
		return "redirect:/members/" + id + "/edit";
	}
}
