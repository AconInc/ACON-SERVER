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
@Table(name = "spot_image")
public class SpotImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @Column(name = "image", nullable = false)
    private String image;

    @Builder
    public SpotImageEntity(
            Long id,
            Long spotId,
            String image
    ) {
        this.id = id;
        this.spotId = spotId;
        this.image = image;
    }
}
