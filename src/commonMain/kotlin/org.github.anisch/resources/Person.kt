package org.github.anisch.resources

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("persons")
class Person {
    @Serializable
    @Resource("{id}")
    class Id(val parent: Person = Person(), val id: Long)
}
