# In-Game Commands Reference

This document describes all available in-game commands for the Minecraft 1.1.1 Horror Edition.

## Table of Contents
- [System Commands](#system-commands)
- [Debug Commands](#debug-commands)
- [Effect Commands](#effect-commands)
- [Event List](#event-list)
- [Command History](#command-history)

---

## System Commands

### `/safe`
**Description:** Disables all horror effects and enters safe mode.

**Usage:**
```
/safe
```

**Effects:**
- Stops all active horror effects
- Resets fog, blood time, sounds, and visual glitches
- Restores monitor gamma to normal
- Disables Unknown.dll effects
- Calls `HorrorEffects.resetAll()` to clear all states
- Displays confirmation message

**Example:**
```
> /safe
Safe mode enabled.
Good luck!
```

---

### `/x<multiplier>`
**Description:** Changes the speed multiplier for horror effect intervals.

**Usage:**
```
/x<number>
```

**Parameters:**
- `number` - Speed multiplier (0.1 to 100)
  - `1.0` = Normal speed (3-8 minutes between effects)
  - `10.0` = 10x faster (18-48 seconds between effects)
  - `100.0` = 100x faster (1.8-4.8 seconds between effects)
  - `0.5` = Half speed (6-16 minutes between effects)

**Examples:**
```
/x1          → Normal speed
/x10         → 10x faster (testing mode)
/x100        → 100x faster (extreme mode)
/x0.5        → Half speed (slower gameplay)
```

**Output:**
```
> /x10
Horror speed set to x10
Effects will appear 10.0x faster
```

---

### `/mstinfo`
**Description:** Displays detailed information about the horror system state.

**Usage:**
```
/mstinfo
```

**Information Displayed:**
- **Stage** - Current horror stage (Stage 1-4)
- **Speed** - Current speed multiplier
- **Next effect in** - Time until next effect (minutes:seconds)
- **Next effect** - Name of the upcoming effect
- **Repeats** - Current/maximum repeats (e.g., 2/3)
- **Total triggered** - Total number of effects triggered

**Example Output:**
```
> /mstinfo
=== Monster Info ===
Stage: Stage 2 (Medium)
Speed: x10
Next effect in: 2m 34s
Next effect: Behind You Glitch
Repeats: 1/3
Total triggered: 12
```

---

## Debug Commands

### `/event <id>`
**Description:** Manually triggers a specific horror effect by ID.

**Usage:**
```
/event <number>
```

**Parameters:**
- `number` - Event ID (0-50)

**Examples:**
```
/event 19    → Trigger Screen Melt effect
/event 30    → Spawn Phantom Observer
/event 50    → Spawn Bedrock Tunnel (FINALE)
```

**Output:**
```
> /event 19
Triggered event #19
```

---

### `powershell wininit`
**Description:** ⚠️ **DANGEROUS** - Triggers Windows BSOD.

**Warning:** This command is for testing purposes ONLY. Use in Virtual Machine ONLY!

**Effects:**
- Creates `STAYAWAY.txt` on desktop
- Executes Windows BSOD trigger
- **WILL CRASH YOUR SYSTEM**

**Safety:**
- NEVER use on production machine
- ONLY test in isolated VM
- Included for horror game authenticity

---

## Command History

### Arrow Key Navigation
**NEW FEATURE:** Navigate through command history using arrow keys.

**Controls:**
- **↑ (Arrow Up)** - Previous command (older)
- **↓ (Arrow Down)** - Next command (newer)

**Features:**
- Saves up to 50 commands
- Automatically ignores duplicate commands
- Preserves current typing when navigating history
- Works with all commands starting with `/` or `powershell`

**Usage Example:**
```
1. Type: /event 19
2. Press Enter
3. Open chat again
4. Press ↑ → Shows: /event 19
5. Press ↑ again → Shows previous command
6. Press ↓ → Returns to /event 19
7. Press ↓ again → Returns to empty input
```

---

## Event List

### Basic Effects (0-9)

All basic effects are implemented in the centralized `HorrorEffects.java` class.

| ID | Effect Name | Description | Implementation |
|----|-------------|-------------|----------------|
| 0 | Footstep Echo | Stone/wood footstep sound plays 0.5-1s after player stops | `HorrorEffects.triggerFootstepEcho()` |
| 1 | Minor Inventory Glitch | Random item name glitches (3s duration) | `HorrorEffects.triggerInventoryGlitchMinor()` |
| 2 | Major Inventory Glitch | Phantom items appear in slots (5s duration) | `HorrorEffects.triggerInventoryGlitchMajor()` |
| 3 | Ambient Sound | Plays ambient cave sound with lowered pitch | `HorrorEffects.playAmbientSound()` |
| 4 | Low Hum | Plays low-frequency hum (0.3 pitch) | `HorrorEffects.playLowHum()` |
| 5 | Slight Fog | Increases fog slightly (0.7x multiplier, 5s) | `HorrorEffects.increaseFogSlightly()` |
| 6 | Moderate Fog | Moderate fog increase (0.4x multiplier, 8s) | `HorrorEffects.triggerModerateFog()` |
| 7 | Heavy Fog | Heavy fog - 2-3 block visibility (0.15x, 10s) | `HorrorEffects.triggerHeavyFog()` |
| 8 | Blood Time Cycle | Fixes time at midnight + red sky tint (15s) | `HorrorEffects.triggerBloodTime()` |
| 9 | Behind You Glitch | Changes blocks behind player (torches/leaves/bricks) | `HorrorEffects.checkBehindYouGlitch()` |

---

### Unknown.dll Effects (10-23)

Native Windows effects via JNA interface to Unknown.dll.

| ID | Effect Name | Description | DLL Function |
|----|-------------|-------------|--------------|
| 10 | Hardware Beep (37Hz) | Low-frequency system beep (500ms) | `SystemBeepHardware(37, 500)` |
| 11 | Hardware Beep (4000Hz) | High-frequency system beep (500ms) | `SystemBeepHardware(4000, 500)` |
| 12 | Window Jitter (10px) | Window shakes 10 pixels (2s) | `WindowPhysicalJitter(10, 2000)` |
| 13 | Window Jitter (20px) | Window shakes 20 pixels (3s) | `WindowPhysicalJitter(20, 3000)` |
| 14 | Cursor Pull (Weak) | Cursor attracted to (400,300) @ 30% (3s) | `CursorPossession(400, 300, 30, 3000)` |
| 15 | Cursor Pull (Strong) | Cursor attracted to (200,200) @ 60% (4s) | `CursorPossession(200, 200, 60, 4000)` |
| 16 | Gamma Corrupt (Red) | Monitor gamma → red monochrome (2s) | `GammaRampCorruptor(0, 2000)` |
| 17 | Gamma Corrupt (B/W) | Monitor gamma → high contrast B&W (2s) | `GammaRampCorruptor(1, 2000)` |
| 18 | Gamma Corrupt (Dark Red) | Monitor gamma → dark red (2s) | `GammaRampCorruptor(2, 2000)` |
| 19 | Screen Melt | GDI screen melting effect (1.5s, intensity 8) | `GDI_ScreenMelt(1500, 8)` |
| 20 | Ghost Overlay | Transparent ghost overlay (2s) | `DesktopOverlayGhost(2000)` |
| 21 | Taskbar Aggression | Taskbar flashing + error sounds (5s) | `TaskbarAggression(5000)` |
| 22 | Clipboard Whisper | Replaces clipboard with coordinates | `ClipboardWhisper("X:... Y:... Z:...")` |
| 23 | Test MessageBox | Shows "unknown" MessageBox (DLL test) | `TestMessageBox()` |

---

### Entity Spawns (30-31)

| ID | Effect Name | Description | Implementation |
|----|-------------|-------------|----------------|
| 30 | Spawn Phantom Observer | Spawns silent stalker entity that despawns when close | `HorrorEffects.spawnPhantomObserver()` |
| 31 | Spawn Mirror Double | Spawns player doppelganger with 2-3s delay | `HorrorEffects.spawnMirrorDouble()` |

---

### Advanced Effects (35-44)

| ID | Effect Name | Description | Implementation |
|----|-------------|-------------|----------------|
| 35 | Partisan Interference | Breaks torches, opens doors in adjacent chunks | `HorrorEffects.triggerPartisanInterference()` |
| 36 | Disappearing Anomalies | Generates structures that vanish when far | `HorrorEffects.generateDisappearingAnomaly()` |
| 37 | Dynamic Window Title | Changes window title to glitched coordinates | `HorrorEffects.triggerGlitchedWindowTitle()` |
| 38 | Fake Frame Freeze | Freezes render 1-2s + teleports player | `HorrorEffects.triggerFakeFrameFreeze()` |
| 39 | Violent Shake | Screen shake effect | `HorrorEffects.triggerViolentShake()` |
| 40 | Immediate Glitches | Triggers GlitchManager combo (x10) | `GlitchManager.triggerImmediateGlitches(10.0F)` |

---

### Combo Effects (45-50)

| ID | Effect Name | Effects Included | Purpose |
|----|-------------|------------------|---------|
| 45 | Light Combo | Footstep + Minor Glitch + Beep (37Hz) | Testing light effects |
| 46 | Medium Combo | Fog + Behind You + Window Jitter | Testing medium effects |
| 47 | Heavy Combo | Blood Time + Heavy Fog + Gamma (Red) | Testing heavy effects |
| 48 | Extreme Combo | Screen Melt + Ghost Overlay + Violent Shake | Testing extreme effects |
| 49 | Entity Combo | Spawns both Phantom Observer + Mirror Double | Testing entity spawns |
| 50 | **FINALE: BEDROCK TUNNEL** | **Spawns bedrock tunnel near player** | **GAME FINALE EVENT** |

---

## New Architecture

### Centralized Horror Effects

All in-game horror effects (except Unknown.dll) are now unified in a single class:

**`HorrorEffects.java`** - Centralized effects manager
- ✅ All 14 game effects in one place
- ✅ Simple static methods
- ✅ Unified state management
- ✅ Easy to debug and maintain

**Integration:**
```java
// Old (fragmented):
FootstepEcho.triggerEcho();
InventoryGlitch.triggerMinorGlitch();
DynamicFogHandler.triggerHeavyFog();

// New (unified):
HorrorEffects.triggerFootstepEcho(player);
HorrorEffects.triggerInventoryGlitchMinor();
HorrorEffects.triggerHeavyFog();
```

---

## Usage Examples

### Testing Individual Effects
```bash
# Test footstep echo
/event 0

# Test inventory glitch
/event 2

# Test fog effects
/event 5    # Slight
/event 6    # Moderate
/event 7    # Heavy

# Test blood time
/event 8

# Test behind you glitch
/event 9
```

### Testing Unknown.dll Effects
```bash
# Test DLL is loaded
/event 23   # Should show MessageBox "unknown"

# Test screen effects
/event 19   # Screen melt
/event 20   # Ghost overlay

# Test cursor possession
/event 14   # Weak
/event 15   # Strong

# Test gamma corruption
/event 16   # Red
/event 17   # B/W
/event 18   # Dark Red
```

### Testing Entity Spawns
```bash
# Spawn phantom observer
/event 30

# Spawn mirror double
/event 31

# Spawn both entities
/event 49
```

### Testing Advanced Effects
```bash
# Test partisan interference
/event 35

# Test disappearing anomalies
/event 36

# Test window title glitch
/event 37

# Test fake frame freeze
/event 38
```

### Testing Combos
```bash
# Light combo (safe testing)
/event 45

# Medium combo
/event 46

# Heavy combo
/event 47

# Extreme combo (intense)
/event 48

# Entity combo
/event 49

# FINALE - Bedrock Tunnel
/event 50
```

### Speed Testing
```bash
# Normal gameplay
/x1

# Quick testing
/x10

# Rapid-fire effects
/x100

# Check current state
/mstinfo
```

### Using Command History
```bash
# Type and execute
/event 19
[Enter]

# Reopen chat, press ↑
[Up Arrow] → /event 19 appears

# Navigate history
[Up Arrow] → Previous command
[Down Arrow] → Next command
[Down Arrow] → Back to empty input

# Quick repeat testing
/event 19
[Enter]
[Chat]
[Up]
[Enter]  # Instant repeat
```

### Recovery
```bash
# Stop all effects
/safe

# Check system state
/mstinfo

# Re-enable effects with speed
/x10
```

---

## Effect Timing System

The horror system uses a randomized timing mechanism:

**Default Behavior:**
- **Interval:** 3-8 minutes between effects (randomized each trigger)
- **Repeats:** Each effect repeats 1-3 times (randomized)
- **Stages:** Progressive difficulty (Stage 1 → Stage 4)

**With Speed Multiplier:**
```
Actual Interval = (3-8 minutes) / multiplier

Examples:
/x1   → 3-8 minutes (180-480 seconds)
/x10  → 18-48 seconds
/x100 → 1.8-4.8 seconds
/x0.5 → 6-16 minutes
```

**Stage Progression:**
- **Stage 1 (Weak):** First 4 effects
  - Basic sounds, minor glitches, weak DLL effects
- **Stage 2 (Medium):** Next 4 effects (total 8)
  - Visual glitches, fog, medium cursor pull
- **Stage 3 (Strong):** Next 4 effects (total 12)
  - Blood time, gamma corruption, entity spawns
- **Stage 4 (Extreme):** After 12+ effects
  - Screen melt, all native effects, max intensity

**Repeat Behavior:**
```
Effect triggers → Plays 1-3 times → Next effect
Example:
- Footstep Echo triggers
- Plays 2 times (random: 1-3)
- After 3-8 minutes → Next effect
- Behind You Glitch triggers
- Plays 1 time (random: 1-3)
- After 3-8 minutes → Next effect
...
```

---

## Command Priority

Commands are processed in this order:

1. `/safe` - Highest priority, immediate stop
2. `/mstinfo` - System information
3. `/event <n>` - Manual effect trigger
4. `/x<n>` - Speed multiplier
5. `powershell wininit` - BSOD trigger (DANGEROUS)

**Command History:**
- Arrow keys work in any command context
- History persists across chat sessions
- Maximum 50 commands stored
- Duplicates automatically filtered

---

## Tips & Best Practices

### For Testing
1. ✅ Start with `/event 23` to verify Unknown.dll is loaded
2. ✅ Use `/x10` for quick effect testing
3. ✅ Use `/event <n>` to test individual effects
4. ✅ Always have `/safe` ready to stop effects
5. ✅ Use `/mstinfo` to track system state
6. ✅ Use **↑/↓ arrows** to repeat commands quickly
7. ✅ Test basic effects (0-9) before DLL effects (10-23)
8. ✅ Test combos (45-49) after individual effects work

### For Gameplay
1. ✅ Start with `/x1` for intended experience
2. ✅ Use `/x0.5` for slower, more atmospheric gameplay
3. ✅ Never use `/x100` unless you want chaos
4. ✅ `/safe` is your panic button
5. ✅ Watch `/mstinfo` to know what's coming
6. ✅ Repeats make effects less predictable (1-3 times each)

### For Development
1. ✅ All game effects now in `HorrorEffects.java`
2. ✅ Unknown.dll effects in `UnknownEffects.java` (JNA)
3. ✅ Test each effect individually first (`/event 0-40`)
4. ✅ Test combos after individual effects work (`/event 45-50`)
5. ✅ Verify Unknown.dll is loaded (`/event 23`)
6. ✅ Check system state frequently (`/mstinfo`)
7. ✅ Always test BSOD trigger in VM only
8. ✅ Use arrow keys to repeat tests quickly

---

## Troubleshooting

### Unknown.dll not loading
**Symptoms:** Native effects (10-23) don't work, event 23 fails

**Solutions:**
1. Check console for `[Unknown.dll] Loaded successfully`
2. Verify DLL exists: `libraries/natives/Unknown.dll`
3. Test with `/event 23` (should show MessageBox "unknown")
4. Rebuild DLL: `cd minecraft/dll && make clean && make && make install`
5. Check DLL size: should be ~52KB
6. Verify JNA library is in classpath

**Console Messages:**
```
✅ [Unknown.dll] Loading from: C:\...\libraries\natives\Unknown.dll
✅ [Unknown.dll] Loaded successfully

❌ [Unknown.dll] Failed to load: ...
❌ [Unknown.dll] Not found in any expected location
```

### Game effects not triggering
**Symptoms:** Effects 0-9, 30-40 don't work

**Solutions:**
1. Check console: `[HorrorEffectsManager] Cannot trigger effect: player or world is null`
2. Ensure you're in a loaded world (not main menu)
3. Verify `HorrorEffects.java` is compiled
4. Check for Java exceptions in console
5. Try different effect IDs to narrow down issue

### Effects not triggering automatically
**Symptoms:** No effects appear during gameplay

**Solutions:**
1. Check `/mstinfo` - verify speed and timer
2. Ensure `/safe` was not activated
3. Check `HorrorState.safeMode = false`
4. Verify interval: should see "Next effect in: Xm Ys"
5. Try `/x10` to speed up testing
6. Check console for errors

### Effects too slow/fast
**Solutions:**
1. Adjust with `/x<n>` command
2. Use `/mstinfo` to verify current speed
3. Default is `/x1` (3-8 minutes intervals)
4. For testing use `/x10` (18-48 seconds)
5. For extreme testing use `/x100` (1.8-4.8 seconds)

### Command history not working
**Symptoms:** Arrow keys don't show previous commands

**Solutions:**
1. Verify you typed a command starting with `/` or `powershell`
2. Commands must be executed (pressed Enter) to be saved
3. History only stores last 50 commands
4. Duplicates are automatically skipped
5. Check `GuiChat.java` is properly compiled

### Event 50 not working
**Symptoms:** `/event 50` doesn't spawn bedrock tunnel

**Solutions:**
1. Ensure you're in a world (not void/superflat)
2. Check console for `WorldGenBedrockTunnel` errors
3. Verify player coordinates are valid
4. Try moving to different location
5. Check Y-level (should work at any height)

---

## File Locations

### Source Code
- **Horror Effects:** `minecraft/src/net/minecraft/src/HorrorEffects.java`
- **Effects Manager:** `minecraft/src/net/minecraft/src/HorrorEffectsManager.java`
- **Unknown.dll Interface:** `minecraft/src/net/minecraft/src/UnknownEffects.java`
- **Chat GUI:** `minecraft/src/net/minecraft/src/GuiChat.java`
- **DLL Source:** `minecraft/dll/main.cpp`

### Build Artifacts
- **Unknown.dll:** `minecraft/libraries/natives/Unknown.dll`
- **Compiled Classes:** `minecraft/bin/net/minecraft/src/`

### Documentation
- **Commands:** `COMMANDS.md` (this file)
- **Architecture:** `CLAUDE.md`
- **Design:** `doc/DESIGN.md`
- **Progress:** `doc/PROGRESS.md`

---

## See Also

- **CLAUDE.md** - Development guide and architecture
- **doc/DESIGN.md** - Feature design documentation
- **doc/PROGRESS.md** - Implementation progress
- **minecraft/dll/main.cpp** - Unknown.dll source code
- **minecraft/src/net/minecraft/src/HorrorEffects.java** - Centralized effects class

---

## Changelog

### Version 1.2 (2026-09-05)
- ✅ Created centralized `HorrorEffects.java` class
- ✅ Unified all 14 in-game effects into single class
- ✅ Added command history (↑/↓ arrow keys)
- ✅ Fixed event 50: now spawns bedrock tunnel (finale)
- ✅ Updated all documentation
- ✅ Improved effect timing system (3-8 min intervals)
- ✅ Added effect repeat system (1-3 times per effect)

### Version 1.1 (2026-09-04)
- ✅ Integrated Unknown.dll (8 native Windows effects)
- ✅ Added `/mstinfo` command
- ✅ Added `/event <n>` command (0-50 effects)
- ✅ Progressive staging system (Stage 1-4)
- ✅ Speed multiplier system `/x<n>`

### Version 1.0 (Initial Release)
- ✅ Basic horror effects
- ✅ `/safe` command
- ✅ Entity spawning (Phantom Observer, Mirror Double)

---

**Last Updated:** 2026-09-05 00:38 UTC  
**Version:** Minecraft 1.1.1 Horror Edition v1.2  
**Status:** Stable - Centralized Architecture


---

## System Commands

### `/safe`
**Description:** Disables all horror effects and enters safe mode.

**Usage:**
```
/safe
```

**Effects:**
- Stops all active horror effects
- Resets fog, blood time, sounds, and visual glitches
- Restores monitor gamma to normal
- Disables Unknown.dll effects
- Displays confirmation message

**Example:**
```
> /safe
Safe mode enabled.
Good luck!
```

---

### `/x<multiplier>`
**Description:** Changes the speed multiplier for horror effect intervals.

**Usage:**
```
/x<number>
```

**Parameters:**
- `number` - Speed multiplier (0.1 to 100)
  - `1.0` = Normal speed (3-8 minutes between effects)
  - `10.0` = 10x faster (18-48 seconds between effects)
  - `100.0` = 100x faster (1.8-4.8 seconds between effects)
  - `0.5` = Half speed (6-16 minutes between effects)

**Examples:**
```
/x1          → Normal speed
/x10         → 10x faster (testing mode)
/x100        → 100x faster (extreme mode)
/x0.5        → Half speed (slower gameplay)
```

**Output:**
```
> /x10
Horror speed set to x10
Effects will appear 10.0x faster
```

---

### `/mstinfo`
**Description:** Displays detailed information about the horror system state.

**Usage:**
```
/mstinfo
```

**Information Displayed:**
- **Stage** - Current horror stage (Stage 1-4)
- **Speed** - Current speed multiplier
- **Next effect in** - Time until next effect (minutes:seconds)
- **Next effect** - Name of the upcoming effect
- **Repeats** - Current/maximum repeats (e.g., 2/3)
- **Total triggered** - Total number of effects triggered

**Example Output:**
```
> /mstinfo
=== Monster Info ===
Stage: Stage 2 (Medium)
Speed: x10
Next effect in: 2m 34s
Next effect: Behind You Glitch
Repeats: 1/3
Total triggered: 12
```

---

## Debug Commands

### `/event <id>`
**Description:** Manually triggers a specific horror effect by ID.

**Usage:**
```
/event <number>
```

**Parameters:**
- `number` - Event ID (0-50)

**Examples:**
```
/event 19    → Trigger Screen Melt effect
/event 30    → Spawn Phantom Observer
/event 50    → Trigger FINAL COMBO (all effects)
```

**Output:**
```
> /event 19
Triggered event #19
```

---

### `powershell wininit`
**Description:** ⚠️ **DANGEROUS** - Triggers Windows BSOD.

**Warning:** This command is for testing purposes ONLY. Use in Virtual Machine ONLY!

**Effects:**
- Creates `STAYAWAY.txt` on desktop
- Executes Windows BSOD trigger
- **WILL CRASH YOUR SYSTEM**

**Safety:**
- NEVER use on production machine
- ONLY test in isolated VM
- Included for horror game authenticity

---

## Event List

### Basic Effects (0-9)

| ID | Effect Name | Description |
|----|-------------|-------------|
| 0 | Footstep Echo | Stone/wood footstep sound plays after player stops |
| 1 | Minor Inventory Glitch | Random item name glitches |
| 2 | Major Inventory Glitch | Phantom items appear in slots |
| 3 | Ambient Sound | Plays ambient cave sound |
| 4 | Low Hum | Plays low-frequency hum |
| 5 | Slight Fog | Increases fog slightly |
| 6 | Moderate Fog | Moderate fog increase |
| 7 | Heavy Fog | Heavy fog (2-3 block visibility) |
| 8 | Blood Time Cycle | Midnight + red sky tint |
| 9 | Behind You Glitch | Blocks change when looking away |

---

### Unknown.dll Effects (10-23)

| ID | Effect Name | Description |
|----|-------------|-------------|
| 10 | Hardware Beep (37Hz) | Low-frequency system beep |
| 11 | Hardware Beep (4000Hz) | High-frequency system beep |
| 12 | Window Jitter (10px) | Window shakes 10 pixels |
| 13 | Window Jitter (20px) | Window shakes 20 pixels |
| 14 | Cursor Pull (Weak) | Cursor attracted to point (30% strength) |
| 15 | Cursor Pull (Strong) | Cursor attracted to point (60% strength) |
| 16 | Gamma Corrupt (Red) | Monitor gamma → red monochrome |
| 17 | Gamma Corrupt (B/W) | Monitor gamma → high contrast B&W |
| 18 | Gamma Corrupt (Dark Red) | Monitor gamma → dark red |
| 19 | Screen Melt | GDI screen melting effect |
| 20 | Ghost Overlay | Transparent ghost overlay (2s) |
| 21 | Taskbar Aggression | Taskbar flashing + error sounds |
| 22 | Clipboard Whisper | Replaces clipboard with coordinates |
| 23 | Test MessageBox | Shows "unknown" MessageBox (DLL test) |

---

### Entity Spawns (30-31)

| ID | Effect Name | Description |
|----|-------------|-------------|
| 30 | Spawn Phantom Observer | Spawns silent stalker entity |
| 31 | Spawn Mirror Double | Spawns player doppelganger |

---

### Advanced Effects (35-40)

| ID | Effect Name | Description |
|----|-------------|-------------|
| 35 | Partisan Interference | Breaks torches, opens doors in adjacent chunks |
| 36 | Disappearing Anomalies | Generates structures that vanish |
| 37 | Dynamic Window Title | Changes window title to glitched text |
| 38 | Fake Frame Freeze | Freezes render + teleports player |
| 39 | Violent Shake | Screen shake effect |
| 40 | Immediate Glitches | Triggers GlitchManager combo (x10) |

---

### Combo Effects (45-50)

| ID | Effect Name | Effects Included |
|----|-------------|------------------|
| 45 | Light Combo | Footstep + Minor Glitch + Beep (37Hz) |
| 46 | Medium Combo | Fog + Behind You + Window Jitter |
| 47 | Heavy Combo | Blood Time + Heavy Fog + Gamma (Red) |
| 48 | Extreme Combo | Screen Melt + Ghost Overlay + Violent Shake |
| 49 | Entity Combo | Spawns both Phantom Observer + Mirror Double |
| 50 | **FINAL COMBO** | **ALL EFFECTS SIMULTANEOUSLY** (equivalent to `/x100`) |

---

## Usage Examples

### Testing Individual Effects
```bash
# Test screen melt effect
/event 19

# Test cursor possession
/event 15

# Test gamma corruption
/event 16
```

### Speed Testing
```bash
# Normal gameplay
/x1

# Quick testing
/x10

# Rapid-fire effects
/x100

# Check current state
/mstinfo
```

### Entity Testing
```bash
# Spawn phantom observer
/event 30

# Spawn mirror double
/event 31

# Spawn both entities
/event 49
```

### Combo Testing
```bash
# Test light effects
/event 45

# Test medium effects
/event 46

# Test extreme effects
/event 48

# Nuclear option (all effects)
/event 50
```

### Recovery
```bash
# Stop all effects
/safe

# Check system state
/mstinfo
```

---

## Effect Timing System

The horror system uses a randomized timing mechanism:

**Default Behavior:**
- **Interval:** 3-8 minutes between effects (randomized)
- **Repeats:** Each effect repeats 1-3 times (randomized)
- **Stages:** Progressive difficulty (Stage 1 → Stage 4)

**With Speed Multiplier:**
```
Actual Interval = (3-8 minutes) / multiplier

Examples:
/x1   → 3-8 minutes (180-480 seconds)
/x10  → 18-48 seconds
/x100 → 1.8-4.8 seconds
/x0.5 → 6-16 minutes
```

**Stage Progression:**
- **Stage 1 (Weak):** First 4 effects - Basic sounds, minor glitches
- **Stage 2 (Medium):** Next 4 effects - Visual glitches, fog, cursor pull
- **Stage 3 (Strong):** Next 4 effects - Blood time, gamma corruption, entities
- **Stage 4 (Extreme):** After 12+ effects - Screen melt, all native effects

---

## Command Priority

Commands are processed in this order:

1. `/safe` - Highest priority, immediate stop
2. `/mstinfo` - System information
3. `/event <n>` - Manual effect trigger
4. `/x<n>` - Speed multiplier
5. `powershell wininit` - BSOD trigger (DANGEROUS)

---

## Tips & Best Practices

### For Testing
1. Use `/x10` for quick effect testing
2. Use `/event <n>` to test individual effects
3. Always have `/safe` ready to stop effects
4. Use `/mstinfo` to track system state
5. Test Unknown.dll effects with `/event 23` first

### For Gameplay
1. Start with `/x1` for intended experience
2. Use `/x0.5` for slower, more atmospheric gameplay
3. Never use `/x100` unless you want chaos
4. `/safe` is your panic button

### For Development
1. Test each effect individually first (`/event 0-40`)
2. Test combos after individual effects work (`/event 45-50`)
3. Verify Unknown.dll is loaded (`/event 23`)
4. Check system state frequently (`/mstinfo`)
5. Always test BSOD trigger in VM only

---

## Troubleshooting

### Unknown.dll not loading
**Symptoms:** Native effects (10-23) don't work

**Solutions:**
1. Check console for `[Unknown.dll] Loaded successfully`
2. Verify DLL exists: `libraries/natives/Unknown.dll`
3. Test with `/event 23` (should show MessageBox)
4. Rebuild DLL: `cd minecraft/dll && make && make install`

### Effects not triggering
**Symptoms:** No effects appear during gameplay

**Solutions:**
1. Check `/mstinfo` - verify speed and timer
2. Ensure `/safe` was not activated
3. Verify `HorrorState.safeMode = false`
4. Check console for errors

### Effects too slow/fast
**Solutions:**
1. Adjust with `/x<n>` command
2. Use `/mstinfo` to verify current speed
3. Default is `/x1` (3-8 minutes)

---

## See Also

- **CLAUDE.md** - Development guide and architecture
- **doc/DESIGN.md** - Feature design documentation
- **doc/PROGRESS.md** - Implementation progress
- **minecraft/dll/main.cpp** - Unknown.dll source code

---

**Last Updated:** 2026-09-05  
**Version:** Minecraft 1.1.1 Horror Edition v1.2
