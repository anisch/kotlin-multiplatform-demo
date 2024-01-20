package org.github.anisch.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.github.anisch.repos.PersonRepository
import org.koin.ktor.ext.inject
import org.github.anisch.resources.Person as RPerson
import org.github.anisch.serial.Person as SPerson

fun Application.personRouting() {

    val personRepository by inject<PersonRepository>()

    routing {
        get<RPerson> { _ ->
            val result = personRepository.read()
            call.respond(result)
        }
        get<RPerson.Id> { p ->
            val result = personRepository.read(p.id)
            if (result != null) call.respond(result)
            else call.respond(HttpStatusCode.NotFound, "Person not found")
        }
        post<RPerson> {
            val p = call.receive<SPerson>()
            val id = personRepository.create(p)
            call.respond(HttpStatusCode.Created, id)
        }
        put<RPerson> {
            call.respond(HttpStatusCode.NotImplemented)
        }
        delete<RPerson> {
            val result = personRepository.delete()
            call.respond(result)
        }
        delete<RPerson.Id> { p ->
            val result = personRepository.delete(p.id)
            if (result != null) call.respond(result)
            call.respond(HttpStatusCode.NotFound, "Person not found")
        }
    }
}
