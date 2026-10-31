
package com.vqms.controller;

import com.vqms.dto.request.VendorRequest;
import com.vqms.dto.response.VendorResponse;
import com.vqms.exception.BadRequestException;
import com.vqms.service.VendorService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vendors")
public class VendorController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "name",
            "email",
            "createdAt",
            "updatedAt"
    );

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    // CREATE VENDOR
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VendorResponse createVendor(
            @Valid @RequestBody VendorRequest request) {

        return vendorService.createVendor(request);
    }

    // GET VENDOR BY ID
    @GetMapping("/{id}")
    public VendorResponse getVendorById(@PathVariable UUID id) {

        return vendorService.getVendorById(id);
    }

    // GET ALL VENDORS

    @GetMapping
    public ResponseEntity<Page<VendorResponse>> getAllVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (page < 0) {
            throw new BadRequestException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new BadRequestException(
                    "Page size must be between 1 and 100"
            );
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            throw new BadRequestException(
                    "Direction must be either 'asc' or 'desc'"
            );
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid sort field. Allowed fields: "
                            + String.join(", ", ALLOWED_SORT_FIELDS)
            );
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(
                vendorService.getAllVendors(pageable)
        );
    }


    // UPDATE VENDOR
    @PutMapping("/{id}")
    public VendorResponse updateVendor(
            @PathVariable UUID id,
            @Valid @RequestBody VendorRequest request) {

        return vendorService.updateVendor(id, request);
    }

    // DELETE VENDOR
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVendor(@PathVariable UUID id) {

        vendorService.deleteVendor(id);
    }

    // GET VENDORS WITH PAGINATION AND SORTING
    @GetMapping("/page")
    public ResponseEntity<Page<VendorResponse>> getVendorsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        // Validate page number
        if (page < 0) {
            throw new BadRequestException(
                    "Page number cannot be negative"
            );
        }

        // Validate page size
        if (size < 1 || size > 100) {
            throw new BadRequestException(
                    "Page size must be between 1 and 100"
            );
        }

        // Validate sorting direction
        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new BadRequestException(
                    "Direction must be either 'asc' or 'desc'"
            );
        }

        // Validate sorting field
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid sort field. Allowed fields: "
                            + String.join(", ", ALLOWED_SORT_FIELDS)
            );
        }

        // Create sorting configuration
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        // Create pagination configuration
        Pageable pageable = PageRequest.of(page, size, sort);

        // Retrieve paginated vendors
        Page<VendorResponse> vendors =
                vendorService.getAllVendors(pageable);

        return ResponseEntity.ok(vendors);
    }



    @GetMapping("/search")
    public ResponseEntity<Page<VendorResponse>> searchVendors(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (keyword == null || keyword.isBlank()) {
            throw new BadRequestException(
                    "Search keyword is required and cannot be blank"
            );
        }

        if (page < 0) {
            throw new BadRequestException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new BadRequestException(
                    "Page size must be between 1 and 100"
            );
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            throw new BadRequestException(
                    "Direction must be either 'asc' or 'desc'"
            );
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid sort field. Allowed fields: "
                            + String.join(", ", ALLOWED_SORT_FIELDS)
            );
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<VendorResponse> vendors =
                vendorService.searchVendors(keyword.trim(), pageable);

        return ResponseEntity.ok(vendors);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<VendorResponse>> getVendorsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (page < 0) {
            throw new BadRequestException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new BadRequestException(
                    "Page size must be between 1 and 100"
            );
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            throw new BadRequestException(
                    "Direction must be either 'asc' or 'desc'"
            );
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid sort field. Allowed fields: "
                            + String.join(", ", ALLOWED_SORT_FIELDS)
            );
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<VendorResponse> vendors =
                vendorService.getVendorsByStatus(status, pageable);

        return ResponseEntity.ok(vendors);
    }

    // ACTIVATE VENDOR
    @PatchMapping("/{id}/activate")
    public ResponseEntity<VendorResponse> activateVendor(
            @PathVariable UUID id) {

        VendorResponse response = vendorService.activateVendor(id);

        return ResponseEntity.ok(response);
    }

    // DEACTIVATE VENDOR
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<VendorResponse> deactivateVendor(
            @PathVariable UUID id) {

        VendorResponse response = vendorService.deactivateVendor(id);

        return ResponseEntity.ok(response);
    }

    // CHECK WHETHER A VENDOR IS ACTIVE
    @GetMapping("/{id}/active")
    public ResponseEntity<Boolean> isVendorActive(
            @PathVariable UUID id) {

        boolean active = vendorService.isVendorActive(id);

        return ResponseEntity.ok(active);
    }


}
