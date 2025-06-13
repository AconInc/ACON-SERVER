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
    private final LocalTime startTime;
    private final LocalTime endTime;

    @Builder
    public OpeningHour(
            final Long id,
            final Long spotId,
            final DayOfWeek dayOfWeek,
            final LocalTime startTime,
            final LocalTime endTime
    ) {
        this.id = id;
        this.spotId = spotId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
