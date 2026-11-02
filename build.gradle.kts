plugins {
    java
    id("com.gradleup.shadow") version "9.6.1"
}

group = "com.aykutuysal"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.20")
    runtimeOnly("ch.qos.logback:logback-classic:1.6.6")

    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("version.properties") {
        expand("version" to project.version)
    }
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    archiveFileName = "triangle-classifier.jar"
    manifest {
        attributes("Main-Class" to "com.aykutuysal.triangleclassifier.Main")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
    dependsOn(tasks.shadowJar)
    systemProperty("packagedJar", tasks.shadowJar.get().archiveFile.get().asFile.path)
    systemProperty("expectedVersion", project.version.toString())
}
