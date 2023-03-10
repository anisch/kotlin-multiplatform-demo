package org.github.anisch

import io.github.oshai.KotlinLogging
import kotlinx.datetime.LocalDate
import org.github.anisch.model.PersonTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

private val log = KotlinLogging.logger { }

object DatabaseFactory {
    fun init() {

        log.debug { "init db" }

        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db"
        val database = Database.connect(jdbcURL, driverClassName)

        transaction(database) {
            SchemaUtils.drop(PersonTable)
            SchemaUtils.create(PersonTable)

            PersonTable.insert {
                it[name] = "Simpson"
                it[givenName] = "Homer"
                it[insuranceNumber] = "abc12345"
                it[birthDay] = LocalDate(2020, 1, 2)
            }
            PersonTable.insert {
                it[name] = "Simpson"
                it[givenName] = "Marge"
                it[insuranceNumber] = "abc23456"
                it[birthDay] = LocalDate(2020, 1, 5)
            }
        }
    }
}
