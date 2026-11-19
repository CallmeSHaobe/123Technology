
plugins {
    id("com.github.ElytraServers.elytra-conventions") version "v1.1.1"
    id("com.gtnewhorizons.gtnhconvention")
}

// Gradle shortens long Windows classpaths using a manifest JAR, which hides RFB plugins.
// Modern Java supports argument files: preserve the full classpath for plugin discovery.
tasks.withType<JavaExec>().configureEach {
    if (name.matches(Regex("run(Client|Server)(17|21|25)"))) {
        doFirst {
            val launchTask = this as JavaExec
            val argumentFile = temporaryDir.resolve("minecraft-classpath.args")
            val fullClasspath = launchTask.classpath.asPath.replace("\\", "\\\\").replace("\"", "\\\"")
            argumentFile.writeText("-classpath\n\"$fullClasspath\"\n")
            launchTask.classpath = launchTask.classpath.filter { false }
            launchTask.jvmArgs("@${argumentFile.absolutePath}")
        }
    }
}

tasks.named<Jar>("jar") {
    doFirst {
        manifest.attributes["MixinConfigs"] = "mixins.123technology.json"
    }
}
