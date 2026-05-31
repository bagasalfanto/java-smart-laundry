package com.laundry.smartlaundry.app.services.auth;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.laundry.smartlaundry.app.models.User;
import com.laundry.smartlaundry.app.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	private final LoginAttemptService loginAttemptService;

	public CustomUserDetailsService(UserRepository userRepository, LoginAttemptService loginAttemptService) {
		this.userRepository = userRepository;
		this.loginAttemptService = loginAttemptService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (loginAttemptService.isBlocked(username)) {
			throw new LockedException("Terlalu banyak percobaan login gagal. Coba lagi nanti.");
		}

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan: " + username));

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				Boolean.TRUE.equals(user.getActive()),
				true,
				true,
				true,
				List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
	}
}
