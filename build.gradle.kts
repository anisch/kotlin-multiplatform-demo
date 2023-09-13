import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.9.20-Beta"
    kotlin("plugin.serialization") version "1.9.20-Beta"
    id("org.jetbrains.dokka") version "1.9.0"
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
    sourceSets {
        val ktor_version: String by project
        val serial_version: String by project
        val kotlin_wrapper_version: String by project
        val exposed_version: String by project
        val h2_version: String by project
        val kotlinx_coroutines: String by project
        val kotlinx_datetime: String by project

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinx_datetime")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_coroutines")

                implementation(enforcedPlatform(serial("bom:$serial_version")))
                implementation(serial("json"))

                implementation(enforcedPlatform(ktor("bom:$ktor_version")))
                implementation(ktor("resources"))
                implementation(ktor("serialization-kotlinx-json"))

                implementation("io.github.oshai:kotlin-logging:5.1.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))

                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.9.1")

                implementation(ktor("server-call-logging"))
                implementation(ktor("server-content-negotiation"))
                implementation(ktor("server-core-jvm"))
                implementation(ktor("server-cors"))
                implementation(ktor("server-default-headers"))
                implementation(ktor("server-netty"))
                implementation(ktor("server-resources"))
                implementation(ktor("server-html-builder-jvm"))

                implementation("io.insert-koin:koin-core:3.5.0")
                implementation("io.insert-koin:koin-ktor:3.5.0")

                implementation(enforcedPlatform(exposed("exposed-bom:$exposed_version")))
                implementation(exposed("exposed-core"))
                implementation(exposed("exposed-dao"))
                implementation(exposed("exposed-jdbc"))
                implementation(exposed("exposed-kotlin-datetime"))

                implementation("com.h2database:h2:$h2_version")

                runtimeOnly("org.slf4j:slf4j-simple:2.0.6")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {

                implementation(kotlin("stdlib-js"))

                implementation(enforcedPlatform(kotlinw("wrappers-bom:$kotlin_wrapper_version")))
                implementation(kotlinw("react"))
                implementation(kotlinw("react-dom"))
                implementation(kotlinw("emotion"))
                implementation(kotlinw("mui"))
                implementation(kotlinw("mui-icons"))

                implementation(ktor("client-core"))
                implementation(ktor("client-content-negotiation"))
                implementation(ktor("client-resources"))
            }
        }
        val jsTest by getting
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

fun exposed(target: String): String = "org.jetbrains.exposed:$target"
fun kotlinw(target: String): String = "org.jetbrains.kotlin-wrappers:kotlin-$target"
fun ktor(target: String): String = "io.ktor:ktor-$target"
fun serial(target: String): String = "org.jetbrains.kotlinx:kotlinx-serialization-$target"
