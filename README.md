# SyTP - Better Teleport Plugin


A feature-rich and easy-to-use Minecraft Paper/Purpur server teleport plugin with support for economy systems, teleport delay, particle effects, and more.

## Features

- **Multiple Teleport Methods**
  - `/tpa` - Request to teleport to another player
  - `/tpc` - Request another player to teleport to you
  - `/tpw` - Invite all players on the server to teleport to you

- **Economy System Support**
  - Supports Vault economy system
  - Configurable teleport costs
  - Admin bypass for costs

- **Teleport Delay**
  - Configurable teleport wait time
  - Movement cancels teleport
  - Cooldown system

- **GUI Interface**
  - Visual teleport request management
  - Click to accept/reject

- **Particle Effects**
  - Particle effects on teleport completion
  - Multiple particle types available

- **Highly Configurable**
  - All messages are customizable
  - Toggle features on/off
  - Flexible cost and delay settings

## Requirements

- **Server**: Paper / Purpur 1.21.x
- **Java**: 21 or higher
- **Optional Dependency**: Vault + Economy plugin (e.g., EssentialsX)

## Installation

1. Download the latest version `SyTP-1.0.0.jar`
2. Place the JAR file in your server's `plugins` folder
3. Restart the server or load the plugin
4. Edit `plugins/SyTP/config.yml` to customize settings
5. Execute `/sytp reload` to reload configuration

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/sytp reload` | Reload plugin configuration | `sytp.admin` |
| `/tpa <player>` | Request to teleport to a player | `sytp.tpa` |
| `/tpc <player>` | Request a player to teleport to you | `sytp.tpc` |
| `/tpw` | Invite all players to teleport to you | `sytp.tpw` |
| `/tpaccept` | Accept teleport request | `sytp.accept` |
| `/tpdeny` | Deny teleport request | `sytp.deny` |

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `sytp.admin` | Plugin admin permission | OP |
| `sytp.tpa` | Use /tpa command | Everyone |
| `sytp.tpc` | Use /tpc command | Everyone |
| `sytp.tpw` | Use /tpw command | OP |
| `sytp.accept` | Accept teleport requests | Everyone |
| `sytp.deny` | Deny teleport requests | Everyone |
| `sytp.bypass.cost` | Bypass teleport costs | OP |
| `sytp.bypass.delay` | Bypass teleport delay | OP |

## Configuration

```yaml
# Feature Toggles
enable-teleport-cost: true        # Enable teleport cost
enable-teleport-delay: true       # Enable teleport delay
enable-particle-effect: true      # Enable particle effects

# Cost Settings
teleport-cost: 100.0              # TPA cost
teleport-here-cost: 100.0         # TPC cost
tpw-cost: 500.0                   # TPW cost

# Teleport Settings
teleport-delay-seconds: 3         # Teleport delay (seconds)
request-timeout-seconds: 30       # Request timeout (seconds)
teleport-cooldown-seconds: 10     # Cooldown time (seconds)

# Particle Effects
particle-type: PORTAL             # Particle type
particle-count: 50                # Particle count
```

## Building

```bash
# Build with Maven
mvn clean package

# Or use the included Maven Wrapper
./mvnw clean package
```

The compiled JAR file will be located at `target/SyTP-1.0.0.jar`


## Project Structure

```
SyTP/
├── src/main/java/com/shiyuan/sytp/
│   ├── SyTP.java                 # Main class
│   ├── commands/                 # Command handlers
│   │   ├── SyTPCommand.java
│   │   ├── TPACommand.java
│   │   ├── TPCCommand.java
│   │   ├── TPWCommand.java
│   │   ├── TPAcceptCommand.java
│   │   └── TPDenyCommand.java
│   ├── managers/                 # Managers
│   │   ├── ConfigManager.java
│   │   ├── CooldownManager.java
│   │   ├── MessageManager.java
│   │   ├── ParticleManager.java
│   │   ├── RequestManager.java
│   │   └── TeleportManager.java
│   ├── gui/
│   │   └── GUIListener.java      # GUI listener
│   ├── requests/
│   │   ├── TeleportRequest.java
│   │   └── RequestType.java
│   └── utils/
│       └── TeleportListener.java
├── src/main/resources/
│   ├── plugin.yml               # Plugin config
│   └── config.yml               # Default config
└── pom.xml                      # Maven config
```

