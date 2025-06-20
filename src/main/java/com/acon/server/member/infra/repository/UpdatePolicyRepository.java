package com.acon.server.member.infra.repository;

import com.acon.server.member.domain.enums.Platform;
import com.acon.server.member.infra.entity.UpdatePolicyEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpdatePolicyRepository extends JpaRepository<UpdatePolicyEntity, Long> {

    public List<UpdatePolicyEntity> findAllByPlatformOrderById(Platform platform);
}
