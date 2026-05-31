package com.laundry.smartlaundry.app.controllers.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

	@GetMapping("/")
	public String root() {
		return "redirect:/dashboard";
	}

	@GetMapping("/login")
	public String login(Authentication authentication) {
		if (authentication != null
				&& authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken)) {
			return "redirect:/dashboard";
		}

		return "auth/login";
	}
}
