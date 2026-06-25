package com.safo.icos.modules.identity.user.domain.repository;

import com.safo.icos.modules.identity.user.domain.model.Email;
import com.safo.icos.modules.identity.user.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  Optional<User> findById(UUID id);

  Optional<User> findByEmail(Email email);

  boolean existsByEmail(Email email);

  boolean existsByEmailAndCompanyId(Email email, UUID companyId);

  User save(User user);

  void delete(User user);

  void deleteById(UUID id);
}
