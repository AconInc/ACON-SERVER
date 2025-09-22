package com.acon.server.spot.infra.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "opening_hour"
//        indexes = @Index(
//                name = "idx_opening_hour_spot_id_day_of_week",
//                columnList = "spot_id, day_of_week"
//        )
)
public class OpeningHourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", length = 10, nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "closed", nullable = false)
    private Boolean closed;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "break_start_time")
    private LocalTime breakStartTime;

    @Column(name = "break_end_time")
    private LocalTime breakEndTime;

    @Builder
    public OpeningHourEntity(
            Long id,
            Long spotId,
            DayOfWeek dayOfWeek,
            Boolean closed,
            LocalTime startTime,
            LocalTime endTime,
            LocalTime breakStartTime,
            LocalTime breakEndTime
    ) {
        this.id = id;
        this.spotId = spotId;
        this.dayOfWeek = dayOfWeek;
        this.closed = closed;
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
    }
}
