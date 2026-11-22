pluginManagement {
    repositories {
        maven {
            // RetroFuturaGradle
            name = "GTNH Maven"
            url = uri("https://nexus.gtnewhorizons.com/repository/public/")
            mavenContent {
                includeGroup("com.gtnewhorizons")
                includeGroupByRegex("com\\.gtnewhorizons\\..+")
            }
        }
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri( "https://jitpack.io")
            content {
                includeGroup ("com.github.Nxer")
            }
        }
        maven {
            name = "JitPack"
            url = uri("https://jitpack.io")
            mavenContent {
                includeGroupByRegex("com\\.github\\..+")
            }
        }
        mavenLocal()
    }
}

plugins {
    id("com.gtnewhorizons.gtnhsettingsconvention") version("2.0.34")
}
