package org.github.anisch.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.github.anisch.dao.PersonDao
import org.koin.ktor.ext.inject
import org.github.anisch.resources.Person as RPerson
import org.github.anisch.serial.Person as SPerson

fun Application.personRouting() {
    install(Resources)

    val personDao by inject<PersonDao>()

    routing {
        get<RPerson> { _ ->
            val result = personDao.getAll()
            call.respond(result)
        }
        get<RPerson.Id> { p ->
            val result = personDao.getByID(p.id)
            if (result != null) call.respond(result)
            else call.respond(HttpStatusCode.NotFound, "Person not found")
        }
        post<RPerson> {
            val p = call.receive<SPerson>()
            val id = personDao.savePerson(p)
            call.respond(HttpStatusCode.Created, id)
        }
    }
}
