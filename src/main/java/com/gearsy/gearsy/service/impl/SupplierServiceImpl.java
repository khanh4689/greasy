package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.entity.Categories;
import com.gearsy.gearsy.entity.Suppliers;
import com.gearsy.gearsy.repository.SupplierRepository;
import com.gearsy.gearsy.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository suppliersRepository;

    @Override
    public List<Suppliers> getAllSuppliers() {
        return suppliersRepository.findAll();
    }

    @Override
    public Page<Suppliers> findPaginated(String name, int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("supplierId").ascending());
        if (name != null && !name.isEmpty()) {
            return suppliersRepository.findByNameContainingIgnoreCase(name, pageable);
        }
        return suppliersRepository.findAll(pageable);
    }

    @Override
    public Optional<Suppliers> getSupplierById(Long id) {
        return suppliersRepository.findById(id);
    }

    @Override
    public void saveSupplier(Suppliers supplier) {
        suppliersRepository.save(supplier);
    }

    @Override
    public void deleteSupplier(Long id) {
        suppliersRepository.deleteById(id);
    }

    @Override
    public List<Suppliers> getVisibleCategories() {
        return suppliersRepository.findByHiddenFalse();
    }
}
