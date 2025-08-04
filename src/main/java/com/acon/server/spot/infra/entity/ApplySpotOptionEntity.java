package com.acon.server.spot.infra.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "apply_spot_option")
public class ApplySpotOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "apply_spot_id", nullable = false)
    private Long applySpotId;

    @Column(name = "option_id", nullable = false)
    private Long optionId;

    @Builder
    public ApplySpotOptionEntity(
            Long id,
            Long applySpotId,
            Long optionId
    ) {
        this.id = id;
        this.applySpotId = applySpotId;
        this.optionId = optionId;
    }
}
