import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js {
        browser {
            binaries.executable()
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.html)

                implementation(project.dependencies.enforcedPlatform(libs.kotlinx.serialization.bom))
                implementation(libs.kotlinx.serialization.json)

                implementation(project.dependencies.enforcedPlatform(libs.ktor.bom))
                implementation(libs.ktor.resources)
                implementation(libs.ktor.serialization.kotlinx.json)

                implementation(libs.kotlin.logging)

                implementation(libs.clikt)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.ktor.server.call.logging)
                implementation(libs.ktor.server.content.negotiation)
                implementation(libs.ktor.server.core.jvm)
                implementation(libs.ktor.server.cors)
                implementation(libs.ktor.server.default.headers)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.server.resources)
                implementation(libs.ktor.server.html.builder.jvm)
                implementation(libs.ktor.server.swagger)

                implementation(project.dependencies.enforcedPlatform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.ktor)

                implementation(project.dependencies.enforcedPlatform(libs.exposed.bom))
                implementation(libs.exposed.core)
                implementation(libs.exposed.dao)
                implementation(libs.exposed.jdbc)
                implementation(libs.exposed.kotlin.datetime)

                implementation(libs.h2)
                runtimeOnly(libs.slf4j.simple)
            }
        }
        jsMain {
            dependencies {
                implementation(project.dependencies.enforcedPlatform(libs.kotlin.wrappers.bom))
                implementation(kotlinw("react"))
                implementation(kotlinw("react-dom"))
                implementation(kotlinw("react-router-dom"))

                implementation(kotlinw("emotion"))

                implementation(kotlinw("mui-base"))
                implementation(kotlinw("mui-lab"))
                implementation(kotlinw("mui-system"))
                implementation(kotlinw("mui-material"))
                implementation(kotlinw("mui-icons-material"))
                implementation(kotlinw("muix-date-pickers"))

                implementation(npm("date-fns", "3.6.0"))
                implementation(npm("@date-io/date-fns", "3.0.0"))

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.resources)
            }
        }
    }
}

application {
    mainClass.set("org.github.anisch.ServerKt")
    applicationDefaultJvmArgs = listOf(
        "-Dfile.encoding=UTF-8",
        "-Dio.ktor.development=true",
        "-Dorg.slf4j.simpleLogger.defaultLogLevel=debug"
    )
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}

fun kotlinw(target: String): String = "org.jetbrains.kotlin-wrappers:kotlin-$target"
