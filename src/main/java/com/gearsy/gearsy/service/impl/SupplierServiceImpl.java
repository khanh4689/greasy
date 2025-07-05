package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.entity.Suppliers;
import com.gearsy.gearsy.repository.SupplierRepository;
import com.gearsy.gearsy.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository suppliersRepository;

    @Override
    public List<Suppliers> getAllSuppliers() {
        return suppliersRepository.findAll();
    }
}
