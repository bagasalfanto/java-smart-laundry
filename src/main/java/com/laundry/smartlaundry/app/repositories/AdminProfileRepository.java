package com.laundry.smartlaundry.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundry.smartlaundry.app.models.AdminProfile;
import com.laundry.smartlaundry.app.models.User;

public interface AdminProfileRepository extends JpaRepository<AdminProfile, Long> {

	Optional<AdminProfile> findByUser(User user);
}
