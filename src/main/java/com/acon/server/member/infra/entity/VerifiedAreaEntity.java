package com.acon.server.member.infra.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "verified_area",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_verified_area_member_id_name",
                columnNames = {"member_id", "name"}
        )
        // TODO: 복합 인덱스, 단일 인덱스 등 추가 고려
//        indexes = @Index(
//                name = "idx_verified_area_member_id",
//                columnList = "member_id"
//        )
)
public class VerifiedAreaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public VerifiedAreaEntity(
            Long id,
            Long memberId,
            String name,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.createdAt = createdAt;
    }
}
