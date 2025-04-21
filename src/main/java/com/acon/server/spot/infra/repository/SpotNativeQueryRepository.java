package com.acon.server.spot.infra.repository;

import com.acon.server.spot.api.request.SpotListRequest.Condition.Filter;
import com.acon.server.spot.infra.entity.SpotEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

// TODO: 리팩토링
@Repository
public class SpotNativeQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<SpotEntity> findSpotsWithinDistance(
            double lat,
            double lng,
            double distanceMeter,
            String spotType,
            Integer priceRange,
            List<Filter> filterList
    ) {
        // 1) 기본 쿼리
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT s.* ")
                .append("FROM spot s ")
                .append("WHERE ")
                .append("ST_DWithin(s.geom::geography, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, :distanceMeter) ");
        if (spotType != null && !spotType.trim().isEmpty()) {
            sb.append("AND s.spot_type = :spotType ");
        }
        if (priceRange != null) {
            sb.append("AND EXISTS ( ")
                    .append("SELECT 1 FROM menu m WHERE m.spot_id = s.id AND m.main_menu = TRUE AND m.price <= :priceRange) ");
        }

        // 2) filterList가 있는 경우, 각 항목마다 AND EXISTS 서브쿼리 추가
        if (filterList != null && !filterList.isEmpty()) {
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).optionList().isEmpty()) {
                    continue;
                }
                sb.append("AND EXISTS (")
                        .append("SELECT 1 FROM spot_option so ")
                        .append("JOIN \"option\" o ON o.id = so.option_id ")
                        .append("JOIN category c ON c.id = o.category_id ")
                        .append("WHERE so.spot_id = s.id ")
                        .append("AND c.name = :categoryName_").append(i).append(" ")
                        .append("AND o.name IN (:optionNames_").append(i).append(") ")
                        .append(") ");
            }
        }

        // 3) Native Query 생성
        Query query = entityManager.createNativeQuery(sb.toString(), SpotEntity.class);

        // 4) 파라미터 바인딩
        query.setParameter("lat", lat);
        query.setParameter("lng", lng);
        query.setParameter("distanceMeter", distanceMeter);

        if (spotType != null && !spotType.trim().isEmpty()) {
            query.setParameter("spotType", spotType);
        }

        if (priceRange != null) {
            query.setParameter("priceRange", priceRange);
        }

        // 5) filterList 파라미터 바인딩
        if (filterList != null && !filterList.isEmpty()) {
            for (int i = 0; i < filterList.size(); i++) {
                Filter filter = filterList.get(i);
                if (filter.optionList().isEmpty()) {
                    continue;
                }
                query.setParameter("categoryName_" + i, filter.category());
                query.setParameter("optionNames_" + i, filter.optionList());
            }
        }

        // 6) 쿼리 실행
        List<SpotEntity> result = query.getResultList();

        return result;
    }
}
