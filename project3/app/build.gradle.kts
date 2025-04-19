plugins {
    application
}

repositories {
    mavenCentral()
}

val lwjglVersion = "3.3.1"

dependencies {
    // Utility library
    implementation("com.google.guava:guava:31.1-jre")
    
    // JOML for matrix math
    implementation("org.joml:joml:1.10.5")

    // LWJGL BOM and core modules
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")

    // Native runtime libraries for Windows x64
    runtimeOnly("org.lwjgl:lwjgl::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-stb::natives-windows")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("org.example.App")
    // On macOS, uncomment the following line if needed:
    // applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
