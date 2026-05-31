package com.laundry.smartlaundry.database.seeders;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseSeeder {

	private final JdbcTemplate jdbcTemplate;
	private final Environment environment;
	private final PasswordEncoder passwordEncoder;

	public DatabaseSeeder(JdbcTemplate jdbcTemplate, Environment environment, PasswordEncoder passwordEncoder) {
		this.jdbcTemplate = jdbcTemplate;
		this.environment = environment;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public void seed() {
		seedUsers();
		seedLayanan();
		seedInventaris();
	}

	private void seedUsers() {
		String adminUsername = environment.getProperty("SEED_ADMIN_USERNAME", "admin");
		String adminPassword = environment.getProperty("SEED_ADMIN_PASSWORD", "admin123");
		String staffUsername = environment.getProperty("SEED_STAFF_USERNAME", "staff");
		String staffPassword = environment.getProperty("SEED_STAFF_PASSWORD", "staff123");

		Long adminId = ensureUser(adminUsername, adminPassword, "ADMIN");
		ensureAdminProfile(adminId, "Administrator", "ADM-001");

		Long staffId = ensureUser(staffUsername, staffPassword, "STAFF");
		ensureStaffProfile(staffId, "Staff Laundry", 1);
	}

	private Long ensureUser(String username, String rawPassword, String role) {
		Long existingId = findLong("SELECT id FROM users WHERE username = ?", username);
		if (existingId != null) {
			return existingId;
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO users (username, password, role, active) VALUES (?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, username);
			statement.setString(2, passwordEncoder.encode(rawPassword));
			statement.setString(3, role);
			statement.setBoolean(4, true);
			return statement;
		}, keyHolder);

		return keyHolder.getKey().longValue();
	}

	private void ensureAdminProfile(Long userId, String nama, String kodeOtoritas) {
		if (exists("SELECT COUNT(*) FROM admin_profiles WHERE user_id = ?", userId)) {
			return;
		}

		jdbcTemplate.update(
				"INSERT INTO admin_profiles (user_id, nama, kode_otoritas) VALUES (?, ?, ?)",
				userId, nama, kodeOtoritas);
	}

	private void ensureStaffProfile(Long userId, String nama, int jumlahShift) {
		if (exists("SELECT COUNT(*) FROM staff_profiles WHERE user_id = ?", userId)) {
			return;
		}

		jdbcTemplate.update(
				"INSERT INTO staff_profiles (user_id, nama, jumlah_shift) VALUES (?, ?, ?)",
				userId, nama, jumlahShift);
	}

	private void seedLayanan() {
		ensureLayanan("Cuci Kering", new BigDecimal("7000.00"), 24);
		ensureLayanan("Cuci Setrika", new BigDecimal("10000.00"), 48);
		ensureLayanan("Express", new BigDecimal("15000.00"), 12);
	}

	private void ensureLayanan(String namaPaket, BigDecimal hargaPerKg, int estimasiWaktu) {
		if (exists("SELECT COUNT(*) FROM layanan WHERE nama_paket = ?", namaPaket)) {
			return;
		}

		jdbcTemplate.update(
				"INSERT INTO layanan (nama_paket, harga_per_kg, estimasi_waktu, active) VALUES (?, ?, ?, ?)",
				namaPaket, hargaPerKg, estimasiWaktu, true);
	}

	private void seedInventaris() {
		ensureInventaris("Deterjen", 50, "kg");
		ensureInventaris("Pewangi", 50, "liter");
		ensureInventaris("Plastik", 100, "pcs");
	}

	private void ensureInventaris(String namaBarang, int stok, String satuan) {
		if (exists("SELECT COUNT(*) FROM inventaris WHERE nama_barang = ?", namaBarang)) {
			return;
		}

		jdbcTemplate.update(
				"INSERT INTO inventaris (nama_barang, stok, satuan) VALUES (?, ?, ?)",
				namaBarang, stok, satuan);
	}

	private boolean exists(String sql, Object... args) {
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, args);
		return count != null && count > 0;
	}

	private Long findLong(String sql, Object... args) {
		List<Long> results = jdbcTemplate.queryForList(sql, Long.class, args);
		if (results.isEmpty()) {
			return null;
		}
		return results.get(0);
	}
}
