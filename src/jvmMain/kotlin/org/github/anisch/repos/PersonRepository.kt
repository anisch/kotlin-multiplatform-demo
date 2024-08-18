package org.github.anisch.repos

import kotlinx.coroutines.Dispatchers
import org.github.anisch.model.PersonTable
import org.github.anisch.model.PersonTable.birthDay
import org.github.anisch.model.PersonTable.givenName
import org.github.anisch.model.PersonTable.id
import org.github.anisch.model.PersonTable.insuranceNumber
import org.github.anisch.model.PersonTable.name
import org.github.anisch.serial.Person
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


fun ResultRow.toPerson() =
    Person(
        id = this[id].value,
        name = this[name],
        givenName = this[givenName],
        birthDay = this[birthDay],
        insuranceNumber = this[insuranceNumber],
    )

interface PersonRepository : CrudRepository<Person>

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class DefaultPersonRepository : PersonRepository {
    override suspend fun create(p: Person): Long = dbQuery {
        val id = PersonTable
            .insert {
                if (p.id > 0L) it[id] = p.id
                it[name] = p.name
                it[givenName] = p.givenName
                it[birthDay] = p.birthDay
                it[insuranceNumber] = p.insuranceNumber
            } get id

        id.value
    }

    override suspend fun read(): List<Person> = dbQuery {
        PersonTable
            .selectAll()
            .toList()
            .map { it.toPerson() }
    }

    override suspend fun read(id: Long): Person? = dbQuery {
        PersonTable
            .selectAll()
            .where { PersonTable.id eq id }
            .toList()
            .map { it.toPerson() }
            .firstOrNull()
    }

    override suspend fun update(p: Person): Person = dbQuery {
        PersonTable
            .update({ id eq p.id }) {
                it[name] = p.name
                it[givenName] = p.givenName
                it[birthDay] = p.birthDay
                it[insuranceNumber] = p.insuranceNumber
            }
        p
    }

    override suspend fun delete(): List<Person> = dbQuery {
        val tmp = read()
        PersonTable.deleteAll()
        tmp
    }

    override suspend fun delete(id: Long): Person? = dbQuery {
        val tmp = read(id)
        PersonTable.deleteWhere { PersonTable.id eq id }
        tmp
    }
}
