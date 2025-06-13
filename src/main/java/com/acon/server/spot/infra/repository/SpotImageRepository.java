package com.acon.server.spot.infra.repository;

import com.acon.server.spot.infra.entity.SpotImageEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpotImageRepository extends JpaRepository<SpotImageEntity, Long> {

    List<SpotImageEntity> findAllBySpotId(Long spotId);

    Optional<SpotImageEntity> findTop1BySpotIdOrderById(Long spotId);

    @Query("""
            SELECT si FROM SpotImageEntity si
            WHERE si.spotId IN :spotIds
            AND si.id = (
                SELECT MIN(si2.id)
                FROM SpotImageEntity si2
                WHERE si2.spotId = si.spotId
            )
            """)
    List<SpotImageEntity> findMainImagesBySpotIds(@Param("spotIds") List<Long> spotIds);
}
