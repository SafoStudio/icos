package com.safo.icos.modules.user.presentation;

import com.safo.icos.modules.user.application.UserService;
import com.safo.icos.modules.user.application.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class UserGraphQLResolver {

    private final UserService userService;

    @QueryMapping
    public UserResponseDto user(@Argument UUID id) {
        return userService.getUserById(id);
    }

    @QueryMapping
    public List<UserResponseDto> usersByCompany(@Argument UUID companyId) {
        return userService.getUsersByCompanyId(companyId);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument UUID id) {
        userService.deleteUser(id);
        return true;
    }
}