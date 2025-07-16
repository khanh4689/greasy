package com.gearsy.gearsy.controller.admin;

import com.gearsy.gearsy.entity.Suppliers;
import com.gearsy.gearsy.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AdminSupplierController {

    private final SupplierService supplierService;

    @GetMapping("admin/suppliers")
    public String listSuppliers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {

        Page<Suppliers> suppliersPage = supplierService.findPaginated(name, page, 10);
        model.addAttribute("suppliersPage", suppliersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("name", name);
        return "admin/suppliers/list";
    }

    @GetMapping("admin/suppliers/add")
    public String showAddForm(Model model) {
        model.addAttribute("supplier", new Suppliers());
        return "admin/suppliers/add";
    }

    @PostMapping("admin/suppliers/add")
    public String addSupplier(@ModelAttribute Suppliers supplier) {
        supplier.setCreatedAt(java.time.LocalDateTime.now());
        supplier.setUpdatedAt(java.time.LocalDateTime.now());
        supplierService.saveSupplier(supplier);
        return "redirect:/layout/suppliers";
    }

    @GetMapping("admin/suppliers/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Suppliers supplier = supplierService.getSupplierById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với ID: " + id));
        model.addAttribute("supplier", supplier);
        return "admin/suppliers/edit";
    }

    @PostMapping("admin/suppliers/edit/{id}")
    public String updateSupplier(@PathVariable Long id, @ModelAttribute Suppliers supplier) {
        Suppliers existingSupplier = supplierService.getSupplierById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với ID: " + id));
        existingSupplier.setName(supplier.getName());
        existingSupplier.setContactEmail(supplier.getContactEmail());
        existingSupplier.setContactPhone(supplier.getContactPhone());
        existingSupplier.setAddress(supplier.getAddress());
        existingSupplier.setUpdatedAt(java.time.LocalDateTime.now());
        supplierService.saveSupplier(existingSupplier);
        return "redirect: /admin/suppliers";
    }

    @GetMapping("admin/suppliers/delete/{id}")
    public String deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return "redirect:/admin/suppliers";
    }

    @GetMapping("/toggle-hidden/{id}")
    public String toggleHidden(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Suppliers suppliers = supplierService.getSupplierById(id).get();
            suppliers.setHidden(!suppliers.isHidden());
            supplierService.saveSupplier(suppliers);

            String status = suppliers.isHidden() ? "đã được ẩn" : "đã hiển thị lại";
            redirectAttributes.addFlashAttribute("message", "Danh mục \"" + suppliers.getName() + "\" " + status + "!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Không thể thay đổi trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/suppliers";
    }
}