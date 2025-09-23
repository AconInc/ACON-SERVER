package com.acon.server.spot.infra.repository;

import com.acon.server.spot.infra.entity.OpeningHourEntity;
import java.time.DayOfWeek;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpeningHourRepository extends JpaRepository<OpeningHourEntity, Long> {

    List<OpeningHourEntity> findAllBySpotIdAndDayOfWeek(Long spotId, DayOfWeek dayOfWeek);

    List<OpeningHourEntity> findAllBySpotId(Long spotId);
}
