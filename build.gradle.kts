import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") version ("1.8.0")
    id("org.jetbrains.compose") version ("1.4.0")
}

apply(plugin = "java")

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

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "ru.avem.viu35.MainKt"
    }
}

val exposedVersion = "0.41.1"

kotlin {
    jvm {
        jvmToolchain(17)
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.linux_arm64)
                implementation(compose.desktop.currentOs)

                implementation("org.jfree:jfreechart:1.5.3")
                implementation("cafe.adriel.voyager:voyager-navigator-desktop:1.0.0-rc02")
                implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator-desktop:1.0.0-rc02")
                implementation("cafe.adriel.voyager:voyager-transitions-desktop:1.0.0-rc02")
                implementation("cafe.adriel.voyager:voyager-tab-navigator-desktop:1.0.0-rc02")
                implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.1.1")

                implementation("org.apache.poi:poi:5.0.0")
                implementation("org.apache.poi:poi-ooxml:5.0.0")

                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.xerial:sqlite-jdbc:3.30.1")

                implementation("com.darkrockstudios:mpfilepicker:1.0.0")

                implementation("ch.qos.logback:logback-classic:1.4.6")
                implementation("ch.qos.logback:logback-core:1.4.6")
                implementation("io.github.microutils:kotlin-logging-jvm:3.0.2")
                implementation("io.github.microutils:kotlin-logging:1.8.3")
                implementation("org.slf4j:slf4j-api:1.7.25")
                implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.9.1")
                implementation("org.apache.logging.log4j:log4j-api:2.9.1")
                implementation("org.apache.logging.log4j:log4j-core:2.9.1")

                implementation("com.fazecast:jSerialComm:2.9.2")
                implementation(":kserialpooler-2.0")
                implementation(":polling-essentials-2.0")
            }
        }
        val jvmTest by getting
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            windows {
                iconFile.set(project.file("icon.ico"))
            }
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "viu35-nevz"
            packageVersion = "1.0.0"
            includeAllModules = true
//            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
        }
    }
}
