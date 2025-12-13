# Mini MapleStory

Java-based 2D Multiplayer Online Role-Playing Game (MMORPG) inspired by MapleStory. Built with pure Java (Swing, Sockets) without external game engines or libraries.

---

## 🚀 Key Features

- **Real-time Multiplayer**: Multiple players can connect and interact in the same world.
- **Login & Signup**: User account management system.
- **Character System**: Choose from different character types.
- **Combat**: Attack monsters and use skills (Q, W, E, R).
- **Chat**: Real-time messaging with other users.
- **Seamless Map Transition**: Move between maps using portals.

## 🛠 Tech Stack

- **Language**: Java
- **GUI**: Java Swing
- **Network**: TCP Sockets
- **Data Format**: Custom JSON Parsing (No external libraries)
- **Concurrency**: `Thread`, `CopyOnWriteArrayList`, `ConcurrentHashMap`

---

## 🏗 Architecture

### Client-Server Model
- **Server**: Manages multiple `ClientHandler` threads, game state (`GameState`), and broadcasts updates via a 60 FPS `GameLoop`.
- **Client**: Handles UI (`GamePanel`), user input (`PlayerInputHandler`), and renders the game (`GameRenderer`) based on state received from the server.

### Project Structure (src)
- **`client/`**: UI, Input Handling, Network Client.
- **`server/`**: Connection Handling, Game Logic, State Management.
- **`common/`**: Shared resources (DTOs, Enums, Item/Monster/Skill Definitions).

---

## 🎮 Getting Started

### 1. Recompile (Required after code changes)

**Mac/Linux (Bash)**
```bash
rm -rf out/* && find src -name "*.java" -print0 | xargs -0 javac -d out -cp src
```

**Windows (Command Prompt)**
```cmd
rmdir /s /q out
mkdir out
dir /s /b src\*.java > sources.txt
javac -d out -cp src @sources.txt
del sources.txt
```

### 2. Run Server
```bash
cd out
java server.GameServer
```

### 3. Run Client
Open a new terminal for each client.
```bash
cd out
java client.GameClient
```

---

## 🕹 Controls

| Key | Action |
|---|---|
| `Arrow Keys` | Move |
| `Space` | Jump |
| `Q`, `W`, `E`, `R` | Use Skills |
| `I` | Toggle Inventory |
| `O` | Toggle Equipment |
| `S` | Toggle Stats |
| `Z` | Pick up Item |
| `Enter` | Chat |
