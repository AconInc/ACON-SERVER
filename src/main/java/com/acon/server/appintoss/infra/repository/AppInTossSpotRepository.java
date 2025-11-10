package com.acon.server.appintoss.infra.repository;

import com.acon.server.appintoss.infra.entity.AppInTossSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppInTossSpotRepository extends JpaRepository<AppInTossSpotEntity, Long> {

}
