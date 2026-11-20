
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

// These checks have main methods rather than JUnit tests. Keep them out of the test
// source set and execute them explicitly as part of check, with Java 8 API checking.
val regressionSources = sourceSets.create("regression")
dependencies {
    add(regressionSources.implementationConfigurationName, "com.google.code.gson:gson:2.2.4")
}
val compileRegression = tasks.named<JavaCompile>(regressionSources.compileJavaTaskName) {
    source("src/main/java/com/newmaa/othtech/common/entity/FlightDynamics.java")
    source("src/main/java/com/newmaa/othtech/utils/ShapeLoader.java")
    options.release.set(8)
}
val flightDynamicsCheck = tasks.register<JavaExec>("flightDynamicsCheck") {
    group = "verification"
    description = "Run the standalone fixed-wing flight regression."
    dependsOn(compileRegression)
    classpath = regressionSources.runtimeClasspath
    mainClass.set("com.newmaa.othtech.common.entity.FlightDynamicsCheck")
}
val shapeLoaderCheck = tasks.register<JavaExec>("shapeLoaderCheck") {
    group = "verification"
    description = "Verify compressed structures, whitespace, cache isolation and invalid resources."
    dependsOn(compileRegression)
    val fixtures = layout.buildDirectory.dir("shape-loader-check-fixtures")
    val structures = layout.projectDirectory.dir("src/main/resources/assets/123technology/structures/ote")
    classpath = regressionSources.runtimeClasspath + files("src/main/resources", fixtures)
    mainClass.set("com.newmaa.othtech.utils.ShapeLoaderCheck")
    args(fixtures.get().asFile.absolutePath, structures.asFile.absolutePath)
    // Create the classpath directory before Java starts so its URL is treated as a directory.
    doFirst {
        fixtures.get().asFile.mkdirs()
    }
}
tasks.named("check") {
    dependsOn(flightDynamicsCheck, shapeLoaderCheck)
}

tasks.named<Jar>("jar") {
    doFirst {
        manifest.attributes["MixinConfigs"] = "mixins.123technology.json"
    }
}
