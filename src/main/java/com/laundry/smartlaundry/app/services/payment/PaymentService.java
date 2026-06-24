package com.laundry.smartlaundry.app.services.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.smartlaundry.app.enums.PaymentStatus;
import com.laundry.smartlaundry.app.models.Transaksi;
import com.laundry.smartlaundry.app.repositories.TransaksiRepository;

// Logika pembayaran versi web. Mirip BillingManager di modul standalone.
@Service
public class PaymentService {

    private final TransaksiRepository transaksiRepository;

    public PaymentService(TransaksiRepository transaksiRepository) {
        this.transaksiRepository = transaksiRepository;
    }

    // kosong -> tampilkan semua; ada isi -> cari lewat invoice / nama
    public List<Transaksi> cariTransaksi(String kataKunci) {
        if (kataKunci == null || kataKunci.isBlank()) {
            return transaksiRepository.findAllByOrderByCreatedAtDesc();
        }
        return transaksiRepository
                .findByInvoiceNumberContainingIgnoreCaseOrPelangganNamaContainingIgnoreCaseOrderByCreatedAtDesc(
                        kataKunci, kataKunci);
    }

    // total semua transaksi yang lunas
    public BigDecimal hitungTotalPendapatan() {
        BigDecimal total = BigDecimal.ZERO;
        for (Transaksi trx : transaksiRepository.findByPaymentStatus(PaymentStatus.LUNAS)) {
            total = total.add(trx.getTotalBayar());
        }
        return total;
    }

    public long jumlahLunas() {
        return transaksiRepository.countByPaymentStatus(PaymentStatus.LUNAS);
    }

    public long jumlahBelumLunas() {
        return transaksiRepository.countByPaymentStatus(PaymentStatus.BELUM_LUNAS);
    }

    // tandai transaksi jadi lunas (sekali aja)
    @Transactional
    public void prosesPembayaran(Long id) {
        Transaksi trx = transaksiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaksi tidak ditemukan"));

        if (trx.getPaymentStatus() == PaymentStatus.LUNAS) {
            return; // udah lunas, stop
        }

        trx.setPaymentStatus(PaymentStatus.LUNAS);
        trx.setPaidAt(LocalDateTime.now());
        transaksiRepository.save(trx);
    }
}
