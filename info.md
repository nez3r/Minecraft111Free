This is a custom Minecraft 1.1 Free version with integrated horror mechanics, built using the original Minecraft 1.1 source code modified with security vulnerability demonstrations:

### Core Components

**Minecraft Client (minecraft/)**
- Modified from Minecraft 1.1 source code (src/net/minecraft/src/)
- Custom horror features integrated into the core game loop
- Uses RetroMCP compilation system
- Entity404 stalker system (minecraft/src/net/minecraft/src/PlayerController.java, etc.)
- Screamer triggers, world glitches, exit traps
- Custom dimension (ID 66) with bedrock tunnel
- Window-level effects and game-breaking mechanics

**Build System**
- Uses RetroMCP-Java-CLI.jar for compilation
- `recompile.bat` script in project root
- Configuration in `options.cfg`
- Extracts and repacks classes into JAR files

## Common Development Tasks

### Building the Project
```batch
cd C:\Users\nez3r\Desktop\vers\minecraft1_1_1
recompile.bat
