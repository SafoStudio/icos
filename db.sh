#!/bin/bash

# Database configuration
export DB_URL=jdbc:postgresql://localhost:5435/icos_db
export DB_USER=safo
export DB_PASSWORD=password

ACTION=$1

case "$ACTION" in
    clean)
        echo "Cleaning database..."
        ./mvnw flyway:clean -Dflyway.url=$DB_URL -Dflyway.user=$DB_USER -Dflyway.password=$DB_PASSWORD -Dflyway.cleanDisabled=false
        ;;
    repair)
        echo "Repairing flyway metadata..."
        ./mvnw flyway:repair -Dflyway.url=$DB_URL -Dflyway.user=$DB_USER -Dflyway.password=$DB_PASSWORD
        ;;
    migrate)
        echo "Running migrations..."
        ./mvnw flyway:migrate -Dflyway.url=$DB_URL -Dflyway.user=$DB_USER -Dflyway.password=$DB_PASSWORD
        ;;
    *)
        echo "Usage: $0 {clean|repair|migrate}"
        exit 1
        ;;
esac
