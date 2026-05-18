package com.safo.icos.modules.user.domain.repository;

import com.safo.icos.modules.user.domain.model.Email;
import com.safo.icos.modules.user.domain.model.User;
import com.safo.icos.modules.user.domain.model.UserId;
import com.safo.icos.modules.user.domain.model.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UserId id);
    Optional<User> findByEmailAndCompanyId(Email email, UUID companyId);
    List<User> findByCompanyId(UUID companyId);
    List<User> findByCompanyIdAndStatus(UUID companyId, UserStatus status);
    boolean existsByEmailAndCompanyId(Email email, UUID companyId);
    User save(User user);
    void delete(User user);
}