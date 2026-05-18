package com.safo.icos.modules.user.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;
import com.safo.icos.modules.user.application.UserService;
import com.safo.icos.modules.user.application.dto.UserCreateDto;
import com.safo.icos.modules.user.application.dto.UserResponseDto;
import com.safo.icos.modules.user.application.dto.UserUpdateDto;

@Controller
@RequiredArgsConstructor
public class UserGraphQLResolver {

    private final UserService userService;

    @QueryMapping
    public UserResponseDto getUserById(@Argument UUID id) {
        return userService.getUserById(id);
    }

    @QueryMapping
    public UserResponseDto getUserByEmail(@Argument String email, @Argument UUID companyId) {
        return userService.getUserByEmailAndCompanyId(email, companyId);
    }

    @QueryMapping
    public List<UserResponseDto> getUsersByCompanyId(@Argument UUID companyId) {
        return userService.getUsersByCompanyId(companyId);
    }

    @MutationMapping
    public UserResponseDto createUser(@Argument String firstName,
                                      @Argument String lastName,
                                      @Argument String email,
                                      @Argument UUID companyId,
                                      @Argument UUID createdBy) {
        UserCreateDto dto = UserCreateDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .companyId(companyId)
                .build();
        return userService.createUser(dto, createdBy);
    }

    @MutationMapping
    public UserResponseDto updateUser(@Argument UUID id,
                                      @Argument String firstName,
                                      @Argument String lastName,
                                      @Argument String email,
                                      @Argument UUID updatedBy) {
        UserUpdateDto dto = UserUpdateDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
        return userService.updateUser(id, dto, updatedBy);
    }

    @MutationMapping
    public Boolean activateUser(@Argument UUID id) {
        userService.activateUser(id);
        return true;
    }

    @MutationMapping
    public Boolean suspendUser(@Argument UUID id) {
        userService.suspendUser(id);
        return true;
    }

    @MutationMapping
    public Boolean terminateUser(@Argument UUID id) {
        userService.terminateUser(id);
        return true;
    }

    @MutationMapping
    public Boolean deleteUser(@Argument UUID id) {
        userService.deleteUser(id);
        return true;
    }
}