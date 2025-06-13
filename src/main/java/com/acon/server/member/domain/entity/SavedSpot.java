package com.acon.server.member.domain.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SavedSpot {

    private final Long id;
    private final Long memberId;
    private final Long spotId;
    private final LocalDateTime createdAt;

    @Builder
    public SavedSpot(
            final Long id,
            final Long memberId,
            final Long spotId,
            final LocalDateTime createdAt
    ) {
        this.id = id;
        this.memberId = memberId;
        this.spotId = spotId;
        this.createdAt = createdAt;
    }
}
