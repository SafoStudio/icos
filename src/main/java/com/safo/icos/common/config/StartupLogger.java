package com.safo.icos.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupLogger {

  @Value("${server.port:8080}")
  private String port;

  @Value("${spring.graphql.path:/graphql}")
  private String graphqlPath;

  @Value("${spring.graphql.graphiql.path:/graphiql}")
  private String graphiqlPath;

  @EventListener(ApplicationReadyEvent.class)
  public void logStartup() {
    String baseUrl = "http://localhost:" + port;
    log.info("""
            ----------------------------------------------------------
            \t Application is running! Access URLs:
            \t Local:    \t\t{}
            \t GraphQL:  \t{}{}
            \t GraphiQL: \t{}{}
            \t pgAdmin:  \thttp://localhost:5050
            ----------------------------------------------------------
            """, baseUrl, baseUrl, graphqlPath, baseUrl, graphiqlPath);
  }
}
