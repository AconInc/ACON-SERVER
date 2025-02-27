package com.acon.server.member.infra.repository;

import com.acon.server.member.infra.entity.WithdrawalReasonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalReasonRepository extends JpaRepository<WithdrawalReasonEntity, Long> {

}
