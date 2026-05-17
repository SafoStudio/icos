package com.safo.icos.modules.user.controller;

import com.safo.icos.modules.user.dto.UserCreateDTO;
import com.safo.icos.modules.user.entity.User;
import com.safo.icos.modules.user.entity.UserProfile;
import com.safo.icos.modules.user.service.UserProfileService;
import com.safo.icos.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserProfileService profileService;

    @QueryMapping
    public List<User> users() {
        return userService.getAllUsers();
    }

    @QueryMapping
    public User userById(@Argument UUID id) {
        return userService.getUserById(id);
    }

    @QueryMapping
    public UserProfile userProfile(@Argument UUID userId) {
        return profileService.getProfileByUserId(userId);
    }

    @MutationMapping
    public User createUser(@Argument UserCreateDTO input) {
        return userService.createUser(input);
    }

    @MutationMapping
    public User updateProfile(@Argument UUID userId, @Argument UserProfile input) {
        return profileService.updateProfile(userId, input).getUser();
    }

    @SchemaMapping
    public UserProfile profile(User user) {
        try {
            return profileService.getProfileByUserId(user.getId());
        } catch (Exception e) {
            return null;
        }
    }
}
