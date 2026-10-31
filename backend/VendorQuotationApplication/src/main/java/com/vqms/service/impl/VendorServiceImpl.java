
package com.vqms.service.impl;

import com.vqms.dto.request.VendorRequest;
import com.vqms.dto.response.VendorResponse;
import com.vqms.entity.Vendor;
import com.vqms.exception.BadRequestException;
import com.vqms.exception.DuplicateVendorEmailException;
import com.vqms.exception.VendorNotFoundException;
import com.vqms.mapper.VendorMapper;
import com.vqms.repository.VendorRepository;
import com.vqms.service.VendorService;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;

    // CREATE VENDOR
    @Override
    public VendorResponse createVendor(VendorRequest request) {

        if (vendorRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateVendorEmailException(
                    "A vendor with this email already exists"
            );
        }

        Vendor vendor = vendorMapper.toEntity(request);

        Vendor savedVendor = vendorRepository.save(vendor);

        return vendorMapper.toResponse(savedVendor);
    }

    // GET VENDOR BY ID
    @Override
    @Transactional(readOnly = true)
    public VendorResponse getVendorById(UUID id) {

        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new VendorNotFoundException(
                        "Vendor not found with id: " + id
                ));

        return vendorMapper.toResponse(vendor);
    }

    // GET ALL VENDORS WITHOUT PAGINATION
    @Override
    @Transactional(readOnly = true)
    public List<VendorResponse> getAllVendors() {

        return vendorRepository.findAll()
                .stream()
                .map(vendorMapper::toResponse)
                .toList();
    }

    // GET VENDORS WITH PAGINATION AND SORTING
    @Override
    @Transactional(readOnly = true)
    public Page<VendorResponse> getAllVendors(Pageable pageable) {

        return vendorRepository.findAll(pageable)
                .map(vendorMapper::toResponse);
    }

    // SEARCH VENDORS BY NAME OR EMAIL
    @Override
    @Transactional(readOnly = true)
    public Page<VendorResponse> searchVendors(
            String keyword,
            Pageable pageable) {

        if (keyword == null || keyword.isBlank()) {
            throw new BadRequestException(
                    "Search keyword cannot be empty"
            );
        }

        String searchKeyword = keyword.trim();

        Page<Vendor> vendors =
                vendorRepository
                        .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                                searchKeyword,
                                searchKeyword,
                                pageable
                        );

        return vendors.map(vendorMapper::toResponse);
    }

    // UPDATE VENDOR
    @Override
    public VendorResponse updateVendor(UUID id, VendorRequest request) {

        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new VendorNotFoundException(
                        "Vendor not found with id: " + id
                ));

        boolean emailBelongsToAnotherVendor =
                vendorRepository.existsByEmailIgnoreCase(request.getEmail())
                        && !vendor.getEmail()
                        .equalsIgnoreCase(request.getEmail());

        if (emailBelongsToAnotherVendor) {
            throw new DuplicateVendorEmailException(
                    "Another vendor is already using this email"
            );
        }

        vendor.setName(request.getName());
        vendor.setEmail(request.getEmail());
        vendor.setPhone(request.getPhone());
        vendor.setAddress(request.getAddress());

        Vendor updatedVendor = vendorRepository.save(vendor);

        return vendorMapper.toResponse(updatedVendor);
    }

    // DELETE VENDOR
    @Override
    public void deleteVendor(UUID id) {

        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new VendorNotFoundException(
                        "Vendor not found with id: " + id
                ));

        vendorRepository.delete(vendor);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VendorResponse> getVendorsByStatus(
            String status,
            Pageable pageable) {

        if (status == null || status.isBlank()) {
            throw new BadRequestException(
                    "Vendor status cannot be empty"
            );
        }

        String normalizedStatus = status.trim().toUpperCase();

        if (!normalizedStatus.equals("ACTIVE")
                && !normalizedStatus.equals("INACTIVE")) {
            throw new BadRequestException(
                    "Invalid vendor status. Allowed values: ACTIVE, INACTIVE"
            );
        }

        Page<Vendor> vendors =
                vendorRepository.findByStatusIgnoreCase(
                        normalizedStatus,
                        pageable
                );

        return vendors.map(vendorMapper::toResponse);
    }

    // ACTIVATE VENDOR
    @Override
    public VendorResponse activateVendor(UUID id) {

        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new VendorNotFoundException(
                        "Vendor not found with id: " + id
                ));

        vendor.setStatus("ACTIVE");

        Vendor updatedVendor = vendorRepository.save(vendor);

        return vendorMapper.toResponse(updatedVendor);
    }

    // DEACTIVATE VENDOR
    @Override
    public VendorResponse deactivateVendor(UUID id) {

        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new VendorNotFoundException(
                        "Vendor not found with id: " + id
                ));

        vendor.setStatus("INACTIVE");

        Vendor updatedVendor = vendorRepository.save(vendor);

        return vendorMapper.toResponse(updatedVendor);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVendorActive(UUID id) {

        return vendorRepository.existsByIdAndStatusIgnoreCase(
                id,
                "ACTIVE"
        );
    }


}
