package com.laundry.smartlaundry.app.controllers;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AppViewModelAdvice {

	@ModelAttribute("username")
	public String username(Authentication authentication) {
		if (isAuthenticated(authentication)) {
			return authentication.getName();
		}
		return "";
	}

	@ModelAttribute("role")
	public String role(Authentication authentication) {
		if (isAuthenticated(authentication)) {
			return authentication.getAuthorities().stream()
					.map(authority -> authority.getAuthority().replace("ROLE_", ""))
					.findFirst()
					.orElse("");
		}
		return "";
	}

	@ModelAttribute("currentPath")
	public String currentPath(HttpServletRequest request) {
		return request.getRequestURI();
	}

	private boolean isAuthenticated(Authentication authentication) {
		return authentication != null
				&& authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken);
	}
}
