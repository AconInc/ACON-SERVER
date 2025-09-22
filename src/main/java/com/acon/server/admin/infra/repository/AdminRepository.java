package com.acon.server.admin.infra.repository;

import com.acon.server.admin.infra.entity.AdminEntity;
import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Long> {

    Optional<AdminEntity> findByUsername(String username);

    default AdminEntity findByUsernameOrElseThrow(String username) {
        return findByUsername(username).orElseThrow(
                () -> new BusinessException(ErrorType.NOT_FOUND_MEMBER_ERROR)
        );
    }
}
