
package com.vqms.service;

import com.vqms.dto.request.VendorRequest;
import com.vqms.dto.response.VendorResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface VendorService {

    // Create a new vendor
    VendorResponse createVendor(VendorRequest request);

    // Retrieve a vendor by ID
    VendorResponse getVendorById(UUID id);

    // Retrieve all vendors without pagination
    List<VendorResponse> getAllVendors();

    // Retrieve vendors with pagination and sorting
    Page<VendorResponse> getAllVendors(Pageable pageable);

    // Search vendors by name or email with pagination
    Page<VendorResponse> searchVendors(
            String keyword,
            Pageable pageable
    );
    Page<VendorResponse> getVendorsByStatus(
            String status,
            Pageable pageable
    );

    // Update an existing vendor
    VendorResponse updateVendor(
            UUID id,
            VendorRequest request
    );

    // Delete a vendor by ID
    void deleteVendor(UUID id);


    // Activate an existing vendor
    VendorResponse activateVendor(UUID id);

    // Deactivate an existing vendor
    VendorResponse deactivateVendor(UUID id);

    boolean isVendorActive(UUID id);

}
