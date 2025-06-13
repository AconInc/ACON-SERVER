package com.acon.server.member.infra.repository;

import com.acon.server.member.infra.entity.SavedSpotEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedSpotRepository extends JpaRepository<SavedSpotEntity, Long> {

    void deleteByMemberIdAndSpotId(Long memberId, Long spotId);

    List<SavedSpotEntity> findTop10ByMemberIdOrderByIdDesc(Long memberId);

    List<SavedSpotEntity> findAllByMemberIdOrderByIdDesc(Long memberId);
}
