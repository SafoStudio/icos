package com.safo.icos.modules.identity.auth.infrastructure.repository.jpa;

import com.safo.icos.modules.identity.auth.infrastructure.repository.entity.AuthCredentialsJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthCredentialsJpaRepository extends JpaRepository<AuthCredentialsJpaEntity, UUID> {
}
