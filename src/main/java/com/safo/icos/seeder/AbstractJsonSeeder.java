package com.safo.icos.seeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safo.icos.seeder.exception.SeedDataLoadingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
public abstract class AbstractJsonSeeder<T> implements TableSeeder {

  protected final ObjectMapper objectMapper;
  protected final String jsonPath;
  protected final Class<T[]> dtoClass;
  protected static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  protected static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

  protected AbstractJsonSeeder(String jsonPath, Class<T[]> dtoClass) {
    this.objectMapper = new ObjectMapper();
    this.jsonPath = jsonPath;
    this.dtoClass = dtoClass;
  }

  protected List<T> loadDtos() {
    try {
      ClassPathResource resource = new ClassPathResource(jsonPath);
      if (!resource.exists()) {
        log.warn("Seed file not found: {}", jsonPath);
        return Collections.emptyList();
      }
      try (InputStream is = resource.getInputStream()) {
        T[] array = objectMapper.readValue(is, dtoClass);
        return Arrays.asList(array);
      }
    } catch (IOException e) {
      throw new SeedDataLoadingException("Failed to load seed data from " + jsonPath, e);
    }
  }

  protected LocalDateTime parseDateTime(String dateTimeStr) {
    if (dateTimeStr == null || dateTimeStr.isBlank()) {
      return LocalDateTime.now();
    }
    try {
      return LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
    } catch (Exception e) {
      log.warn("Failed to parse date-time: {}, using now", dateTimeStr);
      return LocalDateTime.now();
    }
  }

  protected LocalDate parseDate(String dateStr) {
    if (dateStr == null || dateStr.isBlank()) {
      return null;
    }
    try {
      return LocalDate.parse(dateStr, dateFormatter);
    } catch (Exception e) {
      log.warn("Failed to parse date: {}, using null", dateStr);
      return null;
    }
  }

  protected UUID parseUuid(String uuidStr) {
    if (uuidStr == null || uuidStr.isBlank()) {
      return UUID.randomUUID();
    }
    try {
      return UUID.fromString(uuidStr);
    } catch (Exception e) {
      log.warn("Failed to parse UUID: {}, using random", uuidStr);
      return UUID.randomUUID();
    }
  }
}

