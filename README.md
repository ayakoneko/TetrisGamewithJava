# 🎮 Tetris Game with JavaFX

An enhanced **Tetris clone** built in **Java** with **JavaFX**, developed as part of the  
7010ICT / 7805ICT Object-Oriented Software Development course.  
The project implements both several features following OOP principles, design patterns, and advanced programming practices.

---

## 📌 Project Overview

This project recreates the classic Tetris experience with a modernized UI and extended gameplay.  
It was developed incrementally across two milestones:

- **Milestone 1**: Core Tetris gameplay with menus, scoring, and basic features.  
- **Milestone 2**: Extended system with advanced features (AI play, multiplayer, server, persistency, etc.).

---

## 🎥 Demo Video

👉 [Demo Video MT1](https://www.youtube.com/shorts/iusKhrHulPc)             👉 [Demo Video MT2](https://www.youtube.com/watch?v=HNqPD5l0_mk&t=5s)

Shows all milestone 1 & 2 features as per marking criteria.

---

## ✨ Features

### Milestone 1 (Core Requirements)
- Splash screen with course & team details
- Main menu (Play, Configurations, High Scores, Exit)
- Configuration screen (field size, level, music, sound, AI mode, extended mode)
- High score screen (top 10, dummy data acceptable)
- Gameplay (10x20 grid, smooth tetromino controls)
- Full row detection and erasure
- Pause/resume and exit confirmation dialogs

### Milestone 2 (Enhanced Features)
- Persistency: Save/load configurations and high scores via JSON
- Dynamic resizing (adjustable field sizes, one/two-player mode)
- Background music & sound effects (toggles with **M** and **S** keys)
- Real-time scoring & high score persistence
- AI Player (achieves ≥ 500 points automatically)
- Networking with **TetrisServer.jar** for external players
- Two-player mode (human vs human, AI vs human, AI vs AI, external vs AI, etc.)
- Design principles applied (SOLID & GRASP)
- Design patterns: Singleton, Factory, State/Command, etc.
- Advanced programming practices: Threads, Streams, Lambdas, CSS, Generics
- Full JUnit testing suite with coverage reports

---

## 🖥️ Controls

### Single Player
| Action        | Key(s) |
|---------------|--------|
| Move Left     | `,` or Left Arrow |
| Move Right    | `.` or Right Arrow |
| Move Down     | `Space` or Down Arrow |
| Rotate        | `L` or Up Arrow |
| Pause / Resume | `P` |
| Toggle Music  | `M` |
| Toggle Sound  | `S` |

### Two Player (Extend Mode)
- **Player 1**: `,` `.` `Space` `L`
- **Player 2**: Arrow keys
  
---

## 🚀 Run the Game

### Requirements
- Java 11+
- JavaFX SDK
- Maven

### Steps
```bash
# Clone repository
git clone https://github.com/ayakoneko/TetrisGamewithJava.git
cd TetrisGamewithJava

# Build with Maven
mvn clean install

# Run the game
mvn javafx:run
```

Or run the prebuilt JAR:
```bash
java -jar target/TetrisJava.jar
```

If using **external players**:
```bash
java -jar TetrisServer.jar
```

---

## 📊 Architecture & Design

- **Architecture**: MVC with clear separation of Model, View, and Controller  
- **Design Principles**: SOLID & GRASP applied for maintainability  
- **Patterns Used**: Singleton (Game Manager), Factory (Tetromino creation), State/Command (UI/game updates), etc.  
- **Advanced Programming**: Threads for responsiveness, JSON for persistence, Streams/Lambdas for cleaner code, CSS for UI styling  

---

## ✅ Testing

- JUnit tests for core methods  
- Parameterized tests and mocks (Mockito) for advanced coverage  

---

## 🤝 Team & Contributions
- [@Sora Won](https://github.com/Sora-Won)
- [@Cedric Alain Jadin](https://github.com/Cedou90)
- [@Jaemin You](https://github.com/JamKorea)
- [@Seungyeon Kim](https://github.com/summerit114)
- [@Ayako Kaneko (this repo)](https://github.com/ayakoneko)  
---

