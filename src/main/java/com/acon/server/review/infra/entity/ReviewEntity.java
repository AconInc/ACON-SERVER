package com.acon.server.review.infra.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "acorn_count", nullable = false)
    private int acornCount;

    @Column(name = "local_acorn", nullable = false)
    private boolean localAcorn;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public ReviewEntity(
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
