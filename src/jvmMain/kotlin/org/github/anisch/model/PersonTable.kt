package org.github.anisch.model

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object PersonTable : LongIdTable() {
    val name = text("name")
    val givenName = text("given_name")
    val birthDay = date("birthday")
    val insuranceNumber = varchar("insurance_number", 20)
}
