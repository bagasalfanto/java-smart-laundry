package com.laundry.smartlaundry.app.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.laundry.smartlaundry.app.services.admin.ActivityLogService;
import com.laundry.smartlaundry.app.services.auth.LoginAttemptService;

@Component
public class AuthenticationEventListener {

	private final LoginAttemptService loginAttemptService;
	private final ActivityLogService activityLogService;

	public AuthenticationEventListener(LoginAttemptService loginAttemptService, ActivityLogService activityLogService) {
		this.loginAttemptService = loginAttemptService;
		this.activityLogService = activityLogService;
	}

	@EventListener
	public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
		loginAttemptService.recordFailure(event.getAuthentication().getName());
	}

	@EventListener
	public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
		String username = event.getAuthentication().getName();
		loginAttemptService.recordSuccess(username);

		String ipAddress = null;
		Object details = event.getAuthentication().getDetails();
		if (details instanceof WebAuthenticationDetails) {
			ipAddress = ((WebAuthenticationDetails) details).getRemoteAddress();
		}

		activityLogService.logLoginSuccess(username, ipAddress);
	}
}
