package org.github.anisch

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.serialization.json.Json
import org.github.anisch.modules.repositoryModule
import org.github.anisch.routing.personRouting
import org.koin.core.context.startKoin

fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            +"Hello from Ktor v3"
        }
        div {
            id = "root"
        }
        script(src = "/static/sentimental-daemon.js") {}
    }
}

fun main(args: Array<String>) = Main().main(args)

class Main : CliktCommand() {
    private val port: Int by option().int().default(8080).help("Run Server on Port")

    override fun run() {
        embeddedServer(
            factory = Netty,
            port = port,
            host = "localhost",
            watchPaths = listOf("classes", "resources")
        ) {
            DatabaseFactory.init()

            install(CallLogging)
            install(DefaultHeaders)
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                })
            }
            install(Resources)

            startKoin {
                modules(repositoryModule)
            }

            routing {
                swaggerUI("swagger")
            }

            routing {
                get("/") {
                    call.respondHtml(HttpStatusCode.OK, HTML::index)
                }
                staticResources("/static", "")
            }
            personRouting()

        }.start(wait = true)
    }
}
