package com.laundry.smartlaundry.console;

import com.laundry.smartlaundry.database.seeders.DatabaseSeeder;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ArtisanCommandRunner implements ApplicationRunner {

	private final ConfigurableApplicationContext applicationContext;
	private final DatabaseSeeder databaseSeeder;
	private final Environment environment;

	public ArtisanCommandRunner(
			ConfigurableApplicationContext applicationContext,
			DatabaseSeeder databaseSeeder,
			Environment environment) {
		this.applicationContext = applicationContext;
		this.databaseSeeder = databaseSeeder;
		this.environment = environment;
	}

	@Override
	public void run(ApplicationArguments args) {
		String command = environment.getProperty("app.command", "").trim().toLowerCase();
		if (command.isBlank()) {
			return;
		}

		switch (command) {
			case "migrate" -> System.out.println("Migration selesai. Flyway sudah menjalankan migration yang belum pernah dijalankan.");
			case "db:seed" -> {
				databaseSeeder.seed();
				System.out.println("Seeder selesai. Data awal berhasil dipastikan ada.");
			}
			case "migrate:seed" -> {
				databaseSeeder.seed();
				System.out.println("Migration dan seeder selesai.");
			}
			default -> throw new IllegalArgumentException("Command tidak dikenal: " + command);
		}

		int exitCode = SpringApplication.exit(applicationContext, () -> 0);
		System.exit(exitCode);
	}
}
