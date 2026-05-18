package com.safo.icos.modules.user.domain.model;

import com.safo.icos.modules.user.domain.exception.UserStatusTransitionException;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class User {
    private final UserId id;
    private UserName name;
    private Email email;
    private UserStatus status;
    private final UUID companyId;
    private final UUID createdBy;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User(UserId id, UserName name, Email email, UserStatus status,
                 UUID companyId, UUID createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.companyId = companyId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public static User invite(UserName name, Email email, UUID companyId, UUID createdBy) {
        if (companyId == null) {
            throw new IllegalArgumentException("CompanyId is required");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("CreatedBy is required");
        }

        return new User(
                UserId.generate(),
                name,
                email,
                UserStatus.INVITED,
                companyId,
                createdBy,
                LocalDateTime.now()
        );
    }

    public static User reconstitute(UserId id, UserName name, Email email, UserStatus status,
                                    UUID companyId, UUID createdBy, LocalDateTime createdAt) {
        return new User(id, name, email, status, companyId, createdBy, createdAt);
    }

    public void activate() {
        if (status != UserStatus.INVITED) {
            throw new UserStatusTransitionException(status, UserStatus.ACTIVE);
        }
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        if (status != UserStatus.ACTIVE) {
            throw new UserStatusTransitionException(status, UserStatus.SUSPENDED);
        }
        this.status = UserStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void terminate() {
        if (status == UserStatus.DELETED) {
            throw new UserStatusTransitionException(status, UserStatus.DELETED);
        }
        this.status = UserStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeEmail(Email newEmail) {
        if (newEmail == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        this.email = newEmail;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeName(UserName newName) {
        if (newName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }
}