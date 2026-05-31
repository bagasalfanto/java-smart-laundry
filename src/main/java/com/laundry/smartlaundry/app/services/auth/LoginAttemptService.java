package com.laundry.smartlaundry.app.services.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

	private static final int MAX_ATTEMPTS = 5;
	private static final Duration LOCK_DURATION = Duration.ofMinutes(1);

	private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

	public void recordFailure(String username) {
		String key = normalize(username);
		if (key.isBlank()) {
			return;
		}

		attempts.compute(key, (ignored, current) -> {
			if (current == null || current.isExpired()) {
				return new LoginAttempt(1, Instant.now().plus(LOCK_DURATION));
			}
			return new LoginAttempt(current.count() + 1, current.lockUntil());
		});
	}

	public void recordSuccess(String username) {
		attempts.remove(normalize(username));
	}

	public boolean isBlocked(String username) {
		String key = normalize(username);
		LoginAttempt attempt = attempts.get(key);
		if (attempt == null) {
			return false;
		}

		if (attempt.isExpired()) {
			attempts.remove(key);
			return false;
		}

		return attempt.count() >= MAX_ATTEMPTS;
	}

	private String normalize(String username) {
		if (username == null) {
			return "";
		}
		return username.trim().toLowerCase(Locale.ROOT);
	}

	private record LoginAttempt(int count, Instant lockUntil) {
		private boolean isExpired() {
			return Instant.now().isAfter(lockUntil);
		}
	}
}
