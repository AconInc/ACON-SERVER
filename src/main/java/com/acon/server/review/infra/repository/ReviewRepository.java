package com.acon.server.review.infra.repository;

import com.acon.server.review.infra.dto.RecommendedMenuProjection;
import com.acon.server.review.infra.entity.ReviewEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    @Query("""
            SELECT COUNT(DISTINCT r.memberId)
            FROM   ReviewEntity r
            WHERE  r.spotId      = :spotId
              AND  r.acornCount  = :acornCount
              AND  r.localAcorn  = :localAcorn
            """)
    long countDistinctMemberBySpotIdAndAcornCountAndLocalAcorn(
            @Param("spotId") Long spotId,
            @Param("acornCount") int acornCount,
            @Param("localAcorn") boolean localAcorn
    );

    @Query(value = """
            SELECT   r.recommended_menu as menu, COUNT(r.recommended_menu) as count
            FROM     review r
            WHERE    r.spot_id = :spotId
              AND    r.recommended_menu IS NOT NULL
              AND    r.recommended_menu != ''
            GROUP BY r.recommended_menu
            ORDER BY count DESC
            LIMIT    3
            """, nativeQuery = true)
    List<RecommendedMenuProjection> findTop3RecommendedMenusBySpotId(@Param("spotId") Long spotId);
}
