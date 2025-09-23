package com.acon.server.spot.domain.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OpeningHour {

    private final Long id;
    private final Long spotId;
    private final DayOfWeek dayOfWeek;

    private Boolean closed;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;

    @Builder
    public OpeningHour(
            final Long id,
            final Long spotId,
            final DayOfWeek dayOfWeek,
            final Boolean closed,
            final LocalTime startTime,
            final LocalTime endTime,
            final LocalTime breakStartTime,
            final LocalTime breakEndTime
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

    public void updateClosed(Boolean closed) {
        this.closed = closed;
    }

    public void updateStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void updateEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void updateBreakStartTime(LocalTime breakStartTime) {
        this.breakStartTime = breakStartTime;
    }

    public void updateBreakEndTime(LocalTime breakEndTime) {
        this.breakEndTime = breakEndTime;
    }
}
