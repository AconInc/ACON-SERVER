package com.acon.server.member.infra.repository;

import com.acon.server.member.infra.entity.SavedSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedSpotRepository extends JpaRepository<SavedSpotEntity, Long> {

    boolean existsByMemberIdAndSpotId(Long memberId, Long spotId);

    void deleteByMemberIdAndSpotId(Long memberId, Long spotId);
}
