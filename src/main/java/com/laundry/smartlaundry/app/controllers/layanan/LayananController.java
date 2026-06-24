package com.laundry.smartlaundry.app.controllers.layanan;

import com.laundry.smartlaundry.module.servicecatalog.Layanan;
import com.laundry.smartlaundry.module.servicecatalog.ServiceCatalogManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/layanan")
public class LayananController {

    private final ServiceCatalogManager manager;

    public LayananController() {
        this.manager = new ServiceCatalogManager();
        // Init with some dummy data
        manager.tambahLayanan(new Layanan("LYN-01", "Cuci Kering", 6000.0, 2));
        manager.tambahLayanan(new Layanan("LYN-02", "Cuci Setrika", 8000.0, 3));
        manager.tambahLayanan(new Layanan("LYN-03", "Express 1 Hari", 12000.0, 1));
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("layananList", manager.getKatalogLayanan());
        // For standard layout consistency
        model.addAttribute("currentPath", "/layanan");
        return "layanan/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("layanan", new Layanan("", "", 0.0, 1));
        model.addAttribute("mode", "create");
        model.addAttribute("formAction", "/layanan/add");
        model.addAttribute("currentPath", "/layanan");
        return "layanan/form";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        Layanan lay = manager.getLayananById(id).orElse(new Layanan("", "", 0.0, 1));
        model.addAttribute("layanan", lay);
        model.addAttribute("mode", "edit");
        model.addAttribute("formAction", "/layanan/update");
        model.addAttribute("currentPath", "/layanan");
        return "layanan/form";
    }

    @PostMapping("/add")
    public String addLayanan(@RequestParam String idLayanan,
                             @RequestParam String namaPaket,
                             @RequestParam double hargaPerKg,
                             @RequestParam int estimasiWaktu,
                             RedirectAttributes redirectAttributes) {
        try {
            manager.tambahLayanan(new Layanan(idLayanan, namaPaket, hargaPerKg, estimasiWaktu));
            redirectAttributes.addFlashAttribute("success", "Paket layanan berhasil ditambahkan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan paket: " + e.getMessage());
        }
        return "redirect:/layanan";
    }

    @PostMapping("/update")
    public String updateLayanan(@RequestParam String idLayanan,
                                @RequestParam String namaPaket,
                                @RequestParam double hargaPerKg,
                                @RequestParam int estimasiWaktu,
                                RedirectAttributes redirectAttributes) {
        try {
            manager.updateLayanan(idLayanan, namaPaket, hargaPerKg, estimasiWaktu);
            redirectAttributes.addFlashAttribute("success", "Paket layanan berhasil diperbarui!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui paket: " + e.getMessage());
        }
        return "redirect:/layanan";
    }

    @PostMapping("/delete")
    public String deleteLayanan(@RequestParam String id, RedirectAttributes redirectAttributes) {
        try {
            manager.hapusLayanan(id);
            redirectAttributes.addFlashAttribute("success", "Paket layanan berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus paket: " + e.getMessage());
        }
        return "redirect:/layanan";
    }

    @GetMapping("/estimate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> estimatePrice(@RequestParam String idLayanan,
                                                             @RequestParam String beratKg) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Replace comma with dot for locales that use comma as decimal separator
            double berat = Double.parseDouble(beratKg.replace(",", "."));
            if (berat <= 0) {
                response.put("success", false);
                response.put("message", "Berat harus lebih dari 0");
                return ResponseEntity.badRequest().body(response);
            }
            
            double estimatedPrice = manager.estimasiTotalHarga(idLayanan, berat);
            if (estimatedPrice == -1) {
                response.put("success", false);
                response.put("message", "Layanan tidak ditemukan");
            } else {
                response.put("success", true);
                response.put("estimatedPrice", estimatedPrice);
            }
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "Format berat tidak valid");
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
