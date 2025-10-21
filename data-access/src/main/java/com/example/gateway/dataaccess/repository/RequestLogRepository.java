package com.example.gateway.dataaccess.repository;

import com.example.gateway.dataaccess.entity.RequestLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestLogRepository extends JpaRepository<RequestLogEntity, Long> {

    Optional<RequestLogEntity> findByRequestId(String requestId);
}
