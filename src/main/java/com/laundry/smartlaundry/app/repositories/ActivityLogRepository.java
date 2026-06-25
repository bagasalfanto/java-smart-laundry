package com.laundry.smartlaundry.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.laundry.smartlaundry.app.models.ActivityLog;
import com.laundry.smartlaundry.app.models.User;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

	List<ActivityLog> findByUserOrderByCreatedAtDesc(User user);

	@Query("SELECT a FROM ActivityLog a JOIN FETCH a.user ORDER BY a.createdAt DESC")
	List<ActivityLog> findAllWithUserByOrderByCreatedAtDesc();
}
