package com.safo.icos.modules.identity.user.infrastructure.repository.jpa;

import com.safo.icos.modules.identity.user.infrastructure.repository.entity.UserProfileJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileJpaRepository extends JpaRepository<UserProfileJpaEntity, UUID> {
}
