package com.laundry.smartlaundry.app.controllers.inventory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.laundry.smartlaundry.app.models.Inventaris;
import com.laundry.smartlaundry.app.repositories.InventarisRepository;

import java.util.List;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final InventarisRepository inventarisRepository;

    public InventoryController(InventarisRepository inventarisRepository) {
        this.inventarisRepository = inventarisRepository;
    }

    @GetMapping
    public String index(Model model) {
        List<Inventaris> items = inventarisRepository.findAll();
        model.addAttribute("items", items);
        return "inventory/index";
    }

    @PostMapping("/update")
    public String updateStok(@RequestParam Long id, @RequestParam String action, @RequestParam int jumlah, RedirectAttributes redirectAttributes) {
        Inventaris item = inventarisRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Item tidak ditemukan"));
        
        if ("TAMBAH".equalsIgnoreCase(action)) {
            item.tambahStok(jumlah);
            redirectAttributes.addFlashAttribute("successMessage", "Stok berhasil ditambah!");
        } else if ("KURANG".equalsIgnoreCase(action)) {
            try {
                item.kurangiStok(jumlah);
                redirectAttributes.addFlashAttribute("successMessage", "Stok berhasil dikurangi!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Stok tidak mencukupi atau jumlah tidak valid!");
            }
        }
        
        inventarisRepository.save(item);
        return "redirect:/inventory";
    }
}
