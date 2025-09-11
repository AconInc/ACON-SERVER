package com.acon.server.spot.infra.entity;

import com.acon.server.spot.api.response.SearchSuggestionResponse;
import com.acon.server.spot.domain.enums.SpotStatus;
import com.acon.server.spot.domain.enums.SpotType;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@SqlResultSetMapping(
        name = "SearchSuggestionResponseMapping",
        classes = {
                @ConstructorResult(
                        targetClass = SearchSuggestionResponse.class,
                        columns = {
                                @ColumnResult(name = "spot_id", type = Long.class),
                                @ColumnResult(name = "spot_name", type = String.class)
                        }
                )
        }
)
@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "spot")
// TODO: 공간 인덱스 설정
public class SpotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "spot_type", length = 20, nullable = false)
    private SpotType spotType;

    @Column(name = "local_acorn_count", nullable = false)
    private Integer localAcornCount;

    @Column(name = "basic_acorn_count", nullable = false)
    private Integer basicAcornCount;

    @Column(name = "address", length = 100, nullable = false)
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(name = "geom", columnDefinition = "geometry(Point, 4326)")
    private Point geom;

    @Column(name = "legal_dong")
    private String legalDong;

    @Column(name = "applied_member_id")
    private Long appliedMemberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "spot_status", length = 20, nullable = false)
    private SpotStatus spotStatus;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public SpotEntity(
            Long id,
            String name,
            SpotType spotType,
            Integer localAcornCount,
            Integer basicAcornCount,
            String address,
            Double latitude,
            Double longitude,
            Point geom,
            String legalDong,
            Long appliedMemberId,
            SpotStatus spotStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.spotType = spotType;
        // TODO: 영속성 엔티티에서 기본값을 설정하는 로직을 도메인 엔티티로 이동
        this.localAcornCount = localAcornCount != null ? localAcornCount : 0;
        this.basicAcornCount = basicAcornCount != null ? basicAcornCount : 0;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geom = geom;
        this.legalDong = legalDong;
        this.appliedMemberId = appliedMemberId;
        this.spotStatus = spotStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
