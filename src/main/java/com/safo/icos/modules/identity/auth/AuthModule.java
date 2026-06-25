package com.safo.icos.modules.identity.auth;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = AuthModule.class)
public class AuthModule {
}
