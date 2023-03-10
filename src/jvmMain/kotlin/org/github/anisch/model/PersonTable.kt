package org.github.anisch.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date

object PersonTable : Table() {
    val id = long("id").autoIncrement()
    val name = text("name")
    val givenName = text("given_name")
    val birthDay = date("birthday")
    val insuranceNumber = varchar("insurance_number", 20)
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}
