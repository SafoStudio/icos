package com.safo.icos.modules.auth.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AuthCredentialsJpaRepository extends JpaRepository<AuthCredentialsJpaEntity, UUID> {
}
