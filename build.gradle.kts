import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    application
    id("org.jetbrains.kotlin.jvm") version "2.3.20"
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "2.25.0"
}

group = "com.resdev.doldom"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"


tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

application {
    mainModule.set("com.resdev.doldom.doldomfarmacia")
    mainClass.set("com.resdev.doldom.doldomfarmacia.HelloApplication")
}
kotlin {
    jvmToolchain(26)

    compilerOptions{
            jvmTarget.set(JvmTarget.JVM_25)
    }
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(26))
    }
}

javafx {
    version = "26"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "app"
    }
}
