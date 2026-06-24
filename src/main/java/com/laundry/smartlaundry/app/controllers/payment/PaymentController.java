package com.laundry.smartlaundry.app.controllers.payment;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.laundry.smartlaundry.app.services.payment.PaymentService;

// Halaman Pembayaran (/payments).
@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // tampilkan daftar transaksi + ringkasan
    @GetMapping
    public String index(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("transaksiList", paymentService.cariTransaksi(search));
        model.addAttribute("search", search);
        model.addAttribute("totalPendapatan", paymentService.hitungTotalPendapatan());
        model.addAttribute("jumlahLunas", paymentService.jumlahLunas());
        model.addAttribute("jumlahBelumLunas", paymentService.jumlahBelumLunas());
        return "payment/index";
    }

    // tombol Bayar -> proses, lalu balik ke daftar
    @PostMapping("/{id}/pay")
    public String pay(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            paymentService.prosesPembayaran(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pembayaran berhasil! Transaksi sekarang LUNAS.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal memproses pembayaran: " + e.getMessage());
        }
        return "redirect:/payments";
    }
}
