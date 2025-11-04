# CMPT276F25_Group17

A 2D top-down adventure game built with Java Swing.

## Tech Stack
- **Language:** Java (JDK 24)
- **Build Tool:** Maven 3.9.11
- **GUI Framework:** Java Swing/AWT

## How to Run

```bash
cd phase2
mvn compile exec:java
```

## Game Controls
- **WASD** - Move player
- **SPACE** - Attack enemy (when in range)


### Combat Stats
**Player:**
- 100 HP
- 20 Attack Power
- 5 Defense
- Sword Slash attack (1 tile range, 10% crit, 2x damage)

**Enemy (Pink Slime):**
- 50 HP
- 10 Attack Power
- 2 Defense
- Slime Attack (1 tile range, 5% crit, 1.5x damage)

### Package Structure
```
phase2/
├── Entity/           # Game entities (Player, Enemy, Entity base class)
├── Tile/             # Tile management and rendering
├── UI/               # Game panel and input handling
└── game/
    ├── combat/       # Combat system (interfaces, managers, data)
    └── stats/        # Health and stat components
```
