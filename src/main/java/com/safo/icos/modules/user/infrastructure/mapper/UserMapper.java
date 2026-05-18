package com.safo.icos.modules.user.infrastructure.mapper;

import com.safo.icos.modules.user.infrastructure.repository.UserJpaEntity;
import org.springframework.stereotype.Component;
import com.safo.icos.modules.user.domain.model.Email;
import com.safo.icos.modules.user.domain.model.User;
import com.safo.icos.modules.user.domain.model.UserId;
import com.safo.icos.modules.user.domain.model.UserName;

@Component
public class UserMapper {

    public UserJpaEntity toJpaEntity(User user) {
        return UserJpaEntity.builder()
                .id(user.getId().getValue())
                .firstName(user.getName().getFirstName())
                .lastName(user.getName().getLastName())
                .email(user.getEmail().getValue())
                .status(user.getStatus())
                .companyId(user.getCompanyId())
                .createdBy(user.getCreatedBy())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toDomain(UserJpaEntity entity) {
        return User.reconstitute(
                UserId.fromUUID(entity.getId()),
                UserName.of(entity.getFirstName(), entity.getLastName()),
                Email.of(entity.getEmail()),
                entity.getStatus(),
                entity.getCompanyId(),
                entity.getCreatedBy(),
                entity.getCreatedAt()
        );
    }
}