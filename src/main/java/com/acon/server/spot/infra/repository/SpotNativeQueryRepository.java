package com.acon.server.spot.infra.repository;

import com.acon.server.spot.api.request.SpotListRequest.Condition.Filter;
import com.acon.server.spot.domain.enums.SpotType;
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
    public List<SpotEntity> findSpotList(
            double latitude,
            double longitude,
            SpotType spotType,
            List<Filter> filterList,
            double radius
    ) {
        // 1) 기본 쿼리
        StringBuilder sqlValue = new StringBuilder();
        sqlValue.append("SELECT s.* \n")
                .append("FROM spot s \n")
                .append("WHERE ST_DWithin(\n")
                .append("        s.geom::geography,\n")
                .append("        ST_SetSRID(ST_MakePoint(:lng, :lat),4326)::geography,\n")
                .append("        :radius) \n")
                .append("  AND s.spot_type = :spotType \n");

        if (filterList != null && !filterList.isEmpty()) {
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).optionList().isEmpty()) {
                    continue;
                }

                sqlValue.append("  AND EXISTS (\n")
                        .append("        SELECT 1 FROM spot_option so\n")
                        .append("        JOIN \"option\" o ON o.id = so.option_id\n")
                        .append("        JOIN category c ON c.id = o.category_id\n")
                        .append("        WHERE so.spot_id = s.id\n")
                        .append("          AND c.name = :cat_").append(i).append("\n")
                        .append("          AND o.name IN (:opt_").append(i).append(")\n")
                        .append("   )\n");
            }
        }

        sqlValue.append("ORDER BY ST_Distance(\n")
                .append("            s.geom::geography,\n")
                .append("            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography\n")
                .append(") ASC");

        Query query = entityManager.createNativeQuery(sqlValue.toString(), SpotEntity.class);
        query.setParameter("lat", latitude);
        query.setParameter("lng", longitude);
        query.setParameter("radius", radius);
        query.setParameter("spotType", spotType.name());

        if (filterList != null && !filterList.isEmpty()) {
            for (int i = 0; i < filterList.size(); i++) {
                Filter filter = filterList.get(i);

                if (filterList.get(i).optionList().isEmpty()) {
                    continue;
                }

                query.setParameter("cat_" + i, filter.category());
                query.setParameter("opt_" + i, filter.optionList());
            }
        }

        @SuppressWarnings("unchecked")
        List<SpotEntity> result = query.getResultList();

        return result;
    }
}
