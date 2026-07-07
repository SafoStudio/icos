package com.safo.icos.seeder;

/**
 * Interface for database seeder components.
 * <p>
 * Each implementation is responsible for populating a specific table or entity
 * with seed data from JSON files during application startup.
 * </p>
 * <p>
 * Seeders are executed in order defined by {@link #getOrder()} to maintain
 * referential integrity (e.g., companies must be seeded before departments).
 * </p>
 *
 * @see AbstractJsonSeeder
 * @see DatabaseSeeder
 */
public interface TableSeeder {

  /**
   * Executes the seeding logic.
   * <p>
   * Typically loads DTOs from a JSON file, converts them to entity objects,
   * and saves them to the database using the corresponding repository.
   * </p>
   */
  void seed();

  /**
   * Defines the execution order of the seeder.
   * <p>
   * Seeders with lower order values are executed first.
   * This ensures proper handling of foreign key dependencies.
   * </p>
   *
   * @return order value (lower = earlier execution)
   */
  int getOrder();
}
