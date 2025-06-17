package com.acon.server.spot.infra.repository;

import com.acon.server.spot.infra.entity.MenuboardImageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuboardImageRepository extends JpaRepository<MenuboardImageEntity, Long> {

    List<MenuboardImageEntity> findAllBySpotIdOrderById(Long spotId);

    boolean existsBySpotId(Long spotId);
}
