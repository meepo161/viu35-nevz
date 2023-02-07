import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version ("1.7.20")
    id("org.jetbrains.compose") version ("1.2.2")
}

group = "ru.avem"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    flatDir {
        dirs("libs")
    }
}

val exposedVersion = "0.41.1"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.linux_arm64)
                implementation(compose.desktop.currentOs)

                implementation(":kserialpooler-1.0")
                implementation(":polling-essentials-1.0")
                implementation("org.jfree:jfreechart:1.5.3")
                implementation("org.apache.poi:poi:5.0.0")
                implementation("org.apache.poi:poi-ooxml:5.0.0")
                implementation("cafe.adriel.voyager:voyager-navigator-desktop:1.0.0-rc02")
                implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator-desktop:1.0.0-rc02")
                implementation("cafe.adriel.voyager:voyager-transitions-desktop:1.0.0-rc02")
                implementation("cafe.adriel.voyager:voyager-tab-navigator-desktop:1.0.0-rc02")
                implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.1.1")
                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.xerial:sqlite-jdbc:3.30.1")
                implementation("io.github.microutils:kotlin-logging-jvm:3.0.2")
            }
        }
        val jvmTest by getting
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ikas-10-multiplatform"
            packageVersion = "1.0.0"
        }
    }
}
dependencies {
//    implementation(kotlin("stdlib-jdk8"))
}
//val compileKotlin: KotlinCompile by tasks
//compileKotlin.kotlinOptions {
//    jvmTarget = "1.8"
//}
//val compileTestKotlin: KotlinCompile by tasks
//compileTestKotlin.kotlinOptions {
//    jvmTarget = "1.8"
//}