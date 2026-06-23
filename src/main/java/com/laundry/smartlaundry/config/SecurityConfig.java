package com.laundry.smartlaundry.config;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
						.requestMatchers("/members", "/members/**", "/workflow", "/workflow/**", "/orders", "/orders/**", "/layanan", "/layanan/**").hasAnyRole("ADMIN", "STAFF")
						.requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
						.requestMatchers("/staff", "/staff/**").hasRole("STAFF")
						.requestMatchers("/dashboard").hasAnyRole("ADMIN", "STAFF")
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.loginProcessingUrl("/login")
						.successHandler(roleBasedSuccessHandler())
						.failureUrl("/login?error")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.clearAuthentication(true)
						.permitAll())
				.sessionManagement(session -> session
						.sessionFixation(sessionFixation -> sessionFixation.migrateSession())
						.invalidSessionUrl("/login?expired")
						.maximumSessions(1)
						.maxSessionsPreventsLogin(false))
				.headers(headers -> headers
						.contentSecurityPolicy(csp -> csp.policyDirectives(
								"default-src 'self'; "
										+ "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; "
										+ "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; "
										+ "img-src 'self' data:; "
										+ "font-src 'self' https://cdn.jsdelivr.net; "
										+ "form-action 'self'; "
										+ "base-uri 'self'; "
										+ "frame-ancestors 'none'"))
						.frameOptions(frameOptions -> frameOptions.deny())
						.referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
						.permissionsPolicyHeader(permissions -> permissions.policy(
								"camera=(), microphone=(), geolocation=(), payment=(), usb=()")));

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Bean
	public AuthenticationSuccessHandler roleBasedSuccessHandler() {
		return new AuthenticationSuccessHandler() {
			@Override
			public void onAuthenticationSuccess(
					HttpServletRequest request,
					HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				response.sendRedirect(request.getContextPath() + "/dashboard");
			}
		};
	}
}
