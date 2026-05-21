package com.safo.icos.common.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Configuration
public class DotEnvConfig {

    private final ConfigurableEnvironment environment;

    public DotEnvConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void loadDotEnv() {
        if (Files.exists(Paths.get(".env"))) {
            log.info("Loading variables from .env file...");
            try {
                Properties props = new Properties();
                props.load(Files.newInputStream(Paths.get(".env")));
                
                Map<String, Object> envMap = new HashMap<>();
                props.forEach((key, value) -> envMap.put(key.toString(), value));
                
                environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envMap));
                log.info("Successfully loaded {} variables from .env", envMap.size());
            } catch (IOException e) {
                log.error("Failed to load .env file", e);
            }
        } else {
            log.warn(".env file not found in project root!");
        }
    }
}
