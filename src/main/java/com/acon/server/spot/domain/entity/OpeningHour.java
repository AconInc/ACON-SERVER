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
    private final Boolean closed;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final LocalTime breakStartTime;
    private final LocalTime breakEndTime;

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
}
