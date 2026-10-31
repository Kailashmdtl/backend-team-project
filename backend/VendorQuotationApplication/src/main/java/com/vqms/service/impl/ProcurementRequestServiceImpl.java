
package com.vqms.service.impl;

import com.vqms.dto.request.ProcurementRequestRequest;
import com.vqms.dto.response.ProcurementRequestResponse;
import com.vqms.entity.ProcurementRequest;
import com.vqms.entity.ProcurementStatus;
import com.vqms.exception.BadRequestException;
import com.vqms.mapper.ProcurementRequestMapper;
import com.vqms.repository.ProcurementRequestRepository;
import com.vqms.service.ProcurementRequestService;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcurementRequestServiceImpl
        implements ProcurementRequestService {

    private final ProcurementRequestRepository procurementRequestRepository;
    private final ProcurementRequestMapper procurementRequestMapper;

    // --------------------------------------------------
    // CREATE PROCUREMENT REQUEST
    // --------------------------------------------------

    @Override
    public ProcurementRequestResponse createRequest(
            ProcurementRequestRequest request,
            UUID requesterId) {

        if (requesterId == null) {
            throw new BadRequestException("Requester ID is required");
        }

        ProcurementRequest procurementRequest =
                procurementRequestMapper.toEntity(request, requesterId);

        procurementRequest.setStatus(ProcurementStatus.DRAFT);

        ProcurementRequest savedRequest =
                procurementRequestRepository.save(procurementRequest);

        return procurementRequestMapper.toResponse(savedRequest);
    }

    // --------------------------------------------------
    // GET PROCUREMENT REQUEST BY ID
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public ProcurementRequestResponse getRequestById(
            UUID requestId,
            UUID currentUserId,
            boolean procurementAdmin) {

        ProcurementRequest request = findRequestById(requestId);

        validateAccess(request, currentUserId, procurementAdmin);

        return procurementRequestMapper.toResponse(request);
    }

    // --------------------------------------------------
    // GET ALL PROCUREMENT REQUESTS
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Page<ProcurementRequestResponse> getAllRequests(
            Pageable pageable) {

        return procurementRequestRepository.findAll(pageable)
                .map(procurementRequestMapper::toResponse);
    }

    // --------------------------------------------------
    // GET REQUESTS CREATED BY CURRENT EMPLOYEE
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Page<ProcurementRequestResponse> getMyRequests(
            UUID requesterId,
            Pageable pageable) {

        validateUserId(requesterId);

        return procurementRequestRepository
                .findByRequesterId(requesterId, pageable)
                .map(procurementRequestMapper::toResponse);
    }

    // --------------------------------------------------
    // SEARCH ALL PROCUREMENT REQUESTS
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Page<ProcurementRequestResponse> searchRequests(
            String keyword,
            Pageable pageable) {

        validateKeyword(keyword);

        return procurementRequestRepository
                .searchRequests(keyword.trim(), pageable)
                .map(procurementRequestMapper::toResponse);
    }

    // --------------------------------------------------
    // SEARCH CURRENT EMPLOYEE'S REQUESTS
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Page<ProcurementRequestResponse> searchMyRequests(
            UUID requesterId,
            String keyword,
            Pageable pageable) {

        validateUserId(requesterId);
        validateKeyword(keyword);

        return procurementRequestRepository
                .searchRequestsByRequester(
                        requesterId,
                        keyword.trim(),
                        pageable
                )
                .map(procurementRequestMapper::toResponse);
    }

    // --------------------------------------------------
    // FILTER REQUESTS BY STATUS
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Page<ProcurementRequestResponse> getRequestsByStatus(
            ProcurementStatus status,
            Pageable pageable) {

        validateStatus(status);

        return procurementRequestRepository
                .findByStatus(status, pageable)
                .map(procurementRequestMapper::toResponse);
    }

    // --------------------------------------------------
    // FILTER CURRENT EMPLOYEE'S REQUESTS BY STATUS
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Page<ProcurementRequestResponse> getMyRequestsByStatus(
            UUID requesterId,
            ProcurementStatus status,
            Pageable pageable) {

        validateUserId(requesterId);
        validateStatus(status);

        return procurementRequestRepository
                .findByRequesterIdAndStatus(
                        requesterId,
                        status,
                        pageable
                )
                .map(procurementRequestMapper::toResponse);
    }

    // --------------------------------------------------
    // UPDATE PROCUREMENT REQUEST
    // --------------------------------------------------

    @Override
    public ProcurementRequestResponse updateRequest(
            UUID requestId,
            ProcurementRequestRequest request,
            UUID currentUserId,
            boolean procurementAdmin) {

        ProcurementRequest existingRequest = findRequestById(requestId);

        validateAccess(existingRequest, currentUserId, procurementAdmin);

        if (existingRequest.getStatus() != ProcurementStatus.DRAFT
                && existingRequest.getStatus() != ProcurementStatus.REJECTED) {

            throw new BadRequestException(
                    "Only draft or rejected requests can be updated"
            );
        }

        procurementRequestMapper.updateEntity(existingRequest, request);

        ProcurementRequest updatedRequest =
                procurementRequestRepository.save(existingRequest);

        return procurementRequestMapper.toResponse(updatedRequest);
    }

    // --------------------------------------------------
    // DELETE PROCUREMENT REQUEST
    // --------------------------------------------------

    @Override
    public void deleteRequest(
            UUID requestId,
            UUID currentUserId,
            boolean procurementAdmin) {

        ProcurementRequest request = findRequestById(requestId);

        validateAccess(request, currentUserId, procurementAdmin);

        if (request.getStatus() != ProcurementStatus.DRAFT) {
            throw new BadRequestException(
                    "Only draft procurement requests can be deleted"
            );
        }

        procurementRequestRepository.delete(request);
    }

    // --------------------------------------------------
    // SUBMIT PROCUREMENT REQUEST
    // --------------------------------------------------

    @Override
    public ProcurementRequestResponse submitRequest(
            UUID requestId,
            UUID currentUserId) {

        validateUserId(currentUserId);

        ProcurementRequest request = findRequestById(requestId);

        if (!request.getRequesterId().equals(currentUserId)) {
            throw new BadRequestException(
                    "You can submit only your own procurement requests"
            );
        }

        if (request.getStatus() != ProcurementStatus.DRAFT
                && request.getStatus() != ProcurementStatus.REJECTED) {

            throw new BadRequestException(
                    "Only draft or rejected requests can be submitted"
            );
        }

        request.setStatus(ProcurementStatus.SUBMITTED);
        request.setSubmittedAt(LocalDateTime.now());

        // Clear previous rejection details on resubmission
        request.setApproverId(null);
        request.setRejectionReason(null);
        request.setApprovalComment(null);
        request.setRejectedAt(null);

        ProcurementRequest submittedRequest =
                procurementRequestRepository.save(request);

        return procurementRequestMapper.toResponse(submittedRequest);
    }

    // --------------------------------------------------
    // APPROVE PROCUREMENT REQUEST
    // --------------------------------------------------

    @Override
    public ProcurementRequestResponse approveRequest(
            UUID requestId,
            UUID approverId,
            String approvalComment) {

        validateUserId(approverId);

        ProcurementRequest request = findRequestById(requestId);

        if (request.getStatus() != ProcurementStatus.SUBMITTED) {
            throw new BadRequestException(
                    "Only submitted requests can be approved"
            );
        }

        if (request.getRequesterId().equals(approverId)) {
            throw new BadRequestException(
                    "Requester cannot approve their own procurement request"
            );
        }

        request.setStatus(ProcurementStatus.APPROVED);
        request.setApproverId(approverId);
        request.setApprovalComment(
                approvalComment == null || approvalComment.isBlank()
                        ? null
                        : approvalComment.trim()
        );
        request.setRejectionReason(null);
        request.setApprovedAt(LocalDateTime.now());

        ProcurementRequest approvedRequest =
                procurementRequestRepository.save(request);

        return procurementRequestMapper.toResponse(approvedRequest);
    }

    // --------------------------------------------------
    // REJECT PROCUREMENT REQUEST
    // --------------------------------------------------

    @Override
    public ProcurementRequestResponse rejectRequest(
            UUID requestId,
            UUID approverId,
            String rejectionReason) {

        validateUserId(approverId);

        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new BadRequestException(
                    "Rejection reason is required"
            );
        }

        if (rejectionReason.trim().length() > 5000) {
            throw new BadRequestException(
                    "Rejection reason cannot exceed 5000 characters"
            );
        }

        ProcurementRequest request = findRequestById(requestId);

        if (request.getStatus() != ProcurementStatus.SUBMITTED) {
            throw new BadRequestException(
                    "Only submitted requests can be rejected"
            );
        }

        if (request.getRequesterId().equals(approverId)) {
            throw new BadRequestException(
                    "Requester cannot reject their own procurement request"
            );
        }

        request.setStatus(ProcurementStatus.REJECTED);
        request.setApproverId(approverId);
        request.setRejectionReason(rejectionReason.trim());
        request.setApprovalComment(null);
        request.setRejectedAt(LocalDateTime.now());

        ProcurementRequest rejectedRequest =
                procurementRequestRepository.save(request);

        return procurementRequestMapper.toResponse(rejectedRequest);
    }

    // --------------------------------------------------
    // CANCEL PROCUREMENT REQUEST
    // --------------------------------------------------

    @Override
    public ProcurementRequestResponse cancelRequest(
            UUID requestId,
            UUID currentUserId,
            boolean procurementAdmin) {

        ProcurementRequest request = findRequestById(requestId);

        validateAccess(request, currentUserId, procurementAdmin);

        if (request.getStatus() != ProcurementStatus.DRAFT
                && request.getStatus() != ProcurementStatus.SUBMITTED
                && request.getStatus() != ProcurementStatus.REJECTED) {

            throw new BadRequestException(
                    "This procurement request cannot be cancelled"
            );
        }

        request.setStatus(ProcurementStatus.CANCELLED);
        request.setCancelledAt(LocalDateTime.now());

        ProcurementRequest cancelledRequest =
                procurementRequestRepository.save(request);

        return procurementRequestMapper.toResponse(cancelledRequest);
    }

    // --------------------------------------------------
    // GET REQUESTS FOR APPROVER
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Page<ProcurementRequestResponse> getRequestsForApprover(
            UUID approverId,
            Pageable pageable) {

        validateUserId(approverId);

        return procurementRequestRepository
                .findByApproverId(approverId, pageable)
                .map(procurementRequestMapper::toResponse);
    }

    // --------------------------------------------------
    // CHECK REQUEST OWNERSHIP
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public boolean isRequestOwner(
            UUID requestId,
            UUID requesterId) {

        validateUserId(requesterId);

        return procurementRequestRepository
                .existsByIdAndRequesterId(requestId, requesterId);
    }

    // --------------------------------------------------
    // CHECK REQUEST STATUS
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public boolean hasStatus(
            UUID requestId,
            ProcurementStatus status) {

        validateStatus(status);

        return procurementRequestRepository
                .existsByIdAndStatus(requestId, status);
    }

    // ==================================================
    // PRIVATE HELPER METHODS
    // ==================================================

    private ProcurementRequest findRequestById(UUID requestId) {

        if (requestId == null) {
            throw new BadRequestException(
                    "Procurement request ID is required"
            );
        }

        return procurementRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Procurement request not found with ID: " + requestId
                ));
    }

    private void validateAccess(
            ProcurementRequest request,
            UUID currentUserId,
            boolean procurementAdmin) {

        validateUserId(currentUserId);

        if (!procurementAdmin
                && !request.getRequesterId().equals(currentUserId)) {

            throw new AccessDeniedException(
                    "You are not authorized to access this procurement request"
            );
        }
    }

    private void validateUserId(UUID userId) {

        if (userId == null) {
            throw new BadRequestException(
                    "User ID is required"
            );
        }
    }

    private void validateKeyword(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            throw new BadRequestException(
                    "Search keyword cannot be empty"
            );
        }

        if (keyword.trim().length() < 2) {
            throw new BadRequestException(
                    "Search keyword must contain at least 2 characters"
            );
        }
    }

    private void validateStatus(ProcurementStatus status) {

        if (status == null) {
            throw new BadRequestException(
                    "Procurement request status is required"
            );
        }
    }
}
