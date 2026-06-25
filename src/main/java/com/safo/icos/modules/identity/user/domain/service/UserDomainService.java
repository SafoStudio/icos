package com.safo.icos.modules.identity.user.domain.service;

import com.safo.icos.modules.identity.user.domain.exception.EmailAlreadyExistsException;
import com.safo.icos.modules.identity.user.domain.exception.UserNotFoundException;
import com.safo.icos.modules.identity.user.domain.model.Email;
import com.safo.icos.modules.identity.user.domain.model.User;
import com.safo.icos.modules.identity.user.domain.model.UserId;
import com.safo.icos.modules.identity.user.domain.model.UserName;
import com.safo.icos.modules.identity.user.domain.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDomainService {
    private final UserRepository userRepository;

    public User createUser(UserName name, Email email, UUID createdBy) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
        return User.create(name.getFirstName(), name.getLastName(), email, createdBy);
    }

    public User updateStatus(UserId userId, String status) {
        User user = userRepository.findById(userId.getValue())
                .orElseThrow(() -> new UserNotFoundException(userId));

        switch (status.toUpperCase()) {
            case "ACTIVE":
                user.activate();
                break;
            case "SUSPENDED":
                user.suspend();
                break;
            case "TERMINATED":
                user.terminate();
                break;
            default:
                throw new IllegalArgumentException("Invalid status: " + status);
        }
        return user;
    }
}
