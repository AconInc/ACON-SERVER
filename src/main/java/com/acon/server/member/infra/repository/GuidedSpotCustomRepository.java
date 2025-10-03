package com.acon.server.member.infra.repository;

import com.acon.server.spot.api.response.SearchSuggestionResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class GuidedSpotCustomRepository {

    @PersistenceContext
    private EntityManager em;

    public List<SearchSuggestionResponse> findRecentGuidedSpotSuggestions(
            Long memberId,
            double lat,
            double lon,
            double range,
            int limit
    ) {
        String sql = """
                    SELECT s.id AS spot_id,
                           s.name AS spot_name
                    FROM guided_spot gs
                    JOIN spot s ON s.id = gs.spot_id
                    WHERE gs.member_id = :memberId
                      AND s.spot_status = 'ACTIVE'
                      AND ST_DWithin(
                          s.geom::geography,
                          ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
                          :range
                      )
                    ORDER BY gs.updated_at DESC
                    LIMIT :limit
                """;

        Query nativeQuery = em.createNativeQuery(sql, "SearchSuggestionResponseMapping");
        nativeQuery.setParameter("memberId", memberId);
        nativeQuery.setParameter("lat", lat);
        nativeQuery.setParameter("lon", lon);
        nativeQuery.setParameter("range", range);
        nativeQuery.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<SearchSuggestionResponse> result = nativeQuery.getResultList();

        return result;
    }
}
