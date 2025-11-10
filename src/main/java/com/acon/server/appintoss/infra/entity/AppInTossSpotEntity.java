package com.acon.server.appintoss.infra.entity;

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
@Table(name = "app_in_toss_spot")
public class AppInTossSpotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "category", length = 20, nullable = false)
    private String category;

    @Column(name = "rating")
    private Integer rating;

    @Builder
    public AppInTossSpotEntity(
            Long id,
            String name,
            String category,
            Integer rating
    ) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.rating = rating;
    }

    public void updateRating(Integer rating) {
        this.rating = rating;
    }
}
