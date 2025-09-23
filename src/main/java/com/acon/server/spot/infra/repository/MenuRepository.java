package com.acon.server.spot.infra.repository;

import com.acon.server.spot.infra.entity.MenuEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

    void deleteAllBySpotId(Long spotId);

    List<MenuEntity> findAllBySpotId(Long spotId);
}
