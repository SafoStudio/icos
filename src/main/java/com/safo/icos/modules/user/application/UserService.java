package com.safo.icos.modules.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.safo.icos.modules.user.domain.exception.UserNotFoundException;
import com.safo.icos.modules.user.domain.model.Email;
import com.safo.icos.modules.user.domain.model.User;
import com.safo.icos.modules.user.domain.model.UserId;
import com.safo.icos.modules.user.domain.model.UserName;
import com.safo.icos.modules.user.domain.repository.UserRepository;
import com.safo.icos.modules.user.domain.service.UserDomainService;
import com.safo.icos.modules.user.application.dto.UserCreateDto;
import com.safo.icos.modules.user.application.dto.UserResponseDto;
import com.safo.icos.modules.user.application.dto.UserUpdateDto;
import com.safo.icos.modules.user.infrastructure.mapper.UserMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final UserMapper userMapper;

    public UserResponseDto createUser(UserCreateDto dto, UUID createdBy) {
        UserName name = UserName.of(dto.getFirstName(), dto.getLastName());
        Email email = Email.of(dto.getEmail());

        User user = userDomainService.createUser(name, email, dto.getCompanyId(), createdBy);
        User savedUser = userRepository.save(user);

        return toResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID userId) {
        User user = userRepository.findById(UserId.fromUUID(userId))
                .orElseThrow(() -> new UserNotFoundException(UserId.fromUUID(userId)));
        return toResponseDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmailAndCompanyId(String email, UUID companyId) {
        Email emailValue = Email.of(email);
        User user = userRepository.findByEmailAndCompanyId(emailValue, companyId)
                .orElseThrow(() -> new UserNotFoundException(email));
        return toResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersByCompanyId(UUID companyId) {
        return userRepository.findByCompanyId(companyId).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto updateUser(UUID userId, UserUpdateDto dto, UUID updatedBy) {
        User user = userRepository.findById(UserId.fromUUID(userId))
                .orElseThrow(() -> new UserNotFoundException(UserId.fromUUID(userId)));

        if (dto.getFirstName() != null || dto.getLastName() != null) {
            String firstName = dto.getFirstName() != null ? dto.getFirstName() : user.getName().getFirstName();
            String lastName = dto.getLastName() != null ? dto.getLastName() : user.getName().getLastName();
            user.changeName(UserName.of(firstName, lastName));
        }

        if (dto.getEmail() != null) {
            Email newEmail = Email.of(dto.getEmail());
            if (!newEmail.equals(user.getEmail())) {
                user = userDomainService.updateUserEmail(user.getId(), newEmail, user.getCompanyId());
            }
        }

        User savedUser = userRepository.save(user);
        return toResponseDto(savedUser);
    }

    public void activateUser(UUID userId) {
        User user = userDomainService.activateUser(UserId.fromUUID(userId));
        userRepository.save(user);
    }

    public void suspendUser(UUID userId) {
        User user = userDomainService.suspendUser(UserId.fromUUID(userId));
        userRepository.save(user);
    }

    public void terminateUser(UUID userId) {
        User user = userDomainService.terminateUser(UserId.fromUUID(userId));
        userRepository.save(user);
    }

    public void deleteUser(UUID userId) {
        User user = userRepository.findById(UserId.fromUUID(userId))
                .orElseThrow(() -> new UserNotFoundException(UserId.fromUUID(userId)));
        userRepository.delete(user);
    }

    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
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
}