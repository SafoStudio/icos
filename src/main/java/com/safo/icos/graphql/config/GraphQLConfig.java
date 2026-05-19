package com.safo.icos.graphql.config;

import com.safo.icos.graphql.scalar.DateTimeScalar;
import com.safo.icos.graphql.scalar.UuidScalar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(DateTimeScalar.DATE_TIME)
                .scalar(UuidScalar.UUID_SCALAR);
    }
}