# Hickz

A **Paper** plugin (Minecraft 1.20.4) that serves as the core plugin of the Hickz server. It combines two game mechanics:

1. **Periodic random item**: every *X* seconds, each online player receives an item picked at random from every item in the game.
2. **Cases**: loot-box style cases that play a scrolling animation in an inventory and award rewards weighted by probability.

> [!WARNING]
> **This project is archived and no longer maintained.** It was a personal project started in May 2024 that stopped at the prototype stage (version `0.0.0`). No further updates, fixes or support are planned. The code is kept here for reference only.

## Purpose

The idea is a game mode where players don't gather resources the usual way: they have to make do with whatever chance gives them, in the spirit of the "Minecraft, but you get a random item every 10 seconds" challenges. Cases add a second, more controlled source of rewards: the server admin chooses their contents, enchantments and odds.

## Features

### Random item drops (`BlockTask`)

- A scheduled task runs every `core.delay` seconds.
- The item is picked from every `Material` that is an item, except those listed in `core.blacklisted-blocks` (air, bedrock, spawner, command blocks, etc.).
- With `core.same: true`, every player receives the **same** item. Otherwise each player gets their own roll.
- Dead players receive nothing.
- Each player gets a translated message, for example "You obtained: Diamond".

### Cases (`cases/`)

- Cases are defined in `cases.yml` and loaded at startup by `CaseLoader`.
- Each item in a case can have a custom name, a description (lore), an amount, enchantments (levels above the vanilla cap are allowed) and a drop chance.
- The chances in a case must add up to exactly `1.0`, or loading fails. The maths uses `BigDecimal` to avoid rounding errors.
- When a case is opened:
  - the reward is rolled **before** the animation starts;
  - a 3-row inventory opens. The top and bottom rows are decorative glass panes, and red panes mark the center slot;
  - items scroll along the middle row, with a bell sound, for `core.case-duration` seconds;
  - the scrolling then slows down until the reward reaches the center. A firework sound plays, the item goes to the player and a message is broadcast to the whole server.
- The case inventory is locked: items can't be clicked or dragged. If the player closes the inventory, the animation speeds up, but the reward is still given.

### Translations

Messages go through Adventure's translation system (`GlobalTranslator`). They are shown in English or French depending on the Minecraft client's language (`src/main/resources/locales/`).

### Custom heads (`misc/Skulls`)

Player heads with base64 textures (Twitter, Discord, YouTube), prepared for a future social links menu. They aren't used anywhere yet.

## Command

| Command | Description |
|---|---|
| `/case <name>` | Opens the case `<name>` defined in `cases.yml` |

## Configuration

### `config.yml`

```yaml
core:
  delay: 10            # seconds between item drops (must be > 0)
  same: false          # true = same item for every player
  case-duration: 3     # how long (in seconds) a case scrolls
  blacklisted-blocks:  # materials that are never given
    - AIR
    - BEDROCK
    # ...
```

### `cases.yml`

```yaml
cases: [ "default" ]          # cases to load

case-default:                 # "case-" prefix + case name
  color: ff0000               # title color (hex, without #)
  items: [ "DIAMOND_SWORD", "CAKE" ]
  DIAMOND_SWORD:              # Material name
    itemName: ""              # "" = vanilla name
    itemDesc: []              # lore lines
    amount: 1
    chance: 0.4
    enchants: ["sharpness"]   # minecraft: keys
    sharpness: 10             # level of each enchantment
  CAKE:
    itemName: Special cake
    itemDesc: ["line 1", "line 2"]
    amount: 1
    chance: 0.6
```

The `chance` values of a case must add up to `1.0`.

## Building and installing

Requirements: Java 17+.

```bash
./gradlew build
```

The jar is built to `build/libs/hickz-0.0.0.jar`. Put it in the `plugins/` folder of a **Paper 1.20.4** server. On first launch, `config.yml` and `cases.yml` are copied to `plugins/hickz/`.

## Code structure

```
src/main/java/com/cuchi/hickz/
├── Hickz.java            # entry point: config, translations, scheduled task, command
├── BlockTask.java        # periodic random item drop
├── CommandHandler.java   # /case command
├── cases/
│   ├── Case.java         # inventory, weighted roll, animation, events
│   └── CaseLoader.java   # reads cases.yml and builds the cases
└── misc/
    ├── ItemCreator.java  # ItemStack builder (name, lore, enchantments)
    └── Skulls.java       # heads with custom textures
```

## Project status and known limitations

Development stopped at the prototype stage and these issues will not be fixed. Keep them in mind if you reuse the code:

- `/case` has **no permission and no cost**: any player can open any case as many times as they like. The command is mostly meant for testing.
- `/case` with no argument, or with an unknown case name, throws an exception instead of showing an error message.
- Every case opening registers a new `Listener` that is never unregistered.
- If the player's inventory is full, the random item or the case reward is lost.
- `Skulls` isn't used yet.
