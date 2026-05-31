package com.laundry.smartlaundry.app.services.admin;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.laundry.smartlaundry.app.dto.admin.StaffForm;
import com.laundry.smartlaundry.app.enums.Role;
import com.laundry.smartlaundry.app.models.StaffProfile;
import com.laundry.smartlaundry.app.models.User;
import com.laundry.smartlaundry.app.repositories.StaffProfileRepository;
import com.laundry.smartlaundry.app.repositories.UserRepository;

@Service
public class StaffManagementService {

	private final StaffProfileRepository staffProfileRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public StaffManagementService(
			StaffProfileRepository staffProfileRepository,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		this.staffProfileRepository = staffProfileRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public List<StaffProfile> findAll() {
		return staffProfileRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	public StaffProfile findById(Long id) {
		return staffProfileRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Staff tidak ditemukan"));
	}

	public StaffForm createForm() {
		return new StaffForm();
	}

	public StaffForm editForm(Long id) {
		StaffProfile profile = findById(id);
		StaffForm form = new StaffForm();
		form.setUsername(profile.getUser().getUsername());
		form.setNama(profile.getNama());
		form.setJumlahShift(profile.getJumlahShift());
		form.setActive(profile.getUser().getActive());
		return form;
	}

	public boolean usernameExistsForCreate(String username) {
		return userRepository.existsByUsername(username);
	}

	public boolean usernameExistsForUpdate(Long staffProfileId, String username) {
		StaffProfile profile = findById(staffProfileId);
		return userRepository.findByUsername(username)
				.map(existing -> !existing.getId().equals(profile.getUser().getId()))
				.orElse(false);
	}

	@Transactional
	public void create(StaffForm form) {
		User user = new User();
		user.setUsername(form.getUsername().trim());
		user.setPassword(passwordEncoder.encode(form.getPassword()));
		user.setRole(Role.STAFF);
		user.setActive(Boolean.TRUE.equals(form.getActive()));
		userRepository.save(user);

		StaffProfile profile = new StaffProfile();
		profile.setUser(user);
		profile.setNama(form.getNama().trim());
		profile.setJumlahShift(normalizeShift(form.getJumlahShift()));
		staffProfileRepository.save(profile);
	}

	@Transactional
	public void update(Long id, StaffForm form) {
		StaffProfile profile = findById(id);
		User user = profile.getUser();

		user.setUsername(form.getUsername().trim());
		if (StringUtils.hasText(form.getPassword())) {
			user.setPassword(passwordEncoder.encode(form.getPassword()));
		}
		user.setActive(Boolean.TRUE.equals(form.getActive()));
		userRepository.save(user);

		profile.setNama(form.getNama().trim());
		profile.setJumlahShift(normalizeShift(form.getJumlahShift()));
		staffProfileRepository.save(profile);
	}

	@Transactional
	public void deactivate(Long id) {
		StaffProfile profile = findById(id);
		User user = profile.getUser();
		user.setActive(false);
		userRepository.save(user);
	}

	private Integer normalizeShift(Integer jumlahShift) {
		if (jumlahShift == null || jumlahShift < 0) {
			return 0;
		}
		return jumlahShift;
	}
}
