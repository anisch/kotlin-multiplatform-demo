package org.github.anisch

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

val client = HttpClient(JsClient()) {
    install(Resources)
    install(DefaultRequest)
    install(ContentNegotiation) {
        json(json = Json {
            prettyPrint = true
        })
    }

    defaultRequest {
        url {
            host = "localhost"
            port = 8080
            protocol = URLProtocol.HTTP
        }
    }
}
