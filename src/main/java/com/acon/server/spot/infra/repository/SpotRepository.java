package com.acon.server.spot.infra.repository;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.spot.domain.enums.SpotStatus;
import com.acon.server.spot.infra.entity.SpotEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotRepository extends JpaRepository<SpotEntity, Long> {

    long countBySpotStatus(SpotStatus spotStatus);

    boolean existsByNameAndAddressAndSpotStatus(String name, String address, SpotStatus spotStatus);

    boolean existsByNameAndAddressAndSpotStatusAndIdNot(String name, String address, SpotStatus spotStatus, Long id);

    List<SpotEntity> findTop10ByNameStartingWithIgnoreCaseAndSpotStatus(String keyword, SpotStatus spotStatus);

    List<SpotEntity> findAllByLatitudeIsNullOrLongitudeIsNullOrGeomIsNullOrLegalDongIsNull();

    default SpotEntity findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(
                () -> new BusinessException(ErrorType.NOT_FOUND_SPOT_ERROR)
        );
    }

    @Modifying
    @Query("""
            UPDATE SpotEntity s
            SET s.createdAt = CURRENT_TIMESTAMP
            WHERE s.id = :id
              AND s.createdAt IS NULL
            """)
    void initCreatedAtIfNull(@Param("id") Long id);

    // TODO: 앱잼 이후 검색 결과 거리 순 정렬
    @Query(value = """
            SELECT *
            FROM spot
            WHERE name ILIKE %:keyword%
              AND spot_status = :spotStatus
            LIMIT :limit
            """, nativeQuery = true)
    List<SpotEntity> findByNameContainingWithLimitIgnoreCase(
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("spotStatus") String spotStatus
    );

    @Query(value = """
            SELECT s.id, s.name
            FROM spot s
            WHERE s.spot_status = 'ACTIVE'
              AND ST_DWithin(
                s.geom,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326),
                :radius
            )
            ORDER BY ST_DistanceSphere(
                s.geom,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
            )
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findNearestSpotList(
            @Param("longitude") double longitude,
            @Param("latitude") double latitude,
            @Param("radius") double radius,
            @Param("limit") int limit
    );

    @Query(value = """
            SELECT ST_DistanceSphere(
                s.geom,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
            )
            FROM spot s
            WHERE s.id = :spotId
            """, nativeQuery = true)
    Double calculateDistanceToSpot(
            @Param("spotId") long spotId,
            @Param("longitude") double longitude,
            @Param("latitude") double latitude
    );
}
