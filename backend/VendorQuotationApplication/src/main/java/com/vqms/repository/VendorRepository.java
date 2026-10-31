
package com.vqms.repository;

import com.vqms.entity.Vendor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {

    // Check whether a vendor email already exists
    boolean existsByEmailIgnoreCase(String email);

    // Search vendors by name
    List<Vendor> findByNameContainingIgnoreCase(String name);

    // Search vendors by email
    List<Vendor> findByEmailContainingIgnoreCase(String email);

    // Search vendors by name OR email with pagination and sorting
    Page<Vendor> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name,
            String email,
            Pageable pageable
    );
    Page<Vendor> findByStatusIgnoreCase(
            String status,
            Pageable pageable
    );

    boolean existsByIdAndStatusIgnoreCase(UUID id, String status);

}
