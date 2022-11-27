

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.8"
    id("com.github.johnrengelman.shadow") version("7.1.2")
    id("maven-publish")
}

publishing {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/laudy-network/networkutils")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
}

group = "com.laudynetwork.networkutils"
version = System.getenv("RELEASE_VERSION") ?: "1.0.0"
description = "Utility Plugin for LaudyNetwork"

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.24")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.6")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
    paperDevBundle("1.19.2-R0.1-SNAPSHOT")
    implementation("biz.paluch.redis:lettuce:4.5.0.Final")
    implementation("com.zaxxer:HikariCP:5.0.1")
}
repositories {
    mavenCentral()
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.laudynetwork"
            artifactId = "networkutils"
            version = System.getenv("RELEASE_VERSION") ?: "1.0.0"

            from(components["java"])
        }
    }
}


tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
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

    /*
    reobfJar {
      // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
      // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
      outputJar.set(layout.buildDirectory.file("libs/PaperweightTestPlugin-${project.version}.jar"))
    }
     */
}
