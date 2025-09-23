package com.acon.server.spot.infra.repository;

import com.acon.server.spot.infra.entity.SpotOptionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpotOptionRepository extends JpaRepository<SpotOptionEntity, Long> {

    List<SpotOptionEntity> findAllBySpotId(Long spotId);

    @Query(value = """
                SELECT DISTINCT so.spot_id
                FROM spot_option so
                JOIN "option" o ON so.option_id = o.id
                WHERE o.name IN :dislikedNames
            """, nativeQuery = true)
    List<Long> findSpotIdsByOptionNames(@Param("dislikedNames") List<String> dislikedNames);

    @Query(value = """
            SELECT o.name
            FROM spot_option so
            JOIN "option" o ON so.option_id = o.id
            JOIN category c ON o.category_id = c.id
            WHERE so.spot_id = :spotId
              AND c.name IN ('RESTAURANT_FEATURE', 'CAFE_FEATURE')
            """, nativeQuery = true)
    List<String> findSpotFeaturesBySpotId(@Param("spotId") Long spotId);

    @Query(value = """
            SELECT o.name
            FROM spot_option so
            JOIN "option" o ON so.option_id = o.id
            JOIN category c ON o.category_id = c.id
            WHERE so.spot_id = :spotId
              AND c.name = 'PRICE'
            LIMIT 1
            """, nativeQuery = true)
    String findPriceFeatureBySpotId(@Param("spotId") Long spotId);
}
