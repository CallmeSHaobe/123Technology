
plugins {
    id("com.github.ElytraServers.elytra-conventions") version "v1.1.1"
    id("com.gtnewhorizons.gtnhconvention")
}

tasks.named<Jar>("jar") {
    doFirst {
        manifest.attributes["MixinConfigs"] = "mixins.123technology.json"
    }
}
