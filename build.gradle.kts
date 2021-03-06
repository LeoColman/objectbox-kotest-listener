/*
 * Copyright 2020 Leonardo Colman Lopes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either press or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.lang.System.getenv

plugins {
    kotlin("jvm") version "1.5.30"
    kotlin("kapt") version "1.6.0"
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.6.0"
    id("io.gitlab.arturbosch.detekt").version("1.18.0")
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("io.objectbox:objectbox-gradle-plugin:2.9.1")
    }
}

apply(plugin = "io.objectbox")

group = "br.com.colman"
version = getenv("RELEASE_VERSION") ?: "local"

repositories {
    mavenCentral()
}

dependencies {
    // Kotest
    implementation("io.kotest:kotest-framework-api:5.0.0")

    testImplementation("io.kotest:kotest-runner-junit5:5.0.0")
}

kotlin {
    explicitApi()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    explicitApi()
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn("dokkaHtml")
    archiveClassifier.set("javadoc")
    from("$buildDir/dokka")
}


publishing {
    repositories {

        maven("https://oss.sonatype.org/service/local/staging/deploy/maven2") {
            credentials {
                username = getenv("OSSRH_USERNAME")
                password = getenv("OSSRH_PASSWORD")
            }
        }
    }

    publications {

        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            pom {
                name.set("objectbox-kotest-listener")
                description.set("ObjectBox Kotest Listener")
                url.set("https://www.github.com/LeoColman/objectbox-kotest-listener")


                scm {
                    connection.set("scm:git:http://www.github.com/LeoColman/objectbox-kotest-listener")
                    developerConnection.set("scm:git:http://github.com/LeoColman/objectbox-kotest-listener")
                    url.set("https://www.github.com/LeoColman/objectbox-kotest-listener")
                }

                licenses {
                    license {
                        name.set("The Apache 2.0 License")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }

                developers {
                    developer {
                        id.set("LeoColman")
                        name.set("Leonardo Colman Lopes")
                        email.set("leonardo.dev@colman.com.br")
                    }
                }
            }
        }
    }
}

val signingKey: String? by project
val signingPassword: String? by project

signing {
    useGpgCmd()
    if(signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }

    sign(publishing.publications["mavenJava"])
}
