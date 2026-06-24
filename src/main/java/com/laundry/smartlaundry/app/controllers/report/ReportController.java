package com.laundry.smartlaundry.app.controllers.report;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.laundry.smartlaundry.app.models.Laporan;
import com.laundry.smartlaundry.app.models.Transaksi;
import com.laundry.smartlaundry.app.repositories.LaporanRepository;
import com.laundry.smartlaundry.app.repositories.TransaksiRepository;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final LaporanRepository laporanRepository;
    private final TransaksiRepository transaksiRepository;

    public ReportController(LaporanRepository laporanRepository, TransaksiRepository transaksiRepository) {
        this.laporanRepository = laporanRepository;
        this.transaksiRepository = transaksiRepository;
    }

    @GetMapping
    public String index(Model model) {
        List<Laporan> reports = laporanRepository.findAll();
        model.addAttribute("reports", reports);
        return "report/index";
    }

    @PostMapping("/generate")
    public String generateReport(RedirectAttributes redirectAttributes) {
        LocalDate today = LocalDate.now();
        List<Transaksi> transaksiHarian = transaksiRepository.findAll();

        Laporan laporan = new Laporan();
        laporan.setPeriode("Harian - " + today.toString());
        laporan.setTanggalMulai(today);
        laporan.setTanggalSelesai(today);
        
        // Memanggil PBO Method untuk menghitung total pendapatan dari transaksi lunas
        laporan.generateLaporanHarian(transaksiHarian);
        
        laporanRepository.save(laporan);
        redirectAttributes.addFlashAttribute("successMessage", "Laporan harian (" + today.toString() + ") berhasil di-generate!");
        return "redirect:/reports";
    }

    @PostMapping("/export")
    public String exportPdf(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        Laporan laporan = laporanRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Laporan tidak ditemukan"));
        
        // Memanggil PBO Method
        laporan.eksporKePDF();
        
        redirectAttributes.addFlashAttribute("successMessage", "Laporan periode " + laporan.getPeriode() + " berhasil diekspor ke PDF! (Aksi tercatat di console backend)");
        return "redirect:/reports";
    }
}
