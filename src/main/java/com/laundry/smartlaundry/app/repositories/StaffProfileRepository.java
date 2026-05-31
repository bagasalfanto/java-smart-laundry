package com.laundry.smartlaundry.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundry.smartlaundry.app.models.StaffProfile;
import com.laundry.smartlaundry.app.models.User;

public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {

	Optional<StaffProfile> findByUser(User user);
}
