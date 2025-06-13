package com.acon.server.review.infra.repository;

import com.acon.server.review.infra.entity.ReviewEntity;
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
}
