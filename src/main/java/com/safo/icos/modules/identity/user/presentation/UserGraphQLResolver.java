package com.safo.icos.modules.identity.user.presentation;

import com.safo.icos.modules.identity.user.application.service.UserService;
import com.safo.icos.modules.identity.user.application.dto.ProfileResponseDto;
import com.safo.icos.modules.identity.user.application.dto.ProfileUpdateDto;
import com.safo.icos.modules.identity.user.application.dto.UserResponseDto;
import com.safo.icos.modules.identity.user.application.dto.UserUpdateDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

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
  public UserResponseDto updateUser(@Argument UUID id, @Argument UserUpdateDto input) {
    return userService.updateUser(id, input);
  }

  @MutationMapping
  public ProfileResponseDto updateProfile(@Argument UUID userId, @Argument ProfileUpdateDto input) {
    return userService.updateProfile(userId, input);
  }

  @MutationMapping
  public Boolean deleteUser(@Argument UUID id) {
    userService.deleteUser(id);
    return true;
  }
}
