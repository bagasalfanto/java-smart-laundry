package com.laundry.smartlaundry.app.services.admin;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.smartlaundry.app.models.ActivityLog;
import com.laundry.smartlaundry.app.models.User;
import com.laundry.smartlaundry.app.repositories.ActivityLogRepository;
import com.laundry.smartlaundry.app.repositories.UserRepository;

@Service
public class ActivityLogService {

	private final ActivityLogRepository activityLogRepository;
	private final UserRepository userRepository;

	public ActivityLogService(ActivityLogRepository activityLogRepository, UserRepository userRepository) {
		this.activityLogRepository = activityLogRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public void logLoginSuccess(String username, String ipAddress) {
		userRepository.findByUsername(username).ifPresent(user -> {
			ActivityLog log = new ActivityLog();
			log.setUser(user);
			log.setActivity("Login Berhasil");
			log.setIpAddress(ipAddress);
			activityLogRepository.save(log);
		});
	}

	@Transactional(readOnly = true)
	public List<ActivityLog> getAllLogs() {
		return activityLogRepository.findAllWithUserByOrderByCreatedAtDesc();
	}

	@Transactional(readOnly = true)
	public List<ActivityLog> getUserLogs(User user) {
		return activityLogRepository.findByUserOrderByCreatedAtDesc(user);
	}
}
