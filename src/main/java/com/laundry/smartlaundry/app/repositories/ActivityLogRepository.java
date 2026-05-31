package com.laundry.smartlaundry.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundry.smartlaundry.app.models.ActivityLog;
import com.laundry.smartlaundry.app.models.User;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

	List<ActivityLog> findByUserOrderByCreatedAtDesc(User user);
}
