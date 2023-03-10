plugins {
    kotlin("multiplatform") version "1.8.20-RC"
    kotlin("plugin.serialization") version "1.8.20-RC"
    id("org.jetbrains.dokka") version "1.8.10"
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
    js(IR) {
        browser {
            binaries.executable()
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
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

                implementation("io.github.oshai:kotlin-logging:4.0.0-beta-23")
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

                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")

                implementation(ktor("server-call-logging"))
                implementation(ktor("server-content-negotiation"))
                implementation(ktor("server-core-jvm"))
                implementation(ktor("server-cors"))
                implementation(ktor("server-default-headers"))
                implementation(ktor("server-netty"))
                implementation(ktor("server-resources"))
                implementation(ktor("server-html-builder-jvm"))

                implementation("io.insert-koin:koin-core:3.3.3")
                implementation("io.insert-koin:koin-ktor:3.3.1")

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

                implementation(ktor("client-core"))
                implementation(ktor("client-content-negotiation"))
                implementation(ktor("client-resources"))

                implementation(npm("bootstrap", "5.3.0-alpha1"))
                implementation(npm("core-js", "3.27.2"))
            }
        }
        val jsTest by getting
    }
}

application {
    mainClass.set("org.github.anisch.ServerKt")
    applicationDefaultJvmArgs = listOf(
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

fun exposed(target: String): String = "org.jetbrains.exposed:$target"
fun kotlinw(target: String): String = "org.jetbrains.kotlin-wrappers:kotlin-$target"
fun ktor(target: String): String = "io.ktor:ktor-$target"
fun serial(target: String): String = "org.jetbrains.kotlinx:kotlinx-serialization-$target"
