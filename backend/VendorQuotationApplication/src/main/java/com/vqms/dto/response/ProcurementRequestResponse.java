
package com.vqms.dto.response;

import com.vqms.entity.ProcurementStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ProcurementRequestResponse {

    private UUID id;

    private String title;

    private String description;

    private Integer quantity;

    private String unit;

    private ProcurementStatus status;

    private LocalDateTime requiredBy;

    private UUID requesterId;

    private UUID approverId;

    private String approvalComment;

    private String rejectionReason;

    private LocalDateTime submittedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime rejectedAt;

    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
