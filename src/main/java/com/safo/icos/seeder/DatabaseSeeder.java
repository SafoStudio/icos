package com.safo.icos.seeder;

import com.safo.icos.modules.org.infrastructure.repository.CompanyJpaRepository;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "seed"})
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

  private final CompanyJpaRepository companyRepository;
  private final List<TableSeeder> seeders;

  public DatabaseSeeder(CompanyJpaRepository companyRepository, List<TableSeeder> seeders) {
    this.companyRepository = companyRepository;
    this.seeders = seeders;
  }

  @Override
  public void run(String @NonNull ... args) {
    if (companyRepository.count() == 0) {
      log.info("Database is empty. Starting database seeding...");
      seeders.stream()
          .sorted(Comparator.comparingInt(TableSeeder::getOrder))
          .forEach(TableSeeder::seed);
      log.info("Database seeding completed successfully.");
    } else {
      log.info("Database is not empty. Skipping data seeding.");
    }
  }
}
