package com.acon.server.spot.infra.entity;

import com.acon.server.spot.domain.enums.SpotApplicationStatus;
import com.acon.server.spot.domain.enums.SpotType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "apply_spot")
public class ApplySpotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "address", length = 100, nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "spot_type", length = 20, nullable = false)
    private SpotType spotType;

    @Column(name = "recommended_menu", length = 50, nullable = false)
    private String recommendedMenu;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_list", nullable = false)
    private List<String> imageList;

    @Enumerated(EnumType.STRING)
    @Column(name = "spot_application_status", length = 20, nullable = false)
    private SpotApplicationStatus spotApplicationStatus;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public ApplySpotEntity(
            Long id,
            Long memberId,
            String name,
            String address,
            SpotType spotType,
            String recommendedMenu,
            List<String> imageList,
            SpotApplicationStatus spotApplicationStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.address = address;
        this.spotType = spotType;
        this.recommendedMenu = recommendedMenu;
        this.imageList = imageList;
        this.spotApplicationStatus = spotApplicationStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
