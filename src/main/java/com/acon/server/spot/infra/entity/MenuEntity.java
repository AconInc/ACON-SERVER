package com.acon.server.spot.infra.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "menu",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_menu_spot_id_name",
                columnNames = {"spot_id", "name"}
        )
//        indexes = @Index(
//                name = "idx_menu_spot_id",
//                columnList = "spot_id"
//        )
)
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Builder
    public MenuEntity(
            Long id,
            Long spotId,
            String name,
            int price
    ) {
        this.id = id;
        this.spotId = spotId;
        this.name = name;
        this.price = price;
    }
}
