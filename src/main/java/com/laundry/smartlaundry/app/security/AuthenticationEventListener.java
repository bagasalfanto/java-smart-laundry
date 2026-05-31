package com.laundry.smartlaundry.app.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.laundry.smartlaundry.app.services.auth.LoginAttemptService;

@Component
public class AuthenticationEventListener {

	private final LoginAttemptService loginAttemptService;

	public AuthenticationEventListener(LoginAttemptService loginAttemptService) {
		this.loginAttemptService = loginAttemptService;
	}

	@EventListener
	public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
		loginAttemptService.recordFailure(event.getAuthentication().getName());
	}

	@EventListener
	public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
		loginAttemptService.recordSuccess(event.getAuthentication().getName());
	}
}
