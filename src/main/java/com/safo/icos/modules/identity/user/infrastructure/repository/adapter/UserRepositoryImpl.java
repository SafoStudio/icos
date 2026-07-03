package com.safo.icos.modules.identity.user.infrastructure.repository.adapter;

import com.safo.icos.modules.identity.user.domain.model.Email;
import com.safo.icos.modules.identity.user.domain.model.User;
import com.safo.icos.modules.identity.user.domain.model.enums.UserStatus;
import com.safo.icos.modules.identity.user.domain.repository.UserRepository;
import com.safo.icos.modules.identity.user.domain.repository.MembershipRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.UserJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.UserJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository jpaRepository;
  private final MembershipRepository membershipRepository;

  @Override
  public Optional<User> findById(UUID id) {
    return jpaRepository.findById(id).map(this::toDomain);
  }

  @Override
  public Optional<User> findByEmail(Email email) {
    return jpaRepository.findByEmail(email.value()).map(this::toDomain);
  }

  @Override
  public boolean existsByEmail(Email email) {
    return jpaRepository.existsByEmail(email.value());
  }

  @Override
  public User save(User user) {
    UserJpaEntity entity = toJpaEntity(user);
    UserJpaEntity saved = jpaRepository.save(entity);
    return toDomain(saved);
  }

  @Override
  public void deleteById(UUID id) {
    jpaRepository.deleteById(id);
  }

  private UserJpaEntity toJpaEntity(User domain) {
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

  private User toDomain(UserJpaEntity entity) {
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
