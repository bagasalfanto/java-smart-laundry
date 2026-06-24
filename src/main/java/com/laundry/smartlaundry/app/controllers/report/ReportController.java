package com.laundry.smartlaundry.app.controllers.report;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.laundry.smartlaundry.app.models.Laporan;
import com.laundry.smartlaundry.app.models.Transaksi;
import com.laundry.smartlaundry.app.repositories.LaporanRepository;
import com.laundry.smartlaundry.app.repositories.TransaksiRepository;
import com.laundry.smartlaundry.app.services.report.PdfService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final LaporanRepository laporanRepository;
    private final TransaksiRepository transaksiRepository;
    private final PdfService pdfService;

    public ReportController(LaporanRepository laporanRepository, TransaksiRepository transaksiRepository, PdfService pdfService) {
        this.laporanRepository = laporanRepository;
        this.transaksiRepository = transaksiRepository;
        this.pdfService = pdfService;
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
    public ResponseEntity<byte[]> exportPdf(@RequestParam Long id) {
        Laporan laporan = laporanRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Laporan tidak ditemukan"));
        
        // Memanggil PBO Method untuk mencatat log
        laporan.eksporKePDF();
        
        // Generate PDF
        byte[] pdfBytes = pdfService.generateLaporanPdf(laporan);

        // Set Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "Laporan_Pendapatan_" + laporan.getTanggalMulai() + ".pdf";
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}

