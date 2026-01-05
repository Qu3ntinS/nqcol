# nqcol

A client-side Minecraft Fabric mod for Minecraft 1.21.4 that automates certain actions with configurable, human-like delays.

## Introduction

**nqcol** is a lightweight, client-side automation mod designed to replicate the functionality of AutoHotkey scripts within Minecraft. The mod provides two main automation features:

- **AutoHit**: Automatically performs attack actions with realistic, gaussian-distributed delays and jitter to simulate human-like clicking patterns
- **AutoPress**: Automatically presses configurable keys at set intervals, perfect for automating repetitive tasks

The mod is built with a focus on **human-like behavior** - using statistical distributions (gaussian delays with jitter) rather than fixed intervals, making automation patterns less detectable. All features are fully configurable through an in-game settings menu (Mod Menu integration) or by editing the config file directly.

**Important**: This mod automates player actions and may violate server rules or Terms of Service. Always check server policies before using automation mods. Use responsibly and at your own risk.

## Features

### AutoHit

- Repeatedly triggers a single left-click/attack action
- Uses a gaussian delay distribution (5000-5500ms) with jitter (±80ms)
- Toggle on/off with a keybind (default: H)

### AutoPress

- Repeatedly presses a configurable key (default: 'O')
- Uses uniform random delay (30000-30500ms)
- Toggle on/off with a keybind (default: P)

## Requirements

- **Minecraft**: 1.21.4
- **Fabric Loader**: 0.16.9 or higher
- **Fabric API**: Required
- **Cloth Config**: Required (for configuration screen)
- **Mod Menu**: Optional (for in-game config access, but mod works without it)

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.4
2. Download and install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download and install [Cloth Config](https://modrinth.com/mod/cloth-config)
4. (Optional) Download and install [Mod Menu](https://modrinth.com/mod/modmenu) for in-game configuration
5. Download the latest `nqcol` jar from releases
6. Place the jar in your `mods` folder
7. Launch Minecraft

## Building

To build the mod from source:

```bash
./gradlew build
```

The built jar will be located at:

```
build/libs/nqcol-1.0.0.jar
```

## Usage

### Keybinds

- **H** - Toggle AutoHit on/off
- **P** - Toggle AutoPress on/off

When toggled, you'll see a chat message indicating the new state (ON/OFF).

### Configuration

If you have Mod Menu installed:

1. Open the Mods menu (Mod Menu button in main menu or pause menu)
2. Find "nqcol" and click the Config button
3. Adjust settings as needed

If you don't have Mod Menu:

- Configuration file is located at: `.minecraft/config/nqcol.json`
- Edit it manually (restart game for changes to take effect)

### Configuration Options

#### AutoHit

- **Enabled**: Enable/disable AutoHit automation
- **Min Delay (ms)**: Minimum delay between attacks (default: 5000)
- **Max Delay (ms)**: Maximum delay between attacks (default: 5500)
- **Jitter (ms)**: Random jitter added to delay (default: 80)

#### AutoPress

- **Enabled**: Enable/disable AutoPress automation
- **Min Delay (ms)**: Minimum delay between key presses (default: 30000)
- **Max Delay (ms)**: Maximum delay between key presses (default: 30500)
- **Key to Press**: GLFW key code of the key to press (default: 79 = 'O')

#### General

- **Only Run In Game**: Only run automation when no menus are open (default: true)

## How It Works

### AutoHit

- Uses a Box-Muller transform to generate gaussian-distributed delays
- Mean delay: (min + max) / 2
- Standard deviation: (max - min) / 6
- Adds uniform random jitter in the range [-jitter, +jitter]
- Simulates a single attack by pressing the attack key for one tick

### AutoPress

- Uses uniform random distribution between min and max delay
- Simulates a key press by finding the matching KeyBinding and triggering it

### Safety Features

- Only runs when world and player exist
- Respects "Only Run In Game" setting (won't run with menus open)
- Non-blocking implementation (uses tick-based scheduler, no while loops)
- Single action per cycle (not continuous holding)

## Technical Details

- **Mod ID**: `nqcol`
- **Package**: `de.nq.nqcol`
- **Client-side only**: Yes
- **Java Version**: 21
- **Build Tool**: Gradle with Fabric Loom

## Important Notice

**Use this mod responsibly and only where permitted.**

- This mod automates player actions, which may violate server rules or Terms of Service
- Always check server rules before using automation mods
- Use at your own risk
- The authors are not responsible for any consequences of using this mod

## License

MIT License - see LICENSE file for details

## Credits

- Built for Minecraft 1.21.4 using Fabric
- Uses Cloth Config for configuration UI
- Optional Mod Menu integration
