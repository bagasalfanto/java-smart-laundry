package com.laundry.smartlaundry.app.dto.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffForm {

	@NotBlank(message = "Username wajib diisi")
	@Size(max = 100, message = "Username maksimal 100 karakter")
	private String username;

	@Size(max = 100, message = "Password maksimal 100 karakter")
	private String password;

	@NotBlank(message = "Nama wajib diisi")
	@Size(max = 150, message = "Nama maksimal 150 karakter")
	private String nama;

	@Min(value = 0, message = "Jumlah shift tidak boleh negatif")
	private Integer jumlahShift = 0;

	private Boolean active = true;
}
