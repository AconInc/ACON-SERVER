package com.acon.server.review.domain.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Review {

    private final Long id;
    private final Long spotId;
    private final Long memberId;
    private final int acornCount;
    private final boolean localAcorn;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Review(
            Long id,
            Long spotId,
            Long memberId,
            int acornCount,
            boolean localAcorn,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.spotId = spotId;
        this.memberId = memberId;
        this.acornCount = acornCount;
        this.localAcorn = localAcorn;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
