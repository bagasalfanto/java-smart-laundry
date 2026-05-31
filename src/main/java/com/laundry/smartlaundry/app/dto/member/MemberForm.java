package com.laundry.smartlaundry.app.dto.member;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {

	@NotBlank(message = "Nama wajib diisi")
	@Size(max = 150, message = "Nama maksimal 150 karakter")
	private String nama;

	@NotBlank(message = "Nomor telepon wajib diisi")
	@Size(max = 30, message = "Nomor telepon maksimal 30 karakter")
	private String noTelp;

	private Boolean member = false;

	@Min(value = 0, message = "Poin tidak boleh negatif")
	private Integer poin = 0;
}
