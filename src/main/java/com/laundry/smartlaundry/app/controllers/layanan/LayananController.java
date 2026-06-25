package com.laundry.smartlaundry.app.controllers.layanan;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.laundry.smartlaundry.app.models.Layanan;
import com.laundry.smartlaundry.app.repositories.LayananRepository;

@Controller
@RequestMapping("/layanan")
public class LayananController {

    @Autowired
    private LayananRepository layananRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("layananList", layananRepository.findByActiveTrue());
        model.addAttribute("currentPath", "/layanan");
        model.addAttribute("role", "ADMIN");
        return "layanan/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("layanan", new Layanan());
        model.addAttribute("mode", "create");
        model.addAttribute("formAction", "/layanan/add");
        model.addAttribute("currentPath", "/layanan");
        return "layanan/form";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Layanan lay = layananRepository.findById(id).orElse(new Layanan());
        model.addAttribute("layanan", lay);
        model.addAttribute("mode", "edit");
        model.addAttribute("formAction", "/layanan/update");
        model.addAttribute("currentPath", "/layanan");
        return "layanan/form";
    }

    @PostMapping("/add")
    public String addLayanan(@RequestParam String namaPaket,
                             @RequestParam BigDecimal hargaPerKg,
                             @RequestParam int estimasiWaktu,
                             RedirectAttributes redirectAttributes) {
        try {
            Layanan lay = new Layanan();
            lay.setNamaPaket(namaPaket);
            lay.setHargaPerKg(hargaPerKg);
            lay.setEstimasiWaktu(estimasiWaktu);
            lay.setActive(true);
            layananRepository.save(lay);
            redirectAttributes.addFlashAttribute("success", "Paket layanan berhasil ditambahkan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan paket: " + e.getMessage());
        }
        return "redirect:/layanan";
    }

    @PostMapping("/update")
    public String updateLayanan(@RequestParam Long id,
                                @RequestParam String namaPaket,
                                @RequestParam BigDecimal hargaPerKg,
                                @RequestParam int estimasiWaktu,
                                RedirectAttributes redirectAttributes) {
        try {
            Optional<Layanan> opt = layananRepository.findById(id);
            if(opt.isPresent()){
                Layanan lay = opt.get();
                lay.setNamaPaket(namaPaket);
                lay.setHargaPerKg(hargaPerKg);
                lay.setEstimasiWaktu(estimasiWaktu);
                layananRepository.save(lay);
                redirectAttributes.addFlashAttribute("success", "Paket layanan berhasil diperbarui!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Gagal memperbarui paket: Layanan tidak ditemukan");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui paket: " + e.getMessage());
        }
        return "redirect:/layanan";
    }

    @PostMapping("/delete")
    public String deleteLayanan(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Layanan> opt = layananRepository.findById(id);
            if(opt.isPresent()){
                Layanan lay = opt.get();
                layananRepository.delete(lay);
                redirectAttributes.addFlashAttribute("success", "Paket layanan berhasil dihapus secara permanen!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus paket: Data ini mungkin sedang digunakan pada transaksi.");
        }
        return "redirect:/layanan";
    }
}
