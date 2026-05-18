package com.safo.icos.modules.user.infrastructure.repository;

import com.safo.icos.modules.user.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.safo.icos.modules.user.domain.model.Email;
import com.safo.icos.modules.user.domain.model.User;
import com.safo.icos.modules.user.domain.model.UserId;
import com.safo.icos.modules.user.domain.model.UserStatus;
import com.safo.icos.modules.user.domain.repository.UserRepository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailAndCompanyId(Email email, UUID companyId) {
        return jpaRepository.findByEmailAndCompanyId(email.getValue(), companyId)
                .map(mapper::toDomain);
    }

    @Override
    public List<User> findByCompanyId(UUID companyId) {
        return jpaRepository.findByCompanyId(companyId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByCompanyIdAndStatus(UUID companyId, UserStatus status) {
        return jpaRepository.findByCompanyIdAndStatus(companyId, status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmailAndCompanyId(Email email, UUID companyId) {
        return jpaRepository.existsByEmailAndCompanyId(email.getValue(), companyId);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toJpaEntity(user);
        UserJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(User user) {
        jpaRepository.deleteById(user.getId().getValue());
    }
}