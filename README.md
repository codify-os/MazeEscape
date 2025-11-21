# CMPT276F25_Group17

A 2D top-down adventure game built with Java Swing.

## Prerequisites

Before building and running the game, ensure you have the following installed:

- **Java Development Kit (JDK):** Version 17 or higher
- **Apache Maven:** Version 3.6.0 or higher

To verify your installation:
```bash
java -version
mvn -version
```

## Building the Project

Navigate to the `phase2` directory and compile the project:

```bash
cd phase2
mvn clean compile
```

This will:
- Clean any previous build artifacts
- Compile all source code in `src/main/java`
- Copy resources from `src/main/resources` to `target/classes`

## Running the Game

After building, run the game with:

```bash
mvn exec:java
```

Or combine building and running:

```bash
mvn clean compile exec:java
```

## Testing the Project

### Running All Tests

Execute the complete test suite (281 tests):

```bash
mvn test
```

### Running Specific Test Classes

Run tests for specific components:

```bash
# Entity tests
mvn test -Dtest="EntityTest"

# Combat system tests
mvn test -Dtest="CombatManagerTest"

# Stats system tests
mvn test -Dtest="HealthComponentTest"

# Tile and pathfinding tests
mvn test -Dtest="PathfinderTest"
```

### Test Coverage Report

After running tests with `mvn test`, a code coverage report is generated at:
```
phase2/target/site/jacoco/index.html
```

Open this file in a web browser to view detailed line and branch coverage metrics.
