import java.io.FileOutputStream

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("com.github.johnrengelman.shadow") version ("8.1.1")
    id("maven-publish")
    id("de.undercouch.download") version ("5.4.0")
}

group = "com.laudynetwork.networkutils"
version = "latest"
description = "Utility Plugin for LaudyNetwork"

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    implementation("biz.paluch.redis:lettuce:4.5.0.Final")
    paperweight.paperDevBundle("1.20-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.viaversion:viaversion-api:4.7.0")
    implementation("com.laudynetwork:database:latest")
    api("eu.thesimplecloud.simplecloud:simplecloud-api:2.5.0")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.mongodb:mongodb-driver-sync:4.10.2")
}
repositories {
    mavenCentral()
    maven("https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local/")
    maven("https://repo.viaversion.com")
    maven("https://jitpack.io")
    maven("https://eldonexus.de/repository/maven-proxies/")
    maven {
        url = uri("https://repo.laudynetwork.com/repository/maven")
        authentication {
            create<BasicAuthentication>("basic")
        }
        credentials {
            username = System.getenv("NEXUS_USER")
            password = System.getenv("NEXUS_PWD")
        }
    }
}
publishing {
    repositories {
        maven {
            url = uri("https://repo.laudynetwork.com/repository/maven")
            credentials {
                username = System.getenv("NEXUS_USER")
                password = System.getenv("NEXUS_PWD")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.laudynetwork"
            artifactId = "networkutils"
            version = "latest"

            from(components["java"])
        }
    }
}

tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    processResources {
        dependsOn("translations")
    }

    build {
        doLast {
            file(layout.buildDirectory.file("libs/NetworkUtils-latest-dev.jar")).delete()
            file(layout.buildDirectory.file("libs/NetworkUtils-latest-dev-all.jar")).delete()
        }
    }

    shadowJar {
        dependencies {
            exclude(dependency("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT"))
            exclude(dependency("eu.thesimplecloud.simplecloud:simplecloud-api:2.5.0"))
            exclude(dependency("eu.thesimplecloud.clientserverapi:clientserverapi:4.1.17"))
            exclude(dependency("eu.thesimplecloud.jsonlib:json-lib:1.0.10"))
            exclude(dependency("eu.thesimplecloud.simplecloud:simplecloud-runner:2.6.0"))
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    reobfJar {
        // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
        // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
        outputJar.set(layout.buildDirectory.file("dist/NetworkUtils.jar"))
    }
}



tasks.register("translations") {
    downloadFile(System.getenv("TOLGEE_TOKEN_PLUGIN"), "own")
    downloadFile(System.getenv("TOLGEE_TOKEN_GENERAL"), "plugins")
}

fun downloadFile(token: String, dir: String) {
    downloadLink(token).forEach {
        downloadFromServer(it.key, it.value + ".json", dir)
    }
}

fun downloadFromServer(url: String, fileName: String, dir: String) {
    file("${projectDir}/src/main/resources/translations/${dir}").mkdirs()
    val f = file("${projectDir}/src/main/resources/translations/${dir}/${fileName}")
    uri(url).toURL().openStream().use {
        it.copyTo(
                FileOutputStream(f)
        )
    }
}

fun downloadLink(token: String): Map<String, String> {
    val map = HashMap<String, String>()
    val params = "format=JSON&zip=false&structureDelimiter"
    map["https://tolgee.laudynetwork.com/v2/projects/export?languages=en&$params&ak=$token"] = "en"
    map["https://tolgee.laudynetwork.com/v2/projects/export?languages=de&$params&ak=$token"] = "de"
    return map
}