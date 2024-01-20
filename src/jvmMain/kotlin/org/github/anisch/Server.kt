package org.github.anisch

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.serialization.json.Json
import org.github.anisch.modules.daoModule
import org.github.anisch.routing.personRouting
import org.koin.core.context.startKoin

fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            +"Hello from Ktor"
        }
        div {
            id = "root"
        }
        script(src = "/static/sentimental-daemon.js") {}
    }
}

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
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
            modules(daoModule)
        }

        personRouting()

        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
            staticResources("/static", "")
        }
    }.start(wait = true)
}
