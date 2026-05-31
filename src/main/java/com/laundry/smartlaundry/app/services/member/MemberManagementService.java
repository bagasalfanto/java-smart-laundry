package com.laundry.smartlaundry.app.services.member;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.smartlaundry.app.dto.member.MemberForm;
import com.laundry.smartlaundry.app.models.Pelanggan;
import com.laundry.smartlaundry.app.repositories.PelangganRepository;
import com.laundry.smartlaundry.app.repositories.TransaksiRepository;

@Service
public class MemberManagementService {

	private final PelangganRepository pelangganRepository;
	private final TransaksiRepository transaksiRepository;

	public MemberManagementService(PelangganRepository pelangganRepository, TransaksiRepository transaksiRepository) {
		this.pelangganRepository = pelangganRepository;
		this.transaksiRepository = transaksiRepository;
	}

	public List<Pelanggan> findAll() {
		return pelangganRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	public Pelanggan findById(Long id) {
		return pelangganRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Member tidak ditemukan"));
	}

	public MemberForm createForm() {
		return new MemberForm();
	}

	public MemberForm editForm(Long id) {
		Pelanggan pelanggan = findById(id);
		MemberForm form = new MemberForm();
		form.setNama(pelanggan.getNama());
		form.setNoTelp(pelanggan.getNoTelp());
		form.setMember(pelanggan.getMember());
		form.setPoin(pelanggan.getPoin());
		return form;
	}

	public boolean phoneExistsForCreate(String noTelp) {
		return pelangganRepository.findByNoTelp(noTelp).isPresent();
	}

	public boolean phoneExistsForUpdate(Long id, String noTelp) {
		return pelangganRepository.findByNoTelp(noTelp)
				.map(existing -> !existing.getId().equals(id))
				.orElse(false);
	}

	public boolean hasTransactions(Long id) {
		return transaksiRepository.existsByPelangganId(id);
	}

	@Transactional
	public void create(MemberForm form) {
		Pelanggan pelanggan = new Pelanggan();
		applyForm(pelanggan, form);
		pelangganRepository.save(pelanggan);
	}

	@Transactional
	public void update(Long id, MemberForm form) {
		Pelanggan pelanggan = findById(id);
		applyForm(pelanggan, form);
		pelangganRepository.save(pelanggan);
	}

	@Transactional
	public void delete(Long id) {
		if (hasTransactions(id)) {
			throw new IllegalStateException("Member sudah memiliki transaksi dan tidak bisa dihapus");
		}
		pelangganRepository.deleteById(id);
	}

	private void applyForm(Pelanggan pelanggan, MemberForm form) {
		pelanggan.setNama(form.getNama().trim());
		pelanggan.setNoTelp(form.getNoTelp().trim());
		pelanggan.setMember(Boolean.TRUE.equals(form.getMember()));
		pelanggan.setPoin(form.getPoin() == null || form.getPoin() < 0 ? 0 : form.getPoin());
	}
}
