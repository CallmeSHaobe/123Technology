
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
    add(regressionSources.implementationConfigurationName, "com.paulscode:soundsystem:20120107")
}
val compileRegression = tasks.named<JavaCompile>(regressionSources.compileJavaTaskName) {
    source("src/main/java/com/newmaa/othtech/common/entity/FlightDynamics.java")
    source("src/main/java/com/newmaa/othtech/common/blocks/AyanamiAction.java")
    source("src/main/java/com/newmaa/othtech/common/blocks/AyanamiBlink.java")
    source("src/main/java/com/newmaa/othtech/common/dialogue/AyanamiChatApi.java")
    source("src/main/java/com/newmaa/othtech/common/dialogue/AyanamiDialogueText.java")
    source("src/main/java/com/newmaa/othtech/common/dialogue/AyanamiEventLinePrompt.java")
    source("src/main/java/com/newmaa/othtech/common/dialogue/AyanamiInputEdit.java")
    source("src/main/java/com/newmaa/othtech/common/dialogue/AyanamiKeyVault.java")
    source("src/main/java/com/newmaa/othtech/common/dialogue/AyanamiReasoning.java")
    source("src/main/java/com/newmaa/othtech/common/render/AyanamiRig.java")
    source("src/main/java/com/newmaa/othtech/utils/ShapeLoader.java")
    source("src/main/java/com/newmaa/othtech/machine/single/SpeakingStonePreset.java")
    source(fileTree("src/main/java/com/newmaa/othtech/client/radio") {
        exclude("FfmpegRadioDecoder.java", "GameRadioOutput.java")
    })
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
val radioDecoderCheck = tasks.register<JavaExec>("radioDecoderCheck") {
    group = "verification"
    description = "Verify the built-in radio playlist parser and MPEG-TS AAC extraction offline."
    dependsOn(compileRegression)
    classpath = regressionSources.runtimeClasspath
    mainClass.set("com.newmaa.othtech.client.radio.RadioDecoderCheck")
}
val ayanamiRigCheck = tasks.register<JavaExec>("ayanamiRigCheck") {
    group = "verification"
    description = "Verify Ayanami OBJ bindings and animation keyframe sampling offline."
    dependsOn(compileRegression)
    classpath = regressionSources.runtimeClasspath + files("src/main/resources")
    mainClass.set("com.newmaa.othtech.common.render.AyanamiRigCheck")
}
val ayanamiDialogueCheck = tasks.register<JavaExec>("ayanamiDialogueCheck") {
    group = "verification"
    description = "Verify Ayanami dialogue against a loopback provider and test the animation allowlist."
    dependsOn(compileRegression, tasks.named("processResources"))
    classpath = regressionSources.runtimeClasspath + files(tasks.named("processResources").map { it.outputs.files })
    mainClass.set("com.newmaa.othtech.common.dialogue.AyanamiChatApiCheck")
}
val ayanamiKeyVaultCheck = tasks.register<JavaExec>("ayanamiKeyVaultCheck") {
    group = "verification"
    description = "Verify authenticated encryption and per-endpoint isolation for Ayanami API keys."
    dependsOn(compileRegression)
    classpath = regressionSources.runtimeClasspath
    mainClass.set("com.newmaa.othtech.common.dialogue.AyanamiKeyVaultCheck")
}
tasks.named("check") {
    dependsOn(flightDynamicsCheck, shapeLoaderCheck, radioDecoderCheck, ayanamiRigCheck, ayanamiDialogueCheck,
        ayanamiKeyVaultCheck)
}

// Isolate only the Java 8 AAC decoder; do not install JAAD's global Java Sound providers.
val radioAac = configurations.create("radioAac") {
    isCanBeConsumed = false
    isTransitive = false
}
dependencies {
    add(radioAac.name, "de.sfuhrm:jaad:0.8.7")
}
val radioAacJar = tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("radioAacJar") {
    archiveFileName.set("oth-radio-aac.jar")
    destinationDirectory.set(layout.buildDirectory.dir("radio-codec"))
    configurations.set(listOf(radioAac))
    include("net/sourceforge/jaad/aac/**")
    relocate("net.sourceforge.jaad.aac", "com.newmaa.othtech.internal.radio.aac")
}
val radioCodec = files(radioAacJar.flatMap { it.archiveFile }).builtBy(radioAacJar)
dependencies {
    implementation(radioCodec)
    add(regressionSources.implementationConfigurationName, radioCodec)
}

interface RadioArchiveAccess {
    @get:javax.inject.Inject
    val archives: org.gradle.api.file.ArchiveOperations
}

val radioArchives = objects.newInstance<RadioArchiveAccess>().archives
tasks.named<Jar>("jar") {
    val archives = radioArchives
    dependsOn(radioAacJar)
    from(radioAacJar.flatMap { it.archiveFile }.map { file -> archives.zipTree(file) }) {
        include("com/newmaa/othtech/internal/radio/aac/**")
    }
    doFirst {
        manifest.attributes["MixinConfigs"] = "mixins.123technology.json"
    }
}
