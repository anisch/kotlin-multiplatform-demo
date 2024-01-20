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
            commonWebpackConfig(Action {
                cssSupport {
                    enabled.set(true)
                }
            })
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val ktor_version = libs.versions.ktor.get()
        val kotlin_wrapper_version = libs.versions.kt.wrappers.get()

        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.html)

                implementation(project.dependencies.enforcedPlatform(libs.kotlinx.serialization.bom))
                implementation(libs.kotlinx.serialization.json)

                implementation(project.dependencies.enforcedPlatform(ktor("bom:$ktor_version")))
                implementation(ktor("resources"))
                implementation(ktor("serialization-kotlinx-json"))

                implementation(libs.kotlin.logging)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        jvmMain {
            dependencies {
                implementation(kotlin("stdlib"))

                implementation(ktor("server-call-logging"))
                implementation(ktor("server-content-negotiation"))
                implementation(ktor("server-core-jvm"))
                implementation(ktor("server-cors"))
                implementation(ktor("server-default-headers"))
                implementation(ktor("server-netty"))
                implementation(ktor("server-resources"))
                implementation(ktor("server-html-builder-jvm"))

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

                implementation(kotlin("stdlib-js"))

                implementation(project.dependencies.enforcedPlatform(kotlinw("wrappers-bom:$kotlin_wrapper_version")))
                implementation(kotlinw("react"))
                implementation(kotlinw("react-dom"))
                implementation(kotlinw("react-router-dom"))

                implementation(kotlinw("emotion"))
                implementation(kotlinw("mui-base"))
                implementation(kotlinw("mui-icons-material"))
                implementation(kotlinw("muix-date-pickers"))

                implementation(npm("date-fns", "2.30.0"))
                implementation(npm("@date-io/date-fns", "2.17.0"))

                implementation(ktor("client-core"))
                implementation(ktor("client-content-negotiation"))
                implementation(ktor("client-resources"))
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

tasks.withType<KotlinCompile> {
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
    }
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
fun ktor(target: String): String = "io.ktor:ktor-$target"
