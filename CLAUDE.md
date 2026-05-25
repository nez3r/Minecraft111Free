# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Minecraft 1.1 modding workspace using **RetroMCP** (Minecraft Coder Pack) for decompiling, modifying, and recompiling Minecraft client and server code. The project uses Java and includes both client and server source code with deobfuscated class names.

**Version**: Minecraft 1.1 (released 2012-01-12)
**Main Class (Client)**: `org.mcphackers.launchwrapper.Launch`
**Main Class (Server)**: `net.minecraft.server.MinecraftServer`

## Project Structure

```
minecraft1_1_1/
├── minecraft/              # Client workspace
│   ├── src/               # Decompiled client source code
│   │   ├── net/minecraft/client/Minecraft.java  # Main client class
│   │   └── net/minecraft/src/                   # Game logic (92k+ lines)
│   ├── bin/               # Compiled client classes
│   ├── game/              # Client runtime directory
│   └── .vscode/launch.json # VS Code debug config for client
├── minecraft_server/      # Server workspace
│   ├── src/               # Decompiled server source code
│   │   ├── net/minecraft/server/MinecraftServer.java
│   │   └── net/minecraft/src/                   # Game logic (60k+ lines)
│   ├── bin/               # Compiled server classes
│   ├── game/              # Server runtime directory
│   └── .vscode/launch.json # VS Code debug config for server
├── conf/
│   ├── mappings.tiny      # Obfuscation mappings (tiny v2 format)
│   ├── exceptions.exc     # Exception declarations for methods
│   └── version.json       # Minecraft version metadata and libraries
├── libraries/             # Game dependencies (LWJGL, ASM, etc.)
├── jars/                  # Original Minecraft JARs and assets
├── RetroMCP-Java-GUI.jar  # RetroMCP GUI tool
├── RetroMCP-Java-CLI.jar  # RetroMCP CLI tool
├── recompile.bat          # Build and repack script
└── options.cfg            # RetroMCP configuration
```

## Building and Running

### Build Commands

**Build both client and server:**
```bash
java -jar RetroMCP-Java-CLI.jar build
```

**Build and repack into JARs:**
```bash
./recompile.bat
```

The `recompile.bat` script:
1. Runs `java -jar RetroMCP-Java-CLI.jar build` to compile source code
2. Extracts `minecraft/build/minecraft.zip` and repacks to `C:\Users\nez3r\Desktop\666\Minecraft.jar`
3. Extracts `minecraft_server/build/minecraft_server.zip` and repacks to `C:\Users\nez3r\Desktop\666\server\server.jar`

### Running the Game

**Client (from VS Code):**
- Use the "Minecraft Client" launch configuration in `minecraft/.vscode/launch.json`
- Main class: `org.mcphackers.launchwrapper.Launch`
- VM args: `-Djava.library.path=${workspaceFolder}/../libraries/natives`

**Server (from VS Code):**
- Use the "Minecraft Server" launch configuration in `minecraft_server/.vscode/launch.json`
- Main class: `net.minecraft.server.MinecraftServer`
- VM args: `-Djava.library.path=${workspaceFolder}/../libraries/natives`

**Client (standalone):**
```bash
cd freeminecraft
./play.bat
```

### Testing Changes

After modifying source code:
1. Run `java -jar RetroMCP-Java-CLI.jar build` to compile
2. Run `./recompile.bat` to package the changes
3. Test the modified client/server JARs

## Code Architecture

### Client-Server Split

- **Client code**: `minecraft/src/` - includes rendering, GUI, input handling, and client-side game logic
- **Server code**: `minecraft_server/src/` - includes server networking, world management, and server-side game logic
- **Shared code**: `net/minecraft/src/` exists in both workspaces with overlapping classes (blocks, entities, world generation, etc.)

### Key Packages

**Client-specific** (`minecraft/src/`):
- `net.minecraft.client.Minecraft` - Main game loop, rendering, input
- `net.minecraft.src.Gui*` - All GUI screens and HUD
- `net.minecraft.src.Render*` - Entity and world rendering
- `net.minecraft.src.EntityRenderer` - Camera and rendering pipeline
- `net.minecraft.src.RenderEngine` - Texture management

**Server-specific** (`minecraft_server/src/`):
- `net.minecraft.server.MinecraftServer` - Server main class and game loop
- `net.minecraft.src.ServerConfigurationManager` - Player management
- `net.minecraft.src.NetworkListenThread` - Server networking

**Shared** (both workspaces):
- `net.minecraft.src.Block*` - Block types and behavior
- `net.minecraft.src.Entity*` - Entity logic
- `net.minecraft.src.World*` - World management and chunk loading
- `net.minecraft.src.BiomeGen*` - Biome generation
- `net.minecraft.src.Item*` - Item definitions
- `net.minecraft.src.TileEntity*` - Block entities (chests, furnaces, etc.)

### Obfuscation Mappings

The `conf/mappings.tiny` file contains mappings between:
- **named**: Human-readable deobfuscated names
- **client**: Obfuscated client names (e.g., `aaa`, `field_30001_a`)
- **server**: Obfuscated server names (e.g., `kw`, `func_35059_a`)

Format: `tiny 2 0 named client server`

## Dependencies

All dependencies are defined in `conf/version.json`:
- **LWJGL 2.9.4** - OpenGL bindings and native libraries
- **JInput 2.0.5** - Controller input
- **Paulscode Sound System** - Audio engine
- **ASM 9.9** - Bytecode manipulation
- **launchwrapper 1.2.4** - Launch wrapper for client

Native libraries are in `libraries/natives/` (platform-specific).

## RetroMCP Configuration

Key settings in `options.cfg`:
- `side=ANY` - Build both client and server
- `lang=RUSSIAN` - GUI language
- `outputsrc=true` - Output decompiled source
- `patch=true` - Apply patches during build
- `stripgenerics=false` - Preserve generic types
- `obf=false` - Don't obfuscate output

## Development Notes

- Java version: Requires Java 5+, recommended Java 8 (see `conf/version.json`)
- The codebase uses legacy Java patterns (no generics in many places, old collection APIs)
- Client uses LWJGL 2.x for OpenGL rendering
- Server runs headless but can show GUI with `ServerGUI`
- Both client and server have `bin/` directories with compiled `.class` files
- Original source is preserved in `src_original/` directories
- Build outputs go to `minecraft/build/` and `minecraft_server/build/`

## Common Modifications

When modifying game behavior:
- **Add new blocks**: Extend `Block` class, register in `Block` static initializer
- **Add new items**: Extend `Item` class, register in `Item` static initializer  
- **Modify world generation**: Edit classes in `BiomeGen*` and `BiomeDecorator`
- **Change rendering**: Modify `Render*` classes (client only)
- **Add GUI screens**: Extend `GuiScreen` (client only)
- **Modify server logic**: Edit `MinecraftServer` and related server classes

Always modify both client and server code when changing shared game logic (blocks, entities, world behavior).
