

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.8"
    id("com.github.johnrengelman.shadow") version("7.1.2")
    id("maven-publish")
}

group = "de.shortexception.networkutils"
version = "1.0.0-SNAPSHOT"
description = "Utility Plugin for LaudyNetwork"

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    paperDevBundle("1.19.2-R0.1-SNAPSHOT")
    // paperweightDevBundle("com.example.paperfork", "1.19.2-R0.1-SNAPSHOT")

    // You will need to manually specify the full dependency if using the groovy gradle dsl
    // (paperDevBundle and paperweightDevBundle functions do not work in groovy)
    // paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.19.2-R0.1-SNAPSHOT")

    // mongo db sync
    implementation("org.mongodb:mongodb-driver-sync:4.7.2")
    implementation("biz.paluch.redis:lettuce:4.5.0.Final")
}
repositories {
    mavenCentral()
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.shortexception"
            artifactId = "networkutils"
            version = "1.0"

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
