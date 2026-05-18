package com.safo.icos.modules.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.safo.icos.modules.user.domain.repository.UserRepository;
import com.safo.icos.modules.user.domain.service.UserDomainService;
import com.safo.icos.modules.user.application.UserService;
import com.safo.icos.modules.user.infrastructure.mapper.UserMapper;

@Configuration
public class UserModule {

    @Bean
    public UserDomainService userDomainService(UserRepository userRepository) {
        return new UserDomainService(userRepository);
    }

    @Bean
    public UserService userService(UserRepository userRepository,
                                   UserDomainService userDomainService,
                                   UserMapper userMapper) {
        return new UserService(userRepository, userDomainService, userMapper);
    }
}