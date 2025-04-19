# Project 3: Minecraft-like Voxel Engine

A Java-based voxel engine built with LWJGL (OpenGL), JOML, and Gradle. Features real-time block rendering, dynamic world generation, camera controls, and block placement & destruction with physics-based debris animations.

## Prerequisites

- **Java Development Kit (JDK)** 21 (or JDK 17)
- **Gradle Wrapper** (`gradlew` / `gradlew.bat`) included in `app/`
- **Internet connection** (to download dependencies on first build)
- **Supported OS:** Windows, macOS, or Linux (ensure correct LWJGL natives)

## Project Structure

```text
project3/
└─ app/
   ├─ build.gradle.kts         # Gradle build script for the app
   ├─ settings.gradle.kts      # Project settings
   ├─ gradlew(.bat)            # Gradle wrappers
   └─ src/
      ├─ main/
      │  ├─ java/org/example/  # Java source packages
      │  │     ├ App.java
      │  │     ├ Camera.java
      │  │     ├ Chunk.java
      │  │     ├ MeshGenerator.java
      │  │     ├ Mesh.java
      │  │     ├ ShaderProgram.java
      │  │     ├ Texture.java
      │  │     ├ Utils.java
      │  │     └ BlockType.java
      │  └─ resources/
      │       ├─ shaders/
      │       │    ├ vertex.glsl
      │       │    └ fragment.glsl
      │       └─ textures/
      │            └ texture_atlas.png
      └─ test/                  # Optional unit tests
```

## Building & Running

### Using Gradle Wrapper (recommended)

1. Open a terminal in the `app/` folder:
   ```bash
   cd D:\files\ICS415-Assignments\project3\app
   ```
2. Build the project and fetch dependencies:
   ```bash
   ./gradlew clean build
   ```
3. Run the application:
   ```bash
   ./gradlew run
   ```

_On Windows, use `gradlew.bat` instead of `./gradlew`._

### Manual Compilation (not recommended)

```bash
# From app/src/main/java:
cd src/main/java
javac -d ../../bin org/example/*.java
# Back in app/ folder:
cd ../../
java -cp bin org.example.App
```

## Controls

- **W / A / S / D**: Move camera
- **Mouse**: Look around
- **Space / Left Shift**: Fly up / down
- **Left Click**: Destroy block (splinters into debris)
- **Right Click**: Place `SPECIAL` block
- **ESC**: Exit application

## Features

- **Flat & Procedural Terrain:** Easily switch between flat or noise-generated hills
- **Chunk Meshing:** Efficient mesh rebuild on block updates
- **Texture Atlas:** Single atlas for multiple block types
- **Debris Animation:** Block destruction spawns physics-driven debris
- **Scalable World:** Configure chunk/grid size (e.g., 16×16 or 500×500)

## Troubleshooting

- **`Could not find or load main class`**: Ensure you run from `app/` and compile all sources.
- **Missing Native Libraries**: Confirm LWJGL natives in `build.gradle.kts` match your platform.
- **Resource Not Found**: Verify shaders/textures are under `src/main/resources`.

