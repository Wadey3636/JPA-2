import org.apache.commons.lang3.SystemUtils
import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    idea
    java
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.0.0"
}

//Constants:

val baseGroup: String by project
val mcVersion: String by project
val version: String by project
val mixinGroup = "$baseGroup.mixin"
val modid: String by project
val transformerFile = file("src/main/resources/accesstransformer.cfg")

// Toolchains:
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

// Minecraft configuration:
loom {
    log4jConfigs.from(file("log4j2.xml"))
    launchConfigs {
        "client" {
            // If you don't want mixins, remove these lines
            property("mixin.debug", "true")
            arg("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
        }
    }
    runConfigs {
        "client" {
            if (SystemUtils.IS_OS_MAC_OSX) {
                // This argument causes a crash on macOS
                vmArgs.remove("-XstartOnFirstThread")

            }
        }
        remove(getByName("server"))
    }
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        // If you don't want mixins, remove this lines
        mixinConfig("mixins.$modid.json")
	    if (transformerFile.exists()) {
			println("Installing access transformer")
		    accessTransformer(transformerFile)
	    }
    }
    // If you don't want mixins, remove these lines
    mixin {
        defaultRefmapName.set("mixins.$modid.refmap.json")
    }
}


tasks.compileJava {
    dependsOn(tasks.processResources)
}

sourceSets.main {
    output.setResourcesDir(sourceSets.main.flatMap { it.java.classesDirectory })
    java.srcDir(layout.projectDirectory.dir("src/main/kotlin"))
    kotlin.destinationDirectory.set(java.destinationDirectory)
}

// Dependencies:

repositories {
    mavenCentral()

    maven("https://repo.essential.gg/public")
    maven("https://repo.spongepowered.org/maven/")
    // If you don't want to log in with your real minecraft account, remove this line
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}


dependencies {
    // Move these from implementation to shadowImpl to ensure they're included in the final jar
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")


    shadowImpl("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    shadowImpl("org.jetbrains.kotlin:kotlin-reflect:1.9.0")

    // Keep your existing shadowImpl
    shadowImpl(kotlin("stdlib-jdk8"))

    // Keep the rest of your dependencies as they are
    compileOnly("com.github.NotEnoughUpdates:NotEnoughUpdates:2.4.0:all")
    implementation("gg.essential:loader-launchwrapper:1.1.3")
    implementation("com.mojang:brigadier:1.2.9")
    implementation("com.github.Stivais:Commodore:bea320fe0a")
    compileOnly("gg.essential:essential-1.8.9-forge:12132+g6e2bf4dc5")

    shadowImpl("gg.essential:loader-launchwrapper:1.1.3")
    shadowImpl("gg.essential:essential-1.8.9-forge:12132+g6e2bf4dc5")
    shadowImpl("com.mojang:brigadier:1.2.9")
    shadowImpl("com.github.Stivais:Commodore:bea320fe0a")
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    // Keep your mixin configuration
    shadowImpl("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        isTransitive = false
    }
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT")

    runtimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.2.1")

}


// Tasks:

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.withType(org.gradle.jvm.tasks.Jar::class) {
    archiveBaseName.set(modid)
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"

        // If you don't want mixins, remove these lines
        this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
        this["MixinConfigs"] = "mixins.$modid.json"
	    if (transformerFile.exists())
			this["FMLAT"] = "${modid}_at.cfg"
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("mcversion", mcVersion)
    inputs.property("modid", modid)
    inputs.property("basePackage", baseGroup)

    filesMatching(listOf("mcmod.info", "mixins.$modid.json")) {
        expand(inputs.properties)
    }

    rename("accesstransformer.cfg", "META-INF/${modid}_at.cfg")
}


val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("")
    from(tasks.shadowJar)
    input.set(tasks.shadowJar.get().archiveFile)
}

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("intermediates"))
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("intermediates"))
    archiveClassifier.set("non-obfuscated-with-deps")
    configurations = listOf(shadowImpl)
    doLast {
        configurations.forEach {
            println("Copying dependencies into mod: ${it.files}")
        }
    }

    // If you want to include other dependencies and shadow them, you can relocate them in here
    fun relocate(name: String) = relocate(name, "$baseGroup.deps.$name")
}

tasks {
    processResources {
        filesMatching("mcmod.info") {
            expand(
                "modid" to modid,
                "version" to project.version,
                "mcversion" to mcVersion
            )
        }
    }
}
tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "ModSide" to "CLIENT",
            "FMLCorePlugin" to "${baseGroup}.init.AutoDiscoveryMixinPlugin",
            "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
            "TweakOrder" to "0"
        )
    }
}


tasks.assemble.get().dependsOn(tasks.remapJar)

