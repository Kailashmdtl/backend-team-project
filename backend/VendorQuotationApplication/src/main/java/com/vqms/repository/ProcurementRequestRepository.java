
package com.vqms.repository;

import com.vqms.entity.ProcurementRequest;
import com.vqms.entity.ProcurementStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcurementRequestRepository
        extends JpaRepository<ProcurementRequest, UUID> {

    // Find all requests created by a particular employee
    Page<ProcurementRequest> findByRequesterId(
            UUID requesterId,
            Pageable pageable
    );

    // Filter requests by status
    Page<ProcurementRequest> findByStatus(
            ProcurementStatus status,
            Pageable pageable
    );

    // Filter requests by requester and status
    Page<ProcurementRequest> findByRequesterIdAndStatus(
            UUID requesterId,
            ProcurementStatus status,
            Pageable pageable
    );

    // Search by title or description
    @Query("""
            SELECT p FROM ProcurementRequest p
            WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<ProcurementRequest> searchRequests(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // Search within requests belonging to a particular employee
    @Query("""
            SELECT p FROM ProcurementRequest p
            WHERE p.requesterId = :requesterId
              AND (
                  LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<ProcurementRequest> searchRequestsByRequester(
            @Param("requesterId") UUID requesterId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // Check whether a request belongs to a particular employee
    boolean existsByIdAndRequesterId(
            UUID id,
            UUID requesterId
    );

    // Check whether a request exists with a particular status
    boolean existsByIdAndStatus(
            UUID id,
            ProcurementStatus status
    );

    // Find requests assigned to a particular approver
    Page<ProcurementRequest> findByApproverId(
            UUID approverId,
            Pageable pageable
    );

    // Find requests awaiting approval
    Page<ProcurementRequest> findByStatusOrderBySubmittedAtAsc(
            ProcurementStatus status,
            Pageable pageable
    );
}
