
package com.vqms.mapper;

import com.vqms.dto.request.ProcurementRequestRequest;
import com.vqms.dto.response.ProcurementRequestResponse;
import com.vqms.entity.ProcurementRequest;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcurementRequestMapper {

    /**
     * Convert request DTO into a new entity.
     * Workflow fields are intentionally controlled by the service.
     */
    public ProcurementRequest toEntity(
            ProcurementRequestRequest request,
            java.util.UUID requesterId) {

        ProcurementRequest entity = new ProcurementRequest();

        entity.setTitle(request.getTitle().trim());
        entity.setDescription(request.getDescription().trim());
        entity.setQuantity(request.getQuantity());
        entity.setUnit(request.getUnit().trim());
        entity.setRequiredBy(request.getRequiredBy());
        entity.setRequesterId(requesterId);

        return entity;
    }

    /**
     * Convert entity into response DTO.
     */
    public ProcurementRequestResponse toResponse(
            ProcurementRequest entity) {

        ProcurementRequestResponse response =
                new ProcurementRequestResponse();

        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setQuantity(entity.getQuantity());
        response.setUnit(entity.getUnit());
        response.setStatus(entity.getStatus());
        response.setRequiredBy(entity.getRequiredBy());

        response.setRequesterId(entity.getRequesterId());
        response.setApproverId(entity.getApproverId());

        response.setApprovalComment(entity.getApprovalComment());
        response.setRejectionReason(entity.getRejectionReason());

        response.setSubmittedAt(entity.getSubmittedAt());
        response.setApprovedAt(entity.getApprovedAt());
        response.setRejectedAt(entity.getRejectedAt());
        response.setCancelledAt(entity.getCancelledAt());

        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        return response;
    }

    /**
     * Update editable fields only.
     * Does not modify ID, requester, status or approval information.
     */
    public void updateEntity(
            ProcurementRequest entity,
            ProcurementRequestRequest request) {

        entity.setTitle(request.getTitle().trim());
        entity.setDescription(request.getDescription().trim());
        entity.setQuantity(request.getQuantity());
        entity.setUnit(request.getUnit().trim());
        entity.setRequiredBy(request.getRequiredBy());
    }

    /**
     * Convert a list of entities into response DTOs.
     */
    public List<ProcurementRequestResponse> toResponseList(
            List<ProcurementRequest> entities) {

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}
