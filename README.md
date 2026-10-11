# NPC Overhead Hider

Hide overhead speech for specific NPC IDs in RuneLite. NPC models, healthbars,
hitsplats, overhead icons and dialogue windows remain visible.

## Configuration

Enable NPC Overhead Hider and open its settings. Enter comma-separated IDs in
**Custom NPC IDs**, for example `12876,786,867,65`. Spaces and duplicates are
allowed. Invalid entries and integers outside the supported range are ignored.
An empty custom list selects no NPCs.

The custom list is combined with enabled presets. Both presets are off by default:

- **Ferox Enclave:** 16576,10382,10389,10383,10390,10379,10380,10381,7316,8721
- **Godswars:** 2215,2205,3162,3129,11278,11279,11280,11281,11282

All instances of a selected NPC type are muted. Both its base ID and transformed
ID are checked. The plugin does not create custom NPCs.

## Behavior

A RenderCallback registered through RenderCallbackManager clears selected speech
immediately before NPC UI rendering. It allows rendering to continue and does
not subscribe to OverheadTextChanged. Disabling the plugin removes the callback.
Previously cleared speech is not restored; new speech becomes visible again.
Other plugins reading that NPC's overhead text may observe the cleared value.

The plugin uses no networking, credentials, file access or automated game actions.

## Development

Open this directory as a Gradle project in IntelliJ. Java 11 and the Gradle wrapper
are supported. The tested RuneLite version is 1.13.1, pinned in build.gradle.

```powershell
.\gradlew.bat clean test jar
.\gradlew.bat run
```

By default, the development client uses an isolated home under `standalone-home`.
For existing locally saved Jagex credentials:

```powershell
.\gradlew.bat run -Pjagex
```

On the author's local setup, Menu Entry Swapper produced an assertion failure on
clicks. An optional development-only workaround disables assertions for that class:

```powershell
.\gradlew.bat run -Pjagex -PlocalMenuSwapperWorkaround
```

This does not repair Menu Entry Swapper and is not enabled by default. The runner
is in src/test and is excluded from the plugin JAR.

## Plugin Hub submission

runelite-plugin.properties specifies `build=standard`. The Plugin Hub replaces
the local Gradle build configuration during its standard build. The runtime plugin
requires only RuneLite-provided dependencies. The local launch workaround is not
part of the plugin JAR. The plugin has not yet been submitted or approved.

Tests cover ID parsing, independent presets, selected and unselected NPCs,
transformed IDs, missing transformations, players, 3D drawing and callback lifecycle.

## License

BSD 2-Clause. See [LICENSE](LICENSE).
