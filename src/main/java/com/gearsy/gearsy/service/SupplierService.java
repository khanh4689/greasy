package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.Suppliers;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface SupplierService {
    public Object getAllSuppliers();
    Page<Suppliers> findPaginated(String name, int page, int size);
    Optional<Suppliers> getSupplierById(Long id);
    void saveSupplier(Suppliers supplier);
    void deleteSupplier(Long id);
    List<Suppliers> getVisibleCategories();
}
