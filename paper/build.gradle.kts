plugins {
    alias(libs.plugins.modrinth)
    alias(libs.plugins.hangar)

    id("paper-plugin")
}

project.group = "${rootProject.group}.paper"

base {
    archivesName.set("${rootProject.name}-${project.name}")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.essentialsx.net/releases/")
}

dependencies {
    implementation("org.bstats", "bstats-bukkit", "3.0.2")

    compileOnly("me.clip", "placeholderapi", "2.11.3")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7.1") {
        exclude("org.bukkit", "bukkit")
    }

    compileOnly("net.essentialsx", "EssentialsX", "2.19.0")

    compileOnly("com.github.LeonMangler", "SuperVanish", "6.2.6-4")
}

val component: SoftwareComponent = components["java"]

tasks {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = "${rootProject.name.lowercase()}-${project.name.lowercase()}-api"
                version = rootProject.version.toString()

                from(component)
            }
        }
    }

    shadowJar {
        listOf(
            "org.bstats",
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        val props = mapOf(
                "name" to rootProject.name,
                "group" to project.group.toString(),
                "version" to rootProject.version,
                "description" to rootProject.description,
                "authors" to rootProject.properties["authors"],
                "apiVersion" to "1.20",
                "website" to "https://modrinth.com/plugin/${rootProject.name.lowercase()}"
        )

        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

val isSnapshot = rootProject.version.toString().contains("snapshot")
val type = if (isSnapshot) "beta" else "release"
val other = if (isSnapshot) "Beta" else "Release"

val file = file("${rootProject.rootDir}/jars/${rootProject.name}-${rootProject.version}.jar")

val description = """
## Changes:
* Added 1.20.2 support.

## New Features:
* N/A

## Other:
* [Feature Requests](https://github.com/Crazy-Crew/${rootProject.name}/issues)
* [Bug Reports](https://github.com/Crazy-Crew/${rootProject.name}/issues)
""".trimIndent()

val versions = listOf(
    "1.20",
    "1.20.1",
    "1.20.2"
)

modrinth {
    autoAddDependsOn.set(false)

    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(rootProject.name.lowercase())

    versionName.set("${rootProject.name} ${rootProject.version}")
    versionNumber.set("${rootProject.version}")

    versionType.set(type)

    uploadFile.set(file)

    gameVersions.addAll(versions)

    changelog.set(description)

    loaders.addAll("paper", "purpur")
}

hangarPublish {
    publications.register("plugin") {
        version.set(rootProject.version as String)

        id.set(rootProject.name)

        channel.set(if (isSnapshot) "Beta" else "Release")

        changelog.set(description)

        apiKey.set(System.getenv("hangar_key"))

        platforms {
            register(Platforms.PAPER) {
                jar.set(file)
                platformVersions.set(versions)
            }
        }
    }
}