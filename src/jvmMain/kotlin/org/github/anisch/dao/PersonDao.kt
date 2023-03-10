package org.github.anisch.dao

import org.github.anisch.model.PersonTable
import org.github.anisch.model.PersonTable.birthDay
import org.github.anisch.model.PersonTable.givenName
import org.github.anisch.model.PersonTable.id
import org.github.anisch.model.PersonTable.name
import org.github.anisch.serial.Person
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


fun ResultRow.toPerson() =
    Person(
        id = this[id],
        name = this[name],
        givenName = this[givenName],
        birthDay = this[birthDay],
        insuranceNumber = this[PersonTable.insuranceNumber],
    )

interface PersonDao {
    suspend fun getAll(): List<Person>
    suspend fun getByID(id: Long): Person?
    suspend fun savePerson(p: Person): Long
}

class DefaultPersonDao : PersonDao {
    override suspend fun getAll(): List<Person> = transaction {
        PersonTable.selectAll().toList()
    }.map { it.toPerson() }

    override suspend fun getByID(id: Long): Person? = transaction {
        PersonTable.select { PersonTable.id eq id }.toList()
    }.map { it.toPerson() }
        .firstOrNull()

    override suspend fun savePerson(p: Person): Long {
        return transaction {
            val id = PersonTable.insert {
                if (p.id > 0L) it[id] = p.id
                it[name] = p.name
                it[givenName] = p.givenName
                it[birthDay] = p.birthDay
                it[insuranceNumber] = p.insuranceNumber
            } get PersonTable.id

            return@transaction id
        }
    }
}
