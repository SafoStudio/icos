package com.safo.icos.modules.identity.user.infrastructure.mapper;

import com.safo.icos.modules.identity.user.domain.model.Email;
import com.safo.icos.modules.identity.user.domain.model.User;
import com.safo.icos.modules.identity.user.domain.model.enums.UserStatus;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserJpaEntity toJpaEntity(User domain) {
    return UserJpaEntity.builder()
            .id(domain.getId())
            .firstName(domain.getFirstName())
            .lastName(domain.getLastName())
            .email(domain.getEmail().value())
            .status(domain.getStatus().name())
            .createdBy(domain.getCreatedBy())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
  }

  public User toDomain(UserJpaEntity entity) {
    return User.builder()
            .id(entity.getId())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .email(new Email(entity.getEmail()))
            .status(UserStatus.valueOf(entity.getStatus()))
            .createdBy(entity.getCreatedBy())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
  }
}
